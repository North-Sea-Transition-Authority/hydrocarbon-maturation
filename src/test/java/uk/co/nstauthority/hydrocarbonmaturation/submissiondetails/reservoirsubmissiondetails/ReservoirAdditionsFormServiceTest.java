package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.ReservoirAdditionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@ExtendWith(MockitoExtension.class)
class ReservoirAdditionsFormServiceTest {

  @Mock
  private ReservoirAdditionsRepository reservoirAdditionsRepository;

  private SubmissionDetail submissionDetail;

  private ReservoirAdditions reservoirAdditions;

  @InjectMocks
  private ReservoirAdditionsFormService reservoirAdditionsFormService;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);

    reservoirAdditions = new ReservoirAdditions()
        .setReservoirAdditionStatus(ReservoirAdditionStatus.NO);
  }

  @Test
  void updateReservoirAdditionStatus_exists_thenUpdate() {
    when(reservoirAdditionsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(reservoirAdditions));
    var captor = ArgumentCaptor.forClass(ReservoirAdditions.class);

    reservoirAdditionsFormService.updateReservoirAdditionStatus(submissionDetail, ReservoirAdditionStatus.NO);
    verify(reservoirAdditionsRepository, times(1)).save(captor.capture());

    assertThat(captor.getValue())
        .extracting(ReservoirAdditions::getReservoirAdditionStatus)
        .isEqualTo(reservoirAdditions.getReservoirAdditionStatus());
  }

  @Test
  void updateReservoirAdditionStatus_noneExists_thenCreateNew() {
    when(reservoirAdditionsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());
    var captor = ArgumentCaptor.forClass(ReservoirAdditions.class);

    reservoirAdditionsFormService.updateReservoirAdditionStatus(submissionDetail, ReservoirAdditionStatus.NO);
    verify(reservoirAdditionsRepository, times(1)).save(captor.capture());

    assertThat(captor.getValue())
        .extracting(ReservoirAdditions::getReservoirAdditionStatus)
        .isEqualTo(reservoirAdditions.getReservoirAdditionStatus());
  }

  @Test
  void getReservoirAdditionsToForm_exists_thenSetStatus() {
    when(reservoirAdditionsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(reservoirAdditions));

    assertThat(reservoirAdditionsFormService.getReservoirAdditionsToForm(submissionDetail))
        .extracting(ReservoirAdditionForm::getReservoirAdditionStatus)
        .isEqualTo(reservoirAdditions.getReservoirAdditionStatus());
  }

  @Test
  void getReservoirAdditionsToForm_noneExists_thenNull() {
    when(reservoirAdditionsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    assertThat(reservoirAdditionsFormService.getReservoirAdditionsToForm(submissionDetail))
        .extracting(ReservoirAdditionForm::getReservoirAdditionStatus)
        .isNull();
  }
}