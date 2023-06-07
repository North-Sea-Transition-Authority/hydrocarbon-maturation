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
@RequestMapping("/discovery/new")
@HasPermission(permissions = {
    RolePermission.EXPLORATION,
    RolePermission.INDUSTRY_SUBMIT,
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER})
public class StartDiscoveryController {

  @GetMapping
  public ModelAndView getStartPage() {
    return new ModelAndView("/remi/discovery/newDiscovery")
        .addObject("actionUrl", ReverseRouter.route(on(StartDiscoveryController.class).startDiscovery()))
        .addObject("backLinkUrl", ReverseRouter.route(on(DiscoveryPurposeController.class)
            .getDiscoveryPurposeForm()));
  }

  @PostMapping
  public ModelAndView startDiscovery() {
    return ReverseRouter.redirect(on(NewDiscoveryController.class).getNewDiscoveryPage(null));
  }
}