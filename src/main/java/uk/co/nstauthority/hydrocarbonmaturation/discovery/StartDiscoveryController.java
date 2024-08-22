package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("/discovery/new")
@HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
@HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
public class StartDiscoveryController {

  @GetMapping
  public ModelAndView getStartPage() {
    return new ModelAndView("/remi/discovery/newDiscovery")
        .addObject("actionUrl", ReverseRouter.route(on(StartDiscoveryController.class).startDiscovery()))
        .addObject("backLinkUrl", ReverseRouter.route(on(DiscoveryPurposeController.class)
            .getDiscoveryPurposeForm(null)));
  }

  @PostMapping
  public ModelAndView startDiscovery() {
    return ReverseRouter.redirect(on(NewDiscoveryController.class).getNewDiscoveryPage(null, null));
  }
}