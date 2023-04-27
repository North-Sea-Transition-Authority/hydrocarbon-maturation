package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import javax.persistence.EntityManager;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionIntegrationTestUtils;

@Transactional
@IntegrationTest
class SubmissionRecordSummaryQueryServiceIntegrationTest {

  @Autowired
  private DSLContext dslContext;

  @Autowired
  private SubmissionRecordSummaryQueryService submissionRecordSummaryQueryService;

  @Autowired
  private EntityManager entityManager;

  @Test
  void getSubmissionRecord_WhenTwoSubmissionRecords_ReturnsSubmissionRecordQueried() {
    var otherResource = buildResource();
    var otherSubmission = buildSubmissionDetail(otherResource, SubmissionType.CREATE_PROSPECT);
    var organisationId1 = 46;
    var generalDetails1 = createGeneralDetail(otherSubmission);
    createResourceDetail(otherSubmission, "Not relevant name", organisationId1);
    createQuadAndBlock(generalDetails1, "25/12");
    createTrapProperty(otherSubmission, "25/12");
    createLicence(951, "AL10", generalDetails1);
    createLicence(953, "AL6", generalDetails1);

    var resourceForQueriedSubmission = buildResource();
    buildResponsibleTeam(resourceForQueriedSubmission, ResponsibleTeamType.EXPLORATION);
    var queriedSubmissionDetail = buildSubmissionDetail(resourceForQueriedSubmission, SubmissionType.CREATE_PROSPECT);
    var resourceToBeQueried = "The resource name B - to be queried";
    var organisationId2 = 48;
    var generalDetailsToBeQueried = createGeneralDetail(queriedSubmissionDetail);
    var resourceDetailsToBeQueried = createResourceDetail(queriedSubmissionDetail, resourceToBeQueried, organisationId2);

    var quadrantBlockTobeQueried = "50/11";
    var centreOfInterestQuadrantBlock = createQuadAndBlock(generalDetailsToBeQueried, quadrantBlockTobeQueried);
    createTrapProperty(queriedSubmissionDetail, quadrantBlockTobeQueried);

    var licenceToBeQueried = createLicence(253, "BL10", generalDetailsToBeQueried);
    var licenceToBeQueried2 = createLicence(351, "BL6", generalDetailsToBeQueried);

    var result = submissionRecordSummaryQueryService
        .getSubmissionRecord(queriedSubmissionDetail.getSubmission().getResource().getId());

    assertThat(result)
        .isPresent()
        .get()
        .extracting(
            SubmissionRecordSummaryQueryResult::getSubmissionDetailId,
            SubmissionRecordSummaryQueryResult::getCentreOfInterestQuadBlock,
            SubmissionRecordSummaryQueryResult::getOrganisationResponsibleId,
            SubmissionRecordSummaryQueryResult::getResourceName,
            SubmissionRecordSummaryQueryResult::getResourceType,
            SubmissionRecordSummaryQueryResult::getResponsibleTeam,
            SubmissionRecordSummaryQueryResult::getLicencesTheResourceIsOn,
            SubmissionRecordSummaryQueryResult::getSubmissionType,
            r -> r.getCreatedOn().getTime(),
            r -> r.getSubmittedOn().getTime()
        )
        .containsExactly(
            queriedSubmissionDetail.getId(),
            centreOfInterestQuadrantBlock.getQuadrantBlock(),
            organisationId2,
            resourceToBeQueried,
            ResourceType.PROSPECT,
            ResponsibleTeamType.EXPLORATION,
            List.of(licenceToBeQueried.getLicenceId(), licenceToBeQueried2.getLicenceId()),
            queriedSubmissionDetail.getSubmissionType(),
            queriedSubmissionDetail.getCreatedTimestamp().toEpochMilli(),
            queriedSubmissionDetail.getSubmittedTimestamp().toEpochMilli()
        );
  }

  @Test
  void getSubmissionRecord_WhenTwoSubmissions_ReturnsLatestSubmitted() {
    var resourceForQueriedSubmission = buildResource();
    buildResponsibleTeam(resourceForQueriedSubmission, ResponsibleTeamType.EXPLORATION);
    var latestSubmittedSubmissionDetail = buildSubmissionDetail(resourceForQueriedSubmission, SubmissionType.CREATE_PROSPECT);
    latestSubmittedSubmissionDetail.setSubmittedTimestamp(Instant.now());
    var otherSubmissionDetail = buildSubmissionDetail(resourceForQueriedSubmission, SubmissionType.CREATE_PROSPECT);
    otherSubmissionDetail.setSubmittedTimestamp(Instant.now().minusSeconds(5000));
    createResourceDetail(otherSubmissionDetail, "Not relevant name", 1);
    createResourceDetail(latestSubmittedSubmissionDetail, "Not relevant name", 1);

    var result = submissionRecordSummaryQueryService
        .getSubmissionRecord(latestSubmittedSubmissionDetail.getSubmission().getResource().getId());

    assertThat(result)
        .isPresent()
        .get()
        .extracting(
            SubmissionRecordSummaryQueryResult::getSubmissionDetailId,
            r -> r.getSubmittedOn().getTime()
        )
        .containsExactly(
            latestSubmittedSubmissionDetail.getId(),
            latestSubmittedSubmissionDetail.getSubmittedTimestamp().toEpochMilli()
        );
  }

  @Test
  void getSubmissionRecord_WhenTwoSubmissionDetailsOnOneSubmission_returnHighestVersion() {
    var resourceForQueriedSubmission = buildResource();
    buildResponsibleTeam(resourceForQueriedSubmission, ResponsibleTeamType.EXPLORATION);
    var firstSubmissionDetail = buildSubmissionDetail(resourceForQueriedSubmission, SubmissionType.CREATE_PROSPECT);
    firstSubmissionDetail.setSubmittedTimestamp(Instant.now());
    var submittedUpdate = buildSubmissionDetail(resourceForQueriedSubmission, SubmissionType.CREATE_PROSPECT);
    submittedUpdate.setVersionNumber(2);
    submittedUpdate.setSubmission(firstSubmissionDetail.getSubmission());
    submittedUpdate.setSubmittedTimestamp(Instant.now().plusSeconds(5000));
    createResourceDetail(submittedUpdate, "Not relevant name", 1);
    createResourceDetail(firstSubmissionDetail, "Not relevant name", 1);

    var result = submissionRecordSummaryQueryService
        .getSubmissionRecord(firstSubmissionDetail.getSubmission().getResource().getId());

    assertThat(result)
        .isPresent()
        .get()
        .extracting(
            SubmissionRecordSummaryQueryResult::getSubmissionDetailId,
            r -> r.getSubmittedOn().getTime()
        )
        .containsExactly(
            submittedUpdate.getId(),
            submittedUpdate.getSubmittedTimestamp().toEpochMilli()
        );
  }

  private Licence createLicence(int id, String ref, GeneralDetail generalDetails) {

    return SubmissionIntegrationTestUtils.createLicence(id, ref, generalDetails, entityManager);
  }

  private TrapProperty createTrapProperty(SubmissionDetail submissionDetail, String centreOfInterestQuadrantBlock) {

    return SubmissionIntegrationTestUtils.createTrapProperty(submissionDetail, centreOfInterestQuadrantBlock, entityManager);
  }

  private QuadrantAndBlock createQuadAndBlock(GeneralDetail generalDetails, String quadrantBlock) {

    return SubmissionIntegrationTestUtils.createQuadAndBlock(generalDetails, quadrantBlock, entityManager);
  }

  private GeneralDetail createGeneralDetail(SubmissionDetail submissionDetail) {

    return SubmissionIntegrationTestUtils.createGeneralDetail(submissionDetail, entityManager);
  }

  private ResourceDetail createResourceDetail(SubmissionDetail submissionDetail, String resourceName, int organisationId) {

    return SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, resourceName, organisationId, entityManager);
  }

  private Resource buildResource() {

    return SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.PROSPECT);
  }

  private RegulatorManagedInformation buildResponsibleTeam(Resource resource, ResponsibleTeamType responsibleTeamType) {

    return SubmissionIntegrationTestUtils.createRegulatorManagedInformation(
        resource, responsibleTeamType, null, entityManager);
  }

  private SubmissionDetail buildSubmissionDetail(Resource resource, SubmissionType submissionType) {
    var submission = SubmissionTestUtils.createSubmissionForIntegrationTest(resource, entityManager);
    return SubmissionIntegrationTestUtils.createSubmissionDetail(submission, submissionType, entityManager);
  }
}