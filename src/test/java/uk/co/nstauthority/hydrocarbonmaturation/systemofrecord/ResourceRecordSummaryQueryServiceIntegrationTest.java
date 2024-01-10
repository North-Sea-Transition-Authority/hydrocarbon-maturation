package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.persistence.EntityManager;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;

@Transactional
@IntegrationTest
class ResourceRecordSummaryQueryServiceIntegrationTest {
  @Autowired
  private DSLContext dslContext;

  @Autowired
  private ResourceRecordSummaryQueryService resourceRecordSummaryQueryService;

  @Autowired
  private EntityManager entityManager;

  @Test
  void getResourceRecord_WhenTwoResourceRecords_ReturnsResourceRecordQueried() {
    var resource1 = buildResource();
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    var generalDetails1 = createGeneralDetail(submissionDetail1);
    var resourceDetails1 = createResourceDetail(submissionDetail1, resourceName1, organisationId1);

    var quadrantBlock1 = "25/12";
    var centreOfInterestQuadrantBlock1 = createQuadAndBlock(
        generalDetails1,
        quadrantBlock1,
        "25",
        12,
        OnshoreOffshore.OFFSHORE
    );
    var trapProperty1 = createTrapProperty(submissionDetail1, quadrantBlock1);

    var licence1 = createLicence(951, "AL10", generalDetails1);
    var licence2 = createLicence(953, "AL6", generalDetails1);

    var resource2 = buildResource();
    var responsibleTeam = buildResponsibleTeam(resource2, ResponsibleTeamType.EXPLORATION);
    var submissionDetail2 = buildSubmissionDetail(resource2, SubmissionType.CREATE_PROSPECT);
    var resourceName2 = "The resource name B - to be queried";
    var organisationId2 = 48;
    var generalDetails2 = createGeneralDetail(submissionDetail2);
    var resourceDetails2 = createResourceDetail(submissionDetail2, resourceName2, organisationId2);

    var quadrantBlock2 = "50/11";
    var centreOfInterestQuadrantBlock2 = createQuadAndBlock(
        generalDetails2,
        quadrantBlock2,
        "50",
        11,
        OnshoreOffshore.OFFSHORE
    );
    var trapProperty2 = createTrapProperty(submissionDetail2, quadrantBlock2);

    var licence3 = createLicence(253, "BL10", generalDetails2);
    var licence4 = createLicence(351, "BL6", generalDetails2);

    var result = resourceRecordSummaryQueryService.getResourceRecord(resource2.getId());

    assertThat(result)
        .isPresent()
        .get()
        .extracting(
            ResourceRecordSummaryQueryResult::submissionDetailId,
            ResourceRecordSummaryQueryResult::centreOfInterestQuadBlock,
            ResourceRecordSummaryQueryResult::organisationResponsibleId,
            ResourceRecordSummaryQueryResult::resourceName,
            ResourceRecordSummaryQueryResult::resourceType,
            ResourceRecordSummaryQueryResult::responsibleTeam,
            ResourceRecordSummaryQueryResult::licencesTheResourceIsOn
        )
        .containsExactly(
            submissionDetail2.getId(),
            centreOfInterestQuadrantBlock2.getQuadrantBlockReference(),
            organisationId2,
            resourceName2,
            ResourceType.PROSPECT,
            ResponsibleTeamType.EXPLORATION,
            List.of(licence3.getLicenceId(), licence4.getLicenceId())
        );
  }

  @Test
  void getResourceRecord_WhenTwoResourceRecords_returnsNonWithdrawnResourceRecord() {
    var resource1 = buildResource();
    var submissionNotWithdrawn = SubmissionIntegrationTestUtils.createSubmission(resource1, null, entityManager);
    var submissionDetail1 = buildSubmissionDetail(resource1, SubmissionType.CREATE_PROSPECT);
    submissionDetail1.setSubmission(submissionNotWithdrawn);
    var resourceName1 = "The resource name A";
    var organisationId1 = 46;
    createGeneralDetail(submissionDetail1);
    createResourceDetail(submissionDetail1, resourceName1, organisationId1);

    buildResponsibleTeam(resource1, ResponsibleTeamType.EXPLORATION);
    var submissionDetail2 = buildSubmissionDetail(resource1, SubmissionType.PROSPECT_UPDATE);
    var submissionWithdrawn = SubmissionIntegrationTestUtils.createSubmission(resource1, true, entityManager);
    submissionDetail2.setSubmission(submissionWithdrawn);
    createGeneralDetail(submissionDetail2);
    createResourceDetail(submissionDetail2, resourceName1, organisationId1);

    var result = resourceRecordSummaryQueryService.getResourceRecord(resource1.getId());

    assertThat(result)
        .isPresent()
        .get()
        .extracting(
            ResourceRecordSummaryQueryResult::submissionDetailId
        )
        .isEqualTo(
            submissionDetail1.getId()
        );
  }

  @Test
  void getResourceRecord_WhenFieldSubmission_returnsResourceRecordWithFieldInformation() {
    var resource = buildResource();
    var submissionNotWithdrawn = SubmissionIntegrationTestUtils.createSubmission(resource, null, entityManager);
    var submissionDetail = buildSubmissionDetail(resource, SubmissionType.CREATE_CSR);
    submissionDetail.setSubmission(submissionNotWithdrawn);
    var resourceName = "The resource name A";
    var organisationId = 46;
    createGeneralDetail(submissionDetail);
    var field = SubmissionIntegrationTestUtils
        .createFieldWithParent(resource, resourceName, organisationId, entityManager);
    var fieldStatusHistory =
        SubmissionIntegrationTestUtils.createFieldStatusHistory(field, FieldStatus.STATUS_300, Instant.now(), entityManager);

    var result = resourceRecordSummaryQueryService.getResourceRecord(resource.getId());

    assertThat(result)
        .isPresent()
        .get()
        .extracting(
            ResourceRecordSummaryQueryResult::resourceName,
            ResourceRecordSummaryQueryResult::organisationResponsibleId,
            ResourceRecordSummaryQueryResult::parentFieldId,
            ResourceRecordSummaryQueryResult::fieldStatus
        )
        .containsExactly(
            field.getCorrectedLegalFieldName(),
            field.getResponsibleOrganisationId(),
            field.getParentDeterminedField().getFieldIdentifier(),
            fieldStatusHistory.getFieldStatus()
        );
  }

  @Test
  void getResourceRecord_WhenFieldSubmission_multipleFieldStatusesOnSameDay_returnsHighestStatus() {
    var resource = buildResource();
    var submissionNotWithdrawn = SubmissionIntegrationTestUtils.createSubmission(resource, null, entityManager);
    var submissionDetail1 = buildSubmissionDetail(resource, SubmissionType.CREATE_CSR);
    submissionDetail1.setSubmission(submissionNotWithdrawn);
    var resourceName = "The resource name A";
    var organisationId = 46;
    createGeneralDetail(submissionDetail1);
    var field = SubmissionIntegrationTestUtils.createFieldWithParent(resource, resourceName, organisationId, entityManager);
    var fixedInstant = Instant.now();
    var lowestFieldStatusHistory =
        SubmissionIntegrationTestUtils.createFieldStatusHistory(field, FieldStatus.STATUS_200, fixedInstant, entityManager);
    var highestFieldStatusHistory =
        SubmissionIntegrationTestUtils.createFieldStatusHistory(field, FieldStatus.STATUS_300, fixedInstant, entityManager);

    var result = resourceRecordSummaryQueryService.getResourceRecord(resource.getId());

    assertThat(result)
        .isPresent();

    assertThat(result.get().fieldStatus())
        .isEqualTo(
            highestFieldStatusHistory.getFieldStatus()
        );
  }

  @Test
  void getResourceRecord_WhenFieldSubmission_multipleFieldStatusesOnDifferentDates_returnsMostRecent() {
    var resource = buildResource();
    var submissionNotWithdrawn = SubmissionIntegrationTestUtils.createSubmission(resource, null, entityManager);
    var submissionDetail1 = buildSubmissionDetail(resource, SubmissionType.CREATE_CSR);
    submissionDetail1.setSubmission(submissionNotWithdrawn);
    var resourceName = "The resource name A";
    var organisationId = 46;
    createGeneralDetail(submissionDetail1);
    var field = SubmissionIntegrationTestUtils.createFieldWithParent(resource, resourceName, organisationId,  entityManager);
    var fixedInstant = Instant.now();
    var latestFieldStatusHistory =
        SubmissionIntegrationTestUtils.createFieldStatusHistory(field, FieldStatus.STATUS_200, fixedInstant.plus(1, ChronoUnit.DAYS), entityManager);
    var earliestFieldStatusHistory =
        SubmissionIntegrationTestUtils.createFieldStatusHistory(field, FieldStatus.STATUS_300, fixedInstant, entityManager);

    var result = resourceRecordSummaryQueryService.getResourceRecord(resource.getId());

    assertThat(result)
        .isPresent();

    assertThat(result.get().fieldStatus())
        .isEqualTo(
            latestFieldStatusHistory.getFieldStatus()
        );
  }

  private Licence createLicence(int id, String ref, GeneralDetail generalDetails) {

    return SubmissionIntegrationTestUtils.createLicence(id, ref, generalDetails, entityManager);
  }

  private TrapProperty createTrapProperty(SubmissionDetail submissionDetail, String centreOfInterestQuadrantBlock) {

    return SubmissionIntegrationTestUtils.createTrapProperty(submissionDetail, centreOfInterestQuadrantBlock, entityManager);
  }

  private QuadrantAndBlock createQuadAndBlock(
      GeneralDetail generalDetails,
      String quadrantBlockReference,
      String quad,
      Integer block,
      OnshoreOffshore onshoreOffshore
  ) {

    return SubmissionIntegrationTestUtils.createQuadAndBlock(
        generalDetails,
        quadrantBlockReference,
        quad,
        block,
        onshoreOffshore,
        entityManager
    );
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