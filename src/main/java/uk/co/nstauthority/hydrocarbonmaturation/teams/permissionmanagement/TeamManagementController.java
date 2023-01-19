package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamManagementController;


@Controller
@RequestMapping("/permission-management")
@AccessibleByServiceUsers
public class TeamManagementController {

  public static final String NEW_TEAM_FORM_URL =
      ReverseRouter.route(on(IndustryTeamManagementController.class).renderNewIndustryTeamForm(null));
  private final TeamManagementService teamManagementService;
  private final UserDetailService userDetailService;

  @Autowired
  TeamManagementController(TeamManagementService teamManagementService,
                           UserDetailService userDetailService) {
    this.teamManagementService = teamManagementService;
    this.userDetailService = userDetailService;
  }

  @GetMapping
  public ModelAndView renderTeamListOrRedirect() {
    var user = userDetailService.getUserDetail();
    
    var teams = teamManagementService.getTeamsThatUserHasAccessTo(user);

    if (teams.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "User with ID %s is not a member of a team".formatted(user)
      );
    }

    if (teams.size() == 1) {
      var team = teams.get(0);
      return ReverseRouter.redirect(TeamManagementUtil.getRenderMemberListRoute(team.teamId(), team.teamType()));
    }
    var modelAndView = new ModelAndView("remi/permissionmanagement/teamList")
        .addObject("pageTitle", "Choose team to manage")
        .addObject("allTeams", teams);

    if (teamManagementService.isIndustryAccessManagerInRegulatorTeam(user)) {
      modelAndView
          .addObject("newTeamFormUrl", NEW_TEAM_FORM_URL);
    }

    return modelAndView;
  }
}