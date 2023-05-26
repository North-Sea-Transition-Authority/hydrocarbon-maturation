package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.onshore;

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
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaRemoveMemberControllerHelperService;

@Controller
@RequestMapping("/permission-management/onshore-area/{teamId}/remove/{wuaId}")
@HasTeamPermission(
    anyTeamPermissionOf = RolePermission.GRANT_ONSHORE_AREA_ROLES
)
public class OnshoreAreaRemoveMemberController extends AbstractOnshoreAreaPermissionManagement {
  private final AreaRemoveMemberControllerHelperService areaRemoveMemberControllerHelperService;

  @Autowired
  public OnshoreAreaRemoveMemberController(
      OnshoreAreaTeamService onshoreAreaTeamService,
      AreaRemoveMemberControllerHelperService areaRemoveMemberControllerHelperService) {
    super(onshoreAreaTeamService);
    this.areaRemoveMemberControllerHelperService = areaRemoveMemberControllerHelperService;
  }

  @GetMapping
  public ModelAndView renderRemoveMember(@PathVariable("teamId") TeamId teamId,
                                         @PathVariable("wuaId") WebUserAccountId wuaId) {
    var team = getAreaTeam(teamId);

    return areaRemoveMemberControllerHelperService.renderRemoveMember(team, wuaId,
        ReverseRouter.redirect(on(OnshoreAreaTeamManagementController.class).renderMemberList(teamId)),
        ReverseRouter.route(on(OnshoreAreaTeamManagementController.class).renderMemberList(teamId)),
        ReverseRouter.route(on(OnshoreAreaRemoveMemberController.class).removeMember(teamId, wuaId, null)));
  }

  @PostMapping
  public ModelAndView removeMember(@PathVariable("teamId") TeamId teamId,
                                   @PathVariable("wuaId") WebUserAccountId wuaId,
                                   RedirectAttributes redirectAttributes) {
    var team = getAreaTeam(teamId);

    return areaRemoveMemberControllerHelperService.removeMember(team, wuaId, renderRemoveMember(teamId, wuaId),
        redirectAttributes,
        ReverseRouter.redirect(on(OnshoreAreaTeamManagementController.class).renderMemberList(teamId)));
  }
}
