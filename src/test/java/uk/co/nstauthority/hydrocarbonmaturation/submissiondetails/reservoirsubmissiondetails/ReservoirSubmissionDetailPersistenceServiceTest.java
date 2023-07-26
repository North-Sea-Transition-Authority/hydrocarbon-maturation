package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@ExtendWith(MockitoExtension.class)
class ReservoirSubmissionDetailPersistenceServiceTest {

  private static final String RESERVOIR_NAME_1 = "reservoir name 1";
  private static final String RESERVOIR_NAME_2 = "reservoir name 2";

  @Mock
  private ReservoirSubmissionDetailRepository reservoirSubmissionDetailRepository;

  @Mock
  private ReservoirPersistenceService reservoirPersistenceService;

  @Captor
  private ArgumentCaptor<List<ReservoirSubmissionDetail>> reservoirSubmissionDetailCaptor;

  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @BeforeEach
  void setUp() {
    reservoirSubmissionDetailPersistenceService = new ReservoirSubmissionDetailPersistenceService(
        reservoirSubmissionDetailRepository, reservoirPersistenceService
    );
  }

  @Test
  void getAllReservoirsForSubmissionDetail() {
    var submissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(submissionDetail);
    verify(reservoirSubmissionDetailRepository, times(1)).findBySubmissionDetailOrderByReservoir_Name(submissionDetail);
  }

  @Test
  void saveReservoirSubmissionDetails() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    var form = new ReservoirListForm();
    form.setReservoirs(List.of(RESERVOIR_NAME_1, RESERVOIR_NAME_2));
    var reservoir1 = new Reservoir();
    var reservoir2 = new Reservoir();
    when(reservoirPersistenceService.createReservoir(RESERVOIR_NAME_1)).thenReturn(reservoir1);
    when(reservoirPersistenceService.createReservoir(RESERVOIR_NAME_2)).thenReturn(reservoir2);
    reservoirSubmissionDetailPersistenceService.saveReservoirSubmissionDetails(form, submissionDetail);
    verify(reservoirSubmissionDetailRepository, times(1)).saveAll(reservoirSubmissionDetailCaptor.capture());
    assertThat(reservoirSubmissionDetailCaptor.getValue()).extracting(
        ReservoirSubmissionDetail::getSubmissionDetail,
        ReservoirSubmissionDetail::getReservoir
    ).containsExactly(
        tuple(submissionDetail, reservoir1),
        tuple(submissionDetail, reservoir2)
    );
  }
}
