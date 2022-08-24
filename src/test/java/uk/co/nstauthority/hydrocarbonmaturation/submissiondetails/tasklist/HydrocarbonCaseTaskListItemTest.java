package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;

class HydrocarbonCaseTaskListItemTest {

  private static HydrocarbonCaseTaskListItem hydrocarbonCaseTaskListItem;
  private static final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();
  private static ProspectTaskListItemType prospectTaskListItemType;

  @Mock
  private HydrocarbonCaseSubmissionService hydrocarbonCaseSubmissionService;

  @BeforeEach
  void setUp() {
    hydrocarbonCaseTaskListItem = new HydrocarbonCaseTaskListItem(hydrocarbonCaseSubmissionService);
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(SUBMISSION_DETAIL_ID);
    prospectTaskListItemType = new ProspectTaskListItemType(submissionDetail);
  }


  @Test
  void getItemDisplayText() {
    assertThat(hydrocarbonCaseTaskListItem.getItemDisplayText()).isEqualTo(HydrocarbonCaseTaskListItem.ITEM_NAME);
  }

  @Test
  void getActionUrl() {
    assertThat(hydrocarbonCaseTaskListItem.getActionUrl(prospectTaskListItemType))
        .isEqualTo(
            ReverseRouter.route(on(HydrocarbonCaseController.class).getHydrocarbonCase(SUBMISSION_DETAIL_ID)));
  }

  @Test
  void getDisplayOrder() {
    assertThat(hydrocarbonCaseTaskListItem.getDisplayOrder()).isEqualTo(HydrocarbonCaseTaskListItem.ITEM_DISPLAY_ORDER);
  }

  @Test
  void getTaskListSection() {
    assertThat(hydrocarbonCaseTaskListItem.getTaskListSection()).isEqualTo(OpportunityTaskListSection.class);
  }
}