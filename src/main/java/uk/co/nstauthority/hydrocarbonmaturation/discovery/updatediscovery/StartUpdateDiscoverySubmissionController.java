package uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;

@Controller
@RequestMapping("/prospect/start-update-discovery")
public class StartUpdateDiscoverySubmissionController {

  @GetMapping
  public ModelAndView getStartUpdateDiscoveryPage() {
    return new ModelAndView("remi/prospect/startUpdateDiscovery")
        .addObject("backLinkUrl",
            ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose()))
        .addObject("actionUrl",
            ReverseRouter.route(on(StartUpdateDiscoverySubmissionController.class).startUpdateDiscovery()));
  }

  @PostMapping
  public ModelAndView startUpdateDiscovery() {
    return ReverseRouter.redirect(on(UpdateDiscoveryController.class).getUpdateDiscoveryPage(null));
  }
}
