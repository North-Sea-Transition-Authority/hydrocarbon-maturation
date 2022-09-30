package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;

class ReviewAndSubmitTaskListItemTest {

  private static ReviewAndSubmitTaskListItem reviewAndSubmitTaskListItem;
  private static final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();
  private static ProspectTaskListItemType prospectTaskListItemType;

  @BeforeAll
  static void setup() {
    reviewAndSubmitTaskListItem = new ReviewAndSubmitTaskListItem();
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(SUBMISSION_DETAIL_ID);
    prospectTaskListItemType = new ProspectTaskListItemType(submissionDetail);
  }

  @Test
  void getItemDisplayText() {
    assertEquals(ReviewAndSubmitTaskListSection.SECTION_NAME, reviewAndSubmitTaskListItem.getItemDisplayText());
  }

  @Test
  void getActionUrl() {
    assertEquals(ReverseRouter.route(on(SubmissionSummaryController.class)
        .showSubmissionSummary(SUBMISSION_DETAIL_ID)), reviewAndSubmitTaskListItem.getActionUrl(prospectTaskListItemType));
  }

  @Test
  void getDisplayOrder() {
    assertEquals(ReviewAndSubmitTaskListItem.ITEM_DISPLAY_ORDER, reviewAndSubmitTaskListItem.getDisplayOrder());
  }

  @Test
  void getTaskListSection() {
    assertEquals(ReviewAndSubmitTaskListSection.class, reviewAndSubmitTaskListItem.getTaskListSection());
  }

  @Test
  void isValid() {
    assertTrue(reviewAndSubmitTaskListItem.isValid(prospectTaskListItemType));
  }

  @Test
  void showTaskListLabels() {
    assertFalse(reviewAndSubmitTaskListItem.showTaskListLabels(prospectTaskListItemType));
  }
}