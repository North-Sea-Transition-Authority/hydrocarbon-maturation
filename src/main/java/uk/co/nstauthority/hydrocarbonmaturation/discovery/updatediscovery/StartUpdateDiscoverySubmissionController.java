package uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.DiscoveryPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("/discovery/start-update-discovery")
@HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
@HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
public class StartUpdateDiscoverySubmissionController {

  @GetMapping
  public ModelAndView getStartUpdateDiscoveryPage() {
    return new ModelAndView("remi/discovery/startUpdateDiscovery")
        .addObject("backLinkUrl",
            ReverseRouter.route(on(DiscoveryPurposeController.class).getDiscoveryPurposeForm(null)))
        .addObject("actionUrl",
            ReverseRouter.route(on(StartUpdateDiscoverySubmissionController.class).startUpdateDiscovery()));
  }

  @PostMapping
  public ModelAndView startUpdateDiscovery() {
    return ReverseRouter.redirect(on(UpdateDiscoveryController.class).getUpdateDiscoveryPage(null));
  }
}
