package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSubmissionService;

@Service
class PostWellDrillTaskListSectionService implements TaskListSectionService {

  private final DryWellDetailsSubmissionService dryWellDetailsSubmissionService;

  @Autowired
  PostWellDrillTaskListSectionService(DryWellDetailsSubmissionService dryWellDetailsSubmissionService) {
    this.dryWellDetailsSubmissionService = dryWellDetailsSubmissionService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {
    if (submissionDetail.getSubmissionType() == SubmissionType.POST_DRILL_DRY_HOLE) {
      var items = List.of(
          new TaskListItem("Dry well details",
              TaskListLabel.notStartedOrComplete(
                  dryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail)),
              ReverseRouter.route(
                  on(DryWellDetailsController.class).getDryWellDetails(submissionDetail.getId(), null)))
      );
      return Optional.of(new TaskListSection("Post well drill", 30, items));
    }

    return Optional.empty();
  }

  @Override
  public Boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    if (submissionDetail.getSubmissionType() == SubmissionType.POST_DRILL_DRY_HOLE) {
      return dryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail);
    } else {
      return true;
    }
  }
}
