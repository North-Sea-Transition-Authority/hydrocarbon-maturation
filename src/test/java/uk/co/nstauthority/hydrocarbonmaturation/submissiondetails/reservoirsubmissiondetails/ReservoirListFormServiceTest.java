package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@ExtendWith(MockitoExtension.class)
class ReservoirListFormServiceTest {

  private static final String RESERVOIR_NAME_1 = "reservoir name 1";
  private static final String RESERVOIR_NAME_2 = "reservoir name 2";

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  private ReservoirListFormService reservoirListFormService;

  @BeforeEach
  void setUp() {
    reservoirListFormService = new ReservoirListFormService(reservoirSubmissionDetailPersistenceService);
  }

  @Test
  void getReservoirForm() {
    var submissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    var reservoir1 = new Reservoir();
    reservoir1.setName(RESERVOIR_NAME_1);
    var reservoirSubmissionDetail1 = new ReservoirSubmissionDetail();
    reservoirSubmissionDetail1.setReservoir(reservoir1);
    var reservoir2 = new Reservoir();
    reservoir2.setName(RESERVOIR_NAME_2);
    var reservoirSubmissionDetail2 = new ReservoirSubmissionDetail();
    reservoirSubmissionDetail2.setReservoir(reservoir2);
    var reservoirSubmissionList = List.of(reservoirSubmissionDetail1, reservoirSubmissionDetail2);
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionList);
    var reservoirListForm = reservoirListFormService.getReservoirForm(submissionDetail);
    assertThat(reservoirListForm.getReservoirs()).containsExactly(RESERVOIR_NAME_1, RESERVOIR_NAME_2);
  }
}
