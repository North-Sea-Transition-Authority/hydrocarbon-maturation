package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesController;

class TrapPropertiesTaskListItemTest {

  private static TrapPropertiesTaskListItem trapPropertiesTaskListItem;
  private static final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();
  private static ProspectTaskListItemType prospectTaskListItemType;

  @BeforeAll
  static void setUp() {
    trapPropertiesTaskListItem = new TrapPropertiesTaskListItem();
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(SUBMISSION_DETAIL_ID);
    prospectTaskListItemType = new ProspectTaskListItemType(submissionDetail);
  }

  @Test
  void getItemDisplayText() {
    assertThat(trapPropertiesTaskListItem.getItemDisplayText()).isEqualTo(TrapPropertiesTaskListItem.ITEM_NAME);
  }

  @Test
  void getActionUrl() {
    assertThat(trapPropertiesTaskListItem.getActionUrl(prospectTaskListItemType))
        .isEqualTo(
            ReverseRouter.route(on(TrapPropertiesController.class).getTrapProperties(SUBMISSION_DETAIL_ID)));
  }

  @Test
  void getDisplayOrder() {
    assertThat(trapPropertiesTaskListItem.getDisplayOrder()).isEqualTo(TrapPropertiesTaskListItem.ITEM_DISPLAY_ORDER);
  }

  @Test
  void getTaskListSection() {
    assertThat(trapPropertiesTaskListItem.getTaskListSection()).isEqualTo(OpportunityTaskListSection.class);
  }
}