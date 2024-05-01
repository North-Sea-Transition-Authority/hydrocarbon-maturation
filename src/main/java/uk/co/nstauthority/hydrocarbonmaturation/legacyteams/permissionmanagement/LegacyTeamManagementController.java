package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamView;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.industry.NewIndustryTeamController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;


@Controller
@RequestMapping("/permission-management")
@AccessibleByServiceUsers
public class LegacyTeamManagementController {

  public static final String NEW_INDUSTRY_TEAM_FORM_URL =
      ReverseRouter.route(on(NewIndustryTeamController.class).renderNewIndustryTeamForm(null));
  private final LegacyTeamManagementService legacyTeamManagementService;
  private final UserService userService;

  @Autowired
  LegacyTeamManagementController(LegacyTeamManagementService legacyTeamManagementService,
                                 UserService userService) {
    this.legacyTeamManagementService = legacyTeamManagementService;
    this.userService = userService;
  }

  @GetMapping
  public ModelAndView renderTeamListOrRedirect() {
    var user = userService.getUser();

    var teams = legacyTeamManagementService.getTeamsThatUserHasAccessTo(user);
    var teamMap = new TreeMap<>(teams.stream().collect(Collectors.groupingBy(TeamView::teamType)));

    if (teams.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "User with ID %s is not a member of a team".formatted(user)
      );
    }

    var userIsIndustryAccessManagerInRegulatorTeam = legacyTeamManagementService.isIndustryAccessManagerInRegulatorTeam(user);

    if (teams.size() == 1 && !userIsIndustryAccessManagerInRegulatorTeam) {
      var team = teams.get(0);
      return ReverseRouter.redirect(TeamManagementUtil.getRenderMemberListRoute(team.teamId(), team.teamType()));
    }

    var modelAndView = new ModelAndView("remi/permissionmanagement/teamList")
        .addObject("pageTitle", "Choose team to manage")
        .addObject("teamGroupMap", teamMap);

    if (userIsIndustryAccessManagerInRegulatorTeam) {
      modelAndView
          .addObject("newIndustryTeamFormUrl", NEW_INDUSTRY_TEAM_FORM_URL);
    }

    return modelAndView;
  }
}