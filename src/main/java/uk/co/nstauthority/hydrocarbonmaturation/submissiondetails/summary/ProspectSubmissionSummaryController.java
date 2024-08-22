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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionApprovalService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.ReviewAndSubmitTaskListSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/summary")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.PROSPECT_TYPES)
@IsFromResponsibleTeam
public class ProspectSubmissionSummaryController {
  private final SubmissionDetailService submissionDetailService;
  private final InternalTeamService internalTeamService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final SubmissionReadyService submissionReadyService;
  private final SubmissionApprovalService submissionApprovalService;
  private final SubmissionSummaryFormValidator submissionSummaryFormValidator;

  public static final String PAGE_NAME = "Review your submission before submitting";

  @Autowired
  public ProspectSubmissionSummaryController(SubmissionDetailService submissionDetailService,
                                             InternalTeamService internalTeamService,
                                             SubmissionSummaryModelService submissionSummaryModelService,
                                             SubmissionReadyService submissionReadyService,
                                             SubmissionApprovalService submissionApprovalService,
                                             SubmissionSummaryFormValidator submissionSummaryFormValidator) {
    this.submissionDetailService = submissionDetailService;
    this.internalTeamService = internalTeamService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.submissionReadyService = submissionReadyService;
    this.submissionApprovalService = submissionApprovalService;
    this.submissionSummaryFormValidator = submissionSummaryFormValidator;
  }

  @GetMapping
  public ModelAndView showSubmissionSummary(@PathVariable("submissionDetailId") UUID submissionDetailId, User user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getSubmissionSummaryModelAndView(new SubmissionSummaryForm(), submissionDetail, user);
  }

  @PostMapping
  public ModelAndView processSubmission(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                        @ModelAttribute("form") SubmissionSummaryForm form,
                                        BindingResult bindingResult,
                                        User user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    if (!submissionSummaryFormValidator.isValid(bindingResult, submissionDetail, user)) {
      return getSubmissionSummaryModelAndView(form, submissionDetail, user);
    } else {
      submissionApprovalService.submitSubmission(submissionDetail, user);
      return ReverseRouter.redirect(on(SubmissionConfirmationController.class)
          .showSubmissionConfirmation(submissionDetailId));
    }
  }

  private ModelAndView getSubmissionSummaryModelAndView(SubmissionSummaryForm form,
                                                        SubmissionDetail submissionDetail,
                                                        User user) {
    var modelAndView = submissionSummaryModelService.createProspectSubmissionSummaryModelAndView(
        submissionDetail,
        "remi/submissiondetails/summary/prospectSubmissionSummary"
    );

    modelAndView
        .addObject("form", form)
        .addObject("pageName", PAGE_NAME)
        .addObject("cancelUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId(), null)))
        .addObject("submittable", submissionReadyService.isSubmissionSubmittable(submissionDetail, user))
        .addObject("isRegulatorOrAreaTeamUser", internalTeamService.isRegulatorOrAreaTeamUser(user));

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(
        modelAndView,
        ReviewAndSubmitTaskListSectionService.REVIEW_SUBMIT_ITEM_NAME,
        submissionDetail
    );

    return modelAndView;
  }
}
