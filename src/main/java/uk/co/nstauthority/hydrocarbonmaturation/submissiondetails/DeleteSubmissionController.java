package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/delete-submission")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@IsFromResponsibleTeam
public class DeleteSubmissionController {

  private final SubmissionDetailService submissionDetailService;
  private final SubmissionResourceService submissionResourceService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final SubmissionDetailPersistenceService submissionDetailPersistenceService;

  @Autowired
  public DeleteSubmissionController(SubmissionDetailService submissionDetailService,
                                    SubmissionResourceService submissionResourceService,
                                    SubmissionSummaryModelService submissionSummaryModelService,
                                    SubmissionDetailPersistenceService submissionDetailPersistenceService) {
    this.submissionDetailService = submissionDetailService;
    this.submissionResourceService = submissionResourceService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
  }

  @GetMapping
  public ModelAndView showDeleteConfirmationPage(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    var pageName = getPageName(submissionDetail);
    var modelAndView = submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(
        submissionDetail,
        "remi/submissiondetails/deleteConfirmation"
    );

    String deleteButtonText;
    if (BooleanUtils.isTrue(submissionDetail.isCorrection())) {
      deleteButtonText = "Delete draft correction";
    } else if (submissionDetail.isUpdateSubmission()) {
      deleteButtonText = "Delete draft update";
    } else {
      deleteButtonText = "Delete";
    }
    modelAndView
        .addObject("pageName", pageName)
        .addObject("resourceName", submissionResourceService.getResourceNameOrDefault(submissionDetail))
        .addObject("submissionType", submissionDetail.getSubmissionType())
        .addObject("deleteButtonText", deleteButtonText)
        .addObject("deleteUrl",
            ReverseRouter.route(on(DeleteSubmissionController.class).deleteSubmission(submissionDetailId, null)))
        .addObject("cancelUrl",
            ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)));

    TaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(modelAndView, "Delete", submissionDetail);

    return modelAndView;
  }

  @PostMapping
  public ModelAndView deleteSubmission(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                       RedirectAttributes redirectAttributes) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    submissionDetailPersistenceService.deleteSubmission(submissionDetail);

    NotificationBannerUtil.applyNotificationBanner(
        redirectAttributes, getNotificationBanner(submissionDetail));

    return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea(null));
  }

  private String getPageName(SubmissionDetail submissionDetail) {
    return "Are you sure you want to delete this %s%s?".formatted(
        submissionDetailService.getSubmissionTypeName(submissionDetail).toLowerCase(),
        BooleanUtils.isTrue(submissionDetail.isCorrection()) ? " correction" : ""
    );
  }

  private NotificationBanner getNotificationBanner(SubmissionDetail submissionDetail) {
    var bannerContent = "";
    var bannerTitle = "";
    if (BooleanUtils.isTrue(submissionDetail.isCorrection())) {
      bannerContent = "%s draft correction for %s has been deleted";
      bannerTitle = "%s draft correction deleted";
    } else if (submissionDetail.isUpdateSubmission()) {
      bannerContent = "%s draft update for %s has been deleted";
      bannerTitle = "%s draft update deleted";
    } else {
      bannerContent = "%s for %s has been deleted";
      bannerTitle = "%s deleted";
    }

    return NotificationBanner.builder()
        .withBannerType(NotificationBannerType.SUCCESS)
        .withContent(bannerContent
            .formatted(submissionDetailService.getSubmissionTypeName(submissionDetail),
                submissionResourceService.getResourceNameOrDefault(submissionDetail)))
        .withTitle(bannerTitle.formatted(submissionDetailService.getSubmissionTypeName(submissionDetail)))
        .build();
  }
}
