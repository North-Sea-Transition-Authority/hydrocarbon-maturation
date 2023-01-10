package uk.co.nstauthority.hydrocarbonmaturation.workarea;


import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import org.jooq.DSLContext;
import org.jooq.conf.RenderMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.SamlAuthenticationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.JooqSchemaProviderService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.Licence;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRoleUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;

@Transactional
@IntegrationTest
class SubmissionWorkAreaQueryServiceIntegrationTest {

  @Autowired
  private DSLContext dslContext;

  @Autowired
  private UserDetailService userDetailService;

  @Autowired
  private TeamMemberService teamMemberService;

  @Autowired
  private SubmissionWorkAreaQueryService submissionWorkAreaQueryService;

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private TeamMemberRoleService teamMemberRoleService;

  @Autowired
  private JooqSchemaProviderService jooqSchemaProviderService;

  private final long LAST_SUBMISSION_DETAIL_EPOCH_TIME = 10000000;

  @BeforeEach
  void setUp() {
    dslContext.settings().setRenderMapping(new RenderMapping().withDefaultSchema("remi"));
  }

  @ParameterizedTest
  @EnumSource(value = RegulatorTeamRole.class, mode = EnumSource.Mode.EXCLUDE, names={"EXPLORATION"})
  void getSubmissions_whenNotExplorationTeamRole_assertNothingReturned(RegulatorTeamRole regulatorTeamRole) {
    Set<TeamRole> userRoles = Set.of(regulatorTeamRole);
    setUpTest(userRoles);

    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    resource.setCreatedTimestamp(Instant.now());
    entityManager.persistAndFlush(resource);
    entityManager.persistAndFlush(responsibleTeam);
    submissionDetail.setResourceId(resource);
    entityManager.persistAndFlush(submissionDetail);

    var submittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submittedSubmissionDetail.setId(null);
    submittedSubmissionDetail.setResourceId(resource);

    submittedSubmissionDetail = entityManager.persistAndFlush(submittedSubmissionDetail);

    var submittedGeneralDetails = new GeneralDetail();
    submittedGeneralDetails.setResourceName("Resource name");
    submittedGeneralDetails.setSubmissionDetail(submittedSubmissionDetail);

    entityManager.persistAndFlush(submittedGeneralDetails);

    var results = submissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    assertThat(results).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(names = {"DELETED", "APPROVED"})
  void getSubmissions_whenExplorationTeamRole_whenOneNotViewableAndOneDraft_assertOnlyDraftSubmissionReturned(
      SubmissionDetailStatus nonViewableStatus) {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpTest(userRoles);

    var draftSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    draftSubmissionDetail.setId(null);
    var resource = new Resource();
    var createdTimestamp = Instant.now();
    draftSubmissionDetail.setCreatedTimestamp(createdTimestamp);
    resource.setType(ResourceType.PROSPECT);
    draftSubmissionDetail.setResourceId(resource);
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    responsibleTeam.setResource(resource);
    entityManager.persistAndFlush(resource);
    entityManager.persistAndFlush(responsibleTeam);
    entityManager.persistAndFlush(draftSubmissionDetail);

    var nonViewableSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    nonViewableSubmissionDetail.setStatus(nonViewableStatus);
    nonViewableSubmissionDetail.setId(null);
    nonViewableSubmissionDetail.setResourceId(resource);
    nonViewableSubmissionDetail = entityManager.persistAndFlush(nonViewableSubmissionDetail);

    var submittedGeneralDetails = new GeneralDetail();
    submittedGeneralDetails.setResourceName("Resource name");
    submittedGeneralDetails.setSubmissionDetail(nonViewableSubmissionDetail);

    entityManager.persistAndFlush(submittedGeneralDetails);

    var results = submissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    assertThat(results)
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            SubmissionWorkAreaQueryResult::getCentreOfInterestQuadBlock,
            SubmissionWorkAreaQueryResult::getOrganisationResponsibleId,
            SubmissionWorkAreaQueryResult::getResourceName,
            SubmissionWorkAreaQueryResult::getSubmissionType,
            SubmissionWorkAreaQueryResult::getStatus,
            SubmissionWorkAreaQueryResult::getLicencesTheResourceIsOn,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
            tuple(
                draftSubmissionDetail.getId(),
                null,
                null,
                null,
                SubmissionType.CREATE_PROSPECT,
                SubmissionDetailStatus.DRAFT,
                null,
                createdTimestamp.truncatedTo(ChronoUnit.MILLIS))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ResponsibleTeamType.class, mode = EnumSource.Mode.EXCLUDE, names={"EXPLORATION"})
  void getSubmissions_whenResourceResponsibleTeamNotExploration_assertNothingReturned(ResponsibleTeamType responsibleTeamType) {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpTest(userRoles);

    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    var resource = new Resource();
    resource.setCreatedTimestamp(Instant.now());
    submissionDetail.setResourceId(resource);
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResponsibleTeamType(responsibleTeamType);
    responsibleTeam.setResource(resource);
    entityManager.persistAndFlush(resource);
    entityManager.persistAndFlush(responsibleTeam);
    entityManager.persistAndFlush(submissionDetail);

    var submittedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submittedSubmissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submittedSubmissionDetail.setId(null);
    submittedSubmissionDetail.setResourceId(resource);
    submittedSubmissionDetail = entityManager.persistAndFlush(submittedSubmissionDetail);

    var submittedGeneralDetails = new GeneralDetail();
    submittedGeneralDetails.setResourceName("Resource name");
    submittedGeneralDetails.setSubmissionDetail(submittedSubmissionDetail);

    entityManager.persistAndFlush(submittedGeneralDetails);

    var results = submissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    assertThat(results).isEmpty();
  }

  @Test
  void getSubmissions_assertCorrectOrderingAndValues() {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpTest(userRoles);

    var secondSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    secondSubmissionDetail.setId(null);
    var secondSubmissionDetailTimestamp = Instant.ofEpochMilli(LAST_SUBMISSION_DETAIL_EPOCH_TIME*2);
    secondSubmissionDetail.setCreatedTimestamp(secondSubmissionDetailTimestamp);
    var resource = new Resource();
    resource.setCreatedTimestamp(Instant.now());
    resource.setType(ResourceType.PROSPECT);
    secondSubmissionDetail.setResourceId(resource);
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    responsibleTeam.setResource(resource);
    entityManager.persistAndFlush(resource);
    entityManager.persistAndFlush(responsibleTeam);
    entityManager.persistAndFlush(secondSubmissionDetail);

    var firstSubmissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    firstSubmissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    firstSubmissionDetail.setId(null);
    firstSubmissionDetail.setResourceId(resource);
    var firstSubmissionDetailTimeStamp = Instant.ofEpochMilli(LAST_SUBMISSION_DETAIL_EPOCH_TIME*3);
    firstSubmissionDetail.setCreatedTimestamp(firstSubmissionDetailTimeStamp);
    firstSubmissionDetail = entityManager.persistAndFlush(firstSubmissionDetail);
    var firstGeneralDetails = new GeneralDetail();
    firstGeneralDetails.setResourceName("Resource name");
    firstGeneralDetails.setSubmissionDetail(firstSubmissionDetail);
    entityManager.persistAndFlush(firstGeneralDetails);

    var thirdSubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var secondResource = new Resource();
    var secondResponsibleTeam = new ResponsibleTeam();
    secondResponsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    secondResponsibleTeam.setResource(secondResource);
    secondResource.setType(ResourceType.DISCOVERY);

    thirdSubmissionDetail.setId(null);
    thirdSubmissionDetail.setResourceId(secondResource);
    thirdSubmissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    var thirdSubmissionDetailTimeStamp = Instant.ofEpochMilli(LAST_SUBMISSION_DETAIL_EPOCH_TIME);
    thirdSubmissionDetail.setCreatedTimestamp(thirdSubmissionDetailTimeStamp);
    entityManager.persistAndFlush(secondResource);
    entityManager.persistAndFlush(secondResponsibleTeam);
    entityManager.persistAndFlush(thirdSubmissionDetail);

    var results = submissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    // asserts correct ordering of the list
    assertThat(results)
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
            tuple(
                firstSubmissionDetail.getId(),
                firstSubmissionDetailTimeStamp.truncatedTo(ChronoUnit.MILLIS)),
            tuple(
                secondSubmissionDetail.getId(),
                secondSubmissionDetailTimestamp.truncatedTo(ChronoUnit.MILLIS)),
            tuple(
                thirdSubmissionDetail.getId(),
                thirdSubmissionDetailTimeStamp.truncatedTo(ChronoUnit.MILLIS))
        );
  }

  @Test
  void getSubmissions_whenOneFullSubmission_assertAllValues() {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.EXPLORATION);
    setUpTest(userRoles);

    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(null);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    var createdTimestamp = Instant.now();
    submissionDetail.setCreatedTimestamp(createdTimestamp);
    entityManager.persistAndFlush(resource);
    entityManager.persistAndFlush(responsibleTeam);
    submissionDetail.setResourceId(resource);
    entityManager.persistAndFlush(submissionDetail);

    var generalDetails = new GeneralDetail();
    generalDetails.setResourceName("The resource name");
    generalDetails.setSubmissionDetail(submissionDetail);
    generalDetails.setOrganisationId(46);
    entityManager.persistAndFlush(generalDetails);

    var quadBlocks = new QuadrantAndBlock();
    quadBlocks.setGeneralDetail(generalDetails);
    quadBlocks.setQuadrantBlock("25/12");
    entityManager.persistAndFlush(quadBlocks);

    var trapProperty = new TrapProperty();
    trapProperty.setSubmissionDetail(submissionDetail);
    trapProperty.setCentreOfInterestQuadrantBlock("25/12");
    entityManager.persistAndFlush(trapProperty);

    var licence = new Licence();
    licence.setLicenceId(953);
    licence.setLicenceReference("AL10");
    licence.setGeneralDetail(generalDetails);
    var licence2 = new Licence();
    licence2.setLicenceId(951);
    licence2.setLicenceReference("AL6");
    licence2.setGeneralDetail(generalDetails);
    entityManager.persistAndFlush(licence);
    entityManager.persistAndFlush(licence2);

    var results = submissionWorkAreaQueryService.getWorkAreaSubmissionItems();

    assertThat(results.get(0))
        .extracting(
            SubmissionWorkAreaQueryResult::getSubmissionDetailId,
            SubmissionWorkAreaQueryResult::getCentreOfInterestQuadBlock,
            SubmissionWorkAreaQueryResult::getOrganisationResponsibleId,
            SubmissionWorkAreaQueryResult::getResourceName,
            SubmissionWorkAreaQueryResult::getSubmissionType,
            SubmissionWorkAreaQueryResult::getLicencesTheResourceIsOn,
            SubmissionWorkAreaQueryResult::getStatus,
            result -> result.getCreatedOn().toInstant().truncatedTo(ChronoUnit.MILLIS))
        .containsExactly(
            submissionDetail.getId(),
            "25/12",
            46,
            "The resource name",
            SubmissionType.CREATE_PROSPECT,
            List.of(953, 951),
            SubmissionDetailStatus.DRAFT,
            createdTimestamp.truncatedTo(ChronoUnit.MILLIS)
        );
  }

  private void setUpTest(Set<TeamRole> userRoles) {
    // Setup user
    var user = ServiceUserDetailTestUtil.Builder()
        .withWuaId(1000L)
        .build();

    // Ensure user is treated as currently logged in
    SamlAuthenticationUtil.Builder()
        .withUser(user)
        .setSecurityContext();

    // Stub regulator team
    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .withId(null)
        .build();

    entityManager.persistAndFlush(team);

    // Add user to team with the previously defined roles
    teamMemberRoleService.updateUserTeamRoles(team, new WebUserAccountId(user.wuaId()),
        TeamRoleUtil.getRoleNames(userRoles));
  }
}