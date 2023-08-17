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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySubmissionService;
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
    when(generalReservoirInformationSubmissionService.isSectionSubmittable(any())).thenReturn(true);
    when(permeabilitySubmissionService.isSectionSubmittable(any())).thenReturn(true);
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
    when(generalReservoirInformationSubmissionService.isSectionSubmittable(any())).thenReturn(true);
    when(permeabilitySubmissionService.isSectionSubmittable(any())).thenReturn(true);
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
    when(generalReservoirInformationSubmissionService.isSectionSubmittable(any())).thenReturn(false);
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
  void getAllReservoirTaskLists_oilItems() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
    var reservoir = new Reservoir();
    reservoir.setName(RESERVOIR_NAME_1);
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        reservoir,
        submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));
    when(generalReservoirInformationSubmissionService.isSectionSubmittable(any())).thenReturn(true);
    when(permeabilitySubmissionService.isSectionSubmittable(any())).thenReturn(true);
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
                new TaskListItem(
                    "Oil case volumetric parameters",
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(OilCaseVolumetricsController.class).getOilCaseVolumetrics(
                        reservoirSubmissionDetail.getSubmissionDetail().getId(),
                        reservoirSubmissionDetail.getId()))
                )
            )
        )
    );
  }
}
