package uk.co.nstauthority.hydrocarbonmaturation.prospect;

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
@RequestMapping("/prospect/new")
@HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
@HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
public class StartProspectController {

  @GetMapping
  public ModelAndView getStartPage() {
    return new ModelAndView("/remi/prospect/startProspect")
        .addObject("actionUrl", ReverseRouter.route(on(StartProspectController.class).startProspect()))
        .addObject("backLinkUrl", ReverseRouter.route(on(ProspectPurposeController.class).getProspectPurposeForm(null)));
  }

  @PostMapping
  public ModelAndView startProspect() {
    return ReverseRouter.redirect(on(NewProspectController.class).getNewProspectPage(null, null));
  }
}
