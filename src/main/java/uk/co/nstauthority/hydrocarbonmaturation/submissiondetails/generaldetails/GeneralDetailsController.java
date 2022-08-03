package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.StartProspectController;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist.ProspectTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/general-details")
public class GeneralDetailsController {

  private final GeneralDetailsService generalDetailsService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  public GeneralDetailsController(GeneralDetailsService generalDetailsService,
                                  ControllerHelperService controllerHelperService,
                                  SubmissionDetailService submissionDetailService) {
    this.generalDetailsService = generalDetailsService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
  }

  @GetMapping
  public ModelAndView getGeneralDetailsPage(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @ModelAttribute("form") GeneralDetailsForm form) {
    return getGeneralDetailsModelAndView(form);
  }

  @PostMapping
  public ModelAndView saveGeneralDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @ModelAttribute("form") GeneralDetailsForm form, BindingResult bindingResult) {
    bindingResult = generalDetailsService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getGeneralDetailsModelAndView(form),
        () -> {
          var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
          generalDetailsService.saveGeneralDetails(form, submissionDetail);
          return ReverseRouter.redirect(on(ProspectTaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getGeneralDetailsModelAndView(GeneralDetailsForm form) {
    return new ModelAndView("hm/submissiondetails/generalDetails")
        .addObject("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore())
        .addObject("informationSources", InformationSource.getInformationSource())
        .addObject("form", form)
        .addObject("actionUrl", "#")
        .addObject("backLinkUrl", ReverseRouter.route(on(StartProspectController.class).getStartPage()));
  }
}
