package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;

class ChanceOfSuccessTaskListItemTest {

  private static ProspectTaskListItemType prospectTaskListItemType;

  private static ChanceOfSuccessTaskListItem chanceOfSuccessTaskListItem;
  private static ChanceOfSuccessSubmissionService chanceOfSuccessSubmissionService;

  private static SubmissionDetail submissionDetail;
  private static UUID submissionDetailId;

  @BeforeAll
  static void setUp() {
    chanceOfSuccessSubmissionService = mock(ChanceOfSuccessSubmissionService.class);
    chanceOfSuccessTaskListItem = new ChanceOfSuccessTaskListItem(chanceOfSuccessSubmissionService);
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
    prospectTaskListItemType = new ProspectTaskListItemType(submissionDetail);
  }

  @Test
  void getItemDisplayText() {
    assertThat(chanceOfSuccessTaskListItem.getItemDisplayText()).isEqualTo(ChanceOfSuccessTaskListItem.ITEM_NAME);
  }

  @Test
  void getActionUrl() {
    assertThat(chanceOfSuccessTaskListItem.getActionUrl(prospectTaskListItemType))
        .isEqualTo(
            ReverseRouter.route(on(ChanceOfSuccessController.class).getChanceOfSuccess(submissionDetailId)));
  }

  @Test
  void getDisplayOrder() {
    assertThat(chanceOfSuccessTaskListItem.getDisplayOrder()).isEqualTo(ChanceOfSuccessTaskListItem.ITEM_DISPLAY_ORDER);
  }

  @Test
  void getTaskListSection() {
    assertThat(chanceOfSuccessTaskListItem.getTaskListSection()).isEqualTo(OpportunityTaskListSection.class);
  }

  @Test
  void isValid_whenFormIsValid_thenTrue() {
    when(chanceOfSuccessSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    assertTrue(
        chanceOfSuccessTaskListItem.isValid(new ProspectTaskListItemType(submissionDetail))
    );
  }

  @Test
  void isValid_whenFormIsInvalid_thenFalse() {
    when(chanceOfSuccessSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);

    assertFalse(
        chanceOfSuccessTaskListItem.isValid(new ProspectTaskListItemType(submissionDetail))
    );
  }
}