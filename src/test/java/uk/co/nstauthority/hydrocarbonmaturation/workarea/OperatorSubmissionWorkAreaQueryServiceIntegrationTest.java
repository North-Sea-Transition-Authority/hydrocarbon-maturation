package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.BooleanUtils;
import org.assertj.core.groups.Tuple;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.SamlAuthenticationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;

@Transactional
@IntegrationTest
class OperatorSubmissionWorkAreaQueryServiceIntegrationTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder()
      .withWuaId(2000L)
      .build();

  @Autowired
  private DSLContext dslContext;

  @MockBean
  private IndustryTeamService industryTeamService;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private OperatorSubmissionWorkAreaQueryService operatorSubmissionWorkAreaQueryService;

  @Test
  void getSubmissions_whenOneFullSubmittedSubmission_asIndustryInResponsibleOrganisation_assertAllValues() {
    var responsibleOrganisationIdForSubmission = 46;
    var userOrgUnits = List.of(responsibleOrganisationIdForSubmission);
    setUpIndustryTest(userOrgUnits, USER);
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    var createdTimestamp = Instant.now();
    var resource = createResource(createdTimestamp, entityManager);
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    submissionDetail.setCreatedTimestamp(createdTimestamp);
    entityManager.persist(responsibleTeam);
    entityManager.flush();
    submissionDetail.getSubmission().setResource(resource);
    submissionDetail.setSubmission(SubmissionIntegrationTestUtils.createSubmission(resource, entityManager));

    entityManager.persist(submissionDetail);
    entityManager.flush();

    var generalDetails = new GeneralDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setResourceName("The resource name");
    generalDetails.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(responsibleOrganisationIdForSubmission);
    entityManager.persist(generalDetails);
    entityManager.flush();
    entityManager.persist(resourceDetail);
    entityManager.flush();

    var quadBlocks = new QuadrantAndBlock();
    quadBlocks.setGeneralDetail(generalDetails);
    quadBlocks.setQuadrantBlockReference("25/12");
    entityManager.persist(quadBlocks);
    entityManager.flush();

    var trapProperty = new TrapProperty();
    trapProperty.setSubmissionDetail(submissionDetail);
    trapProperty.setCentreOfInterestQuadrantBlock("25/12");
    entityManager.persist(trapProperty);
    entityManager.flush();

    var licence = new Licence();
    var licenceId1 = 953;
    licence.setLicenceId(licenceId1);
    licence.setLicenceReference("AL10");
    licence.setGeneralDetail(generalDetails);
    var licence2 = new Licence();
    var licenceId2 = 951;
    licence2.setLicenceId(licenceId2);
    licence2.setLicenceReference("AL6");
    licence2.setGeneralDetail(generalDetails);
    entityManager.persist(licence);
    entityManager.flush();
    entityManager.persist(licence2);
    entityManager.flush();

    var results = operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    assertThat(results.get(0))
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            SubmissionWorkAreaQueryResult::getCentreOfInterestQuadBlock,
            SubmissionWorkAreaQueryResult::getOrganisationResponsibleId,
            SubmissionWorkAreaQueryResult::getResourceName,
            SubmissionWorkAreaQueryResult::getSubmissionType,
            SubmissionWorkAreaQueryResult::getStatus,
            SubmissionWorkAreaQueryResult::isUpdateRequested)
        .containsExactly(
            submissionDetail.getId(),
            "25/12",
            46,
            "The resource name",
            SubmissionType.CREATE_PROSPECT,
            SubmissionDetailStatus.SUBMITTED,
            false
        );

    assertThat(results.get(0)
        .getLicencesTheResourceIsOn())
        .containsExactlyInAnyOrder(
            licenceId1, licenceId2);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"}
  )
  void getSubmissions_whenOneFullFieldSubmission_asIndustryInResponsibleOrganisation_assertAllValues(SubmissionType submissionType) {
    var responsibleOrganisationIdForSubmission = 46;
    var userOrgUnits = List.of(responsibleOrganisationIdForSubmission);
    setUpIndustryTest(userOrgUnits, USER);
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    submissionDetail.setId(null);
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    submissionDetail.setCorrection(false);

    var createdTimestamp = Instant.now();
    submissionDetail.setCreatedTimestamp(createdTimestamp);

    var resource = createResource(ResourceType.FIELD, createdTimestamp, entityManager);
    submissionDetail.getSubmission().setResource(resource);
    submissionDetail.setSubmission(SubmissionIntegrationTestUtils.createSubmission(resource, entityManager));
    entityManager.persist(submissionDetail);
    entityManager.flush();

    var generalDetails = new GeneralDetail();
    generalDetails.setSubmissionDetail(submissionDetail);
    entityManager.persist(generalDetails);
    entityManager.flush();

    var trapProperty = new TrapProperty();
    trapProperty.setSubmissionDetail(submissionDetail);
    trapProperty.setCentreOfInterestQuadrantBlock("25/12");
    entityManager.persist(trapProperty);
    entityManager.flush();

    var licence = new Licence();
    var licenceId1 = 953;
    licence.setLicenceId(licenceId1);
    licence.setLicenceReference("AL10");
    licence.setGeneralDetail(generalDetails);
    var licence2 = new Licence();
    var licenceId2 = 951;
    licence2.setLicenceId(licenceId2);
    licence2.setLicenceReference("AL6");
    licence2.setGeneralDetail(generalDetails);
    entityManager.persist(licence);
    entityManager.flush();
    entityManager.persist(licence2);
    entityManager.flush();

    var fieldName = "Field name";
    var field = SubmissionIntegrationTestUtils
        .createFieldWithParent(resource, fieldName, responsibleOrganisationIdForSubmission, entityManager
    );

    var today = Instant.now();
    var yesterday = today.minus(1, ChronoUnit.DAYS);
    var oldFieldStatusHistory = SubmissionIntegrationTestUtils
        .createFieldStatusHistory(field, FieldStatus.STATUS_200, yesterday, entityManager);
    var newestFieldStatusHistory = SubmissionIntegrationTestUtils
        .createFieldStatusHistory(field, FieldStatus.STATUS_300, today, entityManager);

    var results = operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    assertThat(results)
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            SubmissionWorkAreaQueryResult::getSubmissionType,
            SubmissionWorkAreaQueryResult::getStatus,
            SubmissionWorkAreaQueryResult::getResourceName,
            SubmissionWorkAreaQueryResult::getCentreOfInterestQuadBlock,
            SubmissionWorkAreaQueryResult::getOrganisationResponsibleId,
            SubmissionWorkAreaQueryResult::isCorrection,
            SubmissionWorkAreaQueryResult::getParentFieldId,
            SubmissionWorkAreaQueryResult::getFieldStatus
        ).containsExactly(
            Tuple.tuple(
                submissionDetail.getId(),
                submissionDetail.getSubmissionType(),
                submissionDetail.getStatus(),
                field.getCorrectedLegalFieldName(),
                trapProperty.getCentreOfInterestQuadrantBlock(),
                responsibleOrganisationIdForSubmission,
                submissionDetail.isCorrection(),
                field.getParentDeterminedField().getFieldIdentifier(),
                newestFieldStatusHistory.getFieldStatus()
            ));

    assertThat(results.get(0)
        .getLicencesTheResourceIsOn())
        .containsExactlyInAnyOrder(
            licenceId1, licenceId2);
  }

  @NotNull
  private static Resource createResource(Instant createdTimestamp, EntityManager entityManager) {
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    resource.setCreatedTimestamp(createdTimestamp);
    entityManager.persist(resource);
    entityManager.flush();
    return resource;
  }

  @NotNull
  private static Resource createResource(ResourceType resourceType, Instant createdTimestamp, EntityManager entityManager) {
    var resource = new Resource();
    resource.setType(resourceType);
    resource.setCreatedTimestamp(createdTimestamp);
    entityManager.persist(resource);
    entityManager.flush();
    return resource;
  }

  @Test
  void getSubmissions_whenTwoSubmissions_whenInResponsibleOrganisationOnLatestApprovedOnResource_assertReturn() {
    var responsibleOrganisationIdForApprovedSubmission = 46;
    var userOrgUnits = List.of(responsibleOrganisationIdForApprovedSubmission);
    setUpIndustryTest(userOrgUnits, USER);
    var submissionWithWrongResponsibleOrg = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionWithWrongResponsibleOrg.setId(null);
    submissionWithWrongResponsibleOrg.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionWithWrongResponsibleOrg.setSubmitterType(SubmitterType.INDUSTRY);

    var createdTimestamp = Instant.now();
    var resource = new Resource();
    submissionWithWrongResponsibleOrg.setCreatedTimestamp(createdTimestamp);
    resource.setType(ResourceType.PROSPECT);
    resource.setCreatedTimestamp(createdTimestamp);
    entityManager.persist(resource);
    entityManager.flush();
    submissionWithWrongResponsibleOrg.setSubmission(SubmissionIntegrationTestUtils.createSubmission(resource, entityManager));

    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    entityManager.persist(responsibleTeam);
    entityManager.flush();
    submissionWithWrongResponsibleOrg.getSubmission().setResource(resource);
    entityManager.persist(submissionWithWrongResponsibleOrg);
    entityManager.flush();

    var generalDetails = new GeneralDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionWithWrongResponsibleOrg);
    resourceDetail.setResourceName("The resource name");
    generalDetails.setSubmissionDetail(submissionWithWrongResponsibleOrg);
    var organisationIdUserIsNotResponsibleFor = 1;
    resourceDetail.setOrganisationId(organisationIdUserIsNotResponsibleFor);
    setUpLatestApprovedSubmissionOnResource(resource, responsibleOrganisationIdForApprovedSubmission);
    entityManager.persist(generalDetails);
    entityManager.flush();
    entityManager.persist(resourceDetail);
    entityManager.flush();

    var results = operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    assertThat(results)
        .extracting(SubmissionWorkAreaQueryResult::getSubmissionDetailId)
        .containsExactly(submissionWithWrongResponsibleOrg.getId());
  }

  @Test
  void getSubmissions_whenTwoSubmissions_whenOneHasUnresolvedUpdateAndOneResolved_assertUpdateRequested() {
    var responsibleOrganisationId = 46;
    var userOrgUnits = List.of(responsibleOrganisationId);
    setUpIndustryTest(userOrgUnits, USER);
    var submissionDetailWithResolvedUpdate = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailWithResolvedUpdate.setId(null);
    submissionDetailWithResolvedUpdate.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetailWithResolvedUpdate.setSubmitterType(SubmitterType.INDUSTRY);

    var createdTimestamp = Instant.now();
    var resource = new Resource();
    submissionDetailWithResolvedUpdate.setCreatedTimestamp(createdTimestamp);
    resource.setType(ResourceType.PROSPECT);
    resource.setCreatedTimestamp(createdTimestamp);
    entityManager.persist(resource);
    entityManager.flush();
    var submission = SubmissionIntegrationTestUtils.createSubmission(resource, entityManager);
    submissionDetailWithResolvedUpdate.setSubmission(submission);
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    entityManager.persist(responsibleTeam);
    entityManager.flush();
    submissionDetailWithResolvedUpdate.getSubmission().setResource(resource);
    entityManager.persist(submissionDetailWithResolvedUpdate);
    entityManager.flush();
    var submissionUpdateResolved = SubmissionUpdateIntegrationTestUtil.createSubmissionUpdate(
        submission, true, entityManager);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(responsibleOrganisationId);
    resourceDetail.setSubmissionDetail(submissionDetailWithResolvedUpdate);
    entityManager.persist(resourceDetail);
    entityManager.flush();

    var submissionDetailWithUnresolvedUpdate =
        SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailWithUnresolvedUpdate.setId(null);
    submissionDetailWithUnresolvedUpdate.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetailWithUnresolvedUpdate.setSubmitterType(SubmitterType.INDUSTRY);
    submissionDetailWithUnresolvedUpdate.setCreatedTimestamp(Instant.now());
    var resource2 = createResource(Instant.now(), entityManager);
    entityManager.persist(resource2);
    entityManager.flush();
    var submissionWithUnresolvedUpdate =
        SubmissionIntegrationTestUtils.createSubmission(resource2, entityManager);
    submissionDetailWithUnresolvedUpdate.setSubmission(submissionWithUnresolvedUpdate);
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResource(resource2);
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    entityManager.persist(regulatorManagedInformation);
    entityManager.flush();
    submissionDetailWithUnresolvedUpdate.getSubmission().setResource(resource2);
    entityManager.persist(submissionDetailWithUnresolvedUpdate);
    entityManager.flush();
    var submissionUpdateUnresolved = SubmissionUpdateIntegrationTestUtil.createSubmissionUpdate(
        submissionWithUnresolvedUpdate, false, entityManager);
    var resourceDetail2 = new ResourceDetail();
    resourceDetail2.setOrganisationId(responsibleOrganisationId);
    resourceDetail2.setSubmissionDetail(submissionDetailWithUnresolvedUpdate);
    entityManager.persist(resourceDetail2);
    entityManager.flush();

    var results = operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    assertThat(results)
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            SubmissionWorkAreaQueryResult::isUpdateRequested)
        .containsExactly(
            tuple(
                submissionDetailWithUnresolvedUpdate.getId(),
                true
            ),
            tuple(
                submissionDetailWithResolvedUpdate.getId(),
                false
            )
        );
  }

  @Test
  void getSubmissions_whenOneSubmittedSubmission_asIndustryWhenNotInResponsibleOrganisation_assertAllValues() {
    var responsibleOrganisationIdForSubmission = 46;
    var userOrgUnits = List.of(1,2,3,4);
    setUpIndustryTest(userOrgUnits, USER);
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    var createdTimestamp = Instant.now();
    var resource = createResource(createdTimestamp, entityManager);
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    submissionDetail.setCreatedTimestamp(createdTimestamp);
    entityManager.persist(responsibleTeam);
    entityManager.flush();
    submissionDetail.getSubmission().setResource(resource);
    submissionDetail.setSubmission(null);
    entityManager.persist(submissionDetail);
    entityManager.flush();

    var generalDetails = new GeneralDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setResourceName("The resource name");
    generalDetails.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(responsibleOrganisationIdForSubmission);
    entityManager.persist(generalDetails);
    entityManager.flush();
    entityManager.persist(resourceDetail);
    entityManager.flush();

    var results = operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    assertThat(results).isEmpty();
  }

  @Test
  void getSubmissions_whenOneHasUpdateInProgressAndOneHasSubmittedUpdate_assertCorrectSubmissionsReturned() {
    var responsibleOrganisationId = 46;
    var userOrgUnits = List.of(responsibleOrganisationId);
    setUpIndustryTest(userOrgUnits, USER);

    var resource = createResource(Instant.now(), entityManager);
    var submissionWithTwoSubmitted = SubmissionIntegrationTestUtils.createSubmission(resource, entityManager);
    var initialSubmissionDetail = setUpSubmissionDetailWithResponsibleOrgForSubmission(
        submissionWithTwoSubmitted, Instant.parse("2006-12-03T10:15:15.00Z"),
        responsibleOrganisationId, 1, SubmissionDetailStatus.SUBMITTED);
    var updateSubmissionDetailSubmitted = setUpSubmissionDetailWithResponsibleOrgForSubmission(
        submissionWithTwoSubmitted, Instant.parse("2007-12-03T10:15:15.00Z"),
        responsibleOrganisationId, 2, SubmissionDetailStatus.SUBMITTED);

    var resource2 = createResource(Instant.now(), entityManager);
    var submissionWithDraftUpdate = SubmissionIntegrationTestUtils.createSubmission(resource2, entityManager);
    var initialSubmissionDetail2 = setUpSubmissionDetailWithResponsibleOrgForSubmission(
        submissionWithDraftUpdate, Instant.parse("2006-12-03T10:15:30.00Z"),
        responsibleOrganisationId, 1, SubmissionDetailStatus.SUBMITTED);
    var updateSubmissionDetailDraft = setUpSubmissionDetailWithResponsibleOrgForSubmission(
        submissionWithDraftUpdate, Instant.parse("2007-12-03T10:15:30.00Z"),
        responsibleOrganisationId, 2, SubmissionDetailStatus.DRAFT);

    var results = operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    assertThat(results)
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId)
        .containsExactly(
            updateSubmissionDetailSubmitted.getId(),
            initialSubmissionDetail2.getId()
        );
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getSubmissions_whenWithdrawn_assertReturnWhenNotWithdrawn(Boolean withdrawn) {
    if(!withdrawn) {
      withdrawn = null;
    }
    var responsibleOrganisationId = 46;
    var userOrgUnits = List.of(responsibleOrganisationId);
    setUpIndustryTest(userOrgUnits, USER);

    var resource = createResource(Instant.now(), entityManager);
    var submissionWithTwoSubmitted =
        SubmissionIntegrationTestUtils.createSubmission(resource, withdrawn, entityManager);
    var initialSubmissionDetail = setUpSubmissionDetailWithResponsibleOrgForSubmission(
        submissionWithTwoSubmitted, Instant.parse("2006-12-03T10:15:15.00Z"),
        responsibleOrganisationId, 1, SubmissionDetailStatus.SUBMITTED);
    var updateSubmissionDetailSubmitted = setUpSubmissionDetailWithResponsibleOrgForSubmission(
        submissionWithTwoSubmitted, Instant.parse("2007-12-03T10:15:15.00Z"),
        responsibleOrganisationId, 2, SubmissionDetailStatus.SUBMITTED);

    var results = operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);
    if (BooleanUtils.isTrue(withdrawn)) {
      assertThat(results).isEmpty();
    } else {
      assertThat(results)
          .extracting(
              SubmissionWorkAreaQueryResult::getSubmissionDetailId)
          .containsExactly(
              updateSubmissionDetailSubmitted.getId()
          );
    }
  }

  private SubmissionDetail setUpSubmissionDetailWithResponsibleOrgForSubmission(Submission submission,
                                                                                Instant submittedTimestamp,
                                                                                int responsibleOrgId,
                                                                                int versionNumber,
                                                                                SubmissionDetailStatus status) {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setStatus(status);
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    submissionDetail.setSubmittedTimestamp(submittedTimestamp);
    submissionDetail.setVersionNumber(versionNumber);
    submissionDetail.setSubmission(submission);
    var createdTimestamp = Instant.now();
    var resource = new Resource();
    submissionDetail.setCreatedTimestamp(createdTimestamp);
    resource.setType(ResourceType.PROSPECT);
    resource.setCreatedTimestamp(createdTimestamp);
    entityManager.persist(resource);
    entityManager.flush();
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    entityManager.persist(responsibleTeam);
    entityManager.flush();
    entityManager.persist(submissionDetail);
    entityManager.flush();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(responsibleOrgId);
    resourceDetail.setSubmissionDetail(submissionDetail);
    entityManager.persist(resourceDetail);
    entityManager.flush();
    return submissionDetail;
  }

  private void setUpLatestApprovedSubmissionOnResource(Resource resource,
                                                       Integer responsibleOrganisationIdForSubmission) {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    submissionDetail.setSubmission(SubmissionIntegrationTestUtils.createSubmission(resource, entityManager));

    var createdTimestamp = Instant.now().minusMillis(2000);
    submissionDetail.setCreatedTimestamp(createdTimestamp);
    submissionDetail.setAcceptedTimestamp(createdTimestamp.plusMillis(1000));
    submissionDetail.getSubmission().setResource(resource);
    entityManager.persist(submissionDetail);
    entityManager.flush();
    var generalDetails = new GeneralDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setResourceName("The approved name");
    generalDetails.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(responsibleOrganisationIdForSubmission);
    entityManager.persist(generalDetails);
    entityManager.flush();
    entityManager.persist(resourceDetail);
    entityManager.flush();
  }

  private void setUpIndustryTest(List<Integer> userResponsibleOrganisationUnitIds, ServiceUserDetail user) {
    // Ensure user is treated as currently logged in
    SamlAuthenticationUtil.Builder()
        .withUser(user)
        .setSecurityContext();

    // Stub regulator team
    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withId(null)
        .build();

    entityManager.persist(team);
    entityManager.flush();

    when(industryTeamService.getOrganisationUnitIdsForUser(user))
        .thenReturn(userResponsibleOrganisationUnitIds);
  }
}