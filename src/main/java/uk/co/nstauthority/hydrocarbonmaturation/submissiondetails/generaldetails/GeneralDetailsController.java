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
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/general-details")
public class GeneralDetailsController {

  private final GeneralDetailsFormService generalDetailsFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Autowired
  public GeneralDetailsController(GeneralDetailsFormService generalDetailsFormService,
                                  ControllerHelperService controllerHelperService,
                                  SubmissionDetailService submissionDetailService,
                                  GeneralDetailsPersistenceService generalDetailsPersistenceService) {
    this.generalDetailsFormService = generalDetailsFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
  }

  @GetMapping
  public ModelAndView getGeneralDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @ModelAttribute("form") GeneralDetailsForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getGeneralDetailsModelAndView(generalDetailsFormService.getGeneralDetailsForm(submissionDetail), submissionDetailId);
  }

  @PostMapping
  public ModelAndView saveGeneralDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @ModelAttribute("form") GeneralDetailsForm form, BindingResult bindingResult,
                                         ValidationType validationType) {
    bindingResult = generalDetailsFormService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getGeneralDetailsModelAndView(form, submissionDetailId),
        () -> {
          var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
          generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail);
          return ReverseRouter.redirect(on(ProspectTaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getGeneralDetailsModelAndView(GeneralDetailsForm form, UUID submissionDetailId) {
    return new ModelAndView("hm/submissiondetails/generalDetails")
        .addObject("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore())
        .addObject("informationSources", InformationSource.getInformationSource())
        .addObject("form", form)
        .addObject("backLinkUrl", ReverseRouter.route(on(StartProspectController.class).getStartPage()));
  }
}
