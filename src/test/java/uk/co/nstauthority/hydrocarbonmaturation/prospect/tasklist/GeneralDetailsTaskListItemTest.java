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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsController;

@ExtendWith(MockitoExtension.class)
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
    assertThat(generalDetailsTaskListItem.getItemDisplayText()).isEqualTo(GeneralDetailsController.PAGE_NAME);
  }

  @Test
  void getActionUrl() {
    assertThat(generalDetailsTaskListItem.getActionUrl(prospectTaskListItemType))
        .isEqualTo(ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(SUBMISSION_DETAIL_ID, null)));
  }

  @Test
  void getDisplayOrder() {
    assertThat(generalDetailsTaskListItem.getDisplayOrder()).isEqualTo(10);
  }

  @Test
  void getTaskListSection() {
    assertThat(generalDetailsTaskListItem.getTaskListSection()).isEqualTo(GeneralDetailsTaskListSection.class);

  }
}