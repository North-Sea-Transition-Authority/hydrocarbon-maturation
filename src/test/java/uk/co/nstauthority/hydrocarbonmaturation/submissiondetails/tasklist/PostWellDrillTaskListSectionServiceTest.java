package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSubmissionService;

@ExtendWith(MockitoExtension.class)
class PostWellDrillTaskListSectionServiceTest {

  @Mock
  private DryWellDetailsSubmissionService dryWellDetailsSubmissionService;

  @InjectMocks
  private PostWellDrillTaskListSectionService postWellDrillTaskListSectionService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @Test
  void getSection_postDrillSubmission() {
    var taskListSectionOptional = postWellDrillTaskListSectionService.getSection(submissionDetail);

    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder)
        .containsExactly(List.of(
            new TaskListItem("Dry well details",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(DryWellDetailsController.class).getDryWellDetails(submissionDetail.getId(), null)))),
            "Post well drill",
            30
        );
  }

  @Test
  void getSection_prospectSubmission() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    var taskListSectionOptional = postWellDrillTaskListSectionService.getSection(submissionDetail);

    assertThat(taskListSectionOptional)
        .isNotPresent();
  }

  @Test
  void isSectionSubmittable_prospectSubmission_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);

    assertTrue(postWellDrillTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_postDrillSubmissionWhenSubmittable_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    when(dryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    assertTrue(postWellDrillTaskListSectionService.isSectionSubmittable(submissionDetail));
  }
}