package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMember;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRemovalService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamMemberEditRolesValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamMemberRolesForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidationUtils;

@Service
class RegulatorTeamMemberEditRolesValidator implements SmartValidator {

  public static final String ROLES_FIELD_NAME = "roles";
  public static final String ROLES_FIELD_REQUIRED_ERROR_CODE = "%s.required".formatted(ROLES_FIELD_NAME);
  public static final String ROLES_FIELD_REQUIRED_ERROR_MESSAGE = "Select at least one role";

  public static final String ROLES_NO_ACCESS_MANAGER_ERROR_CODE =
      "%s.accessManagerRequired".formatted(ROLES_FIELD_NAME);

  public static final String ROLES_NO_ACCESS_MANAGER_ERROR_MESSAGE =
      "There must always be at least one access manager in the team";

  private final TeamMemberRemovalService teamMemberRemovalService;

  @Autowired
  RegulatorTeamMemberEditRolesValidator(
      TeamMemberRemovalService teamMemberRemovalService) {
    this.teamMemberRemovalService = teamMemberRemovalService;
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (TeamMemberRolesForm) target;
    var validatorHints = SubmissionFormValidationUtils.extractEditRolesValidatorHints(validationHints);

    Set<String> formRoles = form.getRoles() != null ? form.getRoles() : Set.of();

    if (formRoles.isEmpty()) {
      errors.rejectValue(ROLES_FIELD_NAME, ROLES_FIELD_REQUIRED_ERROR_CODE, ROLES_FIELD_REQUIRED_ERROR_MESSAGE);
      return;
    }

    var newRoles = formRoles
        .stream()
        .map(RegulatorTeamRole::valueOf)
        .toList();

    if (!canUpdateTeamMemberWithNewRoles(validatorHints.getTeam(), validatorHints.getTeamMember(), newRoles)) {
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
                                                  Collection<RegulatorTeamRole> newRoles) {
    var isRemovingAccessManagerRole = Sets.difference(teamMember.roles(), Set.copyOf(newRoles))
        .contains(RegulatorTeamRole.ACCESS_MANAGER);

    return !isRemovingAccessManagerRole || teamMemberRemovalService.canRemoveTeamMember(team, teamMember);
  }
}
