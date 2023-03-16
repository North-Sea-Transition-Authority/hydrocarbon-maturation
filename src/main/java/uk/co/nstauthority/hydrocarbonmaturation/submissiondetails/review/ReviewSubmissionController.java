package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;

@Controller
@RequestMapping("/review/{submissionDetailId}")
@HasSubmissionStatus(statuses = {SubmissionDetailStatus.SUBMITTED})
public class ReviewSubmissionController {

  private final ReviewSubmissionService reviewSubmissionService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final SubmissionDetailService submissionDetailService;
  private final UserDetailService userDetailService;

  @Autowired
  public ReviewSubmissionController(ReviewSubmissionService reviewSubmissionService,
                                    SubmissionSummaryModelService submissionSummaryModelService,
                                    SubmissionDetailService submissionDetailService,
                                    UserDetailService userDetailService) {
    this.reviewSubmissionService = reviewSubmissionService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.submissionDetailService = submissionDetailService;
    this.userDetailService = userDetailService;
  }

  @GetMapping
  public ModelAndView showReviewPage(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionRecord = reviewSubmissionService.getSubmissionRecordOrThrow(submissionDetailId);
    var userDetail = userDetailService.getUserDetail();

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(
        submissionRecord.submissionDetailId());

    if (!reviewSubmissionService.canViewSubmission(submissionRecord, userDetail, submissionDetail)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Submission cannot be reviewed by user with ID %s ".formatted(userDetail.wuaId()));
    }

    return getModelAndView(submissionRecord, submissionDetail);
  }

  private ModelAndView getModelAndView(SubmissionRecordSummaryView submissionRecord, SubmissionDetail submissionDetail) {
    var modelAndView = submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(
        submissionDetail,
        "remi/review/reviewSubmission"
    );

    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(submissionRecord.resourceNameAndCentreOfInterest())
        .addWorkAreaBreadcrumb()
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);

    return modelAndView
        .addObject("submissionRecord", submissionRecord)
        .addObject("submissionType", submissionDetail.getSubmissionType().getTypeName())
        .addObject("canReview", reviewSubmissionService.canReviewSubmission(
            submissionRecord, userDetailService.getUserDetail()))
        .addObject("approveUrl",
            ReverseRouter.route(on(ApproveSubmissionController.class)
                .showApproveConfirmationPage(submissionDetail.getId())))
        .addObject("regulatorManagedInformationUrl",
            ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .getRegulatorManagedInformationPage(
                    submissionDetail.getResourceId().getId(), submissionDetail.getId()))
        );
  }
}
