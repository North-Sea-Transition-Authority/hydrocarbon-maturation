package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
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

  @InjectMocks
  private ReservoirTaskListService reservoirTaskListService;

  @Test
  void getAllReservoirTaskLists() {
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
}
