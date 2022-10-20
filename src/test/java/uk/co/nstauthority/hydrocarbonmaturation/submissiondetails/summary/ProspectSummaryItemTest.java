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
class ProspectSummaryItemTest {

  private ProspectSummaryItem prospectSummaryItem;

  @Mock
  private SourceAndSealSubmissionService sourceAndSealSubmissionService;

  @BeforeEach
  void setUp() {
    prospectSummaryItem = new ProspectSummaryItem(new SourceAndSealTaskListItem(sourceAndSealSubmissionService));
  }

  @Test
  void getItemDisplayText() {
    assertThat(prospectSummaryItem.getItemDisplayText()).hasToString("Source and seal");
  }

  @Test
  void getAction() {
    assertThat(prospectSummaryItem.getAction()).hasToString("#sourceandseal");
  }

  @Test
  void getTaskListSection() {
    assertThat(prospectSummaryItem.getTaskListSection().toString())
        .hasToString("class uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.OpportunityTaskListSection");
  }
}