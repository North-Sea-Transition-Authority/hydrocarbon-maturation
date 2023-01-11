package uk.co.nstauthority.hydrocarbonmaturation.teams;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Set;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryEditMemberController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryRemoveMemberController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorEditMemberController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorRemoveMemberController;

public record TeamMemberView(WebUserAccountId wuaId, TeamView teamView, String title, String firstName,
                             String lastName, String contactEmail, String contactNumber,
                             Set<TeamRole> teamRoles) {

  public String getDisplayName() {
    return "%s %s".formatted(firstName, lastName);
  }

  public String removeUrl() {
    return switch (teamView.teamType()) {
      case REGULATOR -> ReverseRouter.route(on(RegulatorRemoveMemberController.class)
          .renderRemoveMember(teamView.teamId(), wuaId));
      case INDUSTRY -> ReverseRouter.route(on(IndustryRemoveMemberController.class)
          .renderRemoveMember(teamView.teamId(), wuaId));
    };
  }

  public String editUrl() {
    return switch (teamView.teamType()) {
      case REGULATOR -> ReverseRouter.route(on(RegulatorEditMemberController.class)
          .renderEditMember(teamView.teamId(), wuaId));
      case INDUSTRY -> ReverseRouter.route(on(IndustryEditMemberController.class)
          .renderEditMember(teamView.teamId(), wuaId));
    };
  }
}
