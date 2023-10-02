package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldgasvolumetrics.FieldGasVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldoilvolumetrics.FieldOilVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListLabel;

@ExtendWith(MockitoExtension.class)
class ReservoirTaskListServiceTest {

  private static final String RESERVOIR_NAME_1 = "reservoir name 1";
  private static final String RESERVOIR_NAME_2 = "reservoir name 2";

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Mock
  private PermeabilitySubmissionService permeabilitySubmissionService;

  @Mock
  private GeneralReservoirInformationSubmissionService generalReservoirInformationSubmissionService;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @Mock
  private OilCaseVolumetricsSubmissionService oilCaseVolumetricsSubmissionService;

  @Mock
  private ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService;

  @Mock
  private GasCaseVolumetricsSubmissionService gasCaseVolumetricsSubmissionService;

  @Mock
  private OilCaseOutputsSubmissionService oilCaseOutputsSubmissionService;

  @Mock
  private GasCaseOutputsSubmissionService gasCaseOutputsSubmissionService;

  @Mock
  private DrillStemTestSubmissionService drillStemTestSubmissionService;

  @InjectMocks
  private ReservoirTaskListService reservoirTaskListService;

  @Test
  void getAllReservoirTaskLists_commonItems() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var reservoir1 = new Reservoir();
    reservoir1.setName(RESERVOIR_NAME_1);
    var reservoirSubmissionDetail1 = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        reservoir1,
        submissionDetail);
    var reservoir2 = new Reservoir();
    reservoir2.setName(RESERVOIR_NAME_2);
    var reservoirSubmissionDetail2 = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        reservoir2,
        submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail1, reservoirSubmissionDetail2));
    when(generalReservoirInformationSubmissionService.isSectionSubmittable(any(ReservoirSubmissionDetail.class))).thenReturn(true);
    when(permeabilitySubmissionService.isSectionSubmittable(any(ReservoirSubmissionDetail.class))).thenReturn(true);
    when(reservoirPropertiesSubmissionService.isSectionSubmittable(any(ReservoirSubmissionDetail.class))).thenReturn(false);
    when(drillStemTestSubmissionService.isSectionSubmittable(any(ReservoirSubmissionDetail.class))).thenReturn(true);

    var reservoirTaskListItems = reservoirTaskListService.getAllReservoirTaskLists(submissionDetail);

    assertThat(reservoirTaskListItems).extracting(
        ReservoirTaskListItem::reservoirName,
        ReservoirTaskListItem::taskListItems
    ).containsExactly(
        tuple(
            RESERVOIR_NAME_1,
            List.of(
                new TaskListItem(
                    "Discovery reservoir",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(GeneralReservoirInformationController.class).getGeneralReservoirInformationPage(
                        reservoirSubmissionDetail1.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail1.getId()))
                ),
                new TaskListItem(
                    "Permeability",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(PermeabilityController.class).getPermeabilityPage(
                        reservoirSubmissionDetail1.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail1.getId()))
                ),
                new TaskListItem("Reservoir properties",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(
                        reservoirSubmissionDetail1.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail1.getId()))
              ),
                new TaskListItem(
                    "Well test results",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(DrillStemTestController.class)
                        .getDrillStemTestPage(
                            submissionDetail.getId(),
                            reservoirSubmissionDetail1.getId(),
                            null))
                )
            )
        ),
        tuple(
            RESERVOIR_NAME_2,
            List.of(
                new TaskListItem(
                    "Discovery reservoir",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(GeneralReservoirInformationController.class).getGeneralReservoirInformationPage(
                        reservoirSubmissionDetail2.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail2.getId()))
                ),
                new TaskListItem(
                    "Permeability",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(PermeabilityController.class).getPermeabilityPage(
                        reservoirSubmissionDetail2.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail2.getId()))
                ),
                new TaskListItem("Reservoir properties",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(
                        reservoirSubmissionDetail2.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail2.getId()))
                ),
                new TaskListItem(
                    "Well test results",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(DrillStemTestController.class)
                        .getDrillStemTestPage(
                            submissionDetail.getId(),
                            reservoirSubmissionDetail2.getId(),
                            null))
                )
            )
        )
    );
  }

  @Test
  void getAllReservoirTaskLists_fieldSubmission() {
    var fieldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
    var reservoir = new Reservoir();
    reservoir.setName(RESERVOIR_NAME_1);
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        reservoir,
        fieldSubmissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(fieldSubmissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));
    when(generalReservoirInformationSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(drillStemTestSubmissionService.isSectionSubmittable(any(ReservoirSubmissionDetail.class))).thenReturn(true);

    var reservoirTaskListItems = reservoirTaskListService.getAllReservoirTaskLists(fieldSubmissionDetail);

    assertThat(reservoirTaskListItems).extracting(
        ReservoirTaskListItem::reservoirName,
        ReservoirTaskListItem::taskListItems
    ).containsExactly(
        tuple(
            RESERVOIR_NAME_1,
            List.of(
                new TaskListItem(
                    "Field reservoir",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(GeneralReservoirInformationController.class).getGeneralReservoirInformationPage(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Permeability",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(PermeabilityController.class).getPermeabilityPage(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem("Reservoir properties",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Well test results",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(DrillStemTestController.class)
                        .getDrillStemTestPage(
                            fieldSubmissionDetail.getId(),
                            reservoirSubmissionDetail.getId(),
                            null))
                )
            )
        )
    );
  }

  @Test
  void getAllReservoirTaskLists_onlyReservoirInformation() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
    var reservoir = new Reservoir();
    reservoir.setName(RESERVOIR_NAME_1);
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        reservoir,
        submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));
    when(generalReservoirInformationSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(false);
    var reservoirTaskListItems = reservoirTaskListService.getAllReservoirTaskLists(submissionDetail);

    assertThat(reservoirTaskListItems).extracting(
        ReservoirTaskListItem::reservoirName,
        ReservoirTaskListItem::taskListItems
    ).containsExactly(
        tuple(
            RESERVOIR_NAME_1,
            List.of(
                new TaskListItem(
                    "Discovery reservoir",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(GeneralReservoirInformationController.class).getGeneralReservoirInformationPage(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                )
            )
        )
    );
  }

  @Test
  void getAllReservoirTaskLists_whenDiscovery_oilItems() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
    var reservoir = new Reservoir();
    reservoir.setName(RESERVOIR_NAME_1);
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        reservoir,
        submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));
    when(generalReservoirInformationSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(oilCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(oilCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(drillStemTestSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);

    var reservoirTaskListItems = reservoirTaskListService.getAllReservoirTaskLists(submissionDetail);

    assertThat(reservoirTaskListItems).extracting(
        ReservoirTaskListItem::reservoirName,
        ReservoirTaskListItem::taskListItems
    ).containsExactly(
        tuple(
            RESERVOIR_NAME_1,
            List.of(
                new TaskListItem(
                    "Discovery reservoir",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(GeneralReservoirInformationController.class).getGeneralReservoirInformationPage(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Permeability",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(PermeabilityController.class).getPermeabilityPage(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem("Reservoir properties",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Oil case volumetric parameters",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(OilCaseVolumetricsController.class).getOilCaseVolumetrics(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Oil case outputs (resources)",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(OilCaseOutputsController.class).getOilCaseOutputs(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Well test results",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(DrillStemTestController.class)
                        .getDrillStemTestPage(
                            submissionDetail.getId(),
                            reservoirSubmissionDetail.getId(),
                            null))
                )
            )
        )
    );
  }

  @Test
  void getAllReservoirTaskLists_whenField_oilItems() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    var reservoir = new Reservoir();
    reservoir.setName(RESERVOIR_NAME_1);
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        reservoir,
        submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));
    when(generalReservoirInformationSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(oilCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    var reservoirTaskListItems = reservoirTaskListService.getAllReservoirTaskLists(submissionDetail);

    assertThat(reservoirTaskListItems).extracting(
        ReservoirTaskListItem::reservoirName,
        ReservoirTaskListItem::taskListItems
    ).containsExactly(
        tuple(
            RESERVOIR_NAME_1,
            List.of(
                new TaskListItem(
                    "Field reservoir",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(GeneralReservoirInformationController.class).getGeneralReservoirInformationPage(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Permeability",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(PermeabilityController.class).getPermeabilityPage(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem("Reservoir properties",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Oil volumetric parameters",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(FieldOilVolumetricsController.class).getOilVolumetrics(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Well test results",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(DrillStemTestController.class)
                        .getDrillStemTestPage(
                            submissionDetail.getId(),
                            reservoirSubmissionDetail.getId(),
                            null))
                )
            )
        )
    );
  }

  @Test
  void getAllReservoirTaskLists_whenDiscovery_gasItems() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
    var reservoir = new Reservoir();
    reservoir.setName(RESERVOIR_NAME_1);
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        reservoir,
        submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));
    when(generalReservoirInformationSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(gasCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(gasCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(drillStemTestSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);

    var reservoirTaskListItems = reservoirTaskListService.getAllReservoirTaskLists(submissionDetail);

    assertThat(reservoirTaskListItems).extracting(
        ReservoirTaskListItem::reservoirName,
        ReservoirTaskListItem::taskListItems
    ).containsExactly(
        tuple(
            RESERVOIR_NAME_1,
            List.of(
                new TaskListItem(
                    "Discovery reservoir",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(GeneralReservoirInformationController.class).getGeneralReservoirInformationPage(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Permeability",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(PermeabilityController.class).getPermeabilityPage(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem("Reservoir properties",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Gas case volumetric parameters",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(GasCaseVolumetricsController.class).getGasCaseVolumetrics(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Gas case outputs (resources)",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(GasCaseOutputsController.class).getGasCaseOutputs(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Well test results",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(DrillStemTestController.class)
                        .getDrillStemTestPage(
                            submissionDetail.getId(),
                            reservoirSubmissionDetail.getId(),
                            null))
                    )
            )
        )
    );
  }

  @Test
  void getAllReservoirTaskLists_whenField_gasItems() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    var reservoir = new Reservoir();
    reservoir.setName(RESERVOIR_NAME_1);
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        reservoir,
        submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));
    when(generalReservoirInformationSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(gasCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    var reservoirTaskListItems = reservoirTaskListService.getAllReservoirTaskLists(submissionDetail);

    assertThat(reservoirTaskListItems).extracting(
        ReservoirTaskListItem::reservoirName,
        ReservoirTaskListItem::taskListItems
    ).containsExactly(
        tuple(
            RESERVOIR_NAME_1,
            List.of(
                new TaskListItem(
                    "Field reservoir",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(GeneralReservoirInformationController.class).getGeneralReservoirInformationPage(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Permeability",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(PermeabilityController.class).getPermeabilityPage(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem("Reservoir properties",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Gas volumetric parameters",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(FieldGasVolumetricsController.class).getGasVolumetrics(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Well test results",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(DrillStemTestController.class)
                        .getDrillStemTestPage(
                            submissionDetail.getId(),
                            reservoirSubmissionDetail.getId(),
                            null))
                )
            )
        )
    );
  }

  @Test
  void getAllReservoirTaskLists_whenDiscovery_mixedItems() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
    var reservoir = new Reservoir();
    reservoir.setName(RESERVOIR_NAME_1);
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        reservoir,
        submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));
    when(generalReservoirInformationSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(oilCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(oilCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(gasCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(gasCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(drillStemTestSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);

    var reservoirTaskListItems = reservoirTaskListService.getAllReservoirTaskLists(submissionDetail);

    assertThat(reservoirTaskListItems).extracting(
        ReservoirTaskListItem::reservoirName,
        ReservoirTaskListItem::taskListItems
    ).containsExactly(
        tuple(
            RESERVOIR_NAME_1,
            List.of(
                new TaskListItem(
                    "Discovery reservoir",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(
                        on(GeneralReservoirInformationController.class).getGeneralReservoirInformationPage(
                            reservoirSubmissionDetail.getSubmissionDetail().getId(),
                            reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Permeability",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(PermeabilityController.class).getPermeabilityPage(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem("Reservoir properties",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Oil case volumetric parameters",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(OilCaseVolumetricsController.class).getOilCaseVolumetrics(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Oil case outputs (resources)",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(OilCaseOutputsController.class).getOilCaseOutputs(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Gas case volumetric parameters",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(GasCaseVolumetricsController.class).getGasCaseVolumetrics(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Gas case outputs (resources)",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(GasCaseOutputsController.class).getGasCaseOutputs(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Well test results",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(DrillStemTestController.class)
                        .getDrillStemTestPage(
                            submissionDetail.getId(),
                            reservoirSubmissionDetail.getId(),
                            null))
                    )
            )
        )
    );
  }

  @Test
  void getAllReservoirTaskLists_whenField_mixedItems() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    var reservoir = new Reservoir();
    reservoir.setName(RESERVOIR_NAME_1);
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        reservoir,
        submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(
        submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));
    when(generalReservoirInformationSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(oilCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    when(gasCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail)).thenReturn(true);
    var reservoirTaskListItems = reservoirTaskListService.getAllReservoirTaskLists(submissionDetail);

    assertThat(reservoirTaskListItems).extracting(
        ReservoirTaskListItem::reservoirName,
        ReservoirTaskListItem::taskListItems
    ).containsExactly(
        tuple(
            RESERVOIR_NAME_1,
            List.of(
                new TaskListItem(
                    "Field reservoir",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(
                        on(GeneralReservoirInformationController.class).getGeneralReservoirInformationPage(
                            reservoirSubmissionDetail.getSubmissionDetail().getId(),
                            reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Permeability",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(PermeabilityController.class).getPermeabilityPage(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem("Reservoir properties",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ReservoirPropertiesController.class).getReservoirProperties(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Oil volumetric parameters",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(FieldOilVolumetricsController.class).getOilVolumetrics(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Gas volumetric parameters",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(FieldGasVolumetricsController.class).getGasVolumetrics(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                ),
                new TaskListItem(
                    "Well test results",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(DrillStemTestController.class)
                        .getDrillStemTestPage(
                            submissionDetail.getId(),
                            reservoirSubmissionDetail.getId(),
                            null))
                )
            )
        )
    );
  }
}
