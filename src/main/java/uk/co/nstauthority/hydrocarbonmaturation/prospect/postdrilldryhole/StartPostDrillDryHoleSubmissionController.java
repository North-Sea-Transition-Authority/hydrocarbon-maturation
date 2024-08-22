package uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.ProspectPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("/prospect/start-post-drill-dry-hole")
@HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
@HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
public class StartPostDrillDryHoleSubmissionController {

  @GetMapping
  public ModelAndView getStartPostDrillDryHolePage() {
    return new ModelAndView("remi/prospect/startPostDrillDryHoleProspect")
        .addObject("actionUrl", ReverseRouter.route(on(StartPostDrillDryHoleSubmissionController.class).startPostDrillDryHole()))
        .addObject("backLinkUrl", ReverseRouter.route(on(ProspectPurposeController.class).getProspectPurposeForm(null)));
  }

  @PostMapping
  public ModelAndView startPostDrillDryHole() {
    return ReverseRouter.redirect(on(PostDrillDryHoleController.class).getPostDrillDryHolePage(null));
  }
}
