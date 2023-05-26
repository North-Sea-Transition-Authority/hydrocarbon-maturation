package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMember;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRoleUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamMemberRolesForm;

@Service
public class AreaEditMemberControllerHelperService {

  private final TeamMemberService teamMemberService;
  private final TeamMemberViewService teamMemberViewService;

  @Autowired
  public AreaEditMemberControllerHelperService(TeamMemberService teamMemberService,
                                               TeamMemberViewService teamMemberViewService) {
    this.teamMemberService = teamMemberService;
    this.teamMemberViewService = teamMemberViewService;
  }

  public ModelAndView renderEditMember(Team team, WebUserAccountId wuaId,
                                       Map<String, String> roleMap, String backLinkUrl) {
    var teamMember = getTeamMemberOrThrow(team, wuaId);
    var userView = getTeamMemberViewOrThrow(teamMember);

    var form = new TeamMemberRolesForm();
    form.setRoles(TeamRoleUtil.getRoleNames(teamMember.roles()));
    return getEditModelAndView(userView, form, roleMap, backLinkUrl);
  }

  public ModelAndView getEditModelAndView(TeamMemberView userView, TeamMemberRolesForm form,
                                           Map<String, String> roleMap, String backLinkUrl) {
    return new ModelAndView("remi/permissionmanagement/addTeamMemberRoles")
        .addObject("form", form)
        .addObject("pageTitle", userView.getDisplayName())
        .addObject("roles", roleMap)
        .addObject("backLinkUrl", backLinkUrl);
  }

  public TeamMember getTeamMemberOrThrow(Team team, WebUserAccountId wuaId) {
    return teamMemberService.getTeamMember(team, wuaId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No user [%s] in team [%s]".formatted(wuaId, team.toTeamId())));
  }

  public TeamMemberView getTeamMemberViewOrThrow(TeamMember teamMember) {
    return teamMemberViewService.getTeamMemberView(teamMember)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No roles found for user [%s] in team [%s]".formatted(teamMember.wuaId(), teamMember.teamView().teamId())));
  }
}
