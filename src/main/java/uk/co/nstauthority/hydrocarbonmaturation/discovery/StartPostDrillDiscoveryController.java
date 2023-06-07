package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/discovery/start")
@HasPermission(permissions = {
    RolePermission.EXPLORATION,
    RolePermission.INDUSTRY_SUBMIT,
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER})
public class StartPostDrillDiscoveryController {

  @GetMapping
  public ModelAndView getStartPostDrillDiscoveryPage() {
    return new ModelAndView("remi/discovery/startDiscoveryFromProspect")
        .addObject("actionUrl",
            ReverseRouter.route(on(StartPostDrillDiscoveryController.class).startPostDrillDiscovery()));
  }

  @PostMapping
  public ModelAndView startPostDrillDiscovery() {
    return ReverseRouter.redirect(on(PostDrillDiscoveryController.class).getStartDiscoveryPage(null));
  }
}
