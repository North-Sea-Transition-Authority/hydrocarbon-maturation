package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;

class ReservoirPropertiesTaskListItemTest {

  private static ReservoirPropertiesTaskListItem reservoirPropertiesTaskListItem;
  private static final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();
  private static ProspectTaskListItemType prospectTaskListItemType;

  @BeforeAll
  static void setUp() {
    reservoirPropertiesTaskListItem = new ReservoirPropertiesTaskListItem();
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(SUBMISSION_DETAIL_ID);
    prospectTaskListItemType = new ProspectTaskListItemType(submissionDetail);
  }

  @Test
  void getItemDisplayText() {
    assertThat(reservoirPropertiesTaskListItem.getItemDisplayText()).isEqualTo(ReservoirPropertiesController.PAGE_NAME);
  }

  @Test
  void getActionUrl() {
    assertThat(reservoirPropertiesTaskListItem.getActionUrl(prospectTaskListItemType))
        .isEqualTo(ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(SUBMISSION_DETAIL_ID)));
  }

  @Test
  void getDisplayOrder() {
    assertThat(reservoirPropertiesTaskListItem.getDisplayOrder()).isEqualTo(10);
  }

  @Test
  void getTaskListSection() {
    assertThat(reservoirPropertiesTaskListItem.getTaskListSection()).isEqualTo(ReservoirTaskListSection.class);
  }
}