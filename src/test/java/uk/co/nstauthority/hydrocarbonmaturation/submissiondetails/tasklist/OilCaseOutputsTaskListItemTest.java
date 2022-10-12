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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;

@ExtendWith(MockitoExtension.class)
class OilCaseOutputsTaskListItemTest {

  @Mock
  private OilCaseOutputsSubmissionService oilCaseOutputsSubmissionService;

  @Mock
  private HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @InjectMocks
  private OilCaseOutputsTaskListItem oilCaseOutputsTaskListItem;

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
    assertThat(oilCaseOutputsTaskListItem.getItemDisplayText()).isEqualTo(OilCaseOutputsTaskListItem.ITEM_NAME);
  }
  @Test
  void getActionUrl() {
    assertThat(oilCaseOutputsTaskListItem.getActionUrl(prospectTaskListItemType))
        .isEqualTo(
            ReverseRouter.route(on(OilCaseOutputsController.class).getOilCaseOutputs(submissionDetailId, null)));
  }

  @Test
  void getDisplayOrder() {
    assertThat(oilCaseOutputsTaskListItem.getDisplayOrder()).isEqualTo(OilCaseOutputsTaskListItem.ITEM_DISPLAY_ORDER);
  }

  @Test
  void getTaskListSection() {
    assertThat(oilCaseOutputsTaskListItem.getTaskListSection()).isEqualTo(VolumetricsTaskListSection.class);
  }

  @Test
  void isValid_whenFormIsValid_thenTrue() {
    when(oilCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    assertTrue(
        oilCaseOutputsTaskListItem.isValid(new ProspectTaskListItemType(submissionDetail))
    );
  }

  @Test
  void isValid_whenFormIsInvalid_thenFalse() {
    when(oilCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);

    assertFalse(
        oilCaseOutputsTaskListItem.isValid(new ProspectTaskListItemType(submissionDetail))
    );
  }
}