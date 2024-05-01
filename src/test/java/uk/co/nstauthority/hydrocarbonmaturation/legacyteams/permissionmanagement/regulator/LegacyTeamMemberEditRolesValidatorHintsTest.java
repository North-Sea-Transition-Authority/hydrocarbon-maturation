package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator;

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
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMember;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberRemovalService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamMemberEditRolesValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamMemberRolesForm;

@ExtendWith(MockitoExtension.class)
class LegacyTeamMemberEditRolesValidatorHintsTest {

  @Mock
  private TeamMemberRemovalService regulatorTeamMemberRemovalService;

  @InjectMocks
  private RegulatorTeamMemberEditRolesValidator regulatorTeamMemberEditRolesValidator;

  @Test
  void validate_whenValid_thenNoErrors() {
    var form = new TeamMemberRolesForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    form.setRoles(Set.of(RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER.name()));

    var team = new LegacyTeam(UUID.randomUUID());
    var teamView = TeamTestUtil.createTeamView(team);
    var teamMember = new TeamMember(new WebUserAccountId(123L), teamView,
        Set.of(RegulatorTeamRole.ACCESS_MANAGER));

    when(regulatorTeamMemberRemovalService.canRemoveTeamMember(team, teamMember)).thenReturn(true);

    var dto = new TeamMemberEditRolesValidatorHints(team, teamMember);
    regulatorTeamMemberEditRolesValidator.validate(form, bindingResult, dto);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenNoRolesSelect_thenHasMatchingError() {
    var form = new TeamMemberRolesForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var team = new LegacyTeam(UUID.randomUUID());
    var teamView = TeamTestUtil.createTeamView(team);
    var teamMember = new TeamMember(new WebUserAccountId(123L), teamView,
        Set.of(RegulatorTeamRole.ACCESS_MANAGER));

    var dto = new TeamMemberEditRolesValidatorHints(team, teamMember);
    regulatorTeamMemberEditRolesValidator.validate(form, bindingResult, dto);

    assertTrue(bindingResult.hasErrors());
    assertThat(bindingResult.getFieldErrors()).extracting(
        FieldError::getField,
        DefaultMessageSourceResolvable::getCode,
        DefaultMessageSourceResolvable::getDefaultMessage
    ).containsExactly(
        Tuple.tuple(
            RegulatorTeamMemberEditRolesValidator.ROLES_FIELD_NAME,
            RegulatorTeamMemberEditRolesValidator.ROLES_FIELD_REQUIRED_ERROR_CODE,
            RegulatorTeamMemberEditRolesValidator.ROLES_FIELD_REQUIRED_ERROR_MESSAGE
        )
    );
  }

  @Test
  void validate_whenLastAccessManager_andUpdateHasNoAccessManagerRole_thenHasMatchingError() {
    var form = new TeamMemberRolesForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    form.setRoles(Set.of(RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER.name()));

    var team = new LegacyTeam(UUID.randomUUID());
    var teamView = TeamTestUtil.createTeamView(team);
    var teamMember = new TeamMember(new WebUserAccountId(123L), teamView,
        Set.of(RegulatorTeamRole.ACCESS_MANAGER));

    when(regulatorTeamMemberRemovalService.canRemoveTeamMember(team, teamMember)).thenReturn(false);

    var dto = new TeamMemberEditRolesValidatorHints(team, teamMember);
    regulatorTeamMemberEditRolesValidator.validate(form, bindingResult, dto);

    assertTrue(bindingResult.hasErrors());
    assertThat(bindingResult.getFieldErrors()).extracting(
        FieldError::getField,
        DefaultMessageSourceResolvable::getCode,
        DefaultMessageSourceResolvable::getDefaultMessage
    ).containsExactly(
        Tuple.tuple(
            RegulatorTeamMemberEditRolesValidator.ROLES_FIELD_NAME,
            RegulatorTeamMemberEditRolesValidator.ROLES_NO_ACCESS_MANAGER_ERROR_CODE,
            RegulatorTeamMemberEditRolesValidator.ROLES_NO_ACCESS_MANAGER_ERROR_MESSAGE
        )
    );
  }

  @Test
  void supports_whenCorrectClass_thenSupports() {
    assertTrue(regulatorTeamMemberEditRolesValidator.supports(TeamMemberRolesForm.class));
  }

  @Test
  void supports_whenIncorrectClass_thenDoesNotSupport() {
    assertFalse(regulatorTeamMemberEditRolesValidator.supports(UnsupportedClass.class));
  }

  @Test
  void validate_whenCalledWithTwoArguments_thenThrowsError() {
    var form = new TeamMemberRolesForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertThrows(IllegalCallerException.class, () ->
        regulatorTeamMemberEditRolesValidator.validate(form, bindingResult)
    );
  }

  static class UnsupportedClass {
  }
}