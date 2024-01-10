package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fieldregulatormanagedinformation.FieldRegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.RequestUpdateController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.StartUpdateController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistoryQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistorySelectorUtil;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionSearchSelectorForm;

@Controller
@RequestMapping("/review/resource/{resourceId}")
@AccessibleByServiceUsers
public class ReviewSubmissionController {

  private final ReviewSubmissionService reviewSubmissionService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final SubmissionDetailService submissionDetailService;
  private final UserService userService;
  private final SubmissionHistoryQueryService submissionHistoryQueryService;
  private final RegulatorManagedInformationSummaryService regulatorManagedInformationSummaryService;
  private final ResourceService resourceService;
  private final ResourceGroupSummaryService resourceGroupSummaryService;
  private final ResponsibleTeamService responsibleTeamService;

  @Autowired
  public ReviewSubmissionController(ReviewSubmissionService reviewSubmissionService,
                                    SubmissionSummaryModelService submissionSummaryModelService,
                                    SubmissionDetailService submissionDetailService,
                                    UserService userService,
                                    SubmissionHistoryQueryService submissionHistoryQueryService,
                                    RegulatorManagedInformationSummaryService regulatorManagedInformationSummaryService,
                                    ResourceService resourceService,
                                    ResourceGroupSummaryService resourceGroupSummaryService,
                                    ResponsibleTeamService responsibleTeamService) {
    this.reviewSubmissionService = reviewSubmissionService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.submissionDetailService = submissionDetailService;
    this.userService = userService;
    this.submissionHistoryQueryService = submissionHistoryQueryService;
    this.regulatorManagedInformationSummaryService = regulatorManagedInformationSummaryService;
    this.resourceService = resourceService;
    this.resourceGroupSummaryService = resourceGroupSummaryService;
    this.responsibleTeamService = responsibleTeamService;
  }

  @GetMapping
  public ModelAndView showReviewPage(@PathVariable("resourceId") UUID resourceId) {
    var submissionRecord = reviewSubmissionService.getSubmissionRecordOrThrow(resourceId);
    var user = userService.getUser();

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(
        submissionRecord.submissionDetailId());

    if (!reviewSubmissionService.canViewSubmission(submissionRecord, user, submissionDetail)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Submission cannot be reviewed by user with ID %s ".formatted(user.wuaId()));
    }
    var form = new SubmissionSearchSelectorForm();
    form.setSelectedSubmissionId(submissionRecord.submissionDetailId().toString());
    return getModelAndView(submissionRecord, user, submissionDetail, form, resourceId);
  }

  @GetMapping("submission-details/{submissionDetailId}")
  public ModelAndView showSubmissionSummary(@PathVariable("resourceId") UUID resourceId,
                                            @PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @ModelAttribute("form") SubmissionSearchSelectorForm form) {
    var selectedSubmission = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() -> new RemiEntityNotFoundException("Could not find resource with id %s"
            .formatted(resourceId)));

    if (!reviewSubmissionService.canViewSelectedSubmissionDetail(selectedSubmission, resource)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          ("You cannot view this %s submission detail due to it being a draft or deleted submission detail " +
              "or a submission detail not on the same resource")
              .formatted(submissionDetailId));
    }

    var latestSubmission = submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find latest submitted or accepted submission for resource %s"
                .formatted(resourceId)));

    var submissionRecord = reviewSubmissionService.getSubmissionRecordOrThrow(resourceId);
    var user = userService.getUser();

    if (!reviewSubmissionService.canViewSubmission(submissionRecord, user, latestSubmission)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Submission cannot be viewed by user with ID %s ".formatted(user.wuaId()));
    }

    form.setSelectedSubmissionId(submissionDetailId.toString());
    return getModelAndView(submissionRecord, user, selectedSubmission, form, resourceId);
  }

  @PostMapping
  public ModelAndView viewSubmission(@PathVariable("resourceId") UUID resourceId,
                                     @ModelAttribute("form") SubmissionSearchSelectorForm form) {
    return ReverseRouter.redirect(on(ReviewSubmissionController.class)
        .showSubmissionSummary(resourceId, UUID.fromString(form.getSelectedSubmissionId()), null));
  }

  private ModelAndView getModelAndView(SubmissionRecordSummaryView submissionRecord,
                                       User user,
                                       SubmissionDetail submissionDetail,
                                       SubmissionSearchSelectorForm form,
                                       UUID resourceId) {
    var modelAndView = submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(
        submissionDetail,
        "remi/review/reviewSubmission"
    );

    var querySearchResults = submissionHistoryQueryService.getApprovedAndSubmittedSubmissionsOnResource(
        resourceId
    );
    modelAndView.addObject("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(
        querySearchResults
    ));
    modelAndView.addObject("submissionSelectedUrl",
        ReverseRouter.route(on(ReviewSubmissionController.class).viewSubmission(resourceId, null))
    );

    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(submissionRecord.resourceNameAndCentreOfInterest())
        .addWorkAreaBreadcrumb()
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);

    var isInResponsibleTeam = responsibleTeamService
        .isInResponsibleTeam(user, submissionRecord.responsibleTeamType());
    var optionalSubmissionDetailUpdate = reviewSubmissionService.getUpdateSubmissionDetailBySubmission(
        submissionDetail.getSubmission());
    if (isInResponsibleTeam) {
      addResponsibleTeamProperties(modelAndView, resourceId, submissionDetail);
    }

    var isIndustrySubmitter = reviewSubmissionService
        .canUpdateSubmission(submissionRecord, user, submissionDetail);
    if (isIndustrySubmitter) {
      var updateSubmissionUrl = optionalSubmissionDetailUpdate
          .map(detail -> ReverseRouter.route(on(TaskListController.class)
              .getTaskList(detail.getId())))
          .orElseGet(() -> ReverseRouter.route(on(StartUpdateController.class)
              .showStartUpdatePage(submissionDetail.getId())));

      modelAndView
          .addObject("updateSubmissionUrl", updateSubmissionUrl)
          .addObject("updateStarted", optionalSubmissionDetailUpdate.isPresent());
    }

    reviewSubmissionService.getLatestUnresolvedSubmissionUpdateBySubmission(submissionDetail.getSubmission())
        .ifPresentOrElse(submissionUpdate ->
            modelAndView
                .addObject("submissionUpdateReason", submissionUpdate.getReason())
                .addObject("updateRequested", true),
            () -> modelAndView
                .addObject("updateRequested", false));


    return modelAndView
        .addObject("form", form)
        .addObject("submissionRecord", submissionRecord)
        .addObject("submissionType", submissionDetail.getSubmissionType())
        .addObject("canReview",
            submissionDetail.getStatus() == SubmissionDetailStatus.SUBMITTED && isInResponsibleTeam)
        .addObject("isFieldSubmission", submissionDetail.isFieldSubmission())
        .addObject("resourceType", submissionDetail.getSubmission().getResource().getType().getDisplayName())
        .addObject("isInResponsibleTeam", isInResponsibleTeam)
        .addObject("isIndustrySubmitter", isIndustrySubmitter);
  }

  private ModelAndView addResponsibleTeamProperties(ModelAndView modelAndView, UUID resourceId,
                                                    SubmissionDetail selectedSubmissionDetail) {
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() -> new RemiEntityNotFoundException("Could not find resource for id %s"
            .formatted(resourceId)));
    modelAndView
        .addObject("regulatorManagedInformationSummaryView",
            regulatorManagedInformationSummaryService.getRegulatorManagedInformationSummaryFromResource(resource))
        .addObject("resourceGroupsSummaryView",
            resourceGroupSummaryService.getResourceGroupLinkSummaryFromResource(resource))
        .addObject("canRequestUpdate", reviewSubmissionService
            .canRequestUpdateOnSubmissionDetail(selectedSubmissionDetail))
        .addObject("requestUpdateUrl", ReverseRouter.route(on(RequestUpdateController.class)
            .showRequestUpdateConfirmationPage(selectedSubmissionDetail.getId())))
        .addObject("approveUrl",
            ReverseRouter.route(on(ApproveSubmissionController.class)
                .showApproveConfirmationPage(selectedSubmissionDetail.getId())))
        .addObject("withdrawUrl",
            ReverseRouter.route(on(WithdrawSubmissionController.class)
                .showWithdrawConfirmationPage(selectedSubmissionDetail.getId())));

    if (selectedSubmissionDetail.isFieldSubmission()) {
      modelAndView
          .addObject("regulatorManagedInformationUrl",
              ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                  .getRegulatorManagedInformationPage(resourceId, null)));
    } else {
      modelAndView
          .addObject("regulatorManagedInformationUrl",
              ReverseRouter.route(on(RegulatorManagedInformationController.class)
                  .getRegulatorManagedInformationPage(
                      resourceId)));
    }

    return modelAndView;
  }
}
