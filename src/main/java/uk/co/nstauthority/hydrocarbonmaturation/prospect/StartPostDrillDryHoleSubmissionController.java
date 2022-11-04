package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;

@Controller
@RequestMapping("/prospect/start-post-drill-dry-hole")
public class StartPostDrillDryHoleSubmissionController {

  @GetMapping
  public ModelAndView getStartPostDrillDryHolePage() {
    return new ModelAndView("remi/prospect/startPostDrillDryHoleProspect")
        .addObject("actionUrl", ReverseRouter.route(on(StartPostDrillDryHoleSubmissionController.class).startPostDrillDryHole()))
        .addObject("backLinkUrl", ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose()));
  }

  @PostMapping
  public ModelAndView startPostDrillDryHole() {
    return ReverseRouter.redirect(on(PostDrillDryHoleController.class).getPostDrillDryHolePage(null));
  }
}
