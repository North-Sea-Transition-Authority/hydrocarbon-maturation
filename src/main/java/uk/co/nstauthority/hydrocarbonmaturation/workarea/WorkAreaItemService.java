package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@Service
class WorkAreaItemService {

  private final SubmissionWorkAreaServiceFactory submissionWorkAreaServiceFactory;
  private final SubmissionDetailDisplayService submissionDetailDisplayService;
  private final SubmissionDetailService submissionDetailService;


  @Autowired
  WorkAreaItemService(SubmissionWorkAreaServiceFactory submissionWorkAreaServiceFactory,
                      SubmissionDetailDisplayService submissionDetailDisplayService,
                      SubmissionDetailService submissionDetailService) {
    this.submissionWorkAreaServiceFactory = submissionWorkAreaServiceFactory;
    this.submissionDetailDisplayService = submissionDetailDisplayService;
    this.submissionDetailService = submissionDetailService;
  }

  List<WorkAreaItem> getWorkAreaItems() {
    return submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy()
        .getWorkAreaSubmissionItems()
        .stream()
        .map(this::getWorkAreaItem)
        .toList();
  }

  private WorkAreaItem getWorkAreaItem(SubmissionWorkAreaQueryResult submissionWorkAreaQueryResult) {
    return new WorkAreaItem(
        WorkAreaItemType.SUBMISSION,
        getHeadingText(submissionWorkAreaQueryResult),
        submissionWorkAreaQueryResult.getSubmissionType().getTypeName(),
        getActionUrl(submissionWorkAreaQueryResult),
        getWorkAreaItemModelProperties(submissionWorkAreaQueryResult));
  }

  private String getCentreOfInterestBlockDisplay(SubmissionWorkAreaQueryResult submissionWorkAreaQueryResult) {
    return submissionWorkAreaQueryResult.getCentreOfInterestQuadBlock() != null
        ? " (" + submissionWorkAreaQueryResult.getCentreOfInterestQuadBlock() + ")"
        : "";
  }

  private String getHeadingText(SubmissionWorkAreaQueryResult submissionWorkAreaQueryResult) {
    return submissionWorkAreaQueryResult.getResourceName() != null
        ? submissionWorkAreaQueryResult.getResourceName() + getCentreOfInterestBlockDisplay(submissionWorkAreaQueryResult)
        : SubmissionDetailUtil.getDefaultForNamelessResource(submissionWorkAreaQueryResult.getCreatedOn().toInstant());
  }

  private String getActionUrl(SubmissionWorkAreaQueryResult submissionWorkAreaQueryResult) {
    if (submissionWorkAreaQueryResult.getStatus() == SubmissionDetailStatus.DRAFT) {
      return ReverseRouter.route(on(TaskListController.class)
          .getTaskList(submissionWorkAreaQueryResult.getSubmissionDetailId()));
    } else {
      return ReverseRouter.route(on(ReviewSubmissionController.class)
          .showReviewPage(submissionDetailService
              .getSubmissionDetailForIdOrError(
                  submissionWorkAreaQueryResult.getSubmissionDetailId()).getSubmission().getResource().getId()));
    }
  }

  private WorkAreaItemModelProperties getWorkAreaItemModelProperties(
      SubmissionWorkAreaQueryResult submissionWorkAreaQueryResult) {
    var organisationResponsibleId = submissionWorkAreaQueryResult.getOrganisationResponsibleId();
    var licencesTheResourceIsOn = submissionWorkAreaQueryResult.getLicencesTheResourceIsOn();

    return new WorkAreaItemModelProperties()
        .addProperty("organisationResponsible",
            submissionDetailDisplayService.getOrganisationResponsibleDisplay(organisationResponsibleId))
        .addProperty("licences", submissionDetailDisplayService.getLicencesForDisplay(licencesTheResourceIsOn))
        .addProperty("submissionStatus", submissionWorkAreaQueryResult.getStatus().getDisplayName());
  }
}
