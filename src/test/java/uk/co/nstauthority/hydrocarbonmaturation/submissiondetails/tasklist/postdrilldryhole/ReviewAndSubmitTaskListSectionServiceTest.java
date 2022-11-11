package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryController;

class ReviewAndSubmitTaskListSectionServiceTest {

  private ReviewAndSubmitTaskListSectionService reviewAndSubmitTaskListSectionService;
  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    reviewAndSubmitTaskListSectionService = new ReviewAndSubmitTaskListSectionService();
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @Test
  void getSection() {
    var taskListSectionOptional = reviewAndSubmitTaskListSectionService.getSection(submissionDetail);

    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder)
        .containsExactly(List.of(
            new TaskListItem("Review and submit",
                ReverseRouter.route(on(SubmissionSummaryController.class).showSubmissionSummary(submissionDetail.getId())))),
            "Review and submit",
            Integer.MAX_VALUE
        );
  }
}