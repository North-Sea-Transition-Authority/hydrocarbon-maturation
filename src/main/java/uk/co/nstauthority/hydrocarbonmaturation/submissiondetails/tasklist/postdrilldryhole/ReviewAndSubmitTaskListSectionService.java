package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.PostDrillDryWellSubmissionSummaryController;

@Service
public class ReviewAndSubmitTaskListSectionService implements TaskListSectionService {

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {

    var items = List.of(
        new TaskListItem("Review and submit",
            ReverseRouter.route(on(PostDrillDryWellSubmissionSummaryController.class)
                .showSubmissionSummary(submissionDetail.getId())),
            ShowOnSubNav.FALSE)
    );

    return Optional.of(new TaskListSection("Review and submit", Integer.MAX_VALUE, items));
  }
}
