package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.BooleanUtils;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.FieldStatusHistory;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
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
    var otherResource = buildPropsectResource();
    var otherSubmission = buildSubmissionDetail(otherResource, SubmissionType.CREATE_PROSPECT);
    var organisationId1 = 46;
    var generalDetails1 = createGeneralDetail(otherSubmission);
    createResourceDetail(otherSubmission, "Not relevant name", organisationId1);
    createQuadAndBlock(
        generalDetails1,
        "25/12",
        "25",
        12,
        OnshoreOffshore.OFFSHORE
    );
    createTrapProperty(otherSubmission, "25/12");
    createLicence(951, "AL10", generalDetails1);
    createLicence(953, "AL6", generalDetails1);

    var resourceForQueriedSubmission = buildPropsectResource();
    buildResponsibleTeam(resourceForQueriedSubmission, ResponsibleTeamType.EXPLORATION);
    var queriedSubmissionDetail = buildSubmissionDetail(resourceForQueriedSubmission, SubmissionType.CREATE_PROSPECT);
    var resourceToBeQueried = "The resource name B - to be queried";
    var organisationId2 = 48;
    var generalDetailsToBeQueried = createGeneralDetail(queriedSubmissionDetail);
    var resourceDetailsToBeQueried = createResourceDetail(queriedSubmissionDetail, resourceToBeQueried, organisationId2);

    var quadrantBlockTobeQueried = "50/11";
    var centreOfInterestQuadrantBlock = createQuadAndBlock(
        generalDetailsToBeQueried,
        quadrantBlockTobeQueried,
        "50",
        11,
        OnshoreOffshore.OFFSHORE
    );
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
            SubmissionRecordSummaryQueryResult::getSubmissionType
        )
        .containsExactly(
            queriedSubmissionDetail.getId(),
            centreOfInterestQuadrantBlock.getQuadrantBlockReference(),
            organisationId2,
            resourceToBeQueried,
            ResourceType.PROSPECT,
            ResponsibleTeamType.EXPLORATION,
            List.of(licenceToBeQueried.getLicenceId(), licenceToBeQueried2.getLicenceId()),
            queriedSubmissionDetail.getSubmissionType()
        );
  }

  @Test
  void getSubmissionRecord_WhenTwoSubmissions_ReturnsLatestSubmitted() {
    var resourceForQueriedSubmission = buildPropsectResource();
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
            SubmissionRecordSummaryQueryResult::getSubmissionDetailId
        )
        .isEqualTo(
            latestSubmittedSubmissionDetail.getId()
        );
  }

  @Test
  void getSubmissionRecord_WhenFieldSubmission_AssertFieldInformationPresent() {
    var resourceForQueriedSubmission = buildFieldResource();
    buildResponsibleTeam(resourceForQueriedSubmission, ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);
    var latestSubmittedSubmissionDetail = buildSubmissionDetail(resourceForQueriedSubmission, SubmissionType.CREATE_CSR);
    latestSubmittedSubmissionDetail.setSubmittedTimestamp(Instant.now());
    var field = createField(resourceForQueriedSubmission, 51);
    var mostRecentFieldStatusHistory = buildFieldStatusHistory(field);

    var result = submissionRecordSummaryQueryService
        .getSubmissionRecord(latestSubmittedSubmissionDetail.getSubmission().getResource().getId());

    assertThat(result)
        .isPresent()
        .get()
        .extracting(
            SubmissionRecordSummaryQueryResult::getParentFieldId,
            SubmissionRecordSummaryQueryResult::getFieldStatus
        )
        .containsExactly(
            field.getParentDeterminedField().getFieldIdentifier(),
            mostRecentFieldStatusHistory.getFieldStatus()
        );
  }

  @Test
  void getSubmissionRecord_WhenFieldSubmission_WithMultipleFieldStatusesOnSameDate_ReturnsHighestStatus() {
    var resourceForQueriedSubmission = buildFieldResource();
    buildResponsibleTeam(resourceForQueriedSubmission, ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);
    var latestSubmittedSubmissionDetail = buildSubmissionDetail(resourceForQueriedSubmission, SubmissionType.CREATE_CSR);
    latestSubmittedSubmissionDetail.setSubmittedTimestamp(Instant.now());
    var field = createField(resourceForQueriedSubmission, 51);
    var fixedInstant = Instant.now();
    var lowestFieldStatusHistory = SubmissionIntegrationTestUtils.createFieldStatusHistory(field, FieldStatus.STATUS_200, fixedInstant, entityManager);
    var hihestFieldStatusHistory = SubmissionIntegrationTestUtils.createFieldStatusHistory(field, FieldStatus.STATUS_300, fixedInstant, entityManager);

    var result = submissionRecordSummaryQueryService
        .getSubmissionRecord(latestSubmittedSubmissionDetail.getSubmission().getResource().getId());

    assertThat(result)
        .isPresent()
        .get()
        .extracting(
            SubmissionRecordSummaryQueryResult::getParentFieldId,
            SubmissionRecordSummaryQueryResult::getFieldStatus
        )
        .containsExactly(
            field.getParentDeterminedField().getFieldIdentifier(),
            hihestFieldStatusHistory.getFieldStatus()
        );
  }

  @Test
  void getSubmissionRecord_WhenFieldSubmission_WithMultipleFieldStatusesOnDifferentDate_ReturnsMostRecentStatus() {
    var resourceForQueriedSubmission = buildFieldResource();
    buildResponsibleTeam(resourceForQueriedSubmission, ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);
    var latestSubmittedSubmissionDetail = buildSubmissionDetail(resourceForQueriedSubmission, SubmissionType.CREATE_CSR);
    latestSubmittedSubmissionDetail.setSubmittedTimestamp(Instant.now());
    var field = createField(resourceForQueriedSubmission, 51);
    var fixedInstant = Instant.now();
    var latestFieldStatusHistory = SubmissionIntegrationTestUtils.createFieldStatusHistory(field, FieldStatus.STATUS_200, fixedInstant.plus(1, ChronoUnit.DAYS), entityManager);
    var earliestFieldStatusHistory = SubmissionIntegrationTestUtils.createFieldStatusHistory(field, FieldStatus.STATUS_300, fixedInstant, entityManager);

    var result = submissionRecordSummaryQueryService
        .getSubmissionRecord(latestSubmittedSubmissionDetail.getSubmission().getResource().getId());

    assertThat(result)
        .isPresent()
        .get()
        .extracting(
            SubmissionRecordSummaryQueryResult::getParentFieldId,
            SubmissionRecordSummaryQueryResult::getFieldStatus
        )
        .containsExactly(
            field.getParentDeterminedField().getFieldIdentifier(),
            latestFieldStatusHistory.getFieldStatus()
        );
  }

  @Test
  void getSubmissionRecord_WhenTwoSubmissionDetailsOnOneSubmission_returnHighestVersion() {
    var resourceForQueriedSubmission = buildPropsectResource();
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
            SubmissionRecordSummaryQueryResult::getSubmissionDetailId
        )
        .isEqualTo(
            submittedUpdate.getId()
        );
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void getSubmissionRecord_whenSubmissionUpdateWithdrawn_returnLatestWhenNotWithdrawn(Boolean withdrawn) {
    if(!withdrawn) {
      withdrawn = null;
    }

    var resourceForQueriedSubmission = buildPropsectResource();
    buildResponsibleTeam(resourceForQueriedSubmission, ResponsibleTeamType.EXPLORATION);
    var parentSubmission =
        SubmissionIntegrationTestUtils.createSubmission(resourceForQueriedSubmission, withdrawn, entityManager);
    var submissionDetail = buildSubmissionDetail(resourceForQueriedSubmission, SubmissionType.CREATE_PROSPECT);
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setSubmittedTimestamp(Instant.now());
    submissionDetail.setSubmission(parentSubmission);
    entityManager.persist(submissionDetail);
    entityManager.flush();

    var submittedUpdate = buildSubmissionDetail(resourceForQueriedSubmission, SubmissionType.CREATE_PROSPECT);
    submittedUpdate.setSubmission(parentSubmission);
    submittedUpdate.setVersionNumber(2);
    submittedUpdate.setStatus(SubmissionDetailStatus.SUBMITTED);
    submittedUpdate.getSubmission().setWithdrawn(withdrawn);
    submittedUpdate.setSubmission(submissionDetail.getSubmission());
    submittedUpdate.setSubmittedTimestamp(Instant.now().plusSeconds(5000));
    entityManager.persist(submittedUpdate);
    entityManager.flush();

    createResourceDetail(submittedUpdate, "Not relevant name", 1);
    createResourceDetail(submissionDetail, "Not relevant name", 1);

    var result = submissionRecordSummaryQueryService
        .getSubmissionRecord(submissionDetail.getSubmission().getResource().getId());
    if (BooleanUtils.isTrue(withdrawn)) {
      assertThat(result).isNotPresent();
    } else {
      assertThat(result)
          .isPresent()
          .get()
          .extracting(
              SubmissionRecordSummaryQueryResult::getSubmissionDetailId
          )
          .isEqualTo(
              submittedUpdate.getId()
          );
    }
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
        entityManager);
  }

  private GeneralDetail createGeneralDetail(SubmissionDetail submissionDetail) {

    return SubmissionIntegrationTestUtils.createGeneralDetail(submissionDetail, entityManager);
  }

  private ResourceDetail createResourceDetail(SubmissionDetail submissionDetail, String resourceName, int organisationId) {

    return SubmissionIntegrationTestUtils.createResourceDetail(submissionDetail, resourceName, organisationId, entityManager);
  }

  private Field createField(Resource resource, Integer responsibleOrganisationNumber) {
    return SubmissionIntegrationTestUtils.createFieldWithParent(resource, "legal name", responsibleOrganisationNumber, entityManager);
  }

  private Resource buildPropsectResource() {

    return SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.PROSPECT);
  }

  private Resource buildFieldResource() {

    return SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.FIELD);
  }

  private RegulatorManagedInformation buildResponsibleTeam(Resource resource, ResponsibleTeamType responsibleTeamType) {

    return SubmissionIntegrationTestUtils.createRegulatorManagedInformation(
        resource, responsibleTeamType, null, entityManager);
  }

  private SubmissionDetail buildSubmissionDetail(Resource resource, SubmissionType submissionType) {
    var submission = SubmissionTestUtils.createSubmissionForIntegrationTest(resource, entityManager);
    return SubmissionIntegrationTestUtils.createSubmissionDetail(submission, submissionType, entityManager);
  }

  private FieldStatusHistory buildFieldStatusHistory(Field field) {
    var fixedInstant = Instant.now();
    SubmissionIntegrationTestUtils.createFieldStatusHistory(field, 
        FieldStatus.STATUS_200,
        fixedInstant,
        entityManager);
    SubmissionIntegrationTestUtils.createFieldStatusHistory(field,
        FieldStatus.STATUS_300, 
        fixedInstant.plus(1, ChronoUnit.DAYS), 
        entityManager);
    return SubmissionIntegrationTestUtils.createFieldStatusHistory(
        field, 
        FieldStatus.STATUS_400, 
        fixedInstant.plus(2, ChronoUnit.DAYS), 
        entityManager);
  }
}