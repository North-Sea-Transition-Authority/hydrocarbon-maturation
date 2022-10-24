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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;

@ExtendWith(MockitoExtension.class)
class MixedCaseOutputsTaskListItemTest {

  @Mock
  private MixedCaseOutputsSubmissionService mixedCaseOutputsSubmissionService;

  @Mock
  private HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @InjectMocks
  private MixedCaseOutputsTaskListItem mixedCaseOutputsTaskListItem;

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
    assertThat(mixedCaseOutputsTaskListItem.getItemDisplayText()).isEqualTo(MixedCaseOutputsTaskListItem.ITEM_NAME);
  }
  @Test
  void getActionUrl() {
    assertThat(mixedCaseOutputsTaskListItem.getActionUrl(prospectTaskListItemType))
        .isEqualTo(
            ReverseRouter.route(on(MixedCaseOutputsController.class).getMixedCaseOutputs(submissionDetailId, null)));
  }

  @Test
  void getDisplayOrder() {
    assertThat(mixedCaseOutputsTaskListItem.getDisplayOrder()).isEqualTo(MixedCaseOutputsTaskListItem.ITEM_DISPLAY_ORDER);
  }

  @Test
  void getTaskListSection() {
    assertThat(mixedCaseOutputsTaskListItem.getTaskListSection()).isEqualTo(VolumetricsTaskListSection.class);
  }

  @Test
  void isValid_whenFormIsValid_thenTrue() {
    when(mixedCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    assertTrue(
        mixedCaseOutputsTaskListItem.isValid(new ProspectTaskListItemType(submissionDetail))
    );
  }

  @Test
  void isValid_whenFormIsInvalid_thenFalse() {
    when(mixedCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);

    assertFalse(
        mixedCaseOutputsTaskListItem.isValid(new ProspectTaskListItemType(submissionDetail))
    );
  }

  @Test
  void isVisible_isMixedCase_thenTrue() {
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(submissionDetail)).thenReturn(true);

    assertTrue(
        mixedCaseOutputsTaskListItem.isVisible(new ProspectTaskListItemType(submissionDetail))
    );
  }

  @Test
  void isVisible_isMixedCase_thenFalse() {
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(submissionDetail)).thenReturn(true);

    assertTrue(
        mixedCaseOutputsTaskListItem.isVisible(new ProspectTaskListItemType(submissionDetail))
    );
  }
}