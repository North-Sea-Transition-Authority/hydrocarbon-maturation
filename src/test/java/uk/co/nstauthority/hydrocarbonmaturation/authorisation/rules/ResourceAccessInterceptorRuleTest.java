package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRuleResult;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;

@ExtendWith(MockitoExtension.class)
class ResourceAccessInterceptorRuleTest {

  @Mock
  private ResourceAccessInterceptorFactory resourceAccessInterceptorFactory;

  @Mock
  private OperatorResourceAccessInterceptorRule operatorTeamMembershipInterceptorRule;

  @Mock
  private RegulatorResourceAccessInterceptorRule regulatorTeamMembershipInterceptorRule;

  @InjectMocks
  private ResourceAccessInterceptorRule resourceAccessInterceptorRule;

  @Test
  void supports() {
    assertThat(resourceAccessInterceptorRule.supports())
        .isEqualTo(IsFromResponsibleTeam.class);
  }

  @Test
  void check_whenRegulator_whenRegulatorRuleFail_assertRuleFailForbidden() {
    var mockRequest = mock(HttpServletRequest.class);
    var mockResponse = mock(HttpServletResponse.class);
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var regulatorManagedInformation = new RegulatorManagedInformation();
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.ONSHORE);
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy(user))
        .thenReturn(regulatorTeamMembershipInterceptorRule);
    when(regulatorTeamMembershipInterceptorRule.hasRequiredResourceAccessPermissions(user, submissionDetail))
        .thenReturn(SecurityRuleResult.checkFailedWithStatus(HttpStatus.FORBIDDEN));
    var securityRuleResult = resourceAccessInterceptorRule
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
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var regulatorManagedInformation = new RegulatorManagedInformation();
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.ONSHORE);
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy(user))
        .thenReturn(operatorTeamMembershipInterceptorRule);
    when(operatorTeamMembershipInterceptorRule.hasRequiredResourceAccessPermissions(user, submissionDetail))
        .thenReturn(SecurityRuleResult.checkFailedWithStatus(HttpStatus.FORBIDDEN));
    var securityRuleResult = resourceAccessInterceptorRule
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
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var responsibleTeam = new RegulatorManagedInformation();
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.ONSHORE);
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy(user))
        .thenReturn(regulatorTeamMembershipInterceptorRule);
    when(regulatorTeamMembershipInterceptorRule.hasRequiredResourceAccessPermissions(user, submissionDetail))
        .thenReturn(SecurityRuleResult.continueAsNormal());
    var securityRuleResult = resourceAccessInterceptorRule
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
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var regulatorManagedInformation = new RegulatorManagedInformation();
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.ONSHORE);
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy(user))
        .thenReturn(operatorTeamMembershipInterceptorRule);
    when(operatorTeamMembershipInterceptorRule.hasRequiredResourceAccessPermissions(user, submissionDetail))
        .thenReturn(SecurityRuleResult.continueAsNormal());
    var securityRuleResult = resourceAccessInterceptorRule
        .check(
            IsFromResponsibleTeam.class,
            mockRequest,
            mockResponse,
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isTrue();
  }
}