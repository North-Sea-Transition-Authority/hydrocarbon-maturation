package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.DiscoverySubmissionSummaryController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.PostDrillDryWellSubmissionSummaryController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ProspectSubmissionSummaryController;

@Service
public class ReviewAndSubmitTaskListSectionService implements TaskListSectionService {

  private static final String REVIEW_SUBMIT_ITEM_NAME = "Review and submit";

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {

    var items = getTaskListItemsBySubmissionType(submissionDetail);

    return Optional.of(new TaskListSection(REVIEW_SUBMIT_ITEM_NAME, Integer.MAX_VALUE, items));
  }

  @Override
  public Boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    return true;
  }

  private List<TaskListItem> getTaskListItemsBySubmissionType(SubmissionDetail submissionDetail) {
    return switch (submissionDetail.getSubmissionType()) {
      case CREATE_PROSPECT, PROSPECT_UPDATE, RELINQUISHMENT_PROSPECT ->
          List.of(new TaskListItem(REVIEW_SUBMIT_ITEM_NAME,
              ReverseRouter.route(on(ProspectSubmissionSummaryController.class)
                  .showSubmissionSummary(submissionDetail.getId()))));
      case POST_DRILL_DRY_HOLE ->
          List.of(new TaskListItem(REVIEW_SUBMIT_ITEM_NAME,
              ReverseRouter.route(on(PostDrillDryWellSubmissionSummaryController.class)
                  .showSubmissionSummary(submissionDetail.getId()))));
      case CREATE_DISCOVERY_FROM_PROSPECT, UPDATE_DISCOVERY, CREATE_DISCOVERY ->
          List.of(new TaskListItem(REVIEW_SUBMIT_ITEM_NAME,
              ReverseRouter.route(on(DiscoverySubmissionSummaryController.class)
                  .showSubmissionSummary(submissionDetail.getId()))));
    };
  }
}
