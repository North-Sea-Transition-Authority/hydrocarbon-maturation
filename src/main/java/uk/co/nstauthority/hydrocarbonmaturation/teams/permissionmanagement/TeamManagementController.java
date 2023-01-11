package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
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
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamManagementController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.util.StreamUtil;

@Controller
@RequestMapping("/permission-management")
@AccessibleByServiceUsers
public class TeamManagementController {

  private final IndustryTeamService industryTeamService;
  private final RegulatorTeamService regulatorTeamService;
  private final UserDetailService userDetailService;

  @Autowired
  TeamManagementController(IndustryTeamService industryTeamService,
                           RegulatorTeamService regulatorTeamService,
                           UserDetailService userDetailService) {
    this.industryTeamService = industryTeamService;
    this.regulatorTeamService = regulatorTeamService;
    this.userDetailService = userDetailService;
  }

  @GetMapping
  public ModelAndView renderTeamTypesOrRedirect() {

    var userDetail = userDetailService.getUserDetail();
    var regulatorTeam = regulatorTeamService.getRegulatorTeamForUser(userDetail);
    var industryTeams = industryTeamService.getIndustryTeamsForUser(userDetail);
    var userIsInRegulatorTeam = regulatorTeam.isPresent();
    var userIsInIndustryTeam = !industryTeams.isEmpty();

    if (userIsInRegulatorTeam && userIsInIndustryTeam) {
      return getTeamTypesModelAndView();
    }

    if (userIsInRegulatorTeam) {
      var teamId = TeamId.valueOf(regulatorTeam.get().getUuid());

      if (regulatorTeamService.isIndustryAccessManager(teamId, userDetail)) {
        return getTeamTypesModelAndView();
      }

      return ReverseRouter.redirect(on(RegulatorTeamManagementController.class).renderMemberList(teamId));
    }

    if (userIsInIndustryTeam) {
      return ReverseRouter.redirect(on(IndustryTeamManagementController.class).renderTeamList());
    }

    throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "User with ID %s is not a member of regulator or industry team".formatted(userDetail)
    );
  }

  private ModelAndView getTeamTypesModelAndView() {
    var teamTypeRouteMap = Arrays.stream(TeamType.values())
        .sorted(Comparator.comparingInt(TeamType::getDisplayOrder))
        .collect(StreamUtil.toLinkedHashMap(Function.identity(), ManagementRouteUtil::getRouteForTeamType));

    return new ModelAndView("remi/permissionmanagement/teamTypes")
        .addObject("pageTitle", "Manage teams")
        .addObject("teamTypeRouteMap", teamTypeRouteMap);
  }
}