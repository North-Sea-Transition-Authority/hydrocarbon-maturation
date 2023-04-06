package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.PostDrillDryWellSubmissionSummaryController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ProspectSubmissionSummaryController;

class ReviewAndSubmitTaskListSectionServiceTest {

  private ReviewAndSubmitTaskListSectionService reviewAndSubmitTaskListSectionService;
  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    reviewAndSubmitTaskListSectionService = new ReviewAndSubmitTaskListSectionService();
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @Test
  void getSection_prospectSubmission() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    var taskListSectionOptional = reviewAndSubmitTaskListSectionService.getSection(submissionDetail);
    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(TaskListSection::items)
        .isEqualTo(List.of(
            new TaskListItem("Review and submit",
                ReverseRouter.route(on(ProspectSubmissionSummaryController.class).showSubmissionSummary(submissionDetail.getId())))
        ));
  }

  @Test
  void getSection_dryWellSubmission() {
    var taskListSectionOptional = reviewAndSubmitTaskListSectionService.getSection(submissionDetail);
    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder)
        .containsExactly(List.of(
            new TaskListItem("Review and submit",
                ReverseRouter.route(on(PostDrillDryWellSubmissionSummaryController.class).showSubmissionSummary(submissionDetail.getId())))),
            "Review and submit",
            Integer.MAX_VALUE
        );
  }

  @Test
  void isSectionSubmittable_assertTrue() {
    var submittable = reviewAndSubmitTaskListSectionService.isSectionSubmittable(submissionDetail);
    assertTrue(submittable);
  }
}