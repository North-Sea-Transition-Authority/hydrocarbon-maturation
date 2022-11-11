package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.SubmissionTaskListRedirectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/hydrocarbon-case")
public class HydrocarbonCaseController {

  public static final String PAGE_NAME = "Hydrocarbon case";

  private final HydrocarbonCaseFormService hydrocarbonCaseFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  private final SubmissionTaskListRedirectionService submissionTaskListRedirectionService;

  @Autowired
  public HydrocarbonCaseController(HydrocarbonCaseFormService hydrocarbonCaseFormService,
                                   ControllerHelperService controllerHelperService,
                                   SubmissionDetailService submissionDetailService,
                                   HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService,
                                   SubmissionTaskListRedirectionService submissionTaskListRedirectionService) {
    this.hydrocarbonCaseFormService = hydrocarbonCaseFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
    this.submissionTaskListRedirectionService = submissionTaskListRedirectionService;
  }

  @GetMapping
  public ModelAndView getHydrocarbonCase(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getHydrocarbonCaseModelAndView(
        hydrocarbonCaseFormService.getHydrocarbonCaseForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveHydrocarbonCase(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                          @ModelAttribute("form") HydrocarbonCaseForm form, BindingResult bindingResult,
                                          ValidationType validationType) {
    bindingResult = hydrocarbonCaseFormService.validate(form, bindingResult, validationType);
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getHydrocarbonCaseModelAndView(form, submissionDetail),
        () -> {
          hydrocarbonCasePersistenceService.saveHydrocarbonCase(form, submissionDetail);
          return submissionTaskListRedirectionService.getTaskListEndpoint(submissionDetail);
        }
    );
  }

  private ModelAndView getHydrocarbonCaseModelAndView(HydrocarbonCaseForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/hydrocarbonCase")
        .addObject("form", form);
    return TaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
  }
}
