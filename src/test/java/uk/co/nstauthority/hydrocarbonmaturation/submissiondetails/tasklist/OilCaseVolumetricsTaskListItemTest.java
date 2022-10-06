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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;

@ExtendWith(MockitoExtension.class)
class OilCaseVolumetricsTaskListItemTest {

  @Mock
  private OilCaseVolumetricsSubmissionService oilCaseVolumetricsSubmissionService;

  @Mock
  private HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @InjectMocks
  private OilCaseVolumetricsTaskListItem oilCaseVolumetricsTaskListItem;

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
    assertThat(oilCaseVolumetricsTaskListItem.getItemDisplayText()).isEqualTo(OilCaseVolumetricsTaskListItem.ITEM_NAME);
  }
  @Test
  void getActionUrl() {
    assertThat(oilCaseVolumetricsTaskListItem.getActionUrl(prospectTaskListItemType))
        .isEqualTo(
            ReverseRouter.route(on(OilCaseVolumetricsController.class).getOilCaseVolumetrics(submissionDetailId, null)));
  }

  @Test
  void getDisplayOrder() {
    assertThat(oilCaseVolumetricsTaskListItem.getDisplayOrder()).isEqualTo(OilCaseVolumetricsTaskListItem.ITEM_DISPLAY_ORDER);
  }

  @Test
  void getTaskListSection() {
    assertThat(oilCaseVolumetricsTaskListItem.getTaskListSection()).isEqualTo(VolumetricsTaskListSection.class);
  }

  @Test
  void isValid_whenFormIsValid_thenTrue() {
    when(oilCaseVolumetricsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    assertTrue(
        oilCaseVolumetricsTaskListItem.isValid(new ProspectTaskListItemType(submissionDetail))
    );
  }

  @Test
  void isValid_whenFormIsInvalid_thenFalse() {
    when(oilCaseVolumetricsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);

    assertFalse(
        oilCaseVolumetricsTaskListItem.isValid(new ProspectTaskListItemType(submissionDetail))
    );
  }
}