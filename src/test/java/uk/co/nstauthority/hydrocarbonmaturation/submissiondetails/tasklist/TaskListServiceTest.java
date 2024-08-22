package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class TaskListServiceTest {
  private static final User USER = UserTestUtil.Builder().build();

  private List<TaskListSectionService> taskListSections;

  private TaskListService taskListService;

  private SubmissionDetail submissionDetail;

  @Mock
  private ReservoirTaskListSectionService reservoirTaskListSectionService;

  @Mock
  private PostWellDrillTaskListSectionService postWellDrillTaskListSectionService;


  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    taskListSections = List.of(reservoirTaskListSectionService, postWellDrillTaskListSectionService);
    taskListService = new TaskListService(taskListSections);
  }

  @Test
  void getAllSections() {
    var section1 = Optional.of(new TaskListSection("General details", 10, List.of()));
    var section2 = Optional.of(new TaskListSection("section 2", 20, List.of()));

    when(postWellDrillTaskListSectionService.getSection(submissionDetail, USER)).thenReturn(section1);
    when(reservoirTaskListSectionService.getSection(submissionDetail, USER)).thenReturn(section2);

    var sections = taskListService.getAllSections(submissionDetail, USER);

    assertThat(sections)
        .containsExactly(section1.get(), section2.get());
  }
}