package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;

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
    assertThat(sourceAndSealTaskListItem.getItemDisplayText()).isEqualTo(SourceAndSealTaskListItem.ITEM_NAME);
  }

  @Test
  void getActionUrl() {
    assertThat(sourceAndSealTaskListItem.getActionUrl(prospectTaskListItemType))
        .isEqualTo(ReverseRouter.route(on(SourceAndSealController.class).getSourceAndSealDetails(SUBMISSION_DETAIL_ID)));
  }

  @Test
  void getDisplayOrder() {
      assertThat(sourceAndSealTaskListItem.getDisplayOrder()).isEqualTo(SourceAndSealTaskListItem.ITEM_DISPLAY_ORDER);
  }

  @Test
  void getTaskListSection() {
    assertThat(sourceAndSealTaskListItem.getTaskListSection()).isEqualTo(OpportunityTaskListSection.class);
  }
}