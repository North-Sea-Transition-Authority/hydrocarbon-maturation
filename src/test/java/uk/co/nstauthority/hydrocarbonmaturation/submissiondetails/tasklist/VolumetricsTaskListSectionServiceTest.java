package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Optional;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics.MixedCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics.MixedCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

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

  @Mock
  private DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @InjectMocks
  private VolumetricsTaskListSectionService volumetricsTaskListSectionService;

  private SubmissionDetail submissionDetail;
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

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
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    when(gasCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(gasCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(oilCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(oilCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(mixedCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(mixedCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    var hydrocarbonCase = new HydrocarbonCaseDetail();
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail))
        .thenReturn(Optional.of(hydrocarbonCase));
    when(hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCase)).thenReturn(true);
    when(hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCase)).thenReturn(true);
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCase)).thenReturn(true);
    var taskListSectionOptional = volumetricsTaskListSectionService.getSection(submissionDetail);

    var section = taskListSectionOptional.get();
    assertThat(section)
        .extracting(TaskListSection::items)
        .isEqualTo(List.of(
            new TaskListItem("Oil case volumetrics parameters",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(OilCaseVolumetricsController.class)
                    .getOilCaseVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId()))),
            new TaskListItem(
                "Oil case outputs (resources)",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(OilCaseOutputsController.class)
                    .getOilCaseOutputs(submissionDetail.getId(), reservoirSubmissionDetail.getId()))),
            new TaskListItem("Gas case volumetrics parameters",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(GasCaseVolumetricsController.class)
                    .getGasCaseVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail.getId()))),
            new TaskListItem(
                "Gas case outputs (resources)",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(GasCaseOutputsController.class)
                    .getGasCaseOutputs(submissionDetail.getId(), reservoirSubmissionDetail.getId()))),
            new TaskListItem("Mixed case volumetrics parameters",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(MixedCaseVolumetricsController.class)
                    .getMixedCaseVolumetrics(submissionDetail.getId(), reservoirSubmissionDetail .getId()))),
            new TaskListItem(
                "Mixed case outputs (resources)",
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(MixedCaseOutputsController.class)
                    .getMixedCaseOutputs(submissionDetail.getId(), reservoirSubmissionDetail.getId()))))
        );
  }

  @Test
  void isSectionSubmittable_whenAllCasesAndProspectSubmissionAndSubmittableItems_assertTrue() {
    var hydrocarbonCase = new HydrocarbonCaseDetail();
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail))
        .thenReturn(Optional.of(hydrocarbonCase));
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    when(hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCase)).thenReturn(true);
    when(hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCase)).thenReturn(true);
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCase)).thenReturn(true);

    when(gasCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(gasCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(oilCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(oilCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(mixedCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(mixedCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);

    assertTrue(volumetricsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_whenGasCaseAndNotSubmittableCaseVolumetrics_assertFalse() {
    var hydrocarbonCase = new HydrocarbonCaseDetail();
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail))
        .thenReturn(Optional.of(hydrocarbonCase));
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    when(hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCase)).thenReturn(true);
    when(hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCase)).thenReturn(false);
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCase)).thenReturn(false);

    when(gasCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(false);

    assertFalse(volumetricsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_whenGasCaseAndNotSubmittableCaseOutputs_assertFalse() {
    var hydrocarbonCase = new HydrocarbonCaseDetail();
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail))
        .thenReturn(Optional.of(hydrocarbonCase));
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    when(hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCase)).thenReturn(true);
    when(hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCase)).thenReturn(false);
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCase)).thenReturn(false);

    when(gasCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(gasCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(false);

    assertFalse(volumetricsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_whenOilCaseAndNotSubmittableCaseVolumetrics_assertFalse() {
    var hydrocarbonCase = new HydrocarbonCaseDetail();
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail))
        .thenReturn(Optional.of(hydrocarbonCase));
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    when(hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCase)).thenReturn(false);
    when(hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCase)).thenReturn(true);
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCase)).thenReturn(false);

    when(oilCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(false);

    assertFalse(volumetricsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_whenOilCaseAndNotSubmittableCaseOutputs_assertFalse() {
    var hydrocarbonCase = new HydrocarbonCaseDetail();
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail))
        .thenReturn(Optional.of(hydrocarbonCase));
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    when(hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCase)).thenReturn(false);
    when(hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCase)).thenReturn(true);
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCase)).thenReturn(false);

    when(oilCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(oilCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(false);

    assertFalse(volumetricsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_whenOilOnlyAndGasOnlyCaseAndNotSubmittableCaseVolumetrics_notSubmittableGas_assertFalse() {
    var hydrocarbonCase = new HydrocarbonCaseDetail();
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail))
        .thenReturn(Optional.of(hydrocarbonCase));
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    when(hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCase)).thenReturn(true);
    when(hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCase)).thenReturn(true);
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCase)).thenReturn(false);

    when(gasCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(false);

    assertFalse(volumetricsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_whenMixedCaseAndNotSubmittableCaseVolumetrics_assertFalse() {
    var hydrocarbonCase = new HydrocarbonCaseDetail();
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail))
        .thenReturn(Optional.of(hydrocarbonCase));
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    when(hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCase)).thenReturn(false);
    when(hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCase)).thenReturn(false);
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCase)).thenReturn(true);

    when(mixedCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(false);

    assertFalse(volumetricsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_whenMixedCaseAndNotSubmittableCaseOutputs_assertFalse() {
    var hydrocarbonCase = new HydrocarbonCaseDetail();
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail))
        .thenReturn(Optional.of(hydrocarbonCase));
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    when(hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCase)).thenReturn(false);
    when(hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCase)).thenReturn(false);
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCase)).thenReturn(true);

    when(mixedCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(mixedCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(false);

    assertFalse(volumetricsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_whenMixedAndGasOnlyCaseAndNotSubmittableCaseVolumetrics_notSubmittableGas_assertFalse() {
    var hydrocarbonCase = new HydrocarbonCaseDetail();
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail))
        .thenReturn(Optional.of(hydrocarbonCase));
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    when(hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCase)).thenReturn(true);
    when(hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCase)).thenReturn(false);
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCase)).thenReturn(true);

    when(gasCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(false);

    assertFalse(volumetricsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNoCasesAndProspectUpdateSubmissionAndSubmittableItems_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);

    when(hydrocarbonCasePersistenceService.isGasOnlyPresent(any())).thenReturn(false);
    when(hydrocarbonCasePersistenceService.isOilOnlyPresent(any())).thenReturn(false);
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(any())).thenReturn(false);

    assertTrue(volumetricsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotProspectOrDiscoverySubmission_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    assertTrue(volumetricsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_whenPostDrillSubmission_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    assertTrue(volumetricsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_whenDiscoverySubmission_whenMixedDiscovery_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    assertTrue(volumetricsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }
}