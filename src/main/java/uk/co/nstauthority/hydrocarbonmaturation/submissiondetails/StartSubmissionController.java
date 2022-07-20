package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.StartProspectController;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Controller
@RequestMapping("/start-submission")
public class StartSubmissionController {

  private final SubmissionTypeSelectService submissionTypeSelectService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public StartSubmissionController(SubmissionTypeSelectService submissionTypeSelectService,
                                   ControllerHelperService controllerHelperService) {
    this.submissionTypeSelectService = submissionTypeSelectService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView getSubmissionPurpose() {
    return getSubmissionTypeModelAndView(new SubmissionTypeSelectForm());
  }

  @PostMapping
  public ModelAndView startSubmission(@ModelAttribute("form") SubmissionTypeSelectForm form, BindingResult bindingResult) {
    bindingResult = submissionTypeSelectService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getSubmissionTypeModelAndView(form),
        () -> ReverseRouter.redirect(on(StartProspectController.class).getStartPage()));
  }

  ModelAndView getSubmissionTypeModelAndView(SubmissionTypeSelectForm submissionTypeSelectForm) {
    return new ModelAndView("hm/submissiondetails/startSubmission")
        .addObject("submissionType", SubmissionType.getSubmissionTypes())
        .addObject("form", submissionTypeSelectForm)
        .addObject("actionUrl", ReverseRouter.route(on(StartSubmissionController.class).startSubmission(null, null)))
        .addObject("backLinkUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea()));
  }
}
