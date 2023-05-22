package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsLatestExtantSubmissionDetailVersion;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Controller
@RequestMapping("/review/{submissionDetailId}/withdraw-submission")
@HasSubmissionStatus(statuses = {SubmissionDetailStatus.SUBMITTED})
@HasPermission(permissions = {RolePermission.EXPLORATION})
@IsLatestExtantSubmissionDetailVersion
public class WithdrawSubmissionController {

  private final SubmissionDetailService submissionDetailService;
  private final SubmissionResourceService submissionResourceService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final WithdrawSubmissionService withdrawSubmissionService;
  private final ControllerHelperService controllerHelperService;
  private final UserDetailService userDetailService;

  @Autowired
  public WithdrawSubmissionController(SubmissionDetailService submissionDetailService,
                                      SubmissionResourceService submissionResourceService,
                                      SubmissionSummaryModelService submissionSummaryModelService,
                                      WithdrawSubmissionService withdrawSubmissionService,
                                      ControllerHelperService controllerHelperService,
                                      UserDetailService userDetailService) {
    this.submissionDetailService = submissionDetailService;
    this.submissionResourceService = submissionResourceService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.withdrawSubmissionService = withdrawSubmissionService;
    this.controllerHelperService = controllerHelperService;
    this.userDetailService = userDetailService;
  }

  @GetMapping
  public ModelAndView showWithdrawConfirmationPage(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (!withdrawSubmissionService.canWithdrawSubmission(submissionDetail)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Submission cannot be withdrawn by user with ID %s "
          .formatted(userDetailService.getUserWuaId()));
    }

    return getWithdrawConfirmationModelAndView(submissionDetail, new WithdrawSubmissionForm());
  }

  @PostMapping
  public ModelAndView withdrawSubmission(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @ModelAttribute("form") WithdrawSubmissionForm form,
                                         BindingResult bindingResult,
                                         @Nullable RedirectAttributes redirectAttributes) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = withdrawSubmissionService.validate(bindingResult, form);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getWithdrawConfirmationModelAndView(submissionDetail, form),
        () -> {
          withdrawSubmissionService.withdrawSubmissionOrThrow(submissionDetail, form);
          withdrawSubmissionNotificationBanner(submissionDetail, redirectAttributes);
          return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea());
        });
  }

  private String getPageName(SubmissionDetail submissionDetail) {
    return "Are you sure you want to withdraw this %s?"
        .formatted(submissionDetailService.getSubmissionTypeName(submissionDetail).toLowerCase());
  }

  private void withdrawSubmissionNotificationBanner(SubmissionDetail submissionDetail,
                                                    RedirectAttributes redirectAttributes) {
    var banner = NotificationBanner.builder()
        .withTitle("%s submission withdrawn".formatted(submissionDetailService.getSubmissionTypeName(submissionDetail)))
        .withBannerType(NotificationBannerType.SUCCESS)
        .withContent("%s - %s has been withdrawn."
            .formatted(submissionDetail.getSubmission().getResource().getType().getDisplayName(),
                submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail)))
        .build();

    NotificationBannerUtil.applyNotificationBanner(redirectAttributes, banner);
  }

  private ModelAndView getWithdrawConfirmationModelAndView(SubmissionDetail submissionDetail,
                                                           WithdrawSubmissionForm form) {
    var pageName = getPageName(submissionDetail);
    var modelAndView = submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(
        submissionDetail,
        "remi/review/withdrawSubmissionConfirmation"
    );

    var resourceName = submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail);

    modelAndView
        .addObject("form", form)
        .addObject("pageName", pageName)
        .addObject("resourceName", resourceName)
        .addObject("submissionType", submissionDetail.getSubmissionType())
        .addObject("actionUrl",
            ReverseRouter.route(on(WithdrawSubmissionController.class)
                .withdrawSubmission(submissionDetail.getId(), null, null, null)))
        .addObject("cancelUrl",
            ReverseRouter.route(on(ReviewSubmissionController.class)
                .showReviewPage(submissionDetail.getSubmission().getResource().getId())));

    ReviewPageBreadcrumbUtil
        .addWorkAreaAndManagementPageToModelAndView(modelAndView, pageName, resourceName, submissionDetail);

    return modelAndView;
  }
}
