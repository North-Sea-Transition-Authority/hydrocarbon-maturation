package uk.co.nstauthority.hydrocarbonmaturation.teams.management.view;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import uk.co.fivium.energyportalapi.generated.types.User;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.TeamManagementController;

public record TeamMemberView(
    Long wuaId,
    String forename,
    String surname,
    String email,
    String telNo,
    UUID teamId,
    List<Role> roles
) {
  public String getDisplayName() {
    return Stream.of(forename, surname)
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.joining(" "));
  }

  public String getEditUrl() {
    return ReverseRouter.route(on(TeamManagementController.class).renderUserTeamRoles(teamId, wuaId, null));
  }

  public String getRemoveUrl() {
    return ReverseRouter.route(on(TeamManagementController.class).renderRemoveTeamMember(teamId, wuaId));
  }

  public static TeamMemberView fromEpaUser(User user, UUID teamId, List<Role> roles) {
    return new TeamMemberView(
        user.getWebUserAccountId().longValue(),
        user.getForename(),
        user.getSurname(),
        user.getPrimaryEmailAddress(),
        user.getTelephoneNumber(),
        teamId,
        roles
    );
  }
}