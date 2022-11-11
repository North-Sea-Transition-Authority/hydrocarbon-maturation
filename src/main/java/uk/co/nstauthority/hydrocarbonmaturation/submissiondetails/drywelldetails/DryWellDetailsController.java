package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;


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
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.DryWellRestController;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.SubmissionTaskListRedirectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/dry-well-details")
public class DryWellDetailsController {

  private final DryWellDetailsFormService dryWellDetailsFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final DryWellDetailsPersistenceService dryWellDetailsPersistenceService;
  private final SubmissionTaskListRedirectionService submissionTaskListRedirectionService;

  public static final String PAGE_NAME = "Dry well details";

  @Autowired
  public DryWellDetailsController(DryWellDetailsFormService dryWellDetailsFormService,
                                  ControllerHelperService controllerHelperService,
                                  SubmissionDetailService submissionDetailService,
                                  DryWellDetailsPersistenceService dryWellDetailsPersistenceService,
                                  SubmissionTaskListRedirectionService submissionTaskListRedirectionService) {
    this.dryWellDetailsFormService = dryWellDetailsFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.dryWellDetailsPersistenceService = dryWellDetailsPersistenceService;
    this.submissionTaskListRedirectionService = submissionTaskListRedirectionService;
  }

  @GetMapping
  public ModelAndView getDryWellDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                        @ModelAttribute("form") DryWellDetailsForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getDryWellDetailsModelAndView(dryWellDetailsFormService.getDryWellDetailsForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveDryWellDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @ModelAttribute("form") DryWellDetailsForm form, BindingResult bindingResult,
                                         ValidationType validationType) {
    bindingResult = dryWellDetailsFormService.validate(form, bindingResult, validationType);
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getDryWellDetailsModelAndView(form, submissionDetail),
        () -> {
          dryWellDetailsPersistenceService.saveDryWellDetails(form, submissionDetail);
          return submissionTaskListRedirectionService.getTaskListEndpoint(submissionDetail);
        }
    );
  }

  private ModelAndView getDryWellDetailsModelAndView(DryWellDetailsForm form, SubmissionDetail submissionDetail) {
    var modelAndView =  new ModelAndView("remi/submissiondetails/dryWellDetails")
        .addObject("form", form)
        .addObject("datumTypes", DisplayableEnumOptionUtil.getDisplayableOptions(ReservoirDatum.class))
        .addObject("dryHoleTypes", DisplayableEnumOptionUtil.getDisplayableOptions(DryHoleType.class))
        .addObject("longitudeHemisphere", Hemisphere.getLongitudeHemisphere())
        .addObject("dryWellEndpoint", SearchSelectorService.route(on(DryWellRestController.class).searchDryWells(null)))
        .addObject("dryWell", dryWellDetailsFormService.getPreSelectedDryWell(form.getDryWell()));

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(
        modelAndView, PAGE_NAME, submissionDetail);
    return modelAndView;
  }
}
