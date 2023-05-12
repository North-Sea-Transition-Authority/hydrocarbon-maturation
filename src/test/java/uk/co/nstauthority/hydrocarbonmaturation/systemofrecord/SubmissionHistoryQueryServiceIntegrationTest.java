package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.time.Instant;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@Transactional
@IntegrationTest
class SubmissionHistoryQueryServiceIntegrationTest {

  @Autowired
  private SubmissionHistoryQueryService submissionHistoryQueryService;

  @Autowired
  private EntityManager entityManager;

  @Nested
  class GetApprovedAndSubmittedSubmissionsOnResource {

    @Test
    void WhenOneApprovedAndOneSubmittedOnResource_assertOrderAndAllValues() {
      var prospectResource = createProspectResource();
      var approvedSubmission = createSubmissionDetail(prospectResource, SubmissionType.CREATE_PROSPECT);
      approvedSubmission.setSubmittedTimestamp(approvedSubmission.getSubmittedTimestamp().minusSeconds(3000));
      var submittedSubmission = createSubmissionDetail(prospectResource, SubmissionType.PROSPECT_UPDATE);
      submittedSubmission.setSubmittedTimestamp(approvedSubmission.getSubmittedTimestamp().minusSeconds(1000));
      submittedSubmission.setStatus(SubmissionDetailStatus.SUBMITTED);
      submittedSubmission.setApprovedTimestamp(null);
      entityManager.persist(approvedSubmission);
      entityManager.flush();
      entityManager.persist(submittedSubmission);
      entityManager.flush();

      var resourceId = prospectResource.getId();
      var results = submissionHistoryQueryService
          .getApprovedAndSubmittedSubmissionsOnResource(resourceId);

      assertThat(results)
          .extracting(
              SubmissionQuerySearchRecord::getStatus,
              SubmissionQuerySearchRecord::getSubmissionDetailId,
              SubmissionQuerySearchRecord::getSubmissionType,
              searchRecord -> {
                if (searchRecord.getApprovedTimestamp() != null) {
                  return searchRecord.getApprovedTimestamp().getTime();
                } else {
                  return null;
                }
              })
          .containsExactly(
          tuple(
              SubmissionDetailStatus.APPROVED,
              approvedSubmission.getId(),
              approvedSubmission.getSubmissionType(),
              approvedSubmission.getApprovedTimestamp().toEpochMilli()),
          tuple(
              SubmissionDetailStatus.SUBMITTED,
              submittedSubmission.getId(),
              submittedSubmission.getSubmissionType(),
              null)
      );
    }

    @ParameterizedTest
    @EnumSource(value = SubmissionDetailStatus.class, names = {"SUBMITTED", "APPROVED"}, mode = EnumSource.Mode.EXCLUDE)
    void WhenSubmissionWithNotSubmittedOrApprovedOnResource_assertNoneReturned(SubmissionDetailStatus status) {
      var prospectResource = createProspectResource();
      var submissionWithStatus = createSubmissionDetail(prospectResource, SubmissionType.CREATE_PROSPECT);
      submissionWithStatus.setStatus(status);
      entityManager.persist(submissionWithStatus);
      entityManager.flush();

      var resourceId = prospectResource.getId();
      var results = submissionHistoryQueryService
          .getApprovedAndSubmittedSubmissionsOnResource(resourceId);

      assertThat(results).isEmpty();
    }

    @Test
    void WhenNoApprovedOrSubmittedOnResource_assertNoneReturned() {
      var prospectResource = createProspectResource();

      var resourceId = prospectResource.getId();
      var results = submissionHistoryQueryService
          .getApprovedAndSubmittedSubmissionsOnResource(resourceId);

      assertThat(results).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void WhenDiscoveryFromProspect_whenLinkedOrNot_assertReturnedOrder(boolean linkedResources) {
      var prospectResource = createProspectResource();

      var initialProspectSubmission = createSubmissionDetail(prospectResource, SubmissionType.CREATE_PROSPECT);
      initialProspectSubmission.setSubmittedTimestamp(Instant.now().minusSeconds(4000));
      initialProspectSubmission.setStatus(SubmissionDetailStatus.APPROVED);

      var prospectUpdateSubmission = createSubmissionDetail(prospectResource, SubmissionType.PROSPECT_UPDATE);
      prospectUpdateSubmission.setSubmittedTimestamp(Instant.now().minusSeconds(3000));
      prospectUpdateSubmission.setStatus(SubmissionDetailStatus.APPROVED);

      var discoveryLinkedToAProspect = createDiscoveryResource();

      var initialDiscoverySubmission = createSubmissionDetail(discoveryLinkedToAProspect, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
      initialDiscoverySubmission.setStatus(SubmissionDetailStatus.APPROVED);
      initialDiscoverySubmission.setSubmittedTimestamp(Instant.now().minusSeconds(2000));

      var discoveryUpdateSubmission = createSubmissionDetail(discoveryLinkedToAProspect, SubmissionType.UPDATE_DISCOVERY);
      discoveryUpdateSubmission.setStatus(SubmissionDetailStatus.SUBMITTED);
      discoveryUpdateSubmission.setSubmittedTimestamp(Instant.now().minusSeconds(1000));

      if (linkedResources) {
        var resourceLink = createResourceLink(prospectResource, discoveryLinkedToAProspect);
      }

      entityManager.persist(initialProspectSubmission);
      entityManager.flush();
      entityManager.persist(prospectUpdateSubmission);
      entityManager.flush();
      entityManager.persist(initialDiscoverySubmission);
      entityManager.flush();
      entityManager.persist(discoveryUpdateSubmission);
      entityManager.flush();

      var resourceId = discoveryLinkedToAProspect.getId();
      var results = submissionHistoryQueryService
          .getApprovedAndSubmittedSubmissionsOnResource(resourceId);
      if (linkedResources) {
        assertThat(results)
            .extracting(
                SubmissionQuerySearchRecord::getStatus,
                SubmissionQuerySearchRecord::getSubmissionDetailId)
            .containsExactly(
                tuple(
                    SubmissionDetailStatus.SUBMITTED,
                    discoveryUpdateSubmission.getId()),
                tuple(
                    SubmissionDetailStatus.APPROVED,
                    initialDiscoverySubmission.getId()),
                tuple(
                    SubmissionDetailStatus.APPROVED,
                    prospectUpdateSubmission.getId()),
                tuple(
                    SubmissionDetailStatus.APPROVED,
                    initialProspectSubmission.getId()
                )
            );
      } else {
        //if no link between discovery and prospect, only discovery submissions returned
        assertThat(results)
            .extracting(
                SubmissionQuerySearchRecord::getStatus,
                SubmissionQuerySearchRecord::getSubmissionDetailId)
            .containsExactly(
                tuple(
                    SubmissionDetailStatus.SUBMITTED,
                    discoveryUpdateSubmission.getId()),
                tuple(
                    SubmissionDetailStatus.APPROVED,
                    initialDiscoverySubmission.getId())
            );
      }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void WhenProspect_whenWithdrawnOrNot_assertReturnedOrder(Boolean withdrawn) {
      var prospectResource = createProspectResource();
      if (!withdrawn) {
        withdrawn = null;
      }

      var initialProspectSubmission = createSubmissionDetail(prospectResource, SubmissionType.CREATE_PROSPECT);
      initialProspectSubmission.setSubmittedTimestamp(Instant.now().minusSeconds(4000));
      initialProspectSubmission.setStatus(SubmissionDetailStatus.APPROVED);

      var prospectUpdateSubmission = createSubmissionDetail(
          prospectResource, withdrawn, SubmissionType.PROSPECT_UPDATE);
      prospectUpdateSubmission.setSubmittedTimestamp(Instant.now().minusSeconds(3000));
      prospectUpdateSubmission.setStatus(SubmissionDetailStatus.SUBMITTED);

      entityManager.persist(initialProspectSubmission);
      entityManager.flush();
      entityManager.persist(prospectUpdateSubmission);
      entityManager.flush();

      var resourceId = prospectResource.getId();
      var results = submissionHistoryQueryService
          .getApprovedAndSubmittedSubmissionsOnResource(resourceId);
      if (withdrawn == null) {
        assertThat(results)
            .extracting(
                SubmissionQuerySearchRecord::getStatus,
                SubmissionQuerySearchRecord::getSubmissionDetailId)
            .containsExactly(
                tuple(
                    SubmissionDetailStatus.SUBMITTED,
                    prospectUpdateSubmission.getId()),
                tuple(
                    SubmissionDetailStatus.APPROVED,
                    initialProspectSubmission.getId()
                )
            );
      } else {
        assertThat(results)
            .extracting(
                SubmissionQuerySearchRecord::getStatus,
                SubmissionQuerySearchRecord::getSubmissionDetailId)
            .containsExactly(
                tuple(
                    SubmissionDetailStatus.APPROVED,
                    initialProspectSubmission.getId())
            );
      }
    }
  }

  @Nested
  class GetApprovedOnResource {

    @Test
    void WhenNoApprovedOnResource_assertNoneReturned() {
      var prospectResource = createProspectResource();

      var resourceId = prospectResource.getId();
      var results = submissionHistoryQueryService
          .getApprovedSubmissionsOnResource(resourceId);

      assertThat(results).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(value = SubmissionDetailStatus.class, names = {"APPROVED"}, mode = EnumSource.Mode.EXCLUDE)
    void WhenSubmissionWithNoApprovedOnResource_assertNoneReturned(SubmissionDetailStatus status) {
      var prospectResource = createProspectResource();
      var submissionWithStatus = createSubmissionDetail(prospectResource, SubmissionType.CREATE_PROSPECT);
      submissionWithStatus.setStatus(status);
      entityManager.persist(submissionWithStatus);
      entityManager.flush();

      var resourceId = prospectResource.getId();
      var results = submissionHistoryQueryService
          .getApprovedSubmissionsOnResource(resourceId);

      assertThat(results).isEmpty();
    }

    @Test
    void WhenOneApprovedAndOneSubmittedOnResource_assertApprovedReturnedAndAllValues() {
      var prospectResource = createProspectResource();
      var approvedSubmission = createSubmissionDetail(prospectResource, SubmissionType.CREATE_PROSPECT);
      approvedSubmission.setSubmittedTimestamp(approvedSubmission.getSubmittedTimestamp().minusSeconds(3000));
      var submittedSubmission = createSubmissionDetail(prospectResource, SubmissionType.PROSPECT_UPDATE);
      submittedSubmission.setSubmittedTimestamp(approvedSubmission.getSubmittedTimestamp().minusSeconds(1000));
      submittedSubmission.setStatus(SubmissionDetailStatus.SUBMITTED);
      submittedSubmission.setApprovedTimestamp(null);
      entityManager.persist(approvedSubmission);
      entityManager.flush();
      entityManager.persist(submittedSubmission);
      entityManager.flush();

      var resourceId = prospectResource.getId();
      var results = submissionHistoryQueryService
          .getApprovedSubmissionsOnResource(resourceId);

      assertThat(results)
          .extracting(
              SubmissionQuerySearchRecord::getStatus,
              SubmissionQuerySearchRecord::getSubmissionDetailId,
              SubmissionQuerySearchRecord::getSubmissionType,
              searchRecord -> searchRecord.getApprovedTimestamp().getTime())
          .containsExactly(
              tuple(
                  SubmissionDetailStatus.APPROVED,
                  approvedSubmission.getId(),
                  approvedSubmission.getSubmissionType(),
                  approvedSubmission.getApprovedTimestamp().toEpochMilli())
          );
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void WhenDiscoveryFromProspect_whenLinkedOrNot_assertReturnedOrder(boolean linkedResources) {
      var prospectResource = createProspectResource();

      var initialProspectSubmission = createSubmissionDetail(prospectResource, SubmissionType.CREATE_PROSPECT);
      initialProspectSubmission.setSubmittedTimestamp(Instant.now().minusSeconds(4000));
      initialProspectSubmission.setStatus(SubmissionDetailStatus.APPROVED);

      var prospectUpdateSubmission = createSubmissionDetail(prospectResource, SubmissionType.PROSPECT_UPDATE);
      prospectUpdateSubmission.setSubmittedTimestamp(Instant.now().minusSeconds(3000));
      prospectUpdateSubmission.setStatus(SubmissionDetailStatus.APPROVED);

      var discoveryLinkedToAProspect = createDiscoveryResource();

      var initialDiscoverySubmission = createSubmissionDetail(discoveryLinkedToAProspect, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
      initialDiscoverySubmission.setStatus(SubmissionDetailStatus.APPROVED);
      initialDiscoverySubmission.setSubmittedTimestamp(Instant.now().minusSeconds(2000));

      var discoveryUpdateSubmission = createSubmissionDetail(discoveryLinkedToAProspect, SubmissionType.UPDATE_DISCOVERY);
      discoveryUpdateSubmission.setStatus(SubmissionDetailStatus.APPROVED);
      discoveryUpdateSubmission.setSubmittedTimestamp(Instant.now().minusSeconds(1000));

      if (linkedResources) {
        var resourceLink = createResourceLink(prospectResource, discoveryLinkedToAProspect);
      }

      entityManager.persist(initialProspectSubmission);
      entityManager.flush();
      entityManager.persist(prospectUpdateSubmission);
      entityManager.flush();
      entityManager.persist(initialDiscoverySubmission);
      entityManager.flush();
      entityManager.persist(discoveryUpdateSubmission);
      entityManager.flush();

      var resourceId = discoveryLinkedToAProspect.getId();
      var results = submissionHistoryQueryService
          .getApprovedSubmissionsOnResource(resourceId);
      if (linkedResources) {
        assertThat(results)
            .extracting(
                SubmissionQuerySearchRecord::getStatus,
                SubmissionQuerySearchRecord::getSubmissionDetailId)
            .containsExactly(
                tuple(
                    SubmissionDetailStatus.APPROVED,
                    discoveryUpdateSubmission.getId()),
                tuple(
                    SubmissionDetailStatus.APPROVED,
                    initialDiscoverySubmission.getId()),
                tuple(
                    SubmissionDetailStatus.APPROVED,
                    prospectUpdateSubmission.getId()),
                tuple(
                    SubmissionDetailStatus.APPROVED,
                    initialProspectSubmission.getId())
            );
      } else {
        //if no link between discovery and prospect, only discovery submissions returned
        assertThat(results)
            .extracting(
                SubmissionQuerySearchRecord::getStatus,
                SubmissionQuerySearchRecord::getSubmissionDetailId)
            .containsExactly(
                tuple(
                    SubmissionDetailStatus.APPROVED,
                    discoveryUpdateSubmission.getId()),
                tuple(
                    SubmissionDetailStatus.APPROVED,
                    initialDiscoverySubmission.getId())
            );
      }
    }

    @Test
    void WhenProspectWithUpdate_whenOneDraftOneSubmitted_assertNoDraftUpdatesAndOnlyLatestSubmitted() {
      var prospectResource = createProspectResource();

      var initialProspectSubmission = createSubmissionDetail(prospectResource, SubmissionType.CREATE_PROSPECT);
      initialProspectSubmission.setSubmittedTimestamp(Instant.now().minusSeconds(4000));
      initialProspectSubmission.setStatus(SubmissionDetailStatus.SUBMITTED);
      initialProspectSubmission.setVersionNumber(1);

      var prospectUpdateDraftSubmission = createSubmissionDetail(prospectResource, SubmissionType.CREATE_PROSPECT);
      prospectUpdateDraftSubmission.setSubmission(initialProspectSubmission.getSubmission());
      prospectUpdateDraftSubmission.setVersionNumber(2);
      prospectUpdateDraftSubmission.setSubmittedTimestamp(Instant.now().minusSeconds(3000));
      prospectUpdateDraftSubmission.setStatus(SubmissionDetailStatus.DRAFT);


      var initialProspectSubmission2 = createSubmissionDetail(prospectResource, SubmissionType.CREATE_PROSPECT);
      initialProspectSubmission2.setSubmittedTimestamp(Instant.now().minusSeconds(2000));
      initialProspectSubmission2.setStatus(SubmissionDetailStatus.SUBMITTED);
      initialProspectSubmission2.setVersionNumber(1);

      var prospectUpdateSubmittedSubmission = createSubmissionDetail(prospectResource, SubmissionType.CREATE_PROSPECT);
      prospectUpdateSubmittedSubmission.setSubmission(initialProspectSubmission2.getSubmission());
      prospectUpdateSubmittedSubmission.setVersionNumber(2);
      prospectUpdateSubmittedSubmission.setSubmittedTimestamp(Instant.now().minusSeconds(1000));
      prospectUpdateSubmittedSubmission.setStatus(SubmissionDetailStatus.SUBMITTED);


      entityManager.persist(initialProspectSubmission);
      entityManager.flush();
      entityManager.persist(prospectUpdateDraftSubmission);
      entityManager.flush();
      entityManager.persist(initialProspectSubmission2);
      entityManager.flush();
      entityManager.persist(prospectUpdateSubmittedSubmission);
      entityManager.flush();

      var results = submissionHistoryQueryService
          .getApprovedAndSubmittedSubmissionsOnResource(prospectResource.getId());

      assertThat(results)
          .extracting(
              SubmissionQuerySearchRecord::getStatus,
              SubmissionQuerySearchRecord::getSubmissionDetailId)
          .containsExactly(
              tuple(
                  SubmissionDetailStatus.SUBMITTED,
                  prospectUpdateSubmittedSubmission.getId()),
              tuple(
                  SubmissionDetailStatus.SUBMITTED,
                  initialProspectSubmission.getId())
          );
    }
  }

  private SubmissionDetail createSubmissionDetail(Resource resource, SubmissionType submissionType) {
    var submission = SubmissionTestUtils.createSubmissionForIntegrationTest(resource, entityManager);
    return SubmissionIntegrationTestUtils.createSubmissionDetail(submission, submissionType, entityManager);
  }

  private SubmissionDetail createSubmissionDetail(Resource resource, Boolean withdrawn, SubmissionType submissionType) {
    var submission = SubmissionTestUtils.createSubmissionForIntegrationTest(resource, withdrawn, entityManager);
    return SubmissionIntegrationTestUtils.createSubmissionDetail(submission, submissionType, entityManager);
  }

  private Resource createProspectResource() {
    return SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.PROSPECT);
  }

  private Resource createDiscoveryResource() {
    return SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.DISCOVERY);
  }

  private ResourceLink createResourceLink(Resource discovery, Resource prospect) {
    return SubmissionIntegrationTestUtils.createResourceLink(prospect, discovery, entityManager);
  }
}