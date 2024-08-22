package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRoleTestUtil;

@ExtendWith(MockitoExtension.class)
class SubmissionTypeSelectServiceTest {

  private static SubmissionTypeSelectFormValidator submissionTypeSelectFormValidator;
  private static SubmissionTypeSelectService submissionTypeSelectService;
  private static TeamQueryService teamQueryService;


  private static final User USER = UserTestUtil.Builder().build();

  @BeforeAll
  static void setup() {
    submissionTypeSelectFormValidator = mock(SubmissionTypeSelectFormValidator.class);
    teamQueryService = mock(TeamQueryService.class);
    submissionTypeSelectService = new SubmissionTypeSelectService(submissionTypeSelectFormValidator, teamQueryService);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new SubmissionTypeSelectForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionTypeSelectService.validate(form, bindingResult);
    verify(submissionTypeSelectFormValidator, times(1)).validate(form, bindingResult);
  }

  @Test
  void getSubmissionTypeOptions_discovery_exploration() {
    var teamRole = TeamRoleTestUtil.Builder().withRole(Role.EXPLORATION).build();
    when(teamQueryService.getTeamRolesForUser(USER.wuaId())).thenReturn(Set.of(teamRole));

    assertThat(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.DISCOVERY, USER))
        .containsExactly(
            SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
            SubmissionType.CREATE_DISCOVERY,
            SubmissionType.UPDATE_DISCOVERY,
            SubmissionType.RELINQUISHMENT_DISCOVERY);
  }

  @Test
  void getSubmissionTypeOptions_discovery_industry() {
    var teamRole = TeamRoleTestUtil.Builder().withRole(Role.INDUSTRY_SUBMIT).build();
    when(teamQueryService.getTeamRolesForUser(USER.wuaId())).thenReturn(Set.of(teamRole));

    assertThat(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.DISCOVERY, USER))
        .containsExactly(
            SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
            SubmissionType.CREATE_DISCOVERY,
            SubmissionType.UPDATE_DISCOVERY,
            SubmissionType.RELINQUISHMENT_DISCOVERY);
  }

  @Test
  void getSubmissionTypeOptions_discovery_areaTeam() {
    var teamRole = TeamRoleTestUtil.Builder().withRole(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM).build();
    when(teamQueryService.getTeamRolesForUser(USER.wuaId())).thenReturn(Set.of(teamRole));

    assertThat(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.DISCOVERY, USER))
        .containsExactly(
            SubmissionType.UPDATE_DISCOVERY,
            SubmissionType.RELINQUISHMENT_DISCOVERY);
  }

  @Test
  void getSubmissionTypeOptions_prospect_exploration() {
    var teamRole = TeamRoleTestUtil.Builder().withRole(Role.EXPLORATION).build();
    when(teamQueryService.getTeamRolesForUser(USER.wuaId())).thenReturn(Set.of(teamRole));

    assertThat(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.PROSPECT, USER))
        .containsExactly(
            SubmissionType.CREATE_PROSPECT,
            SubmissionType.PROSPECT_UPDATE,
            SubmissionType.POST_DRILL_DRY_HOLE,
            SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
            SubmissionType.RELINQUISHMENT_PROSPECT);
  }

  @Test
  void getSubmissionTypeOptions_prospect_industry() {
    var teamRole = TeamRoleTestUtil.Builder().withRole(Role.INDUSTRY_SUBMIT).build();
    when(teamQueryService.getTeamRolesForUser(USER.wuaId())).thenReturn(Set.of(teamRole));

    assertThat(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.PROSPECT, USER))
        .containsExactly(
            SubmissionType.CREATE_PROSPECT,
            SubmissionType.PROSPECT_UPDATE,
            SubmissionType.POST_DRILL_DRY_HOLE,
            SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
            SubmissionType.RELINQUISHMENT_PROSPECT);
  }

  @Test
  void getSubmissionTypeOptions_prospect_areaTeam() {
    var teamRole = TeamRoleTestUtil.Builder().withRole(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM).build();
    when(teamQueryService.getTeamRolesForUser(USER.wuaId())).thenReturn(Set.of(teamRole));

    assertThat(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.PROSPECT, USER)).isEmpty();
  }

  @Test
  void getSubmissionTypeOptions_field_exploration() {
    var teamRole = TeamRoleTestUtil.Builder().withRole(Role.EXPLORATION).build();
    when(teamQueryService.getTeamRolesForUser(USER.wuaId())).thenReturn(Set.of(teamRole));

    assertThat(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.FIELD, USER)).isEmpty();
  }

  @Test
  void getSubmissionTypeOptions_field_industry() {
    var teamRole = TeamRoleTestUtil.Builder().withRole(Role.INDUSTRY_SUBMIT).build();
    when(teamQueryService.getTeamRolesForUser(USER.wuaId())).thenReturn(Set.of(teamRole));

    assertThat(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.FIELD, USER))
        .containsOnly(
            SubmissionType.CREATE_CSR,
            SubmissionType.CREATE_FDP,
            SubmissionType.CREATE_FDPA,
            SubmissionType.CREATE_COP);
  }

  @Test
  void getSubmissionTypeOptions_field_areaTeam() {
    var teamRole = TeamRoleTestUtil.Builder().withRole(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM).build();
    when(teamQueryService.getTeamRolesForUser(USER.wuaId())).thenReturn(Set.of(teamRole));

    assertThat(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.FIELD, USER))
        .containsOnly(
            SubmissionType.CREATE_FIELD,
            SubmissionType.CREATE_CSR,
            SubmissionType.CREATE_FDP,
            SubmissionType.CREATE_FDPA,
            SubmissionType.CREATE_COP);
  }

  @ParameterizedTest
  @EnumSource(value = Role.class, mode = EnumSource.Mode.EXCLUDE, names = {"INDUSTRY_SUBMIT", "EXPLORATION", "MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM"})
  void getSubmissionTypeOptions_unexpectedRoles(Role role) {
    var teamRole = TeamRoleTestUtil.Builder().withRole(role).build();
    when(teamQueryService.getTeamRolesForUser(USER.wuaId())).thenReturn(Set.of(teamRole));
    assertThat(submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.FIELD, USER)).isEmpty();
  }
}