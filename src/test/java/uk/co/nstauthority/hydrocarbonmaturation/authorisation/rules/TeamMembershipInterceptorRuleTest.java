package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;

@ExtendWith(MockitoExtension.class)
class TeamMembershipInterceptorRuleTest {

  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private PermissionService permissionService;

  @Mock
  private ResponsibleTeamPersistenceService responsibleTeamPersistenceService;

  @Mock
  private IndustryTeamService industryTeamService;

  @InjectMocks
  private TeamMembershipInterceptorRule teamMembershipInterceptorRule;

  @Test
  void supports() {
    assertThat(teamMembershipInterceptorRule.supports())
        .isEqualTo(IsFromResponsibleTeam.class);
  }

  @Test
  void check_whenIsRegulatorInResponsibleTeam_assertRulePass() {
    var mockRequest = mock(HttpServletRequest.class);
    var mockResponse = mock(HttpServletResponse.class);
    var user = ServiceUserDetailTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    when(responsibleTeamPersistenceService.getResponsibleTeamForResourceOrThrow(submissionDetail.getResourceId()))
        .thenReturn(responsibleTeam);
    when(permissionService.hasPermission(user, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);

    var securityRuleResult = teamMembershipInterceptorRule
        .check(
            IsFromResponsibleTeam.class,
            mockRequest,
            mockResponse,
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isTrue();
  }

  @Test
  void check_whenIsIndustrySubmitterInResponsibleTeam_assertRulePass() {
    var mockRequest = mock(HttpServletRequest.class);
    var mockResponse = mock(HttpServletResponse.class);
    var user = ServiceUserDetailTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    when(responsibleTeamPersistenceService.getResponsibleTeamForResourceOrThrow(submissionDetail.getResourceId()))
        .thenReturn(responsibleTeam);
    when(permissionService.hasPermission(user, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(false);
    var generalDetail = new GeneralDetail();
    generalDetail.setOrganisationId(1);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));
    when(industryTeamService.isSubmitterForOrganisationUnit(user, generalDetail.getOrganisationId()))
        .thenReturn(true);
    var securityRuleResult = teamMembershipInterceptorRule
        .check(
            IsFromResponsibleTeam.class,
            mockRequest,
            mockResponse,
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isTrue();
  }

  @Test
  void check_whenIsNotInResponsibleTeam_assertRuleFailForbidden() {
    var mockRequest = mock(HttpServletRequest.class);
    var mockResponse = mock(HttpServletResponse.class);
    var user = ServiceUserDetailTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    when(responsibleTeamPersistenceService.getResponsibleTeamForResourceOrThrow(submissionDetail.getResourceId()))
        .thenReturn(responsibleTeam);
    when(permissionService.hasPermission(user, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(false);
    var generalDetail = new GeneralDetail();
    generalDetail.setOrganisationId(1);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));
    when(industryTeamService.isSubmitterForOrganisationUnit(user, generalDetail.getOrganisationId()))
        .thenReturn(false);
    var securityRuleResult = teamMembershipInterceptorRule
        .check(
            IsFromResponsibleTeam.class,
            mockRequest,
            mockResponse,
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isFalse();
    assertThat(securityRuleResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void check_whenResponsibleTeamTypeDoesNotMatchPermissionAndNotSubmitter_assertRuleFailForbidden() {
    var mockRequest = mock(HttpServletRequest.class);
    var mockResponse = mock(HttpServletResponse.class);
    var user = ServiceUserDetailTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.ONSHORE);
    when(responsibleTeamPersistenceService.getResponsibleTeamForResourceOrThrow(submissionDetail.getResourceId()))
        .thenReturn(responsibleTeam);
    var generalDetail = new GeneralDetail();
    generalDetail.setOrganisationId(1);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));
    when(industryTeamService.isSubmitterForOrganisationUnit(user, generalDetail.getOrganisationId()))
        .thenReturn(false);
    var securityRuleResult = teamMembershipInterceptorRule
        .check(
            IsFromResponsibleTeam.class,
            mockRequest,
            mockResponse,
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isFalse();
    assertThat(securityRuleResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }
}