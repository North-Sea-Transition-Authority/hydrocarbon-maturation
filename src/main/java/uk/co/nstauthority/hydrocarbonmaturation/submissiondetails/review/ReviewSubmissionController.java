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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.RequestUpdateController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.StartUpdateController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistorySearchSelectorModelService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionSearchSelectorForm;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Controller
@RequestMapping("/review/resource/{resourceId}")
@AccessibleByServiceUsers
public class ReviewSubmissionController {

  private final ReviewSubmissionService reviewSubmissionService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final SubmissionDetailService submissionDetailService;
  private final UserDetailService userDetailService;
  private final SubmissionHistorySearchSelectorModelService submissionHistorySearchSelectorModelService;
  private final RegulatorTeamService regulatorTeamService;
  private final RegulatorManagedInformationSummaryService regulatorManagedInformationSummaryService;
  private final ResourceService resourceService;
  private final ResourceGroupSummaryService resourceGroupSummaryService;

  @Autowired
  public ReviewSubmissionController(ReviewSubmissionService reviewSubmissionService,
                                    SubmissionSummaryModelService submissionSummaryModelService,
                                    SubmissionDetailService submissionDetailService,
                                    SubmissionHistorySearchSelectorModelService submissionHistorySearchSelectorModelService,
                                    UserDetailService userDetailService,
                                    RegulatorTeamService regulatorTeamService,
                                    RegulatorManagedInformationSummaryService regulatorManagedInformationSummaryService,
                                    ResourceService resourceService,
                                    ResourceGroupSummaryService resourceGroupSummaryService) {
    this.reviewSubmissionService = reviewSubmissionService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.submissionDetailService = submissionDetailService;
    this.userDetailService = userDetailService;
    this.submissionHistorySearchSelectorModelService = submissionHistorySearchSelectorModelService;
    this.regulatorTeamService = regulatorTeamService;
    this.regulatorManagedInformationSummaryService = regulatorManagedInformationSummaryService;
    this.resourceService = resourceService;
    this.resourceGroupSummaryService = resourceGroupSummaryService;
  }

  @GetMapping
  public ModelAndView showReviewPage(@PathVariable("resourceId") UUID resourceId) {
    var submissionRecord = reviewSubmissionService.getSubmissionRecordOrThrow(resourceId);
    var userDetail = userDetailService.getUserDetail();

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(
        submissionRecord.submissionDetailId());

    if (!reviewSubmissionService.canViewSubmission(submissionRecord, userDetail, submissionDetail)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Submission cannot be reviewed by user with ID %s ".formatted(userDetail.wuaId()));
    }
    var form = new SubmissionSearchSelectorForm();
    form.setSelectedSubmissionId(submissionRecord.submissionDetailId().toString());
    return getModelAndView(submissionRecord, userDetail, submissionDetail, form, resourceId);
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

    var latestSubmission = submissionDetailService.getApprovedOrSubmittedSubmissionDetailByResource(resource)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find latest submitted or approved submission for resource %s"
                .formatted(resourceId)));

    var submissionRecord = reviewSubmissionService.getSubmissionRecordOrThrow(resourceId);
    var userDetail = userDetailService.getUserDetail();

    if (!reviewSubmissionService.canViewSubmission(submissionRecord, userDetail, latestSubmission)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Submission cannot be viewed by user with ID %s ".formatted(userDetail.wuaId()));
    }

    form.setSelectedSubmissionId(submissionDetailId.toString());
    return getModelAndView(submissionRecord, userDetail, selectedSubmission, form, resourceId);
  }

  @PostMapping
  public ModelAndView viewSubmission(@PathVariable("resourceId") UUID resourceId,
                                     @ModelAttribute("form") SubmissionSearchSelectorForm form) {
    return ReverseRouter.redirect(on(ReviewSubmissionController.class)
        .showSubmissionSummary(resourceId, UUID.fromString(form.getSelectedSubmissionId()), null));
  }

  private ModelAndView getModelAndView(SubmissionRecordSummaryView submissionRecord,
                                       ServiceUserDetail userDetail,
                                       SubmissionDetail submissionDetail,
                                       SubmissionSearchSelectorForm form,
                                       UUID resourceId) {
    var modelAndView = submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(
        submissionDetail,
        "remi/review/reviewSubmission"
    );

    modelAndView = submissionHistorySearchSelectorModelService
        .addReviewSubmissionSearchSelectorItemsToModelAndView(modelAndView, resourceId);

    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(submissionRecord.resourceNameAndCentreOfInterest())
        .addWorkAreaBreadcrumb()
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);

    var isRegulator = regulatorTeamService.isRegulatorUser(userDetail);
    var optionalSubmissionDetailUpdate = reviewSubmissionService.getUpdateSubmissionDetailBySubmission(
        submissionDetail.getSubmission());
    if (isRegulator) {
      var resource = resourceService.getResourceById(resourceId)
          .orElseThrow(() -> new RemiEntityNotFoundException("Could not find resource for id %s"
              .formatted(resourceId)));
      modelAndView
          .addObject("regulatorManagedInformationSummaryView",
              regulatorManagedInformationSummaryService.getRegulatorManagedInformationSummaryFromResource(resource))
          .addObject("resourceGroupsSummaryView",
              resourceGroupSummaryService.getResourceGroupLinkSummaryFromResource(resource))
          .addObject("canRequestUpdate", reviewSubmissionService
              .canRequestUpdateOnSubmissionDetail(submissionDetail))
          .addObject("requestUpdateUrl", ReverseRouter.route(on(RequestUpdateController.class)
              .showRequestUpdateConfirmationPage(submissionDetail.getId())))
          .addObject("approveUrl",
              ReverseRouter.route(on(ApproveSubmissionController.class)
                  .showApproveConfirmationPage(submissionDetail.getId())))
          .addObject("regulatorManagedInformationUrl",
              ReverseRouter.route(on(RegulatorManagedInformationController.class)
                  .getRegulatorManagedInformationPage(
                      resourceId, submissionDetail.getId())))
          .addObject("withdrawUrl",
              ReverseRouter.route(on(WithdrawSubmissionController.class)
                  .showWithdrawConfirmationPage(submissionDetail.getId())));
    }

    var isIndustrySubmitter = reviewSubmissionService
        .canUpdateSubmission(submissionRecord, userDetail, submissionDetail);
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

    return modelAndView
        .addObject("form", form)
        .addObject("submissionRecord", submissionRecord)
        .addObject("submissionType", submissionDetail.getSubmissionType())
        .addObject("canReview", reviewSubmissionService.canReviewSubmission(
            submissionRecord, userDetail, submissionDetail))
        .addObject("resourceType", submissionDetail.getSubmission().getResource().getType().getDisplayName())
        .addObject("isRegulator", isRegulator)
        .addObject("isIndustrySubmitter", isIndustrySubmitter)
        .addObject("updateRequested",
            reviewSubmissionService.hasUpdateBeenRequestedOnSubmission(submissionDetail.getSubmission())
        );
  }
}
