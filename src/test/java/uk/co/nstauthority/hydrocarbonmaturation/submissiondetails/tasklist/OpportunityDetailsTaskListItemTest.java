package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;

class OpportunityDetailsTaskListItemTest {

  private static OpportunityDetailsTaskListItem opportunityDetailsTaskListItem;
  private static final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();
  private static ProspectTaskListItemType prospectTaskListItemType;

  @Mock
  private OpportunityDetailsSubmissionService opportunityDetailsSubmissionService;

  @BeforeEach
  void setUp() {
    opportunityDetailsTaskListItem = new OpportunityDetailsTaskListItem(opportunityDetailsSubmissionService);
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(SUBMISSION_DETAIL_ID);
    prospectTaskListItemType = new ProspectTaskListItemType(submissionDetail);
  }

  @Test
  void getItemDisplayText() {
    assertThat(opportunityDetailsTaskListItem.getItemDisplayText()).isEqualTo(OpportunityDetailsTaskListItem.ITEM_NAME);
  }

  @Test
  void getActionUrl() {
    assertThat(opportunityDetailsTaskListItem.getActionUrl(prospectTaskListItemType))
        .isEqualTo(ReverseRouter.route(on(OpportunityDetailsController.class).getOpportunityDetails(SUBMISSION_DETAIL_ID, null)));
  }

  @Test
  void getDisplayOrder() {
    assertThat(opportunityDetailsTaskListItem.getDisplayOrder()).isEqualTo(OpportunityDetailsTaskListItem.ITEM_DISPLAY_ORDER);
  }

  @Test
  void getTaskListSection() {
    assertThat(opportunityDetailsTaskListItem.getTaskListSection()).isEqualTo(OpportunityTaskListSection.class);
  }
}