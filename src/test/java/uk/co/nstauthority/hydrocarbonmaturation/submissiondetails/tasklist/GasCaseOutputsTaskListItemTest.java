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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;

@ExtendWith(MockitoExtension.class)
class GasCaseOutputsTaskListItemTest {

  @Mock
  private GasCaseOutputsSubmissionService gasCaseOutputsSubmissionService;

  @Mock
  private HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @InjectMocks
  private GasCaseOutputsTaskListItem gasCaseOutputsTaskListItem;

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
    assertThat(gasCaseOutputsTaskListItem.getItemDisplayText()).isEqualTo(GasCaseOutputsTaskListItem.ITEM_NAME);
  }
  @Test
  void getActionUrl() {
    assertThat(gasCaseOutputsTaskListItem.getActionUrl(prospectTaskListItemType))
        .isEqualTo(
            ReverseRouter.route(on(GasCaseOutputsController.class).getGasCaseOutputs(submissionDetailId, null)));
  }

  @Test
  void getDisplayOrder() {
    assertThat(gasCaseOutputsTaskListItem.getDisplayOrder()).isEqualTo(GasCaseOutputsTaskListItem.ITEM_DISPLAY_ORDER);
  }

  @Test
  void getTaskListSection() {
    assertThat(gasCaseOutputsTaskListItem.getTaskListSection()).isEqualTo(VolumetricsTaskListSection.class);
  }

  @Test
  void isValid_whenFormIsValid_thenTrue() {
    when(gasCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    assertTrue(
        gasCaseOutputsTaskListItem.isValid(new ProspectTaskListItemType(submissionDetail))
    );
  }

  @Test
  void isValid_whenFormIsInvalid_thenFalse() {
    when(gasCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);

    assertFalse(
        gasCaseOutputsTaskListItem.isValid(new ProspectTaskListItemType(submissionDetail))
    );
  }
}