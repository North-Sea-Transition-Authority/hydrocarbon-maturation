package uk.co.nstauthority.hydrocarbonmaturation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByRegulatorOrAreaUsersInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRoleInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.OperatorResourceAccessInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.RegulatorResourceAccessInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.RegulatorResponsibleTeamInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.ResourceAccessInterceptorFactory;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.ResourceAccessInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionDetailVersionInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionStatusInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionTypeInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.WebMvcConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Import(value = {
    WebMvcConfiguration.class,
    AccessibleByRegulatorOrAreaUsersInterceptor.class,
    HasAnyRoleInterceptor.class,
    SubmissionStatusInterceptorRule.class,
    RegulatorResponsibleTeamInterceptorRule.class,
    ResourceAccessInterceptorRule.class,
    ResourceAccessInterceptorFactory.class,
    SubmissionDetailVersionInterceptorRule.class,
    OperatorResourceAccessInterceptorRule.class,
    RegulatorResourceAccessInterceptorRule.class,
    SubmissionTypeInterceptorRule.class})
public abstract class AbstractSubmissionControllerTest extends AbstractControllerTest{

  @MockBean
  protected ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @MockBean
  protected OrganisationUnitQueryService organisationUnitQueryService;

  @MockBean
  protected IndustryTeamService industryTeamService;

  @MockBean
  protected FieldPersistenceService fieldPersistenceService;

  @Autowired
  protected OperatorResourceAccessInterceptorRule operatorTeamMembershipInterceptorRule;

  @Autowired
  protected SubmissionDetailVersionInterceptorRule submissionDetailVersionInterceptorRule;

  @Autowired
  protected RegulatorResourceAccessInterceptorRule regulatorTeamMembershipInterceptorRule;

  @Autowired
  protected ResourceAccessInterceptorRule resourceAccessInterceptorRule;

  @MockBean
  protected ResourceAccessInterceptorFactory resourceAccessInterceptorFactory;

  @Autowired
  protected ControllerHelperService controllerHelperService;

  protected static final User USER = UserTestUtil.Builder().build();

  protected static final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();
  protected SubmissionDetail submissionDetail;

  protected ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp(TestInfo testInfo) {
    doCallRealMethod().when(userService).getUser();
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT,
        SUBMISSION_DETAIL_ID);
    submissionDetail.setAcceptedTimestamp(Instant.now());
    when(submissionDetailService.getSubmissionDetail(SUBMISSION_DETAIL_ID)).thenReturn(Optional.of(submissionDetail));
    when(submissionDetailService.getSubmissionDetailForIdOrError(UUID.fromString(submissionDetail.getId().toString())))
        .thenReturn(submissionDetail);
    submissionDetail.setId(SUBMISSION_DETAIL_ID);

    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

    var securityTestAnnotation = testInfo
        .getTestMethod().map(method -> method.getAnnotation(SecurityTest.class));
    if (securityTestAnnotation.isEmpty()) {
      //if the test doesn't have a @SecurityTest annotation, ensure the security passes
      var responsibleTeam = new RegulatorManagedInformation();
      when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy(USER))
          .thenReturn(regulatorTeamMembershipInterceptorRule);
      responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(any()))
          .thenReturn(responsibleTeam);
    }
  }

  protected void setUpWhenUserIsResponsibleOrganisation_passSecurity(User user) {
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy(user))
        .thenReturn(operatorTeamMembershipInterceptorRule);
    var responsibleTeam = new RegulatorManagedInformation();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    responsibleTeam.setResource(submissionDetail.getSubmission().getResource());
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(responsibleTeam);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetail));
    var orgGroupId = 100;
    var orgGroups = List.of(orgGroupId);
    var orgGroupStrings = orgGroups.stream().map(String::valueOf).toList();
    var orgTeam = new Team();
    orgTeam.setTeamType(TeamType.INDUSTRY);
    orgTeam.setScopeId(String.valueOf(orgGroupId));
    var orgTeamRole = new TeamRole();
    orgTeamRole.setTeam(orgTeam);
    orgTeamRole.setRole(Role.INDUSTRY_SUBMIT);
    orgTeamRole.setWuaId(user.wuaId());
    when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(resourceDetail.getOrganisationId()))
        .thenReturn(orgGroups);
    when(teamQueryService.getOrganisationScopedTeams(orgGroupStrings)).thenReturn(Set.of(orgTeam));
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(orgTeamRole));
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(true);
  }

  protected void setUpWhenUserIsResponsibleOrganisation_passSecurity(User user, SubmissionDetail submissionDetail) {
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy(user))
        .thenReturn(operatorTeamMembershipInterceptorRule);
    var responsibleTeam = new RegulatorManagedInformation();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    responsibleTeam.setResource(submissionDetail.getSubmission().getResource());
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(responsibleTeam);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
            .thenReturn(Optional.of(resourceDetail));
    var orgGroupId = 100;
    var orgGroups = List.of(orgGroupId);
    var orgGroupStrings = orgGroups.stream().map(String::valueOf).toList();
    var orgTeam = new Team();
    orgTeam.setTeamType(TeamType.INDUSTRY);
    orgTeam.setScopeId(String.valueOf(orgGroupId));
    var orgTeamRole = new TeamRole();
    orgTeamRole.setTeam(orgTeam);
    orgTeamRole.setRole(Role.INDUSTRY_SUBMIT);
    orgTeamRole.setWuaId(user.wuaId());
    when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(resourceDetail.getOrganisationId()))
        .thenReturn(orgGroups);
    when(teamQueryService.getOrganisationScopedTeams(orgGroupStrings)).thenReturn(Set.of(orgTeam));
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(orgTeamRole));
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(true);
  }

  protected void setUpWhenUserIsResponsibleOrganisation_field_passSecurity(User user, SubmissionDetail submissionDetail) {
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy(user))
            .thenReturn(operatorTeamMembershipInterceptorRule);
    var responsibleTeam = new RegulatorManagedInformation();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    responsibleTeam.setResource(submissionDetail.getSubmission().getResource());
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
            submissionDetail.getSubmission().getResource()))
            .thenReturn(responsibleTeam);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    var field = mock(Field.class);
    when(field.getResponsibleOrganisationId())
            .thenReturn(resourceDetail.getOrganisationId());
    when(field.getFieldLocatedInEngland())
            .thenReturn(true);
    when(fieldPersistenceService.getFieldOrThrow(submissionDetail.getSubmission().getResource()))
            .thenReturn(field);
    var orgGroupId = 100;
    var orgGroups = List.of(orgGroupId);
    var orgGroupStrings = orgGroups.stream().map(String::valueOf).toList();
    var orgTeam = new Team();
    orgTeam.setTeamType(TeamType.INDUSTRY);
    orgTeam.setScopeId(String.valueOf(orgGroupId));
    var orgTeamRole = new TeamRole();
    orgTeamRole.setTeam(orgTeam);
    orgTeamRole.setRole(Role.INDUSTRY_SUBMIT);
    orgTeamRole.setWuaId(user.wuaId());
    when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(resourceDetail.getOrganisationId()))
        .thenReturn(orgGroups);
    when(teamQueryService.getOrganisationScopedTeams(orgGroupStrings)).thenReturn(Set.of(orgTeam));
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(orgTeamRole));
  }

  protected void setUpWhenUserIsResponsibleTeam_passSecurity() {
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy(USER))
        .thenReturn(regulatorTeamMembershipInterceptorRule);
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    regulatorManagedInformation.setResource(submissionDetail.getSubmission().getResource());

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(regulatorManagedInformation);
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(teamQueryService.userHasStaticRole(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
  }

  protected void setUpWhenUserIsResponsibleTeam_passSecurity(
      User user, SubmissionDetail submissionDetail, Role role, TeamType teamType
  ) {
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy(user))
        .thenReturn(regulatorTeamMembershipInterceptorRule);
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    regulatorManagedInformation.setResource(submissionDetail.getSubmission().getResource());

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(regulatorManagedInformation);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), teamType, role)).thenReturn(true);
    when(teamQueryService.userHasStaticRole(user.wuaId(), teamType, role)).thenReturn(true);
  }

  protected void setUpWhenUserIsResponsibleTeam_forField_passSecurity(User user, SubmissionDetail submissionDetail) {
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy(user))
        .thenReturn(regulatorTeamMembershipInterceptorRule);
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);
    regulatorManagedInformation.setResource(submissionDetail.getSubmission().getResource());

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(regulatorManagedInformation);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(true);
    when(teamQueryService.userHasStaticRole(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(true);
  }

  protected void setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(User user) {
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy(user))
        .thenReturn(operatorTeamMembershipInterceptorRule);
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    responsibleTeam.setResource(submissionDetail.getSubmission().getResource());
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(responsibleTeam);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetail));
    var orgGroups = List.of(100);
    var orgGroupStrings = orgGroups.stream().map(String::valueOf).toList();
    when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(resourceDetail.getOrganisationId()))
        .thenReturn(orgGroups);
    when(teamQueryService.getOrganisationScopedTeams(orgGroupStrings)).thenReturn(Set.of());
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of());
  }

  protected void setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(User user, SubmissionDetail submissionDetail) {
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy(user))
        .thenReturn(operatorTeamMembershipInterceptorRule);
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    responsibleTeam.setResource(submissionDetail.getSubmission().getResource());
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(responsibleTeam);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetail));
    var orgGroups = List.of(100);
    var orgGroupStrings = orgGroups.stream().map(String::valueOf).toList();
    when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(resourceDetail.getOrganisationId()))
        .thenReturn(orgGroups);
    when(teamQueryService.getOrganisationScopedTeams(orgGroupStrings)).thenReturn(Set.of());
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of());
  }

  protected void setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_field_failSecurity(User user, SubmissionDetail submissionDetail) {
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy(user))
            .thenReturn(operatorTeamMembershipInterceptorRule);
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    responsibleTeam.setResource(submissionDetail.getSubmission().getResource());
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
            submissionDetail.getSubmission().getResource()))
            .thenReturn(responsibleTeam);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    var field = new Field();
    field.setResponsibleOrganisationId(resourceDetail.getOrganisationId());
    when(fieldPersistenceService.getFieldOrThrow(submissionDetail.getSubmission().getResource()))
            .thenReturn(field);
    when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(resourceDetail.getOrganisationId()))
            .thenReturn(List.of(1));
  }

  protected void setUpWhenIsLatestExtantSubmissionVersion_passSecurity(SubmissionDetail submissionDetail) {
    if (submissionDetail.getVersionNumber() > 1 || submissionDetail.getStatus() != SubmissionDetailStatus.DRAFT) {
      switch (submissionDetail.getStatus()) {
        case ACCEPTED -> when(submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
            submissionDetail,
            Set.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED)))
            .thenReturn(true);
        case DRAFT -> when(submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
            submissionDetail,
            Set.of(SubmissionDetailStatus.DRAFT, SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED)))
            .thenReturn(true);
        case SUBMITTED -> when(submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
            submissionDetail,
            Set.of(SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.ACCEPTED))).thenReturn(true);
        default -> {
        }
      }
    }
  }
}
