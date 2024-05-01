package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.southernoffshore;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasTeamPermission;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.AreaTeamManagementControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Controller
@RequestMapping("/permission-management/southern-offshore-area")
@HasTeamPermission(
    anyTeamPermissionOf = {
        RolePermission.GRANT_SOUTHERN_OFFSHORE_AREA_ROLES,
        RolePermission.VIEW_SYSTEM_OF_RECORD
    }
)
public class SouthernOffshoreAreaTeamManagementController extends AbstractSouthernOffshoreAreaPermissionManagement {

  private final AreaTeamManagementControllerHelperService areaTeamManagementControllerHelperService;

  @Autowired
  SouthernOffshoreAreaTeamManagementController(SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService,
                                               AreaTeamManagementControllerHelperService
                                                   areaTeamManagementControllerHelperService) {
    super(southernOffshoreAreaTeamService);
    this.areaTeamManagementControllerHelperService = areaTeamManagementControllerHelperService;
  }

  @GetMapping("/{teamId}")
  public ModelAndView renderMemberList(@PathVariable("teamId") TeamId teamId) {

    var team = getAreaTeam(teamId);

    return areaTeamManagementControllerHelperService.renderMemberList(team,
        Set.of(RolePermission.GRANT_SOUTHERN_OFFSHORE_AREA_ROLES),
        ReverseRouter.route(on(SouthernOffshoreAreaAddMemberController.class).renderAddTeamMember(teamId)),
        SouthernOffshoreAreaTeamRole.values());
  }
}
