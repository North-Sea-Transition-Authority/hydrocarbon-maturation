package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;

class GeneralDetailsTaskListItemTest {

  private static GeneralDetailsTaskListItem generalDetailsTaskListItem;
  private static final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();
  private static ProspectTaskListItemType prospectTaskListItemType;

  @BeforeAll
  static void setUp() {
    generalDetailsTaskListItem = new GeneralDetailsTaskListItem();
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(SUBMISSION_DETAIL_ID);
    prospectTaskListItemType = new ProspectTaskListItemType(submissionDetail);
  }

  @Test
  void getItemDisplayText() {
    assertThat(generalDetailsTaskListItem.getItemDisplayText()).isEqualTo(GeneralDetailsTaskListItem.ITEM_NAME);
  }

  @Test
  void getActionUrl() {
    assertThat(generalDetailsTaskListItem.getActionUrl(prospectTaskListItemType))
        .isEqualTo(ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(SUBMISSION_DETAIL_ID, null)));
  }

  @Test
  void getDisplayOrder() {
    assertThat(generalDetailsTaskListItem.getDisplayOrder()).isEqualTo(GeneralDetailsTaskListItem.ITEM_DISPLAY_ORDER);
  }

  @Test
  void getTaskListSection() {
    assertThat(generalDetailsTaskListItem.getTaskListSection()).isEqualTo(GeneralDetailsTaskListSection.class);

  }
}