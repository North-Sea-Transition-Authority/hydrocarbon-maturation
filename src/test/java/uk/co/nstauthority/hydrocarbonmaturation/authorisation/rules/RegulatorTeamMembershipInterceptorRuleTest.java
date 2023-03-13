package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ExtendWith(MockitoExtension.class)
class RegulatorTeamMembershipInterceptorRuleTest {

  @Mock
  private ResponsibleTeamPersistenceService responsibleTeamPersistenceService;

  @Mock
  private PermissionService permissionService;

  @InjectMocks
  private RegulatorTeamMembershipInterceptorRule regulatorTeamMembershipInterceptorRule;

  @Test
  void hasRequiredTeamMembershipPermissions_whenInResponsibleTeam_assertRulePass() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    when(responsibleTeamPersistenceService.getResponsibleTeamForResourceOrThrow(submissionDetail.getResourceId()))
        .thenReturn(responsibleTeam);
    when(permissionService.hasPermission(user, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);

    var securityRuleResult = regulatorTeamMembershipInterceptorRule
        .hasRequiredTeamMembershipPermissions(
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isTrue();
  }

  @Test
  void hasRequiredTeamMembershipPermissions_whenInResponsibleTeamAndIndustrySubmissionInDraft_assertRuleFailForbidden() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var responsibleTeam = new ResponsibleTeam();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    when(responsibleTeamPersistenceService.getResponsibleTeamForResourceOrThrow(submissionDetail.getResourceId()))
        .thenReturn(responsibleTeam);

    var securityRuleResult = regulatorTeamMembershipInterceptorRule
        .hasRequiredTeamMembershipPermissions(user, submissionDetail);
    AssertionsForClassTypes.assertThat(securityRuleResult.hasRulePassed()).isFalse();
    AssertionsForClassTypes.assertThat(securityRuleResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void hasRequiredTeamMembershipPermissions_whenNotInResponsibleTeam_assertRuleFailForbidden() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var responsibleTeam = new ResponsibleTeam();
    submissionDetail.setSubmitterType(SubmitterType.NSTA);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.ONSHORE);
    when(responsibleTeamPersistenceService.getResponsibleTeamForResourceOrThrow(submissionDetail.getResourceId()))
        .thenReturn(responsibleTeam);

    var securityRuleResult = regulatorTeamMembershipInterceptorRule
        .hasRequiredTeamMembershipPermissions(user, submissionDetail);
    AssertionsForClassTypes.assertThat(securityRuleResult.hasRulePassed()).isFalse();
    AssertionsForClassTypes.assertThat(securityRuleResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void hasRequiredTeamMembershipPermissions_whenInResponsibleTeamAndIndustrySubmissionInSubmitted_assertRulePass() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    var responsibleTeam = new ResponsibleTeam();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    when(responsibleTeamPersistenceService.getResponsibleTeamForResourceOrThrow(submissionDetail.getResourceId()))
        .thenReturn(responsibleTeam);
    when(permissionService.hasPermission(user, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);

    var securityRuleResult = regulatorTeamMembershipInterceptorRule
        .hasRequiredTeamMembershipPermissions(
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isTrue();
  }
}