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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistorySearchSelectorModelService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionSearchSelectorForm;

@Controller
@RequestMapping("/review/resource/{resourceId}")
@AccessibleByServiceUsers
public class ReviewSubmissionController {

  private final ReviewSubmissionService reviewSubmissionService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final SubmissionDetailService submissionDetailService;
  private final UserDetailService userDetailService;
  private final SubmissionHistorySearchSelectorModelService submissionHistorySearchSelectorModelService;

  @Autowired
  public ReviewSubmissionController(ReviewSubmissionService reviewSubmissionService,
                                    SubmissionSummaryModelService submissionSummaryModelService,
                                    SubmissionDetailService submissionDetailService,
                                    UserDetailService userDetailService,
                                    SubmissionHistorySearchSelectorModelService submissionHistorySearchSelectorModelService) {
    this.reviewSubmissionService = reviewSubmissionService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.submissionDetailService = submissionDetailService;
    this.userDetailService = userDetailService;
    this.submissionHistorySearchSelectorModelService = submissionHistorySearchSelectorModelService;
  }

  @GetMapping
  public ModelAndView showReviewPage(@PathVariable("resourceId") UUID resourceId) {
    var submissionRecord = reviewSubmissionService.getSubmissionRecordOrThrow(resourceId);
    var userDetail = userDetailService.getUserDetail();

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(
        submissionRecord.submissionDetailId());

    if (!reviewSubmissionService.canViewSubmission(submissionRecord, userDetail, submissionDetail)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "Submission %s cannot be reviewed by user with ID %s "
              .formatted(submissionDetail.getId(), userDetail.wuaId()));
    }

    var form = new SubmissionSearchSelectorForm();
    form.setSelectedSubmissionId(submissionRecord.submissionDetailId().toString());
    return getModelAndView(submissionRecord, submissionDetail, form);
  }

  @GetMapping("/{submissionDetailId}")
  public ModelAndView showSubmissionSummary(@PathVariable("resourceId") UUID resourceId,
                                            @PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @ModelAttribute("form") SubmissionSearchSelectorForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var submissionRecord = reviewSubmissionService.getSubmissionRecordOrThrow(resourceId);
    var userDetail = userDetailService.getUserDetail();

    if (!reviewSubmissionService.canViewSubmission(submissionRecord, userDetail, submissionDetail)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Submission cannot be reviewed by user with ID %s ".formatted(userDetail.wuaId()));
    }

    form.setSelectedSubmissionId(submissionDetailId.toString());
    return getModelAndView(submissionRecord, submissionDetail, form);
  }

  @PostMapping
  public ModelAndView viewSubmission(@PathVariable("resourceId") UUID resourceId,
                                     @ModelAttribute("form") SubmissionSearchSelectorForm form) {
    return ReverseRouter.redirect(on(ReviewSubmissionController.class)
        .showSubmissionSummary(resourceId, UUID.fromString(form.getSelectedSubmissionId()), null));
  }

  private ModelAndView getModelAndView(SubmissionRecordSummaryView submissionRecord,
                                       SubmissionDetail submissionDetail,
                                       SubmissionSearchSelectorForm form) {
    var modelAndView = submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(
        submissionDetail,
        "remi/review/reviewSubmission"
    );

    modelAndView = submissionHistorySearchSelectorModelService
        .addReviewSubmissionSearchSelectorItemsToModelAndView(modelAndView, submissionDetail);

    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(submissionRecord.resourceNameAndCentreOfInterest())
        .addWorkAreaBreadcrumb()
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);

    return modelAndView
        .addObject("form", form)
        .addObject("submissionRecord", submissionRecord)
        .addObject("submissionType", submissionDetail.getSubmissionType())
        .addObject("canReview", reviewSubmissionService.canReviewSubmission(
            submissionRecord, userDetailService.getUserDetail(), submissionDetail))
        .addObject("resourceType", submissionDetail.getResourceId().getType().getDisplayName())
        .addObject("approveUrl",
            ReverseRouter.route(on(ApproveSubmissionController.class)
                .showApproveConfirmationPage(submissionDetail.getId())))
        .addObject("regulatorManagedInformationUrl",
            ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .getRegulatorManagedInformationPage(
                    submissionDetail.getResourceId().getId(), submissionDetail.getId())))
        .addObject("withdrawUrl",
            ReverseRouter.route(on(WithdrawSubmissionController.class)
                .showWithdrawConfirmationPage(submissionDetail.getId()))
        );
  }
}
