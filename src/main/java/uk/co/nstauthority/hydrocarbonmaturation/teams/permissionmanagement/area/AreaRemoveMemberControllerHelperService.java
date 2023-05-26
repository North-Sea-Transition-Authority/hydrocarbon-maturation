package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMember;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRemovalService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberViewService;

@Service
public class AreaRemoveMemberControllerHelperService {

  private final TeamMemberService teamMemberService;
  private final TeamMemberViewService teamMemberViewService;
  private final TeamMemberRemovalService teamMemberRemovalService;

  @Autowired
  public AreaRemoveMemberControllerHelperService(TeamMemberService teamMemberService,
                                                 TeamMemberViewService teamMemberViewService,
                                                 TeamMemberRemovalService teamMemberRemovalService) {
    this.teamMemberService = teamMemberService;
    this.teamMemberViewService = teamMemberViewService;
    this.teamMemberRemovalService = teamMemberRemovalService;
  }

  public ModelAndView renderRemoveMember(Team team, WebUserAccountId wuaId,
                                         ModelAndView redirectModelAndView, String backLinkUrl, String removeUrl) {
    var teamMemberOptional = teamMemberService.getTeamMember(team, wuaId);

    if (teamMemberOptional.isEmpty()) {
      return redirectModelAndView;
    }

    var teamMember = teamMemberOptional.get();

    var teamMemberView = getTeamMemberViewOrThrow(teamMember);
    var teamName = teamMember.teamView().displayName();

    var canRemoveTeamMember = teamMemberRemovalService.canRemoveTeamMember(team, teamMember);

    return new ModelAndView("remi/permissionmanagement/removeTeamMember")
        .addObject("pageTitle", getPageTitle(canRemoveTeamMember, teamMemberView, teamName))
        .addObject("teamName", teamName)
        .addObject("teamMember", teamMemberView)
        .addObject("canRemoveTeamMember", canRemoveTeamMember)
        .addObject("backLinkUrl", backLinkUrl)
        .addObject("removeUrl", removeUrl);
  }

  public ModelAndView removeMember(Team team, WebUserAccountId wuaId, ModelAndView renderRemoveMemberRedirectModelAndView,
                                   RedirectAttributes redirectAttributes, ModelAndView redirectModelAndView) {

    var teamMember = getTeamMemberOrThrow(team, wuaId);

    if (!teamMemberRemovalService.canRemoveTeamMember(team, teamMember)) {
      return renderRemoveMemberRedirectModelAndView
          .addObject("singleErrorMessage", TeamMemberRemovalService.LAST_ACCESS_MANAGER_ERROR_MESSAGE);
    }

    teamMemberRemovalService.removeTeamMember(team, teamMember);

    var userView = getTeamMemberViewOrThrow(teamMember);

    var banner = NotificationBanner.builder()
        .withTitle("Removed member from team")
        .withBannerType(NotificationBannerType.SUCCESS)
        .withContent("%s has been removed from the team".formatted(userView.getDisplayName()))
        .build();

    NotificationBannerUtil.applyNotificationBanner(redirectAttributes, banner);

    return redirectModelAndView;
  }

  private TeamMember getTeamMemberOrThrow(Team team, WebUserAccountId wuaId) {
    return teamMemberService.getTeamMember(team, wuaId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No user [%s] in team [%s]".formatted(wuaId, team.toTeamId())));
  }

  private TeamMemberView getTeamMemberViewOrThrow(TeamMember teamMember) {
    return teamMemberViewService.getTeamMemberView(teamMember)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No roles found for user [%s] in team [%s]".formatted(teamMember.wuaId(), teamMember.teamView().teamId())));
  }

  private String getPageTitle(boolean canRemoveMember, TeamMemberView teamMemberView, String teamName) {
    return canRemoveMember
        ? "Are you sure you want to remove %s from %s?".formatted(teamMemberView.getDisplayName(), teamName)
        : "You are unable to remove %s from %s".formatted(teamMemberView.getDisplayName(), teamName);
  }
}
