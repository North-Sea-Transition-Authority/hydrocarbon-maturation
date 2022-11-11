package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
  void getSection() {
    var taskListSectionOptional = postWellDrillTaskListSectionService.getSection(submissionDetail);

    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder)
        .containsExactly(List.of(
            new TaskListItem("Post well drill",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(DryWellDetailsController.class).getDryWellDetails(submissionDetail.getId(), null)))),
            "Post well drill",
            30
        );
  }
}