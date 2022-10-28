package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation.AdditionalInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation.AdditionalInformationSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;

@ExtendWith(MockitoExtension.class)
class AdditionalInformationTaskListItemTest {

  @Mock
  private AdditionalInformationSubmissionService additionalInformationSubmissionService;

  @InjectMocks
  private AdditionalInformationTaskListItem additionalInformationTaskListItem;

  private static SubmissionDetail submissionDetail;
  private static UUID submissionDetailId;
  private static ProspectTaskListItemType prospectTaskListItemType;


  @BeforeAll
  static void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
    prospectTaskListItemType = new ProspectTaskListItemType(submissionDetail);
  }

  @Test
  void getItemDisplayText() {
    assertThat(additionalInformationTaskListItem.getItemDisplayText()).isEqualTo(AdditionalInformationTaskListItem.ITEM_NAME);
  }
  @Test
  void getActionUrl() {
    assertThat(additionalInformationTaskListItem.getActionUrl(prospectTaskListItemType))
        .isEqualTo(
            ReverseRouter.route(on(AdditionalInformationController.class).getAdditionalInformation(submissionDetailId, null)));
  }

  @Test
  void getDisplayOrder() {
    assertThat(additionalInformationTaskListItem.getDisplayOrder()).isEqualTo(AdditionalInformationTaskListItem.ITEM_DISPLAY_ORDER);
  }

  @Test
  void getTaskListSection() {
    assertThat(additionalInformationTaskListItem.getTaskListSection()).isEqualTo(AdditionalInformationTaskListSection.class);
  }

  @Test
  void isValid_whenFormIsValid_thenTrue() {
    when(additionalInformationSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    assertTrue(
        additionalInformationTaskListItem.isValid(new ProspectTaskListItemType(submissionDetail))
    );
  }

  @Test
  void isValid_whenFormIsInvalid_thenFalse() {
    when(additionalInformationSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);

    assertFalse(
        additionalInformationTaskListItem.isValid(new ProspectTaskListItemType(submissionDetail))
    );
  }
}