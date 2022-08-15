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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsController;

@ExtendWith(MockitoExtension.class)
class OpportunityDetailsTaskListItemTest {

  private static OpportunityDetailsTaskListItem opportunityDetailsTaskListItem;
  private static final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();
  private static ProspectTaskListItemType prospectTaskListItemType;

  @BeforeAll
  static void setUp() {
    opportunityDetailsTaskListItem = new OpportunityDetailsTaskListItem();
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(SUBMISSION_DETAIL_ID);
    prospectTaskListItemType = new ProspectTaskListItemType(submissionDetail);
  }

  @Test
  void getItemDisplayText() {
    assertThat(opportunityDetailsTaskListItem.getItemDisplayText()).isEqualTo(OpportunityDetailsController.PAGE_NAME);
  }

  @Test
  void getActionUrl() {
    assertThat(opportunityDetailsTaskListItem.getActionUrl(prospectTaskListItemType))
        .isEqualTo(ReverseRouter.route(on(OpportunityDetailsController.class).getOpportunityDetails(SUBMISSION_DETAIL_ID, null)));
  }

  @Test
  void getDisplayOrder() {
    assertThat(opportunityDetailsTaskListItem.getDisplayOrder()).isEqualTo(10);
  }

  @Test
  void getTaskListSection() {
    assertThat(opportunityDetailsTaskListItem.getTaskListSection()).isEqualTo(OpportunityTaskListSection.class);
  }
}