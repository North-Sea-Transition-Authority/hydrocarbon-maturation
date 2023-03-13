package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;

@ExtendWith(MockitoExtension.class)
class TeamMembershipInterceptorRuleTest {

  @Mock
  private TeamMembershipInterceptorFactory teamMembershipInterceptorFactory;

  @Mock
  private OperatorTeamMembershipInterceptorRule operatorTeamMembershipInterceptorRule;

  @Mock
  private RegulatorTeamMembershipInterceptorRule regulatorTeamMembershipInterceptorRule;

  @InjectMocks
  private TeamMembershipInterceptorRule teamMembershipInterceptorRule;

  @Test
  void supports() {
    assertThat(teamMembershipInterceptorRule.supports())
        .isEqualTo(IsFromResponsibleTeam.class);
  }

  @Test
  void check_whenRegulator_whenRegulatorRuleFail_assertRuleFailForbidden() {
    var mockRequest = mock(HttpServletRequest.class);
    var mockResponse = mock(HttpServletResponse.class);
    var user = ServiceUserDetailTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var responsibleTeam = new ResponsibleTeam();
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.ONSHORE);
    when(teamMembershipInterceptorFactory.getTeamMembershipInterceptorStrategy())
        .thenReturn(regulatorTeamMembershipInterceptorRule);
    when(regulatorTeamMembershipInterceptorRule.hasRequiredTeamMembershipPermissions(user, submissionDetail))
        .thenReturn(SecurityRuleResult.checkFailedWithStatus(HttpStatus.FORBIDDEN));
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
  void check_whenOperator_whenOperatorRuleFail_assertRuleFailForbidden() {
    var mockRequest = mock(HttpServletRequest.class);
    var mockResponse = mock(HttpServletResponse.class);
    var user = ServiceUserDetailTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var responsibleTeam = new ResponsibleTeam();
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.ONSHORE);
    when(teamMembershipInterceptorFactory.getTeamMembershipInterceptorStrategy())
        .thenReturn(operatorTeamMembershipInterceptorRule);
    when(operatorTeamMembershipInterceptorRule.hasRequiredTeamMembershipPermissions(user, submissionDetail))
        .thenReturn(SecurityRuleResult.checkFailedWithStatus(HttpStatus.FORBIDDEN));
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
  void check_whenRegulator_whenRegulatorRulePass_assertRulePass() {
    var mockRequest = mock(HttpServletRequest.class);
    var mockResponse = mock(HttpServletResponse.class);
    var user = ServiceUserDetailTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var responsibleTeam = new ResponsibleTeam();
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.ONSHORE);
    when(teamMembershipInterceptorFactory.getTeamMembershipInterceptorStrategy())
        .thenReturn(regulatorTeamMembershipInterceptorRule);
    when(regulatorTeamMembershipInterceptorRule.hasRequiredTeamMembershipPermissions(user, submissionDetail))
        .thenReturn(SecurityRuleResult.continueAsNormal());
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
  void check_whenOperator_whenOperatorRulePass_assertRulePass() {
    var mockRequest = mock(HttpServletRequest.class);
    var mockResponse = mock(HttpServletResponse.class);
    var user = ServiceUserDetailTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var responsibleTeam = new ResponsibleTeam();
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.ONSHORE);
    when(teamMembershipInterceptorFactory.getTeamMembershipInterceptorStrategy())
        .thenReturn(operatorTeamMembershipInterceptorRule);
    when(operatorTeamMembershipInterceptorRule.hasRequiredTeamMembershipPermissions(user, submissionDetail))
        .thenReturn(SecurityRuleResult.continueAsNormal());
    var securityRuleResult = teamMembershipInterceptorRule
        .check(
            IsFromResponsibleTeam.class,
            mockRequest,
            mockResponse,
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isTrue();
  }
}