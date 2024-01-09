package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTestUtils.createSubmissionForIntegrationTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import javax.persistence.EntityManager;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.SamlAuthenticationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRoleUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.northernoffshore.NorthernOffshoreAreaTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.onshore.OnshoreAreaTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.southernoffshore.SouthernOffshoreAreaTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;

@Transactional
@IntegrationTest
class ResponsibleTeamSubmissionWorkAreaQueryServiceIntegrationTest {

  private static final Integer VERSION_NUMBER_INITIAL = 1;
  private static final Integer VERSION_NUMBER_UPDATE = 2;
  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder()
      .withWuaId(1000L)
      .build();

  @Autowired
  private DSLContext dslContext;

  @Autowired
  private ResponsibleTeamSubmissionWorkAreaQueryService responsibleTeamSubmissionWorkAreaQueryService;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private TeamMemberRoleService teamMemberRoleService;

  private final long LAST_SUBMISSION_DETAIL_EPOCH_TIME = 10000000;

  @ParameterizedTest
  @EnumSource(value = RegulatorTeamRole.class, mode = EnumSource.Mode.EXCLUDE, names={"EXPLORATION"})
  void getSubmissions_whenExplorationResponsibleTeam_andNotExplorationTeamRole_assertNothingReturned(
      RegulatorTeamRole regulatorTeamRole) {
    Set<TeamRole> userRoles = Set.of(regulatorTeamRole);
    setUpUserTest(userRoles, TeamType.REGULATOR, USER);

    var resource = createResource(ResourceType.PROSPECT);
    createResponsibleTeam(resource, ResponsibleTeamType.EXPLORATION);
    createSubmissionDetail(resource);

    var submittedSubmissionDetail = createSubmissionDetail(SubmissionDetailStatus.SUBMITTED, resource);

    var submittedGeneralDetails = new GeneralDetail();
    var submittedResourceDetail = new ResourceDetail();
    submittedResourceDetail.setSubmissionDetail(submittedSubmissionDetail);
    submittedResourceDetail.setResourceName("Resource name");
    submittedGeneralDetails.setSubmissionDetail(submittedSubmissionDetail);
    entityManager.persist(submittedResourceDetail);
    entityManager.flush();
    entityManager.persist(submittedGeneralDetails);
    entityManager.flush();

    var results = responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    assertThat(results).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(names = {"DELETED", "ACCEPTED"})
  void getSubmissions_whenExplorationTeamRole_whenOneNotViewableAndOneDraft_assertOnlyDraftSubmissionReturned(
      SubmissionDetailStatus nonViewableStatus) {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpUserTest(userRoles, TeamType.REGULATOR, USER);

    var resource = createResource(ResourceType.PROSPECT);
    var createdTimestamp = Instant.now();
    createResponsibleTeam(resource, ResponsibleTeamType.EXPLORATION);
    var draftSubmissionDetail = createSubmissionDetail(resource, createdTimestamp);

    var nonViewableSubmissionDetail = createSubmissionDetail(nonViewableStatus, resource);

    var submittedGeneralDetails = new GeneralDetail();
    var submittedResourceDetail = new ResourceDetail();
    submittedResourceDetail.setSubmissionDetail(nonViewableSubmissionDetail);
    submittedResourceDetail.setResourceName("Resource name");
    submittedGeneralDetails.setSubmissionDetail(nonViewableSubmissionDetail);
    entityManager.persist(submittedResourceDetail);
    entityManager.flush();
    entityManager.persist(submittedGeneralDetails);
    entityManager.flush();

    var results = responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    assertThat(results)
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            SubmissionWorkAreaQueryResult::getCentreOfInterestQuadBlock,
            SubmissionWorkAreaQueryResult::getOrganisationResponsibleId,
            SubmissionWorkAreaQueryResult::getResourceName,
            SubmissionWorkAreaQueryResult::getSubmissionType,
            SubmissionWorkAreaQueryResult::getStatus,
            SubmissionWorkAreaQueryResult::getLicencesTheResourceIsOn)
        .containsExactly(
            tuple(
                draftSubmissionDetail.getId(),
                null,
                null,
                null,
                SubmissionType.CREATE_PROSPECT,
                SubmissionDetailStatus.DRAFT,
                null
            )
        );
  }

  @ParameterizedTest
  @EnumSource(value = ResponsibleTeamType.class, mode = EnumSource.Mode.EXCLUDE, names={"EXPLORATION"})
  void getSubmissions_whenResourceResponsibleTeamNotExploration_assertNothingReturned(ResponsibleTeamType responsibleTeamType) {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpUserTest(userRoles, TeamType.REGULATOR, USER);

    var resource = createResource(ResourceType.PROSPECT);
    createSubmissionDetail(resource);

    createResponsibleTeam(resource, responsibleTeamType);

    var submittedSubmissionDetail = createSubmissionDetail(SubmissionDetailStatus.SUBMITTED, resource);

    var submittedGeneralDetails = new GeneralDetail();
    var submittedResourceDetail = new ResourceDetail();

    submittedResourceDetail.setResourceName("Resource name");
    submittedResourceDetail.setSubmissionDetail(submittedSubmissionDetail);
    submittedGeneralDetails.setSubmissionDetail(submittedSubmissionDetail);

    entityManager.persist(submittedGeneralDetails);
    entityManager.flush();
    entityManager.persist(submittedResourceDetail);
    entityManager.flush();

    var results = responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    assertThat(results).isEmpty();
  }

  @Test
  void getSubmissions_whenResourceNorthernOffshoreResponsibleTeam_assertReturned() {
    Set<TeamRole> userRoles = Set.of(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER);
    setUpUserTest(userRoles, TeamType.NORTHERN_OFFSHORE_AREA, USER);

    var resource = createResource(ResourceType.PROSPECT);
    createSubmissionDetail(resource);

    createResponsibleTeam(resource, ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);

    var submittedSubmissionDetail = createSubmissionDetail(SubmissionDetailStatus.SUBMITTED, resource);

    var submittedGeneralDetails = new GeneralDetail();
    var submittedResourceDetail = new ResourceDetail();

    submittedResourceDetail.setResourceName("Resource name");
    submittedResourceDetail.setSubmissionDetail(submittedSubmissionDetail);
    submittedGeneralDetails.setSubmissionDetail(submittedSubmissionDetail);

    entityManager.persist(submittedGeneralDetails);
    entityManager.flush();
    entityManager.persist(submittedResourceDetail);
    entityManager.flush();

    var results = responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    assertThat(results).isNotEmpty();
  }

  @Test
  void getSubmissions_whenResourceSouthernOffshoreResponsibleTeam_assertReturned() {
    Set<TeamRole> userRoles = Set.of(SouthernOffshoreAreaTeamRole.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER);
    setUpUserTest(userRoles, TeamType.SOUTHERN_OFFSHORE_AREA, USER);

    var resource = createResource(ResourceType.PROSPECT);
    createSubmissionDetail(resource);

    createResponsibleTeam(resource, ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);

    var submittedSubmissionDetail = createSubmissionDetail(SubmissionDetailStatus.SUBMITTED, resource);

    var submittedGeneralDetails = new GeneralDetail();
    var submittedResourceDetail = new ResourceDetail();

    submittedResourceDetail.setResourceName("Resource name");
    submittedResourceDetail.setSubmissionDetail(submittedSubmissionDetail);
    submittedGeneralDetails.setSubmissionDetail(submittedSubmissionDetail);

    entityManager.persist(submittedGeneralDetails);
    entityManager.flush();
    entityManager.persist(submittedResourceDetail);
    entityManager.flush();

    var results = responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    assertThat(results).isNotEmpty();
  }

  @Test
  void getSubmissions_whenResourceOnshoreResponsibleTeam_assertReturned() {
    Set<TeamRole> userRoles = Set.of(OnshoreAreaTeamRole.ONSHORE_AREA_TEAM_MEMBER);
    setUpUserTest(userRoles, TeamType.ONSHORE_AREA, USER);

    var resource = createResource(ResourceType.PROSPECT);

    createResponsibleTeam(resource, ResponsibleTeamType.ONSHORE);

    var submittedSubmissionDetail = createSubmissionDetail(SubmissionDetailStatus.SUBMITTED, resource);

    var submittedResourceDetail = new ResourceDetail();

    submittedResourceDetail.setResourceName("Resource name");
    submittedResourceDetail.setSubmissionDetail(submittedSubmissionDetail);

    entityManager.persist(submittedResourceDetail);
    entityManager.flush();

    var results = responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    assertThat(results).isNotEmpty();
  }

  @Test
  void getSubmissions_multipleResponsibleTeams_assertValues() {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpUserTest(userRoles, TeamType.REGULATOR, USER);
    userRoles = Set.of(OnshoreAreaTeamRole.ONSHORE_AREA_TEAM_MEMBER);
    setUpUserTest(userRoles, TeamType.ONSHORE_AREA, USER);

    var explorationSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    explorationSubmissionDetail.setId(null);
    var explorationSubmissionDetailTimestamp = Instant.ofEpochMilli(LAST_SUBMISSION_DETAIL_EPOCH_TIME*3);
    explorationSubmissionDetail.setCreatedTimestamp(explorationSubmissionDetailTimestamp);
    var resource = createResource(ResourceType.PROSPECT);
    createResponsibleTeam(resource, ResponsibleTeamType.EXPLORATION);
    var submission = createSubmissionForIntegrationTest(resource, entityManager);
    explorationSubmissionDetail.setSubmission(submission);
    entityManager.persist(explorationSubmissionDetail);
    entityManager.flush();

    var onshoreSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    onshoreSubmissionDetail.setId(null);
    var onshoreSubmissionDetailTimestamp = Instant.ofEpochMilli(LAST_SUBMISSION_DETAIL_EPOCH_TIME*2);
    onshoreSubmissionDetail.setCreatedTimestamp(onshoreSubmissionDetailTimestamp);
    var resource2 = createResource(ResourceType.PROSPECT);
    createResponsibleTeam(resource2, ResponsibleTeamType.ONSHORE);
    var submission2 = createSubmissionForIntegrationTest(resource2, entityManager);
    onshoreSubmissionDetail.setSubmission(submission2);
    entityManager.persist(onshoreSubmissionDetail);
    entityManager.flush();

    var offshoreSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    offshoreSubmissionDetail.setId(null);
    var offshoreSubmissionDetailTimestamp = Instant.ofEpochMilli(LAST_SUBMISSION_DETAIL_EPOCH_TIME*3);
    offshoreSubmissionDetail.setCreatedTimestamp(offshoreSubmissionDetailTimestamp);
    var resource3 = createResource(ResourceType.PROSPECT);
    createResponsibleTeam(resource3, ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);
    var submission3 = createSubmissionForIntegrationTest(resource3, entityManager);
    offshoreSubmissionDetail.setSubmission(submission3);
    entityManager.persist(offshoreSubmissionDetail);
    entityManager.flush();

    var results = responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    // asserts correct ordering of the list, the items are ordered by submittedOn if its present otherwise createdOn
    assertThat(results)
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId)
        .containsExactly(
            explorationSubmissionDetail.getId(),
            onshoreSubmissionDetail.getId()
        );
  }

  @Test
  void getSubmissions_assertCorrectOrderingAndValues() {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpUserTest(userRoles, TeamType.REGULATOR, USER);

    var secondSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    secondSubmissionDetail.setId(null);
    var secondSubmissionDetailTimestamp = Instant.ofEpochMilli(LAST_SUBMISSION_DETAIL_EPOCH_TIME*2);
    secondSubmissionDetail.setCreatedTimestamp(secondSubmissionDetailTimestamp);
    var resource = createResource(ResourceType.PROSPECT);
    createResponsibleTeam(resource, ResponsibleTeamType.EXPLORATION);
    var submission = createSubmissionForIntegrationTest(resource, entityManager);
    secondSubmissionDetail.setSubmission(submission);
    entityManager.persist(secondSubmissionDetail);
    entityManager.flush();

    var firstSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    firstSubmissionDetail.setId(null);
    var firstSubmissionDetailTimestamp = Instant.ofEpochMilli(LAST_SUBMISSION_DETAIL_EPOCH_TIME*3);
    firstSubmissionDetail.setCreatedTimestamp(firstSubmissionDetailTimestamp);
    var resource2 = createResource(ResourceType.PROSPECT);
    createResponsibleTeam(resource2, ResponsibleTeamType.EXPLORATION);
    var submission2 = createSubmissionForIntegrationTest(resource2, entityManager);
    firstSubmissionDetail.setSubmission(submission2);
    entityManager.persist(firstSubmissionDetail);
    entityManager.flush();

    var thirdSubmissionDetail = SubmissionDetailTestUtils.createDiscoveryFromProspectSubmissionDetail();
    var secondResource = createResource(ResourceType.DISCOVERY);
    var secondResponsibleTeam = new RegulatorManagedInformation();
    secondResponsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    secondResponsibleTeam.setResource(secondResource);
    thirdSubmissionDetail.setId(null);
    thirdSubmissionDetail.setSubmission(createSubmissionForIntegrationTest(secondResource, entityManager));
    thirdSubmissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    var thirdSubmissionDetailTimeStamp = Instant.ofEpochMilli(LAST_SUBMISSION_DETAIL_EPOCH_TIME);
    thirdSubmissionDetail.setCreatedTimestamp(thirdSubmissionDetailTimeStamp);
    entityManager.persist(secondResponsibleTeam);
    entityManager.flush();
    entityManager.persist(thirdSubmissionDetail);
    entityManager.flush();

    var firstSubmittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    firstSubmittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    firstSubmittedSubmissionDetail.setId(null);
    firstSubmittedSubmissionDetail.setSubmittedByWuaId(5);
    firstSubmittedSubmissionDetail.setSubmittedTimestamp(firstSubmissionDetailTimestamp);
    firstSubmittedSubmissionDetail.setCreatedTimestamp(thirdSubmissionDetailTimeStamp);
    var thirdResource = createResource(ResourceType.PROSPECT);
    firstSubmittedSubmissionDetail.setSubmission(createSubmissionForIntegrationTest(thirdResource, entityManager));
    createResponsibleTeam(thirdResource, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(firstSubmittedSubmissionDetail);
    entityManager.flush();

    var secondSubmittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    secondSubmittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    secondSubmittedSubmissionDetail.setId(null);
    secondSubmittedSubmissionDetail.setSubmittedByWuaId(5);
    secondSubmittedSubmissionDetail.setCreatedTimestamp(thirdSubmissionDetailTimeStamp);
    secondSubmittedSubmissionDetail.setSubmittedTimestamp(secondSubmissionDetailTimestamp);
    var fourthResource = createResource(ResourceType.PROSPECT);
    secondSubmittedSubmissionDetail.setSubmission(createSubmissionForIntegrationTest(fourthResource, entityManager));
    createResponsibleTeam(fourthResource, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(secondSubmittedSubmissionDetail);
    entityManager.flush();

    var thirdSubmittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    thirdSubmittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    thirdSubmittedSubmissionDetail.setId(null);
    thirdSubmittedSubmissionDetail.setSubmittedByWuaId(5);
    thirdSubmittedSubmissionDetail.setCreatedTimestamp(firstSubmissionDetailTimestamp);
    thirdSubmittedSubmissionDetail.setSubmittedTimestamp(thirdSubmissionDetailTimeStamp);
    var fifthResource = createResource(ResourceType.PROSPECT);
    thirdSubmittedSubmissionDetail.setSubmission(createSubmissionForIntegrationTest(fifthResource, entityManager));
    createResponsibleTeam(fifthResource, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(thirdSubmittedSubmissionDetail);
    entityManager.flush();

    var results = responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    // asserts correct ordering of the list, the items are ordered by submittedOn if its present otherwise createdOn
    assertThat(results)
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId)
        .containsExactly(
                firstSubmittedSubmissionDetail.getId(),
                secondSubmittedSubmissionDetail.getId(),
                thirdSubmittedSubmissionDetail.getId(),
                firstSubmissionDetail.getId(),
                secondSubmissionDetail.getId(),
                thirdSubmissionDetail.getId()
        );
  }

  @Test
  void getSubmissions_whenOneFullSubmission_assertAllValues() {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpUserTest(userRoles, TeamType.REGULATOR, USER);

    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    var resource = createResource(ResourceType.PROSPECT);
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    var createdTimestamp = Instant.now();
    submissionDetail.setCreatedTimestamp(createdTimestamp);

    entityManager.persist(responsibleTeam);
    entityManager.flush();
    submissionDetail.getSubmission().setResource(resource);
    var submission = createSubmissionForIntegrationTest(resource, entityManager);
    submissionDetail.setSubmission(submission);
    entityManager.persist(submissionDetail);
    entityManager.flush();

    var generalDetails = new GeneralDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setResourceName("The resource name");
    generalDetails.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(46);
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

    var results = responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    assertThat(results.get(0))
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            SubmissionWorkAreaQueryResult::getCentreOfInterestQuadBlock,
            SubmissionWorkAreaQueryResult::getOrganisationResponsibleId,
            SubmissionWorkAreaQueryResult::getResourceName,
            SubmissionWorkAreaQueryResult::getSubmissionType,
            SubmissionWorkAreaQueryResult::getStatus,
            SubmissionWorkAreaQueryResult::isUpdateRequested,
            SubmissionWorkAreaQueryResult::isCorrection)
        .containsExactly(
            submissionDetail.getId(),
            "25/12",
            46,
            "The resource name",
            SubmissionType.CREATE_PROSPECT,
            SubmissionDetailStatus.DRAFT,
            false,
            false
        );

    assertThat(results.get(0)
        .getLicencesTheResourceIsOn())
        .containsExactlyInAnyOrder(
            licenceId1, licenceId2);
  }

  @Test
  void getSubmissions_whenCorrection_assertAllValues() {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpUserTest(userRoles, TeamType.REGULATOR, USER);

    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    submissionDetail.setCorrection(true);
    submissionDetail.setVersionNumber(2);
    var resource = createResource(ResourceType.PROSPECT);
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    var createdTimestamp = Instant.now();
    submissionDetail.setCreatedTimestamp(createdTimestamp);

    entityManager.persist(responsibleTeam);
    entityManager.flush();
    submissionDetail.getSubmission().setResource(resource);
    var submission = createSubmissionForIntegrationTest(resource, entityManager);
    submissionDetail.setSubmission(submission);
    entityManager.persist(submissionDetail);
    entityManager.flush();

    var generalDetails = new GeneralDetail();
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setResourceName("The resource name");
    generalDetails.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(46);
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

    var results = responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    assertThat(results.get(0))
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            SubmissionWorkAreaQueryResult::getCentreOfInterestQuadBlock,
            SubmissionWorkAreaQueryResult::getOrganisationResponsibleId,
            SubmissionWorkAreaQueryResult::getResourceName,
            SubmissionWorkAreaQueryResult::getSubmissionType,
            SubmissionWorkAreaQueryResult::getStatus,
            SubmissionWorkAreaQueryResult::isUpdateRequested,
            SubmissionWorkAreaQueryResult::isCorrection)
        .containsExactly(
            submissionDetail.getId(),
            "25/12",
            46,
            "The resource name",
            SubmissionType.CREATE_PROSPECT,
            SubmissionDetailStatus.DRAFT,
            false,
            true
        );

    assertThat(results.get(0)
        .getLicencesTheResourceIsOn())
        .containsExactlyInAnyOrder(
            licenceId1, licenceId2);
  }

  @Test
  void getSubmissions_whenOneFullFieldSubmission_assertAllValues() {
    Set<TeamRole> userRoles = Set.of(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER);
    setUpUserTest(userRoles, TeamType.NORTHERN_OFFSHORE_AREA, USER);

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
    submissionDetail.setId(null);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);

    var resource = createResource(ResourceType.FIELD);

    var responsibleOrganisationId = 2425;
    var fieldName = "Field name";
    var field = SubmissionIntegrationTestUtils
        .createFieldWithParent(resource, fieldName, responsibleOrganisationId, entityManager);

    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);
    var createdTimestamp = Instant.now();
    submissionDetail.setCreatedTimestamp(createdTimestamp);

    entityManager.persist(responsibleTeam);
    entityManager.flush();
    submissionDetail.getSubmission().setResource(resource);
    var submission = createSubmissionForIntegrationTest(resource, entityManager);
    submissionDetail.setSubmission(submission);
    submissionDetail.setCorrection(false);
    entityManager.persist(submissionDetail);
    entityManager.flush();

    var generalDetails = new GeneralDetail();
    generalDetails.setSubmissionDetail(submissionDetail);
    entityManager.persist(generalDetails);
    entityManager.flush();
    entityManager.persist(field);
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

    var today = Instant.now();
    var yesterday = today.minus(1, ChronoUnit.DAYS);
    var oldFieldStatusHistory = SubmissionIntegrationTestUtils
        .createFieldStatusHistory(field, FieldStatus.STATUS_200, yesterday, entityManager);
    var newestFieldStatusHistory = SubmissionIntegrationTestUtils
        .createFieldStatusHistory(field, FieldStatus.STATUS_300, today, entityManager);

    var results = responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    assertThat(results.get(0))
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            SubmissionWorkAreaQueryResult::getCentreOfInterestQuadBlock,
            SubmissionWorkAreaQueryResult::getOrganisationResponsibleId,
            SubmissionWorkAreaQueryResult::getResourceName,
            SubmissionWorkAreaQueryResult::getSubmissionType,
            SubmissionWorkAreaQueryResult::getStatus,
            SubmissionWorkAreaQueryResult::isCorrection,
            SubmissionWorkAreaQueryResult::getParentFieldId,
            SubmissionWorkAreaQueryResult::getFieldStatus)
        .containsExactly(
            submissionDetail.getId(),
            trapProperty.getCentreOfInterestQuadrantBlock(),
            responsibleOrganisationId,
            fieldName,
            SubmissionType.CREATE_CSR,
            SubmissionDetailStatus.DRAFT,
            submissionDetail.isCorrection(),
            field.getParentDeterminedField().getFieldIdentifier(),
            newestFieldStatusHistory.getFieldStatus()
        );

    assertThat(results.get(0)
        .getLicencesTheResourceIsOn())
        .containsExactlyInAnyOrder(
            licenceId1, licenceId2);
  }

  @Test
  void getSubmissions_whenOneUpdateUnresolvedAndOneResolved_assertUpdateRequestedAndOrder() {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpUserTest(userRoles, TeamType.REGULATOR, USER);

    var updateRequestedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    updateRequestedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    updateRequestedSubmissionDetail.setId(null);
    updateRequestedSubmissionDetail.setSubmittedByWuaId(5);
    updateRequestedSubmissionDetail.setCreatedTimestamp(Instant.now());
    var resource = createResource(ResourceType.PROSPECT);
    updateRequestedSubmissionDetail.getSubmission().setResource(resource);
    var submissionUpdateRequested = createSubmissionForIntegrationTest(resource, entityManager);
    updateRequestedSubmissionDetail.setSubmission(submissionUpdateRequested);
    createResponsibleTeam(resource, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(updateRequestedSubmissionDetail);
    entityManager.flush();
    SubmissionUpdateIntegrationTestUtil.createSubmissionUpdate(submissionUpdateRequested, false, entityManager);

    var updateResolvedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    updateResolvedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    updateResolvedSubmissionDetail.setId(null);
    updateResolvedSubmissionDetail.setSubmittedByWuaId(5);
    updateResolvedSubmissionDetail.setCreatedTimestamp(Instant.now());
    var resource2 = createResource(ResourceType.PROSPECT);
    updateResolvedSubmissionDetail.getSubmission().setResource(resource2);
    var submissionResolved = createSubmissionForIntegrationTest(resource2, entityManager);
    updateResolvedSubmissionDetail.setSubmission(submissionResolved);
    createResponsibleTeam(resource2, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(updateResolvedSubmissionDetail);
    entityManager.flush();
    SubmissionUpdateIntegrationTestUtil.createSubmissionUpdate(submissionResolved, true, entityManager);

    var results = responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    // asserts correct ordering of the list, the items are ordered by submittedOn if its present otherwise createdOn
    assertThat(results)
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            SubmissionWorkAreaQueryResult::isUpdateRequested)
        .containsExactly(
            tuple(
                updateRequestedSubmissionDetail.getId(),
                true),
            tuple(
                updateResolvedSubmissionDetail.getId(),
                false)
        );
  }

  @Test
  void getSubmissions_whenOneHasUpdateInProgressAndOneHasSubmittedUpdate_assertCorrectSubmissionsReturned() {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpUserTest(userRoles, TeamType.REGULATOR, USER);

    var initialSubmittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    initialSubmittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    initialSubmittedSubmissionDetail.setId(null);
    initialSubmittedSubmissionDetail.setSubmittedByWuaId(5);
    initialSubmittedSubmissionDetail.setCreatedTimestamp(Instant.now());
    initialSubmittedSubmissionDetail.setVersionNumber(VERSION_NUMBER_INITIAL);
    var resource = createResource(ResourceType.PROSPECT);
    var submissionWithDraftUpdate = createSubmissionForIntegrationTest(resource, entityManager);
    initialSubmittedSubmissionDetail.setSubmission(submissionWithDraftUpdate);
    createResponsibleTeam(resource, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(initialSubmittedSubmissionDetail);
    entityManager.flush();

    var draftSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    draftSubmissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    draftSubmissionDetail.setId(null);
    draftSubmissionDetail.setSubmittedByWuaId(5);
    draftSubmissionDetail.setVersionNumber(VERSION_NUMBER_UPDATE);
    draftSubmissionDetail.setCreatedTimestamp(Instant.now());
    draftSubmissionDetail.setSubmission(submissionWithDraftUpdate);
    entityManager.persist(draftSubmissionDetail);
    entityManager.flush();
    SubmissionUpdateIntegrationTestUtil.createSubmissionUpdate(submissionWithDraftUpdate, false, entityManager);


    var initialSubmittedSubmissionDetail2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    initialSubmittedSubmissionDetail2.setStatus(SubmissionDetailStatus.SUBMITTED);
    initialSubmittedSubmissionDetail2.setId(null);
    initialSubmittedSubmissionDetail2.setSubmittedByWuaId(5);
    initialSubmittedSubmissionDetail2.setCreatedTimestamp(Instant.now());
    initialSubmittedSubmissionDetail2.setSubmittedTimestamp(Instant.now());
    initialSubmittedSubmissionDetail2.setVersionNumber(VERSION_NUMBER_INITIAL);
    var resource2 = createResource(ResourceType.PROSPECT);
    var submissionWithTwoSubmittedDetails = createSubmissionForIntegrationTest(resource2, entityManager);
    initialSubmittedSubmissionDetail2.setSubmission(submissionWithTwoSubmittedDetails);
    createResponsibleTeam(resource2, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(initialSubmittedSubmissionDetail2);
    entityManager.flush();

    var submittedUpdateSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submittedUpdateSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submittedUpdateSubmissionDetail.setId(null);
    submittedUpdateSubmissionDetail.setSubmittedByWuaId(5);
    submittedUpdateSubmissionDetail.setVersionNumber(VERSION_NUMBER_UPDATE);
    submittedUpdateSubmissionDetail.setCreatedTimestamp(Instant.now());
    submittedUpdateSubmissionDetail.setSubmittedTimestamp(Instant.now());
    submittedUpdateSubmissionDetail.setSubmission(submissionWithTwoSubmittedDetails);
    entityManager.persist(submittedUpdateSubmissionDetail);
    entityManager.flush();
    SubmissionUpdateIntegrationTestUtil.createSubmissionUpdate(submissionWithTwoSubmittedDetails, true, entityManager);

    var results = responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    assertThat(results)
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId)
        .containsExactly(
            initialSubmittedSubmissionDetail.getId(),
            submittedUpdateSubmissionDetail.getId()
        );
  }


  @Test
  void getSubmissions_whenOneHasDeletedDraftUpdateAndOneHasDraftUpdate_assertSubmittedSubmissionsReturned() {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpUserTest(userRoles, TeamType.REGULATOR, USER);

    var initialSubmittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    initialSubmittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    initialSubmittedSubmissionDetail.setId(null);
    initialSubmittedSubmissionDetail.setSubmittedByWuaId(5);
    initialSubmittedSubmissionDetail.setCreatedTimestamp(Instant.now());
    initialSubmittedSubmissionDetail.setVersionNumber(VERSION_NUMBER_INITIAL);
    var resource = createResource(ResourceType.PROSPECT);
    var submissionWithDraftUpdate = createSubmissionForIntegrationTest(resource, entityManager);
    initialSubmittedSubmissionDetail.setSubmission(submissionWithDraftUpdate);
    createResponsibleTeam(resource, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(initialSubmittedSubmissionDetail);
    entityManager.flush();

    var deletedDraftUpdate = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    deletedDraftUpdate.setStatus(SubmissionDetailStatus.DELETED);
    deletedDraftUpdate.setId(null);
    deletedDraftUpdate.setSubmittedByWuaId(5);
    deletedDraftUpdate.setVersionNumber(VERSION_NUMBER_UPDATE);
    deletedDraftUpdate.setCreatedTimestamp(Instant.now());
    deletedDraftUpdate.setSubmission(submissionWithDraftUpdate);
    entityManager.persist(deletedDraftUpdate);
    entityManager.flush();
    SubmissionUpdateIntegrationTestUtil.createSubmissionUpdate(submissionWithDraftUpdate, false, entityManager);

    var initialSubmittedSubmissionDetail2 = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    initialSubmittedSubmissionDetail2.setStatus(SubmissionDetailStatus.SUBMITTED);
    initialSubmittedSubmissionDetail2.setId(null);
    initialSubmittedSubmissionDetail2.setSubmittedByWuaId(5);
    initialSubmittedSubmissionDetail2.setCreatedTimestamp(Instant.now());
    initialSubmittedSubmissionDetail2.setSubmittedTimestamp(Instant.now());
    initialSubmittedSubmissionDetail2.setVersionNumber(VERSION_NUMBER_INITIAL);
    var resource2 = createResource(ResourceType.PROSPECT);
    var submissionWithTwoSubmittedDetails = createSubmissionForIntegrationTest(resource2, entityManager);
    initialSubmittedSubmissionDetail2.setSubmission(submissionWithTwoSubmittedDetails);
    createResponsibleTeam(resource2, ResponsibleTeamType.EXPLORATION);
    entityManager.persist(initialSubmittedSubmissionDetail2);
    entityManager.flush();

    var withdrawnSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    withdrawnSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    withdrawnSubmissionDetail.setId(null);
    withdrawnSubmissionDetail.setSubmittedByWuaId(5);
    withdrawnSubmissionDetail.setVersionNumber(VERSION_NUMBER_UPDATE);
    withdrawnSubmissionDetail.setCreatedTimestamp(Instant.now());
    withdrawnSubmissionDetail.setSubmittedTimestamp(Instant.now());
    withdrawnSubmissionDetail.setSubmission(submissionWithTwoSubmittedDetails);
    submissionWithTwoSubmittedDetails.setWithdrawn(true);
    entityManager.persist(withdrawnSubmissionDetail);
    entityManager.flush();
    SubmissionUpdateIntegrationTestUtil.createSubmissionUpdate(submissionWithTwoSubmittedDetails, true, entityManager);

    var results = responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER);

    assertThat(results)
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId)
        .containsExactly(
            initialSubmittedSubmissionDetail.getId()
        );
  }

  private RegulatorManagedInformation createResponsibleTeam(Resource resource, ResponsibleTeamType responsibleTeamType) {
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(responsibleTeamType);
    entityManager.persist(responsibleTeam);
    entityManager.flush();
    return responsibleTeam;
  }

  private Resource createResource(ResourceType resourceType) {
    var resource = new Resource();
    resource.setType(resourceType);
    resource.setCreatedTimestamp(Instant.now());
    entityManager.persist(resource);
    entityManager.flush();
    return resource;
  }

  private SubmissionDetail createSubmissionDetail(Resource resource, Instant createdTimestamp) {
    var submissionDetail = buildBasicSubmissionDetail(resource);
    submissionDetail.setCreatedTimestamp(createdTimestamp);
    entityManager.persist(submissionDetail);
    entityManager.flush();
    return submissionDetail;
  }

  private SubmissionDetail createSubmissionDetail(SubmissionDetailStatus submitted, Resource resource) {
    var submittedSubmissionDetail = buildBasicSubmissionDetail(resource);
    submittedSubmissionDetail.setStatus(submitted);
    entityManager.persist(submittedSubmissionDetail);
    entityManager.flush();
    return submittedSubmissionDetail;
  }

  private void createSubmissionDetail(Resource resource) {
    var submissionDetail = buildBasicSubmissionDetail(resource);
    entityManager.persist(submissionDetail);
    entityManager.flush();
  }

  private SubmissionDetail buildBasicSubmissionDetail(Resource resource) {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submission = new Submission();
    submission.setResource(resource);
    entityManager.persist(submission);
    entityManager.flush();
    submissionDetail.setId(null);
    submissionDetail.setSubmission(submission);
    submissionDetail.getSubmission().setResource(resource);
    submissionDetail.setCreatedTimestamp(Instant.now());
    return submissionDetail;
  }

  private void setUpUserTest(Set<TeamRole> userRoles, TeamType teamType, ServiceUserDetail user) {

    // Ensure user is treated as currently logged in
    SamlAuthenticationUtil.Builder()
        .withUser(user)
        .setSecurityContext();

    // Stub regulator team
    var team = TeamTestUtil.Builder()
        .withTeamType(teamType)
        .withId(null)
        .build();

    entityManager.persist(team);
    entityManager.flush();

    // Add user to team with the previously defined roles
    teamMemberRoleService.updateUserTeamRoles(team, new WebUserAccountId(user.wuaId()),
        TeamRoleUtil.getRoleNames(userRoles));
  }
}