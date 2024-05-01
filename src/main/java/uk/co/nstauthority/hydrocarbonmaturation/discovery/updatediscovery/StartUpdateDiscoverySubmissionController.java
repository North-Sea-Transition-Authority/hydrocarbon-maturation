package uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.DiscoveryPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Controller
@RequestMapping("/prospect/start-update-discovery")
@HasPermission(permissions = {
    RolePermission.EXPLORATION,
    RolePermission.INDUSTRY_SUBMIT,
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER})
public class StartUpdateDiscoverySubmissionController {

  @GetMapping
  public ModelAndView getStartUpdateDiscoveryPage() {
    return new ModelAndView("remi/discovery/startUpdateDiscovery")
        .addObject("backLinkUrl",
            ReverseRouter.route(on(DiscoveryPurposeController.class).getDiscoveryPurposeForm()))
        .addObject("actionUrl",
            ReverseRouter.route(on(StartUpdateDiscoverySubmissionController.class).startUpdateDiscovery()));
  }

  @PostMapping
  public ModelAndView startUpdateDiscovery() {
    return ReverseRouter.redirect(on(UpdateDiscoveryController.class).getUpdateDiscoveryPage(null));
  }
}
