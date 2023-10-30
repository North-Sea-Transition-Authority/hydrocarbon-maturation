package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@ExtendWith(MockitoExtension.class)
class ReservoirAdditionsDetailPersistenceServiceTest {

  private static final String RESERVOIR_NAME_1 = "reservoir name 1";
  private static final String RESERVOIR_NAME_2 = "reservoir name 2";

  @Mock
  private ReservoirSubmissionDetailRepository reservoirSubmissionDetailRepository;

  @Mock
  private ReservoirPersistenceService reservoirPersistenceService;

  @Captor
  private ArgumentCaptor<List<ReservoirSubmissionDetail>> reservoirSubmissionDetailListCaptor;

  @Captor
  private ArgumentCaptor <ReservoirSubmissionDetail> reservoirSubmissionDetailCaptor;

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
    reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail);
    verify(reservoirSubmissionDetailRepository, times(1))
        .findBySubmissionDetailAndStatusOrderByReservoir_Name(submissionDetail, ReservoirSubmissionDetailStatus.EXTANT);
  }

  @Test
  void submissionHasReservoirs() {
    var submissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    reservoirSubmissionDetailPersistenceService.submissionHasReservoirs(submissionDetail);
    verify(reservoirSubmissionDetailRepository, times(1))
        .existsBySubmissionDetailAndStatus(submissionDetail, ReservoirSubmissionDetailStatus.EXTANT);
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
    verify(reservoirSubmissionDetailRepository, times(1)).saveAll(reservoirSubmissionDetailListCaptor.capture());
    assertThat(reservoirSubmissionDetailListCaptor.getValue()).extracting(
        ReservoirSubmissionDetail::getSubmissionDetail,
        ReservoirSubmissionDetail::getReservoir
    ).containsExactly(
        tuple(submissionDetail, reservoir1),
        tuple(submissionDetail, reservoir2)
    );
  }

  @Test
  void saveReservoirSubmissionDetail() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    var reservoir = new Reservoir();
    reservoirSubmissionDetailPersistenceService.saveReservoirSubmissionDetail(reservoir, submissionDetail);
    verify(reservoirSubmissionDetailRepository, times(1)).save(reservoirSubmissionDetailCaptor.capture());
    assertThat(reservoirSubmissionDetailCaptor.getValue()).extracting(
        ReservoirSubmissionDetail::getSubmissionDetail,
        ReservoirSubmissionDetail::getReservoir
    ).containsExactly(
        submissionDetail,
        reservoir
    );
  }

  @Test
  void getReservoirSubmissionDetail_verifyCalls() {
    var id = UUID.randomUUID();
    reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(id);
    verify(reservoirSubmissionDetailRepository, times(1)).findByIdAndStatus(id, ReservoirSubmissionDetailStatus.EXTANT);
  }

  @Test
  void getExtantReservoirSubmissionDetailOrThrow() {
    var id = UUID.randomUUID();
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();
    reservoirSubmissionDetail.setId(id);
    when(reservoirSubmissionDetailRepository.findByIdAndStatus(id, ReservoirSubmissionDetailStatus.EXTANT)).thenReturn(Optional.of(reservoirSubmissionDetail));
    assertThat(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(id))
        .isEqualTo(reservoirSubmissionDetail);
  }

  @Test
  void getExtantReservoirSubmissionDetailOrThrow_throws() {
    var id = UUID.randomUUID();
    when(reservoirSubmissionDetailRepository.findByIdAndStatus(id, ReservoirSubmissionDetailStatus.EXTANT)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(id))
        .isInstanceOf(RemiEntityNotFoundException.class)
        .hasMessage("Could not find reservoir submission detail for id %s".formatted(id));
  }

  @Test
  void getReservoirSubmissionDetail_submissionDetail() {
    var id = UUID.randomUUID();
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();
    reservoirSubmissionDetail.setId(id);

    var submissionDetail = new SubmissionDetail();
    when(reservoirSubmissionDetailRepository.findBySubmissionDetailAndStatusOrderByReservoir_Name(submissionDetail, ReservoirSubmissionDetailStatus.EXTANT))
        .thenReturn(List.of(reservoirSubmissionDetail));

    assertThat(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(submissionDetail))
        .isEqualTo(reservoirSubmissionDetail);
  }

  @Test
  void getReservoirSubmissionDetail_submissionDetail_throws() {
    var submissionDetail = new SubmissionDetail();
    when(reservoirSubmissionDetailRepository.findBySubmissionDetailAndStatusOrderByReservoir_Name(submissionDetail, ReservoirSubmissionDetailStatus.EXTANT))
        .thenReturn(List.of());
    assertThatThrownBy(() -> reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(submissionDetail))
        .isInstanceOf(RemiEntityNotFoundException.class)
        .hasMessage("Could not find reservoir submission detail for submission detail %s".formatted(submissionDetail.getId()));
  }

  @Test
  void getReservoirSubmissionDetails_verifyCalls() {
    var reservoir = new Reservoir();
    reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetails(reservoir);
    verify(reservoirSubmissionDetailRepository, times(1)).findAllByReservoir(reservoir);
  }

  @Test
  void getReservoirSubmissionDetailsForReservoirIds() {
    var firstReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var secondReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

    var firstReservoir = firstReservoirSubmissionDetail.getReservoir();
    var secondReservoir = secondReservoirSubmissionDetail.getReservoir();

    var thirdReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(firstReservoir);
    var fourthReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(secondReservoir);

    when(reservoirPersistenceService.getReservoirOrThrow(firstReservoir.getId())).thenReturn(firstReservoir);
    when(reservoirPersistenceService.getReservoirOrThrow(secondReservoir.getId())).thenReturn(secondReservoir);

    when(reservoirSubmissionDetailRepository.findAllByReservoir(firstReservoir))
        .thenReturn(List.of(firstReservoirSubmissionDetail, thirdReservoirSubmissionDetail));
    when(reservoirSubmissionDetailRepository.findAllByReservoir(secondReservoir))
        .thenReturn(List.of(secondReservoirSubmissionDetail, fourthReservoirSubmissionDetail));

    assertThat(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetailsForReservoirIds(
        Set.of(firstReservoir.getId().toString(), secondReservoir.getId().toString())))
        .containsExactlyInAnyOrder(
            firstReservoirSubmissionDetail, secondReservoirSubmissionDetail, thirdReservoirSubmissionDetail, fourthReservoirSubmissionDetail
        );
  }
}
