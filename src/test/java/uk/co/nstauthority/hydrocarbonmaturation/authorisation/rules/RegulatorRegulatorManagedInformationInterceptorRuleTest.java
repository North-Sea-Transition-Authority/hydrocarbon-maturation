package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsRegulatorInResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ExtendWith(MockitoExtension.class)
class RegulatorRegulatorManagedInformationInterceptorRuleTest {

  @Mock
  private PermissionService permissionService;

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @InjectMocks
  private RegulatorResponsibleTeamInterceptorRule regulatorResponsibleTeamInterceptorRule;

  @Test
  void supports() {
    assertThat(regulatorResponsibleTeamInterceptorRule.supports())
        .isEqualTo(IsRegulatorInResponsibleTeam.class);
  }

  @Test
  void check_whenIsRegulatorInResponsibleTeam_assertRulePass() {
    var mockRequest = mock(HttpServletRequest.class);
    var mockResponse = mock(HttpServletResponse.class);
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(responsibleTeam);
    when(permissionService.hasPermission(user, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);

    var securityRuleResult = regulatorResponsibleTeamInterceptorRule
        .check(
            IsRegulatorInResponsibleTeam.class,
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
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(responsibleTeam);
    when(permissionService.hasPermission(user, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(false);

    var securityRuleResult = regulatorResponsibleTeamInterceptorRule
        .check(
            IsRegulatorInResponsibleTeam.class,
            mockRequest,
            mockResponse,
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isFalse();
    assertThat(securityRuleResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void check_whenTeamTypeDoesNotMatchPermissionAndNotInResponsibleTeam_assertRuleFailForbidden() {
    var mockRequest = mock(HttpServletRequest.class);
    var mockResponse = mock(HttpServletResponse.class);
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(responsibleTeam);

    var securityRuleResult = regulatorResponsibleTeamInterceptorRule
        .check(
            IsRegulatorInResponsibleTeam.class,
            mockRequest,
            mockResponse,
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isFalse();
    assertThat(securityRuleResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }
}