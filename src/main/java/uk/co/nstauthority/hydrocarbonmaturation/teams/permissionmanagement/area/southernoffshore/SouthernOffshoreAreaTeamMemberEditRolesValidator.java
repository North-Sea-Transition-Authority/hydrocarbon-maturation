package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.southernoffshore;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMember;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRemovalService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamMemberEditRolesValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamMemberRolesForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidationUtils;

@Service
public class SouthernOffshoreAreaTeamMemberEditRolesValidator implements SmartValidator {

  public static final String ROLES_FIELD_NAME = "roles";

  static final String ROLE_INVALID_CODE = "%s.notValid".formatted(ROLES_FIELD_NAME);
  static final String ROLES_INVALID_ERROR_MESSAGE = "Select a valid action";
  public static final String ROLES_FIELD_REQUIRED_ERROR_CODE = "%s.required".formatted(ROLES_FIELD_NAME);
  public static final String ROLES_FIELD_REQUIRED_ERROR_MESSAGE = "Select at least one action";

  public static final String ROLES_NO_ACCESS_MANAGER_ERROR_CODE =
      "%s.accessManagerRequired".formatted(ROLES_FIELD_NAME);

  public static final String ROLES_NO_ACCESS_MANAGER_ERROR_MESSAGE =
      "There must always be at least one access manager in the team";

  private final TeamMemberRemovalService teamMemberRemovalService;

  @Autowired
  public SouthernOffshoreAreaTeamMemberEditRolesValidator(TeamMemberRemovalService teamMemberRemovalService) {
    this.teamMemberRemovalService = teamMemberRemovalService;
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (TeamMemberRolesForm) target;
    var validatorDto = SubmissionFormValidationUtils
        .extractEditRolesValidatorHints(validationHints);

    Set<String> formRoles = form.getRoles() != null ? form.getRoles() : Set.of();

    if (formRoles.isEmpty()) {
      ValidationUtils.rejectIfEmpty(errors,
          ROLES_FIELD_NAME,
          ROLES_FIELD_REQUIRED_ERROR_CODE,
          ROLES_FIELD_REQUIRED_ERROR_MESSAGE);
      return;
    }

    var validRolesFromForm = formRoles
        .stream()
        .map(SouthernOffshoreAreaTeamRole::getRoleFromString)
        .flatMap(Optional::stream)
        .toList();
    if (formRoles.size() != validRolesFromForm.size()) {
      errors.rejectValue(
          ROLES_FIELD_NAME,
          ROLE_INVALID_CODE,
          ROLES_INVALID_ERROR_MESSAGE
      );
    }

    if (!canUpdateTeamMemberWithNewRoles(validatorDto.getTeam(), validatorDto.getTeamMember(), validRolesFromForm)) {
      errors.rejectValue(ROLES_FIELD_NAME, ROLES_NO_ACCESS_MANAGER_ERROR_CODE, ROLES_NO_ACCESS_MANAGER_ERROR_MESSAGE);
    }
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return TeamMemberRolesForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new IllegalCallerException(
        "This validator [%s] requires a %s validation hint"
            .formatted(
                this.getClass().getSimpleName(),
                TeamMemberEditRolesValidatorHints.class.getSimpleName()
            ));
  }

  private boolean canUpdateTeamMemberWithNewRoles(Team team, TeamMember teamMember,
                                                  Collection<SouthernOffshoreAreaTeamRole> newRoles) {
    var changingPermissions = Sets.difference(teamMember.roles(), Set.copyOf(newRoles));
    var isRemovingAccessManagerRole = changingPermissions
        .contains(SouthernOffshoreAreaTeamRole.SOUTHERN_OFFSHORE_AREA_MANAGE_TEAM);

    return !isRemovingAccessManagerRole || teamMemberRemovalService.canRemoveTeamMember(team, teamMember);
  }
}
