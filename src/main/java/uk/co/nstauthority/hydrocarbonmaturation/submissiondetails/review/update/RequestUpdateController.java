package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewPageBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/review/{submissionDetailId}/request-update")
@HasSubmissionStatus(statuses = {SubmissionDetailStatus.SUBMITTED})
@HasPermission(permissions = {
    RolePermission.EXPLORATION,
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER})
public class RequestUpdateController {

  private final SubmissionDetailService submissionDetailService;
  private final SubmissionResourceService submissionResourceService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final SubmissionUpdateService submissionUpdateService;
  private final SubmissionUpdatePersistenceService submissionUpdatePersistenceService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public RequestUpdateController(SubmissionDetailService submissionDetailService,
                                 SubmissionResourceService submissionResourceService,
                                 SubmissionSummaryModelService submissionSummaryModelService,
                                 SubmissionUpdateService submissionUpdateService,
                                 SubmissionUpdatePersistenceService submissionUpdatePersistenceService,
                                 ControllerHelperService controllerHelperService) {
    this.submissionDetailService = submissionDetailService;
    this.submissionResourceService = submissionResourceService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.submissionUpdateService = submissionUpdateService;
    this.submissionUpdatePersistenceService = submissionUpdatePersistenceService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView showRequestUpdateConfirmationPage(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetail(submissionDetailId)
        .orElseThrow(() -> new RemiEntityNotFoundException("Submission detail with id %s not found"
            .formatted(submissionDetailId)));
    if (!submissionUpdateService.canRequestUpdateOnSubmission(submissionDetail)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Already requested an update for submission %s ".formatted(submissionDetailId));
    }
    return getRequestUpdateConfirmationModelAndView(submissionDetail, new RequestUpdateForm());
  }

  @PostMapping
  public ModelAndView requestUpdate(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                    @Nullable RedirectAttributes redirectAttributes,
                                    @ModelAttribute("form") RequestUpdateForm form,
                                    BindingResult bindingResult) {
    var submissionDetail = submissionDetailService.getSubmissionDetail(submissionDetailId)
        .orElseThrow(() -> new RemiEntityNotFoundException("Submission detail with id %s not found"
            .formatted(submissionDetailId)));
    if (!submissionUpdateService.canRequestUpdateOnSubmission(submissionDetail)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Already requested an update for submission %s ".formatted(submissionDetailId));
    }
    bindingResult = submissionUpdateService.validate(bindingResult, form);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getRequestUpdateConfirmationModelAndView(submissionDetail, form),
        () -> {
          submissionUpdatePersistenceService.requestUpdate(submissionDetail, form);
          requestUpdateSubmissionNotificationBanner(submissionDetail, redirectAttributes);
          return ReverseRouter.redirect(on(ReviewSubmissionController.class)
              .showReviewPage(submissionDetail.getSubmission().getResource().getId()));
        });
  }

  private String getPageName(SubmissionDetail submissionDetail) {
    return "Are you sure you want to request an update for this %s?"
        .formatted(submissionDetailService.getSubmissionTypeNameLowercaseWithAcronym(submissionDetail));
  }

  private void requestUpdateSubmissionNotificationBanner(SubmissionDetail submissionDetail,
                                                         RedirectAttributes redirectAttributes) {
    var banner = NotificationBanner.builder()
        .withTitle("Update requested")
        .withBannerType(NotificationBannerType.SUCCESS)
        .withContent("An update has been requested for %s - %s"
            .formatted(submissionDetail.getSubmission().getResource().getType().getDisplayName(),
                submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail)))
        .build();

    NotificationBannerUtil.applyNotificationBanner(redirectAttributes, banner);
  }

  private ModelAndView getRequestUpdateConfirmationModelAndView(SubmissionDetail submissionDetail,
                                                                RequestUpdateForm form) {
    var pageName = getPageName(submissionDetail);
    var modelAndView = submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(
        submissionDetail,
        "remi/review/requestUpdateSubmissionConfirmation"
    );

    var resourceName = submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail);

    modelAndView
        .addObject("form", form)
        .addObject("pageName", pageName)
        .addObject("resourceName", resourceName)
        .addObject("submissionType", submissionDetail.getSubmissionType())
        .addObject("actionUrl",
            ReverseRouter.route(on(RequestUpdateController.class)
                .requestUpdate(submissionDetail.getId(), null, null, null)))
        .addObject("cancelUrl",
            ReverseRouter.route(on(ReviewSubmissionController.class)
                .showReviewPage(submissionDetail.getSubmission().getResource().getId())));

    ReviewPageBreadcrumbUtil
        .addWorkAreaAndManagementPageToModelAndView(modelAndView, pageName, resourceName, submissionDetail);

    return modelAndView;
  }
}

