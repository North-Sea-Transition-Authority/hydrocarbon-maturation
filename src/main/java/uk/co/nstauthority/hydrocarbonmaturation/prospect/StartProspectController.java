package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsController;

@Controller
@RequestMapping("/prospect/new")
public class StartProspectController {

  private final StartProspectService startProspectService;

  @Autowired
  public StartProspectController(StartProspectService startProspectService) {
    this.startProspectService = startProspectService;
  }

  @GetMapping
  public ModelAndView getStartPage() {
    return new ModelAndView("/remi/prospect/startProspect")
        .addObject("actionUrl", ReverseRouter.route(on(StartProspectController.class).startProspect()))
        .addObject("backLinkUrl", ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose()));
  }

  @PostMapping
  public ModelAndView startProspect() {
    var submissionDetail =  startProspectService.createInitialProspect();
    return ReverseRouter.redirect(on(GeneralDetailsController.class).getGeneralDetails(submissionDetail.getId(), null));
  }
}
