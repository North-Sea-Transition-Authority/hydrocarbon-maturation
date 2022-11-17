package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics.MixedCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics.MixedCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSubmissionService;

@ExtendWith(MockitoExtension.class)
class VolumetricsTaskListSectionServiceTest {

  @Mock
  private HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @Mock
  private OilCaseVolumetricsSubmissionService oilCaseVolumetricsSubmissionService;

  @Mock
  private OilCaseOutputsSubmissionService oilCaseOutputsSubmissionService;

  @Mock
  private GasCaseVolumetricsSubmissionService gasCaseVolumetricsSubmissionService;

  @Mock
  private GasCaseOutputsSubmissionService gasCaseOutputsSubmissionService;

  @Mock
  private MixedCaseOutputsSubmissionService mixedCaseOutputsSubmissionService;

  @Mock
  private MixedCaseVolumetricsSubmissionService mixedCaseVolumetricsSubmissionService;

  @InjectMocks
  private VolumetricsTaskListSectionService volumetricsTaskListSectionService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void getSection_prospectSubmission_whenNoCase_assertEmptyOptional() {
    var taskListSectionOptional = volumetricsTaskListSectionService.getSection(submissionDetail);

    assertThat(taskListSectionOptional).isNotPresent();
  }

  @Test
  void getSection_prospectSubmission_whenAllCase_assertItems() {
    when(gasCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(gasCaseVolumetricsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(oilCaseVolumetricsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(oilCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(mixedCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(mixedCaseVolumetricsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(hydrocarbonCasePersistenceService.isGasOnlyPresent(submissionDetail)).thenReturn(true);
    when(hydrocarbonCasePersistenceService.isOilOnlyPresent(submissionDetail)).thenReturn(true);
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(submissionDetail)).thenReturn(true);
    var taskListSectionOptional = volumetricsTaskListSectionService.getSection(submissionDetail);

    var section = taskListSectionOptional.get();
    assertThat(section)
        .extracting(TaskListSection::items)
        .isEqualTo(List.of(
            new TaskListItem("Oil case volumetrics parameters",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(OilCaseVolumetricsController.class)
                    .getOilCaseVolumetrics(submissionDetail.getId(), null))),
            new TaskListItem(
                "Oil case outputs (resources)",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(OilCaseOutputsController.class)
                    .getOilCaseOutputs(submissionDetail.getId(), null))),
            new TaskListItem("Gas case volumetrics parameters",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(GasCaseVolumetricsController.class)
                    .getGasCaseVolumetrics(submissionDetail.getId(), null))),
            new TaskListItem(
                "Gas case outputs (resources)",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(GasCaseOutputsController.class)
                    .getGasCaseOutputs(submissionDetail.getId(), null))),
            new TaskListItem("Mixed case volumetrics parameters",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(MixedCaseVolumetricsController.class)
                    .getMixedCaseVolumetrics(submissionDetail.getId(), null))),
            new TaskListItem(
                "Mixed case outputs (resources)",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(MixedCaseOutputsController.class)
                    .getMixedCaseOutputs(submissionDetail.getId(), null))))
        );
  }

  @Test
  void isSectionSubmittable_whenAllCasesAndProspectSubmissionAndSubmittableItems_assertTrue() {
    when(hydrocarbonCasePersistenceService.isGasOnlyPresent(submissionDetail)).thenReturn(true);
    when(hydrocarbonCasePersistenceService.isOilOnlyPresent(submissionDetail)).thenReturn(true);
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(submissionDetail)).thenReturn(true);
    when(gasCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(gasCaseVolumetricsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(oilCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(oilCaseVolumetricsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(mixedCaseOutputsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(mixedCaseVolumetricsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    assertTrue(volumetricsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNoCasesAndProspectUpdateSubmissionAndSubmittableItems_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);

    when(hydrocarbonCasePersistenceService.isGasOnlyPresent(submissionDetail)).thenReturn(false);
    when(hydrocarbonCasePersistenceService.isOilOnlyPresent(submissionDetail)).thenReturn(false);
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(submissionDetail)).thenReturn(false);

    assertTrue(volumetricsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotProspectSubmission_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    assertTrue(volumetricsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }
}