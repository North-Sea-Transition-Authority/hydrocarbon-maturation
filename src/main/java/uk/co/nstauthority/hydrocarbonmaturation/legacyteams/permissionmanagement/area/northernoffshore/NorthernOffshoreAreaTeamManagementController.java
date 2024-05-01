package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.northernoffshore;

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
@RequestMapping("/permission-management/northern-offshore-area")
@IsMemberOfTeam
public class NorthernOffshoreAreaTeamManagementController extends AbstractNorthernOffshoreAreaPermissionManagement {

  private final AreaTeamManagementControllerHelperService areaTeamManagementControllerHelperService;

  @Autowired
  NorthernOffshoreAreaTeamManagementController(NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService,
                                               AreaTeamManagementControllerHelperService
                                                   areaTeamManagementControllerHelperService) {
    super(northernOffshoreAreaTeamService);
    this.areaTeamManagementControllerHelperService = areaTeamManagementControllerHelperService;
  }

  @GetMapping("/{teamId}")
  public ModelAndView renderMemberList(@PathVariable("teamId") TeamId teamId) {

    var team = getAreaTeam(teamId);

    return areaTeamManagementControllerHelperService.renderMemberList(team,
        Set.of(RolePermission.GRANT_NORTHERN_OFFSHORE_AREA_ROLES),
        ReverseRouter.route(on(NorthernOffshoreAreaAddMemberController.class).renderAddTeamMember(teamId)),
        NorthernOffshoreAreaTeamRole.values());
  }
}
