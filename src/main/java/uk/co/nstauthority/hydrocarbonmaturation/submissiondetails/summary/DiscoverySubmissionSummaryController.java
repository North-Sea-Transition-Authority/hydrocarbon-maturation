package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/discovery-summary")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
public class DiscoverySubmissionSummaryController {

  private final SubmissionDetailService submissionDetailService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionSummaryFormService submissionSummaryFormService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final SubmissionReadyService submissionReadyService;
  private final SubmissionDetailPersistenceService submissionDetailPersistenceService;

  public static final String PAGE_NAME = "Check your answers before submitting";

  @Autowired
  public DiscoverySubmissionSummaryController(SubmissionDetailService submissionDetailService,
                                              ControllerHelperService controllerHelperService,
                                              SubmissionSummaryFormService submissionSummaryFormService,
                                              SubmissionSummaryModelService submissionSummaryModelService,
                                              SubmissionReadyService submissionReadyService,
                                              SubmissionDetailPersistenceService submissionDetailPersistenceService) {
    this.submissionDetailService = submissionDetailService;
    this.controllerHelperService = controllerHelperService;
    this.submissionSummaryFormService = submissionSummaryFormService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.submissionReadyService = submissionReadyService;
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
  }

  @GetMapping
  public ModelAndView showSubmissionSummary(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getSubmissionSummaryModelAndView(new SubmissionSummaryForm(), submissionDetail);
  }

  @PostMapping
  public ModelAndView processSubmission(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                        @ModelAttribute("form") SubmissionSummaryForm form,
                                        BindingResult bindingResult) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = submissionSummaryFormService.validate(form, bindingResult, submissionDetail);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getSubmissionSummaryModelAndView(form, submissionDetail),
        () -> {
          submissionDetailPersistenceService.completeSubmission(submissionDetail);
          return ReverseRouter.redirect(on(SubmissionConfirmationController.class)
              .showSubmissionConfirmation(submissionDetailId));
        }
    );
  }

  private ModelAndView getSubmissionSummaryModelAndView(SubmissionSummaryForm form, SubmissionDetail submissionDetail) {
    var modelAndView = submissionSummaryModelService.createDiscoverySubmissionSummaryModelAndView(
        submissionDetail,
        "remi/submissiondetails/summary/discoverySubmissionSummary"
    );

    modelAndView
        .addObject("form", form)
        .addObject("cancelUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId())))
        .addObject("submittable", submissionReadyService.isSubmissionSubmittable(submissionDetail));

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);

    return modelAndView;
  }
}
