package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

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
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.SubmissionTaskListRedirectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/permeability")
public class PermeabilityController {

  private final SubmissionDetailService submissionDetailService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionTaskListRedirectionService submissionTaskListRedirectionService;
  private final PermeabilityDryWellFormService permeabilityDryWellFormService;
  private final PermeabilityPersistenceService permeabilityPersistenceService;

  public static final String PAGE_NAME = "Permeability";

  @Autowired
  public PermeabilityController(SubmissionDetailService submissionDetailService,
                                ControllerHelperService controllerHelperService,
                                SubmissionTaskListRedirectionService submissionTaskListRedirectionService,
                                PermeabilityDryWellFormService permeabilityDryWellFormService,
                                PermeabilityPersistenceService permeabilityPersistenceService) {
    this.submissionDetailService = submissionDetailService;
    this.controllerHelperService = controllerHelperService;
    this.submissionTaskListRedirectionService = submissionTaskListRedirectionService;
    this.permeabilityDryWellFormService = permeabilityDryWellFormService;
    this.permeabilityPersistenceService = permeabilityPersistenceService;
  }

  @GetMapping
  public ModelAndView getPermeabilityPage(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                          @ModelAttribute("form") PermeabilityDryWellForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getPermeabilityModelAndView(
        permeabilityDryWellFormService.getPermeabilityForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView savePermeabilityDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                              @ModelAttribute("form") PermeabilityDryWellForm form,
                                              BindingResult bindingResult,
                                              ValidationType validationType) {
    bindingResult = permeabilityDryWellFormService.validate(form, bindingResult, validationType);
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getPermeabilityModelAndView(form, submissionDetail),
        () -> {
          permeabilityPersistenceService.savePermeabilityDetail(form, submissionDetail);
          return submissionTaskListRedirectionService.getTaskListEndpoint(submissionDetail);
        }
    );
  }

  private ModelAndView getPermeabilityModelAndView(PermeabilityDryWellForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/permeabilityDryWell")
        .addObject("form", form)
        .addObject("methodUsed", DisplayableEnumOptionUtil.getDisplayableOptions(AverageMethod.class))
        .addObject("source", DisplayableEnumOptionUtil.getDisplayableOptions(PermeabilitySource.class));

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(
        modelAndView, PAGE_NAME, submissionDetail);

    return modelAndView;
  }
}
