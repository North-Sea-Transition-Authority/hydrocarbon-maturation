package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsLatestExtantSubmissionDetailVersion;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewPageBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/start-update")
@HasSubmissionStatus(statuses = {SubmissionDetailStatus.SUBMITTED})
@IsFromResponsibleTeam
@IsLatestExtantSubmissionDetailVersion
public class StartUpdateController {

  private static final String PAGE_NAME = "Update submission";
  private final SubmissionDetailService submissionDetailService;
  private final SubmissionUpdatePersistenceService submissionUpdatePersistenceService;
  private final SubmissionUpdateService submissionUpdateService;
  private final SubmissionResourceService submissionResourceService;

  @Autowired
  public StartUpdateController(SubmissionDetailService submissionDetailService,
                               SubmissionUpdatePersistenceService submissionUpdatePersistenceService,
                               SubmissionUpdateService submissionUpdateService,
                               SubmissionResourceService submissionResourceService) {
    this.submissionDetailService = submissionDetailService;
    this.submissionUpdatePersistenceService = submissionUpdatePersistenceService;
    this.submissionUpdateService = submissionUpdateService;
    this.submissionResourceService = submissionResourceService;
  }

  @GetMapping
  public ModelAndView showStartUpdatePage(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService
        .getSubmissionDetailForIdOrError(submissionDetailId);

    if (!submissionUpdateService.canStartUpdateSubmissionDetailOnSubmission(submissionDetail.getSubmission())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          "Could not start an update due to missing update request for submission %s or a draft update exists"
              .formatted(submissionDetail.getSubmission().getId()));
    }

    return getModelAndView(submissionDetail);
  }

  @PostMapping
  public ModelAndView startUpdate(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService
        .getSubmissionDetailForIdOrError(submissionDetailId);

    if (submissionUpdateService.canStartUpdateSubmissionDetailOnSubmission(submissionDetail.getSubmission())) {
      var updateSubmissionDetail = submissionUpdatePersistenceService.startUpdate(submissionDetail);

      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(updateSubmissionDetail.getId()));
    } else {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          "Could not start an update due to missing update request for submission %s or a draft update exists"
              .formatted(submissionDetail.getSubmission().getId()));
    }
  }

  private ModelAndView getModelAndView(SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/review/startUpdateSubmission");
    var submissionUpdate = submissionUpdatePersistenceService
        .getLatestUnresolvedSubmissionUpdateBySubmission(submissionDetail.getSubmission())
        .orElseThrow(() -> new RemiEntityNotFoundException(
            "Could not find an unresolved submission update for submission %s"
                .formatted(submissionDetail.getSubmission().getId())));
    var resourceName = submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(
        submissionDetail);

    ReviewPageBreadcrumbUtil.addWorkAreaAndManagementPageToModelAndView(
        modelAndView, PAGE_NAME, resourceName, submissionDetail);
    return modelAndView
        .addObject("startUpdateUrl", ReverseRouter.route(on(StartUpdateController.class)
            .startUpdate(submissionDetail.getId())))
        .addObject("pageTitle", PAGE_NAME)
        .addObject("updateRequestReason", submissionUpdate.getReason());
  }
}
