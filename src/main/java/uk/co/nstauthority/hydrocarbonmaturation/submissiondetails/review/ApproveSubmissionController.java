package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsLatestExtantSubmissionDetailVersion;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldSubmissionStatusService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionApprovalService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

@Controller
@RequestMapping("/review/{submissionDetailId}/accept-submission")
@HasSubmissionStatus(statuses = {SubmissionDetailStatus.SUBMITTED})
@HasPermission(permissions = {
    RolePermission.EXPLORATION,
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER})
@IsLatestExtantSubmissionDetailVersion
public class ApproveSubmissionController {

  private final SubmissionDetailService submissionDetailService;
  private final SubmissionResourceService submissionResourceService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final SubmissionApprovalService submissionApprovalService;
  private final UserService userService;
  private final FieldSubmissionStatusService fieldSubmissionStatusService;

  @Autowired
  public ApproveSubmissionController(SubmissionDetailService submissionDetailService,
                                     SubmissionResourceService submissionResourceService,
                                     SubmissionSummaryModelService submissionSummaryModelService,
                                     SubmissionApprovalService submissionApprovalService,
                                     UserService userService,
                                     FieldSubmissionStatusService fieldSubmissionStatusService) {
    this.submissionDetailService = submissionDetailService;
    this.submissionResourceService = submissionResourceService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.submissionApprovalService = submissionApprovalService;
    this.userService = userService;
    this.fieldSubmissionStatusService = fieldSubmissionStatusService;
  }

  @GetMapping
  public ModelAndView showApproveConfirmationPage(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (!submissionApprovalService.canApproveSubmission(submissionDetail)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Submission cannot be accepted by user with ID %s "
          .formatted(userService.getUserWuaId()));
    }

    return getApproveConfirmationModelAndView(submissionDetail);
  }

  @PostMapping
  public ModelAndView approveSubmission(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                        @Nullable RedirectAttributes redirectAttributes) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    submissionApprovalService.approveSubmissionOrThrow(submissionDetail);
    approvedSubmissionNotificationBanner(submissionDetail, redirectAttributes);

    return ReverseRouter.redirect(on(ViewResourceRecordController.class)
        .showResourceRecord(submissionDetail.getSubmission().getResource().getId()));
  }

  private String getPageName(SubmissionDetail submissionDetail) {
    return "Are you sure you want to accept this %s?"
        .formatted(submissionDetailService.getSubmissionTypeNameLowercaseWithAcronym(submissionDetail));
  }

  private void approvedSubmissionNotificationBanner(SubmissionDetail submissionDetail,
                                                    RedirectAttributes redirectAttributes) {
    var banner = NotificationBanner.builder()
        .withTitle("%s submission accepted".formatted(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail)))
        .withBannerType(NotificationBannerType.SUCCESS)
        .withContent("%s - %s has been accepted and applied to the system of record."
            .formatted(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail),
                submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail)))
        .build();

    NotificationBannerUtil.applyNotificationBanner(redirectAttributes, banner);
  }

  private ModelAndView getApproveConfirmationModelAndView(SubmissionDetail submissionDetail) {
    var pageName = getPageName(submissionDetail);
    var modelAndView = submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(
        submissionDetail,
        "remi/review/approveSubmissionConfirmation"
    );

    var resourceName = submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail);
    var isFieldSubmission = submissionDetail.isFieldSubmission();

    modelAndView
        .addObject("pageName", pageName)
        .addObject("resourceName", resourceName)
        .addObject("submissionType", submissionDetail.getSubmissionType())
        .addObject("actionUrl",
            ReverseRouter.route(on(ApproveSubmissionController.class)
                .approveSubmission(submissionDetail.getId(), null)))
        .addObject("cancelUrl",
            ReverseRouter.route(on(ReviewSubmissionController.class)
                .showReviewPage(submissionDetail.getSubmission().getResource().getId())))
        .addObject("isFieldSubmission", isFieldSubmission);

    if (isFieldSubmission)  {
      var fieldStatusAfterAccept = fieldSubmissionStatusService.getNewStatusForAcceptance(submissionDetail)
          .map(FieldStatus::getDisplayName)
          .orElseThrow(() -> new NoSuchElementException(
              "Cannot accept the submissionDetail with id %s because the field status can not be updated"
              .formatted(submissionDetail.getId())));

      modelAndView.addObject("fieldStatusAfterAccept", fieldStatusAfterAccept);
    }

    ReviewPageBreadcrumbUtil
        .addWorkAreaAndManagementPageToModelAndView(modelAndView, pageName, resourceName, submissionDetail);

    return modelAndView;
  }
}
