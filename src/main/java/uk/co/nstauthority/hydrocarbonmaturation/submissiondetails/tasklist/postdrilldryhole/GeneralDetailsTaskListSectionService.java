package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsController;

@Service
class GeneralDetailsTaskListSectionService implements TaskListSectionService {

  private final GeneralDetailSubmissionService generalDetailSubmissionService;

  @Autowired
  GeneralDetailsTaskListSectionService(GeneralDetailSubmissionService generalDetailSubmissionService) {
    this.generalDetailSubmissionService = generalDetailSubmissionService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {

    var items = List.of(
        new TaskListItem("General details",
            TaskListLabel.notStartedOrComplete(generalDetailSubmissionService.isSectionSubmittable(submissionDetail)),
            ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(submissionDetail.getId(), null)))
    );

    return Optional.of(new TaskListSection("General details", 10, items));
  }
}
