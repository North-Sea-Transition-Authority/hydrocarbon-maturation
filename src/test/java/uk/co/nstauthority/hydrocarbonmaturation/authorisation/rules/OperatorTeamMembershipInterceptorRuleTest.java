package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;

@ExtendWith(MockitoExtension.class)
class OperatorTeamMembershipInterceptorRuleTest {

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  private IndustryTeamService industryTeamService;

  @InjectMocks
  private OperatorTeamMembershipInterceptorRule operatorTeamMembershipInterceptorRule;

  @Test
  void hasRequiredTeamMembershipPermissions_whenIsIndustrySubmitterInResponsibleTeam_assertRulePass() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);

    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetail));
    when(industryTeamService.getOrganisationUnitIdsForUserWithIndustryTeamRoles(
        user, Set.of(IndustryTeamRole.SUBMITTER)))
        .thenReturn(List.of(1));
    var securityRuleResult = operatorTeamMembershipInterceptorRule
        .hasRequiredTeamMembershipPermissions(
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isTrue();
  }

  @Test
  void hasRequiredTeamMembershipPermissions_whenIsNotIndustrySubmitterInResponsibleTeam_assertRuleFail() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);

    var resourceDetail = new ResourceDetail();
    var orgUnitIdUserIsNotSubmitterIn = 2;
    resourceDetail.setOrganisationId(orgUnitIdUserIsNotSubmitterIn);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetail));
    when(industryTeamService.getOrganisationUnitIdsForUserWithIndustryTeamRoles(
        user, Set.of(IndustryTeamRole.SUBMITTER)))
        .thenReturn(List.of(1));
    var securityRuleResult = operatorTeamMembershipInterceptorRule
        .hasRequiredTeamMembershipPermissions(
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isFalse();
    assertThat(securityRuleResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void hasRequiredTeamMembershipPermissions_whenSubmissionIsNotIndustry_assertRuleFailForbidden() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setSubmitterType(SubmitterType.NSTA);

    var securityRuleResult = operatorTeamMembershipInterceptorRule
        .hasRequiredTeamMembershipPermissions(user, submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isFalse();
    assertThat(securityRuleResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }
}