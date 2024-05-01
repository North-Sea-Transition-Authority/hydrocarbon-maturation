package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.southernoffshore;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasTeamPermission;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.AreaRemoveMemberControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Controller
@RequestMapping("/permission-management/southern-offshore-area/{teamId}/remove/{wuaId}")
@HasTeamPermission(
    anyTeamPermissionOf = RolePermission.GRANT_SOUTHERN_OFFSHORE_AREA_ROLES
)
public class SouthernOffshoreAreaRemoveMemberController extends AbstractSouthernOffshoreAreaPermissionManagement {

  private final AreaRemoveMemberControllerHelperService areaRemoveMemberControllerHelperService;

  @Autowired
  public SouthernOffshoreAreaRemoveMemberController(AreaRemoveMemberControllerHelperService
                                                          areaRemoveMemberControllerHelperService,
                                                    SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService) {
    super(southernOffshoreAreaTeamService);
    this.areaRemoveMemberControllerHelperService = areaRemoveMemberControllerHelperService;
  }

  @GetMapping
  public ModelAndView renderRemoveMember(@PathVariable("teamId") TeamId teamId,
                                         @PathVariable("wuaId") WebUserAccountId wuaId) {
    var team = getAreaTeam(teamId);

    return areaRemoveMemberControllerHelperService.renderRemoveMember(team, wuaId,
        ReverseRouter.redirect(on(SouthernOffshoreAreaTeamManagementController.class).renderMemberList(teamId)),
        ReverseRouter.route(on(SouthernOffshoreAreaTeamManagementController.class).renderMemberList(teamId)),
        ReverseRouter.route(on(SouthernOffshoreAreaRemoveMemberController.class).removeMember(teamId, wuaId, null)));
  }

  @PostMapping
  public ModelAndView removeMember(@PathVariable("teamId") TeamId teamId,
                                   @PathVariable("wuaId") WebUserAccountId wuaId,
                                   RedirectAttributes redirectAttributes) {
    var team = getAreaTeam(teamId);

    return areaRemoveMemberControllerHelperService.removeMember(team, wuaId,
        renderRemoveMember(teamId, wuaId), redirectAttributes,
        ReverseRouter.redirect(on(SouthernOffshoreAreaTeamManagementController.class).renderMemberList(teamId)));
  }
}
