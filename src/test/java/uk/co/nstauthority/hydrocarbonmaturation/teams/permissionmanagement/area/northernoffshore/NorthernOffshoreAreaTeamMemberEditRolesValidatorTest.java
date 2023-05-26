package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.northernoffshore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMember;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRemovalService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamMemberEditRolesValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamMemberRolesForm;

@ExtendWith(MockitoExtension.class)
class NorthernOffshoreAreaTeamMemberEditRolesValidatorTest {

  @Mock
  private TeamMemberRemovalService teamMemberRemovalService;

  @InjectMocks
  private NorthernOffshoreAreaTeamMemberEditRolesValidator northernOffshoreAreaTeamMemberEditRolesValidator;

  @Test
  void validate_whenValid_thenNoErrors() {
    var form = new TeamMemberRolesForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    form.setRoles(Set.of(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER.name()));

    var team = new Team(UUID.randomUUID());
    var teamView = TeamTestUtil.createTeamView(team);
    var teamMember = new TeamMember(new WebUserAccountId(123L), teamView,
        Set.of(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_MANAGE_TEAM));

    when(teamMemberRemovalService.canRemoveTeamMember(team, teamMember)).thenReturn(true);

    var dto = new TeamMemberEditRolesValidatorHints(team, teamMember);
    northernOffshoreAreaTeamMemberEditRolesValidator.validate(form, bindingResult, dto);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenNoRolesSelect_thenHasMatchingError() {
    var form = new TeamMemberRolesForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var team = new Team(UUID.randomUUID());
    var teamView = TeamTestUtil.createTeamView(team);
    var teamMember = new TeamMember(new WebUserAccountId(123L), teamView,
        Set.of(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_MANAGE_TEAM));

    var dto = new TeamMemberEditRolesValidatorHints(team, teamMember);
    northernOffshoreAreaTeamMemberEditRolesValidator.validate(form, bindingResult, dto);

    assertTrue(bindingResult.hasErrors());
    assertThat(bindingResult.getFieldErrors()).extracting(
        FieldError::getField,
        DefaultMessageSourceResolvable::getCode,
        DefaultMessageSourceResolvable::getDefaultMessage
    ).containsExactly(
        Tuple.tuple(
            NorthernOffshoreAreaTeamMemberEditRolesValidator.ROLES_FIELD_NAME,
            NorthernOffshoreAreaTeamMemberEditRolesValidator.ROLES_FIELD_REQUIRED_ERROR_CODE,
            NorthernOffshoreAreaTeamMemberEditRolesValidator.ROLES_FIELD_REQUIRED_ERROR_MESSAGE
        )
    );
  }

  @Test
  void validate_whenLastAccessManager_andUpdateHasNoAccessManagerRole_thenHasMatchingError() {
    var form = new TeamMemberRolesForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    form.setRoles(Set.of(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER.name()));

    var team = new Team(UUID.randomUUID());
    var teamView = TeamTestUtil.createTeamView(team);
    var teamMember = new TeamMember(new WebUserAccountId(123L), teamView,
        Set.of(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_MANAGE_TEAM));

    when(teamMemberRemovalService.canRemoveTeamMember(team, teamMember)).thenReturn(false);

    var dto = new TeamMemberEditRolesValidatorHints(team, teamMember);
    northernOffshoreAreaTeamMemberEditRolesValidator.validate(form, bindingResult, dto);

    assertTrue(bindingResult.hasErrors());
    assertThat(bindingResult.getFieldErrors()).extracting(
        FieldError::getField,
        DefaultMessageSourceResolvable::getCode,
        DefaultMessageSourceResolvable::getDefaultMessage
    ).containsExactly(
        Tuple.tuple(
            NorthernOffshoreAreaTeamMemberEditRolesValidator.ROLES_FIELD_NAME,
            NorthernOffshoreAreaTeamMemberEditRolesValidator.ROLES_NO_ACCESS_MANAGER_ERROR_CODE,
            NorthernOffshoreAreaTeamMemberEditRolesValidator.ROLES_NO_ACCESS_MANAGER_ERROR_MESSAGE
        )
    );
  }

  @Test
  void supports_whenCorrectClass_thenSupports() {
    assertTrue(northernOffshoreAreaTeamMemberEditRolesValidator.supports(TeamMemberRolesForm.class));
  }

  @Test
  void supports_whenIncorrectClass_thenDoesNotSupport() {
    assertFalse(
        northernOffshoreAreaTeamMemberEditRolesValidator.supports(NorthernOffshoreAreaTeamMemberEditRolesValidatorTest.UnsupportedClass.class));
  }

  @Test
  void validate_whenCalledWithTwoArguments_thenThrowsError() {
    var form = new TeamMemberRolesForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertThrows(IllegalCallerException.class, () ->
        northernOffshoreAreaTeamMemberEditRolesValidator.validate(form, bindingResult)
    );
  }

  static class UnsupportedClass {
  }
}