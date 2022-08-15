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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseController;

@ExtendWith(MockitoExtension.class)
class HydrocarbonCaseTaskListItemTest {

  private static HydrocarbonCaseTaskListItem hydrocarbonCaseTaskListItem;
  private static final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();
  private static ProspectTaskListItemType prospectTaskListItemType;

  @BeforeAll
  static void setUp() {
    hydrocarbonCaseTaskListItem = new HydrocarbonCaseTaskListItem();
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(SUBMISSION_DETAIL_ID);
    prospectTaskListItemType = new ProspectTaskListItemType(submissionDetail);
  }


  @Test
  void getItemDisplayText() {
    assertThat(hydrocarbonCaseTaskListItem.getItemDisplayText()).isEqualTo(HydrocarbonCaseController.PAGE_NAME);
  }

  @Test
  void getActionUrl() {
    assertThat(hydrocarbonCaseTaskListItem.getActionUrl(prospectTaskListItemType))
        .isEqualTo(
            ReverseRouter.route(on(HydrocarbonCaseController.class).getHydrocarbonCase(SUBMISSION_DETAIL_ID)));
  }

  @Test
  void getDisplayOrder() {
    assertThat(hydrocarbonCaseTaskListItem.getDisplayOrder()).isEqualTo(40);
  }

  @Test
  void getTaskListSection() {
    assertThat(hydrocarbonCaseTaskListItem.getTaskListSection()).isEqualTo(OpportunityTaskListSection.class);
  }
}