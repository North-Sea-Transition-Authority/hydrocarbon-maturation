package uk.co.nstauthority.hydrocarbonmaturation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByRegulatorUsersInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermissionInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.OperatorTeamMembershipInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.RegulatorResponsibleTeamInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.RegulatorTeamMembershipInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionStatusInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionTypeInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.TeamMembershipInterceptorFactory;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.TeamMembershipInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.WebMvcConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.PermissionManagementHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryPermissionManagementHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorPermissionManagementHandlerInterceptor;

@Import(value = {
    WebMvcConfiguration.class,
    PermissionManagementHandlerInterceptor.class,
    RegulatorPermissionManagementHandlerInterceptor.class,
    IndustryPermissionManagementHandlerInterceptor.class,
    AccessibleByRegulatorUsersInterceptor.class,
    HasPermissionInterceptor.class,
    SubmissionStatusInterceptorRule.class,
    RegulatorResponsibleTeamInterceptorRule.class,
    TeamMembershipInterceptorRule.class,
    TeamMembershipInterceptorFactory.class,
    OperatorTeamMembershipInterceptorRule.class,
    RegulatorTeamMembershipInterceptorRule.class,
    SubmissionTypeInterceptorRule.class,
    PermissionService.class})
public abstract class AbstractSubmissionControllerTest extends AbstractControllerTest{

  @MockBean
  protected ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @MockBean
  protected ResponsibleTeamPersistenceService responsibleTeamPersistenceService;

  @MockBean
  protected OrganisationUnitQueryService organisationUnitQueryService;

  @MockBean
  protected IndustryTeamService industryTeamService;

  @MockBean
  protected PermissionService permissionService;

  @Autowired
  protected OperatorTeamMembershipInterceptorRule operatorTeamMembershipInterceptorRule;

  @Autowired
  protected RegulatorTeamMembershipInterceptorRule regulatorTeamMembershipInterceptorRule;

  @Autowired
  protected TeamMembershipInterceptorRule teamMembershipInterceptorRule;

  @MockBean
  protected TeamMembershipInterceptorFactory teamMembershipInterceptorFactory;

  @Autowired
  protected ControllerHelperService controllerHelperService;

  protected static final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();
  protected static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  protected SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp(TestInfo testInfo) {
    doCallRealMethod().when(userDetailService).getUserDetail();
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT, SUBMISSION_DETAIL_ID);
    when(submissionDetailService.getSubmissionDetail(SUBMISSION_DETAIL_ID)).thenReturn(Optional.of(submissionDetail));
    submissionDetail.setId(SUBMISSION_DETAIL_ID);
    var securityTestAnnotation = testInfo
        .getTestMethod().map(method -> method.getAnnotation(SecurityTest.class));
    if (securityTestAnnotation.isEmpty()) {
      //if the test doesn't have a @SecurityTest annotation, ensure the security passes
      when(teamMembershipInterceptorFactory.getTeamMembershipInterceptorStrategy())
          .thenReturn(regulatorTeamMembershipInterceptorRule);
      var responsibleTeam = new ResponsibleTeam();
      responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
      when(responsibleTeamPersistenceService.getResponsibleTeamForResourceOrThrow(any()))
          .thenReturn(responsibleTeam);
      when(permissionService.hasPermission(any(), any()))
          .thenReturn(true);
    }
  }

  protected void setUpWhenUserIsResponsibleOrganisation_passSecurity(ServiceUserDetail user) {
    when(teamMembershipInterceptorFactory.getTeamMembershipInterceptorStrategy())
        .thenReturn(operatorTeamMembershipInterceptorRule);
    var responsibleTeam = new ResponsibleTeam();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    responsibleTeam.setResource(submissionDetail.getResourceId());
    when(responsibleTeamPersistenceService.getResponsibleTeamForResourceOrThrow(submissionDetail.getResourceId()))
        .thenReturn(responsibleTeam);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetail));
    when(industryTeamService.getOrganisationUnitIdsForUserWithIndustryTeamRoles(
        user, Set.of(IndustryTeamRole.SUBMITTER)))
        .thenReturn(List.of(1));
  }

  protected void setUpWhenUserIsResponsibleTeam_passSecurity() {
    when(teamMembershipInterceptorFactory.getTeamMembershipInterceptorStrategy())
        .thenReturn(regulatorTeamMembershipInterceptorRule);
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    responsibleTeam.setResource(submissionDetail.getResourceId());

    when(responsibleTeamPersistenceService.getResponsibleTeamForResourceOrThrow(
        submissionDetail.getResourceId()))
        .thenReturn(responsibleTeam);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);
  }

  protected void setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(ServiceUserDetail user) {
    when(teamMembershipInterceptorFactory.getTeamMembershipInterceptorStrategy())
        .thenReturn(operatorTeamMembershipInterceptorRule);
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    responsibleTeam.setResource(submissionDetail.getResourceId());
    when(responsibleTeamPersistenceService.getResponsibleTeamForResourceOrThrow(
        submissionDetail.getResourceId()))
        .thenReturn(responsibleTeam);
    when(permissionService.hasPermission(user, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(false);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetail));
    when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(resourceDetail.getOrganisationId()))
        .thenReturn(List.of(1));
    when(industryTeamService.isSubmitterForOrganisationGroups(user, List.of(1)))
        .thenReturn(false);
  }
}
