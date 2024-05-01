package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;

@ExtendWith(MockitoExtension.class)
class RegulatorResourceAccessInterceptorRuleTest {

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Mock
  private PermissionService permissionService;

  @InjectMocks
  private RegulatorResourceAccessInterceptorRule regulatorTeamMembershipInterceptorRule;

  @Test
  void hasRequiredTeamMembershipPermissions_whenInResponsibleTeam_assertRulePass() {
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(submissionDetail
        .getSubmission().getResource()))
        .thenReturn(regulatorManagedInformation);
    when(permissionService.hasPermission(user, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);

    var securityRuleResult = regulatorTeamMembershipInterceptorRule
        .hasRequiredResourceAccessPermissions(
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isTrue();
  }

  @Test
  void hasRequiredTeamMembershipPermissions_whenInResponsibleTeamAndIndustrySubmissionInDraft_assertRuleFailForbidden() {
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var regulatorManagedInformation = new RegulatorManagedInformation();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(regulatorManagedInformation);

    var securityRuleResult = regulatorTeamMembershipInterceptorRule
        .hasRequiredResourceAccessPermissions(user, submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isFalse();
    assertThat(securityRuleResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void hasRequiredTeamMembershipPermissions_whenNotInResponsibleTeam_assertRuleFailForbidden() {
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var regulatorManagedInformation = new RegulatorManagedInformation();
    submissionDetail.setSubmitterType(SubmitterType.REGULATOR);
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.ONSHORE);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(regulatorManagedInformation);

    var securityRuleResult = regulatorTeamMembershipInterceptorRule
        .hasRequiredResourceAccessPermissions(user, submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isFalse();
    assertThat(securityRuleResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void hasRequiredTeamMembershipPermissions_whenInResponsibleTeamAndIndustrySubmissionInSubmitted_assertRulePass() {
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    var regulatorManagedInformation = new RegulatorManagedInformation();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    when(regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResourceOrThrow(submissionDetail.getSubmission().getResource()))
        .thenReturn(regulatorManagedInformation);
    when(permissionService.hasPermission(user, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);

    var securityRuleResult = regulatorTeamMembershipInterceptorRule
        .hasRequiredResourceAccessPermissions(
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isTrue();
  }
}