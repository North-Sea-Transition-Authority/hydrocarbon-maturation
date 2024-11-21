package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRole;

@ExtendWith(MockitoExtension.class)
class OperatorResourceAccessInterceptorRuleTest {

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  @Mock
  private FieldPersistenceService fieldPersistenceService;
  @Mock
  private TeamQueryService teamQueryService;
  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;

  @InjectMocks
  private OperatorResourceAccessInterceptorRule operatorTeamMembershipInterceptorRule;

  @Test
  void hasRequiredTeamMembershipPermissions_whenIsIndustrySubmitterInResponsibleTeam_assertRulePass() {
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);

    var expectedOrganisationUnitId = 1;
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(expectedOrganisationUnitId);
    var teamUUID = UUID.randomUUID();
    var team = new Team(teamUUID);
    var orgGroupIds = List.of(expectedOrganisationUnitId);
    var orgGroupStrings = orgGroupIds.stream().map(String::valueOf).toList();
    var teamRole = new TeamRole();
    teamRole.setTeam(team);
    teamRole.setRole(Role.INDUSTRY_SUBMIT);
    teamRole.setWuaId(user.wuaId());
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
            .thenReturn(Optional.of(resourceDetail));
    when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(expectedOrganisationUnitId))
            .thenReturn(orgGroupIds);
    when(teamQueryService.getOrganisationScopedTeams(orgGroupStrings))
            .thenReturn(Set.of(team));
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(teamRole));
    var securityRuleResult = operatorTeamMembershipInterceptorRule
        .hasRequiredResourceAccessPermissions(
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isTrue();
  }

  @Test
  void hasRequiredTeamMembershipPermissions_whenIsIndustrySubmitterInResponsibleTeam_fieldSubmission_assertRulePass() {
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);

    var expectedOrganisationUnitId = 1;
    var field = new Field();
    field.setPrePearsOrganisationId(expectedOrganisationUnitId);
    var teamUUID = UUID.randomUUID();
    var team = new Team(teamUUID);
    var orgGroupIds = List.of(expectedOrganisationUnitId);
    var orgGroupStrings = orgGroupIds.stream().map(String::valueOf).toList();
    var teamRole = new TeamRole();
    teamRole.setTeam(team);
    teamRole.setRole(Role.INDUSTRY_SUBMIT);
    teamRole.setWuaId(user.wuaId());
    when(fieldPersistenceService.getFieldOrThrow(submissionDetail.getSubmission().getResource()))
            .thenReturn(field);
    when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(expectedOrganisationUnitId))
            .thenReturn(orgGroupIds);
    when(teamQueryService.getOrganisationScopedTeams(orgGroupStrings))
            .thenReturn(Set.of(team));
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(teamRole));
    var securityRuleResult = operatorTeamMembershipInterceptorRule
        .hasRequiredResourceAccessPermissions(
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isTrue();
  }

  @Test
  void hasRequiredTeamMembershipPermissions_whenIsNotIndustrySubmitterInResponsibleTeam_assertRuleFail() {
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);

    var unexpectedOrganisationUnitId = 1;
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(unexpectedOrganisationUnitId);
    var team = new Team(UUID.randomUUID());
    var orgGroupIds = List.of(unexpectedOrganisationUnitId);
    var orgGroupStrings = orgGroupIds.stream().map(String::valueOf).toList();
    var otherTeam = new Team(UUID.randomUUID());
    var teamRole = new TeamRole();
    teamRole.setTeam(otherTeam);
    teamRole.setRole(Role.INDUSTRY_SUBMIT);
    teamRole.setWuaId(user.wuaId());
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
            .thenReturn(Optional.of(resourceDetail));
    when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(unexpectedOrganisationUnitId))
            .thenReturn(orgGroupIds);
    when(teamQueryService.getOrganisationScopedTeams(orgGroupStrings))
            .thenReturn(Set.of(team));
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(teamRole));
    var securityRuleResult = operatorTeamMembershipInterceptorRule
        .hasRequiredResourceAccessPermissions(
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isFalse();
    assertThat(securityRuleResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void hasRequiredTeamMembershipPermissions_whenSubmissionIsNotIndustry_assertRuleFailForbidden() {
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setSubmitterType(SubmitterType.REGULATOR);

    var securityRuleResult = operatorTeamMembershipInterceptorRule
        .hasRequiredResourceAccessPermissions(user, submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isFalse();
    assertThat(securityRuleResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }
}