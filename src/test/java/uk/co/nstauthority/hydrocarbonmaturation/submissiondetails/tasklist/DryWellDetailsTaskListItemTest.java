package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole.PostDrillDryHoleTaskListItemType;

@ExtendWith(MockitoExtension.class)
class DryWellDetailsTaskListItemTest {

  @Mock
  private DryWellDetailsSubmissionService dryWellDetailsSubmissionService;

  @InjectMocks
  private DryWellDetailsTaskListItem dryWellDetailsTaskListItem;

  private PostDrillDryHoleTaskListItemType postDrillDryHoleTaskListItemType;

  private final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(SUBMISSION_DETAIL_ID);
    postDrillDryHoleTaskListItemType = new PostDrillDryHoleTaskListItemType(submissionDetail);
  }

  @Test
  void getItemDisplayText() {
    assertThat(dryWellDetailsTaskListItem.getItemDisplayText()).isEqualTo("Dry well details");
  }

  @Test
  void getActionUrl() {
    assertThat(dryWellDetailsTaskListItem.getActionUrl(postDrillDryHoleTaskListItemType))
        .isEqualTo(ReverseRouter.route(on(DryWellDetailsController.class).getDryWellDetails(SUBMISSION_DETAIL_ID, null)));
  }

  @Test
  void getDisplayOrder() {
    assertThat(dryWellDetailsTaskListItem.getDisplayOrder()).isEqualTo(Integer.MIN_VALUE);
  }

  @Test
  void getTaskListSection() {
    assertThat(dryWellDetailsTaskListItem.getTaskListSection()).isEqualTo(PostWellDrillTaskListSection.class);
  }

  @Test
  void isValid_verifyMethodCall() {
    dryWellDetailsTaskListItem.isValid(postDrillDryHoleTaskListItemType);

    verify(dryWellDetailsSubmissionService, times(1))
        .isSectionSubmittable(postDrillDryHoleTaskListItemType.submissionDetail());
  }
}