package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.onshore;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsMemberOfTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.AreaTeamManagementControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Controller
@RequestMapping("/permission-management/onshore-area")
@IsMemberOfTeam
public class OnshoreAreaTeamManagementController extends AbstractOnshoreAreaPermissionManagement {

  private final AreaTeamManagementControllerHelperService areaTeamManagementControllerHelperService;

  @Autowired
  OnshoreAreaTeamManagementController(OnshoreAreaTeamService onshoreAreaTeamService,
                                      AreaTeamManagementControllerHelperService areaTeamManagementControllerHelperService) {
    super(onshoreAreaTeamService);
    this.areaTeamManagementControllerHelperService = areaTeamManagementControllerHelperService;
  }

  @GetMapping("/{teamId}")
  public ModelAndView renderMemberList(@PathVariable("teamId") TeamId teamId) {
    var team = getAreaTeam(teamId);

    return areaTeamManagementControllerHelperService.renderMemberList(team,
        Set.of(RolePermission.GRANT_ONSHORE_AREA_ROLES),
        ReverseRouter.route(on(OnshoreAreaAddMemberController.class).renderAddTeamMember(teamId)),
        OnshoreAreaTeamRole.values());
  }
}