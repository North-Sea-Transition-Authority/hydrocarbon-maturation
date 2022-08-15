package uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealController;

@ExtendWith(MockitoExtension.class)
class SourceAndSealTaskListItemTest {

  private static SourceAndSealTaskListItem sourceAndSealTaskListItem;
  private static final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();
  private static ProspectTaskListItemType prospectTaskListItemType;

  @BeforeAll
  static void setUp() {
    sourceAndSealTaskListItem = new SourceAndSealTaskListItem();
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(SUBMISSION_DETAIL_ID);
    prospectTaskListItemType = new ProspectTaskListItemType(submissionDetail);
  }

  @Test
  void getItemDisplayText() {
    assertThat(sourceAndSealTaskListItem.getItemDisplayText()).isEqualTo(SourceAndSealController.PAGE_NAME);
  }

  @Test
  void getActionUrl() {
    assertThat(sourceAndSealTaskListItem.getActionUrl(prospectTaskListItemType))
        .isEqualTo(ReverseRouter.route(on(SourceAndSealController.class).getSourceAndSealDetails(SUBMISSION_DETAIL_ID)));
  }

  @Test
  void getDisplayOrder() {
      assertThat(sourceAndSealTaskListItem.getDisplayOrder()).isEqualTo(30);
  }

  @Test
  void getTaskListSection() {
    assertThat(sourceAndSealTaskListItem.getTaskListSection()).isEqualTo(OpportunityTaskListSection.class);
  }
}