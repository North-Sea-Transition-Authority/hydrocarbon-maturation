package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchProspectAndDiscoveryRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

@Controller
@RequestMapping("/correction/{submissionDetailId}/delete-submission")
@HasPermission(permissions = {
    RolePermission.EXPLORATION,
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER})
@HasSubmissionStatus(statuses = SubmissionDetailStatus.ACCEPTED)
public class DeleteSubmissionFromCorrectionController {

  public static final String VIEW_NAME = "remi/systemofrecord/corrections/deleteSubmissionFromCorrection";

  private final SubmissionDetailService submissionDetailService;
  private final SubmissionResourceService submissionResourceService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final CorrectionService correctionService;

  @Autowired
  public DeleteSubmissionFromCorrectionController(SubmissionDetailService submissionDetailService,
                                                  SubmissionResourceService submissionResourceService,
                                                  SubmissionSummaryModelService submissionSummaryModelService,
                                                  CorrectionService correctionService) {
    this.submissionDetailService = submissionDetailService;
    this.submissionResourceService = submissionResourceService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.correctionService = correctionService;
  }

  @GetMapping
  public ModelAndView showDeleteConfirmationPage(@PathVariable("submissionDetailId") UUID submissionDetailId, User user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var resource = submissionDetail.getSubmission().getResource();

    if (!correctionService.canStartCorrection(resource, user)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "User with id %s cannot start a correction for resource %s"
              .formatted(user.wuaId(), resource.getId())
      );
    }

    var pageName = getPageName(submissionDetail);
    var modelAndView = submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(
        submissionDetail,
        VIEW_NAME
    );
    modelAndView
        .addObject("pageName", pageName)
        .addObject("resourceName", submissionResourceService.getResourceNameOrDefault(submissionDetail))
        .addObject("submissionType", submissionDetail.getSubmissionType())
        .addObject("deleteButtonText", "Delete submission")
        .addObject("deleteUrl",
            ReverseRouter.route(on(DeleteSubmissionFromCorrectionController.class)
                .deleteSubmission(submissionDetailId, null, null)))
        .addObject("cancelUrl",
            ReverseRouter.route(on(CorrectionSelectionController.class).startCorrection(resource.getId(), user)));

    return modelAndView;
  }

  @PostMapping
  public ModelAndView deleteSubmission(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                       User user,
                                       RedirectAttributes redirectAttributes) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    correctionService.deleteSubmission(submissionDetail, user);

    NotificationBannerUtil.applyNotificationBanner(
        redirectAttributes, getNotificationBanner(submissionDetail));

    if (submissionDetail.isInitialResourceSubmission()) {
      return ReverseRouter.redirect(on(SearchProspectAndDiscoveryRecordsController.class)
          .showSearchResourceRecordsScreen(null));
    } else {
      return ReverseRouter.redirect(on(ViewResourceRecordController.class)
          .showResourceRecord(submissionDetail.getSubmission().getResource().getId()));
    }
  }

  private String getPageName(SubmissionDetail submissionDetail) {
    var submissionTypeName = submissionDetailService.getSubmissionTypeNameLowercaseWithAcronym(submissionDetail);
    return String.format("Are you sure you want to delete this %s?", submissionTypeName);
  }

  private NotificationBanner getNotificationBanner(SubmissionDetail submissionDetail) {
    var bannerContent = "%s for %s has been deleted";
    var bannerTitle = "%s deleted";

    return NotificationBanner.builder()
        .withBannerType(NotificationBannerType.SUCCESS)
        .withContent(bannerContent
            .formatted(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail),
                submissionResourceService.getResourceNameOrDefault(submissionDetail)))
        .withTitle(bannerTitle.formatted(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail)))
        .build();
  }
}