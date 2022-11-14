package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.SourceAndSealTaskListItem;

@ExtendWith(MockitoExtension.class)
class TasklistSummaryItemTest {

  private TasklistSummaryItem tasklistSummaryItem;

  @Mock
  private SourceAndSealSubmissionService sourceAndSealSubmissionService;

  @BeforeEach
  void setUp() {
    tasklistSummaryItem = new TasklistSummaryItem(new SourceAndSealTaskListItem(sourceAndSealSubmissionService));
  }

  @Test
  void getItemDisplayText() {
    assertThat(tasklistSummaryItem.getItemDisplayText()).hasToString("Source and seal");
  }

  @Test
  void getAction() {
    assertThat(tasklistSummaryItem.getActionUrl()).hasToString("#sourceandseal");
  }

  @Test
  void getTaskListSection() {
    assertThat(tasklistSummaryItem.getTaskListSection().toString())
        .hasToString("class uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.OpportunityTaskListSection");
  }
}