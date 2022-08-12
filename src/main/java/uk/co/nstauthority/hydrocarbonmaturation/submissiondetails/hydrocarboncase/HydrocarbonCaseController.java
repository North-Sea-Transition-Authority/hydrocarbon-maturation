package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

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
import uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist.ProspectTaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist.ProspectTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/hydrocarbon-case")
public class HydrocarbonCaseController {

  static final String PAGE_NAME = "Hydrocarbon case";
  private final HydrocarbonCaseFormService hydrocarbonCaseFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @Autowired
  public HydrocarbonCaseController(HydrocarbonCaseFormService hydrocarbonCaseFormService,
                                   ControllerHelperService controllerHelperService,
                                   SubmissionDetailService submissionDetailService,
                                   HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService) {
    this.hydrocarbonCaseFormService = hydrocarbonCaseFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
  }

  @GetMapping
  public ModelAndView getHydrocarbonCase(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getHydrocarbonCaseModelAndView(
        hydrocarbonCaseFormService.getHydrocarbonCaseForm(submissionDetail), submissionDetailId);
  }

  @PostMapping
  public ModelAndView saveHydrocarbonCase(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                          @ModelAttribute("form") HydrocarbonCaseForm form, BindingResult bindingResult) {
    bindingResult = hydrocarbonCaseFormService.validate(form, bindingResult);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getHydrocarbonCaseModelAndView(form, submissionDetailId),
        () -> {
          var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
          hydrocarbonCasePersistenceService.saveHydrocarbonCase(form, submissionDetail);
          return ReverseRouter.redirect(on(ProspectTaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getHydrocarbonCaseModelAndView(HydrocarbonCaseForm form, UUID submissionDetailId) {
    var modelAndView = new ModelAndView("hm/submissiondetails/hydrocarbonCase")
        .addObject("form", form);
    return ProspectTaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetailId);
  }
}
