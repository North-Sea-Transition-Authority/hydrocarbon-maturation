package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;

@ExtendWith(MockitoExtension.class)
class GasCaseVolumetricsTaskListItemTest {

  @Mock
  private GasCaseVolumetricsSubmissionService gasCaseVolumetricsSubmissionService;

  @Mock
  private HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @InjectMocks
  private GasCaseVolumetricsTaskListItem gasCaseVolumetricsTaskListItem;

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
    assertThat(gasCaseVolumetricsTaskListItem.getItemDisplayText()).isEqualTo(GasCaseVolumetricsTaskListItem.ITEM_NAME);
  }
  @Test
  void getActionUrl() {
    assertThat(gasCaseVolumetricsTaskListItem.getActionUrl(prospectTaskListItemType))
        .isEqualTo(
            ReverseRouter.route(on(GasCaseVolumetricsController.class).getGasCaseVolumetrics(submissionDetailId, null)));
  }

  @Test
  void getDisplayOrder() {
    assertThat(gasCaseVolumetricsTaskListItem.getDisplayOrder()).isEqualTo(GasCaseVolumetricsTaskListItem.ITEM_DISPLAY_ORDER);
  }

  @Test
  void getTaskListSection() {
    assertThat(gasCaseVolumetricsTaskListItem.getTaskListSection()).isEqualTo(VolumetricsTaskListSection.class);
  }

  @Test
  void isValid_whenFormIsValid_thenTrue() {
    when(gasCaseVolumetricsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    assertTrue(gasCaseVolumetricsTaskListItem.isValid(new ProspectTaskListItemType(submissionDetail)));
  }

  @Test
  void isValid_whenFormIsInvalid_thenFalse() {
    when(gasCaseVolumetricsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);

    assertFalse(gasCaseVolumetricsTaskListItem.isValid(new ProspectTaskListItemType(submissionDetail)));
  }
}