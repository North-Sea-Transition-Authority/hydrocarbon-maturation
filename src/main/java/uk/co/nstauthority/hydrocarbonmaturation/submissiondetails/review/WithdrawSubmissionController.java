package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.annotation.Nullable;
import java.util.UUID;
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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
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
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Controller
@RequestMapping("/review/{submissionDetailId}/withdraw-submission")
@HasSubmissionStatus(statuses = {SubmissionDetailStatus.SUBMITTED})
@HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
@IsLatestExtantSubmissionDetailVersion
public class WithdrawSubmissionController {

  private final SubmissionDetailService submissionDetailService;
  private final SubmissionResourceService submissionResourceService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final WithdrawSubmissionService withdrawSubmissionService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public WithdrawSubmissionController(SubmissionDetailService submissionDetailService,
                                      SubmissionResourceService submissionResourceService,
                                      SubmissionSummaryModelService submissionSummaryModelService,
                                      WithdrawSubmissionService withdrawSubmissionService,
                                      ControllerHelperService controllerHelperService) {
    this.submissionDetailService = submissionDetailService;
    this.submissionResourceService = submissionResourceService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.withdrawSubmissionService = withdrawSubmissionService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView showWithdrawConfirmationPage(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                   User user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (!withdrawSubmissionService.canWithdrawSubmission(submissionDetail, user)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Submission cannot be withdrawn by user with ID %s "
          .formatted(user.wuaId().intValue()));
    }

    return getWithdrawConfirmationModelAndView(submissionDetail, new WithdrawSubmissionForm(), user);
  }

  @PostMapping
  public ModelAndView withdrawSubmission(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @ModelAttribute("form") WithdrawSubmissionForm form,
                                         BindingResult bindingResult,
                                         User user,
                                         @Nullable RedirectAttributes redirectAttributes) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = withdrawSubmissionService.validate(bindingResult, form);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getWithdrawConfirmationModelAndView(submissionDetail, form, user),
        () -> {
          withdrawSubmissionService.withdrawSubmissionOrThrow(submissionDetail, form, user);
          withdrawSubmissionNotificationBanner(submissionDetail, redirectAttributes);
          return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea(null));
        });
  }

  private String getPageName(SubmissionDetail submissionDetail) {
    return "Are you sure you want to withdraw this %s?"
        .formatted(submissionDetailService.getSubmissionTypeNameLowercaseWithAcronym(submissionDetail));
  }

  private void withdrawSubmissionNotificationBanner(SubmissionDetail submissionDetail,
                                                    RedirectAttributes redirectAttributes) {
    var banner = NotificationBanner.builder()
        .withTitle("%s submission withdrawn".formatted(
            submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail)))
        .withBannerType(NotificationBannerType.SUCCESS)
        .withContent("%s - %s has been withdrawn."
            .formatted(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail),
                submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(
                    submissionDetail)))
        .build();

    NotificationBannerUtil.applyNotificationBanner(redirectAttributes, banner);
  }

  private ModelAndView getWithdrawConfirmationModelAndView(SubmissionDetail submissionDetail,
                                                           WithdrawSubmissionForm form,
                                                           User user) {
    var pageName = getPageName(submissionDetail);
    var modelAndView = submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(
        submissionDetail,
        "remi/review/withdrawSubmissionConfirmation",
        user
    );

    var resourceName = submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(
        submissionDetail);

    modelAndView
        .addObject("form", form)
        .addObject("pageName", pageName)
        .addObject("resourceName", resourceName)
        .addObject("submissionType", submissionDetail.getSubmissionType())
        .addObject("actionUrl",
            ReverseRouter.route(on(WithdrawSubmissionController.class)
                .withdrawSubmission(submissionDetail.getId(), null, null, null, null)))
        .addObject("cancelUrl",
            ReverseRouter.route(on(ReviewSubmissionController.class)
                .showReviewPage(submissionDetail.getSubmission().getResource().getId(), null)));

    ReviewPageBreadcrumbUtil
        .addWorkAreaAndManagementPageToModelAndView(modelAndView, pageName, resourceName, submissionDetail);

    return modelAndView;
  }
}
