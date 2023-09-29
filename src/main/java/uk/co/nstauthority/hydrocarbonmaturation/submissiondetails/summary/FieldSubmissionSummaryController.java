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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionApprovalService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshoreService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/field-submission-summary")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.FIELD_SUBMISSIONS)
@IsFromResponsibleTeam
public class FieldSubmissionSummaryController {

  public static final String PAGE_NAME = "Review your submission before submitting";

  private final SubmissionDetailService submissionDetailService;
  private final SubmissionReadyService submissionReadyService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final SubmissionSummaryFormValidator submissionSummaryFormValidator;
  private final SubmissionApprovalService submissionApprovalService;
  private final OnshoreOffshoreService onshoreOffshoreService;
  private final InternalTeamService internalTeamService;

  @Autowired
  public FieldSubmissionSummaryController(SubmissionDetailService submissionDetailService,
                                          SubmissionReadyService submissionReadyService,
                                          SubmissionSummaryModelService submissionSummaryModelService,
                                          SubmissionSummaryFormValidator submissionSummaryFormValidator,
                                          SubmissionApprovalService submissionApprovalService,
                                          OnshoreOffshoreService onshoreOffshoreService,
                                          InternalTeamService internalTeamService) {
    this.submissionDetailService = submissionDetailService;
    this.submissionReadyService = submissionReadyService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.submissionSummaryFormValidator = submissionSummaryFormValidator;
    this.submissionApprovalService = submissionApprovalService;
    this.onshoreOffshoreService = onshoreOffshoreService;
    this.internalTeamService = internalTeamService;
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
    if (!submissionSummaryFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL)) {
      return getSubmissionSummaryModelAndView(form, submissionDetail);
    } else {
      submissionApprovalService.submitSubmission(submissionDetail);
      return ReverseRouter.redirect(on(SubmissionConfirmationController.class)
          .showSubmissionConfirmation(submissionDetailId));
    }
  }

  private ModelAndView getSubmissionSummaryModelAndView(SubmissionSummaryForm form, SubmissionDetail submissionDetail) {
    var modelAndView = submissionSummaryModelService.createFieldSubmissionSummaryModelAndView(
        submissionDetail,
        "remi/submissiondetails/summary/fieldSubmissionSummary"
    );

    modelAndView
        .addObject("form", form)
        .addObject("pageName", PAGE_NAME)
        .addObject("cancelUrl", ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId())))
        .addObject("submittable", submissionReadyService.isSubmissionSubmittable(submissionDetail))
        .addObject("isRegulatorOrAreaTeamUser", internalTeamService.isRegulatorOrAreaTeamUser())
        .addObject("isOffshore", onshoreOffshoreService.getOnshoreOffshore(submissionDetail) == OnshoreOffshore.OFFSHORE);

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);

    return modelAndView;
  }
}
