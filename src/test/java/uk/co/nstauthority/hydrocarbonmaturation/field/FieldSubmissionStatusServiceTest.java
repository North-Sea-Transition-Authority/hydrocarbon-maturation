package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@ExtendWith(MockitoExtension.class)
class FieldSubmissionStatusServiceTest {
  @Mock
  private FieldStatusService fieldStatusService;
  @Mock
  private FieldRepository fieldRepository;
  @InjectMocks
  private FieldSubmissionStatusService fieldSubmissionStatusService;

  private SubmissionDetail submissionDetail;
  private Field field;
  private ArgumentCaptor<FieldStatus> captor;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    field = new Field();
    captor = ArgumentCaptor.forClass(FieldStatus.class);
  }

  @Test
  void updateFieldStatusForSubmission_noFieldForResource_thenThrowEntityNotFound() {
    when(fieldRepository.findByResource(submissionDetail.getSubmission().getResource()))
        .thenReturn(Optional.empty());

    assertThatExceptionOfType(RemiEntityNotFoundException.class)
        .isThrownBy(() -> fieldSubmissionStatusService.updateFieldStatusForSubmission(submissionDetail));
    verifyNoInteractions(fieldStatusService);
  }

  @Test
  void updateFieldStatusForSubmission_csrSubmission_assertCorrectStatusUpdate() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var expectedNewStatus = FieldStatus.STATUS_300;

    when(fieldRepository.findByResource(submissionDetail.getSubmission().getResource()))
        .thenReturn(Optional.of(field));

    fieldSubmissionStatusService.updateFieldStatusForSubmission(submissionDetail);
    verify(fieldStatusService, times(1)).setMostRecentFieldStatus(eq(field), captor.capture());

    assertThat(captor.getValue()).isEqualTo(expectedNewStatus);
  }

  @Test
  void updateFieldStatusForSubmission_fdpSubmission_assertCorrectStatusUpdate() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDP);

    var expectedNewStatus = FieldStatus.STATUS_500;

    when(fieldRepository.findByResource(submissionDetail.getSubmission().getResource()))
        .thenReturn(Optional.of(field));

    fieldSubmissionStatusService.updateFieldStatusForSubmission(submissionDetail);
    verify(fieldStatusService, times(1)).setMostRecentFieldStatus(eq(field), captor.capture());

    assertThat(captor.getValue()).isEqualTo(expectedNewStatus);
  }

  @Test
  void updateFieldStatusForSubmission_copSubmission_assertNoStatusUpdate() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    when(fieldRepository.findByResource(submissionDetail.getSubmission().getResource()))
        .thenReturn(Optional.of(field));

    fieldSubmissionStatusService.updateFieldStatusForSubmission(submissionDetail);
    verify(fieldStatusService, never()).setMostRecentFieldStatus(any(), any());

  }

  @Test
  void updateFieldStatusForAcceptance_noFieldForResource_thenThrowEntityNotFound() {
    when(fieldRepository.findByResource(submissionDetail.getSubmission().getResource()))
        .thenReturn(Optional.empty());

    assertThatExceptionOfType(RemiEntityNotFoundException.class)
        .isThrownBy(() -> fieldSubmissionStatusService.updateFieldStatusForAcceptance(submissionDetail));
    verifyNoInteractions(fieldStatusService);
  }

  @Test
  void updateFieldStatusForAcceptance_csrSubmission_assertCorrectStatusUpdate() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var expectedNewStatus = FieldStatus.STATUS_400;

    when(fieldRepository.findByResource(submissionDetail.getSubmission().getResource()))
        .thenReturn(Optional.of(field));

    fieldSubmissionStatusService.updateFieldStatusForAcceptance(submissionDetail);
    verify(fieldStatusService, times(1)).setMostRecentFieldStatus(eq(field), captor.capture());

    assertThat(captor.getValue()).isEqualTo(expectedNewStatus);
  }

  @Test
  void updateFieldStatusForAcceptance_fdpSubmission_assertCorrectStatusUpdate() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDP);

    var expectedNewStatus = FieldStatus.STATUS_500;

    when(fieldRepository.findByResource(submissionDetail.getSubmission().getResource()))
        .thenReturn(Optional.of(field));

    fieldSubmissionStatusService.updateFieldStatusForAcceptance(submissionDetail);
    verify(fieldStatusService, times(1)).setMostRecentFieldStatus(eq(field), captor.capture());

    assertThat(captor.getValue()).isEqualTo(expectedNewStatus);
  }

  @Test
  void updateFieldStatusForAcceptance_copSubmission_assertCorrectStatusUpdate() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    var expectedNewStatus = FieldStatus.STATUS_900;

    when(fieldRepository.findByResource(submissionDetail.getSubmission().getResource()))
        .thenReturn(Optional.of(field));

    fieldSubmissionStatusService.updateFieldStatusForAcceptance(submissionDetail);
    verify(fieldStatusService, times(1)).setMostRecentFieldStatus(eq(field), captor.capture());

    assertThat(captor.getValue()).isEqualTo(expectedNewStatus);
  }


  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_CSR", "CREATE_FDP", "CREATE_COP"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void updateFieldStatusForAcceptance_anyOtherSubmissionTypes_assertNoStatusUpdate(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    when(fieldRepository.findByResource(submissionDetail.getSubmission().getResource()))
        .thenReturn(Optional.of(field));

    fieldSubmissionStatusService.updateFieldStatusForAcceptance(submissionDetail);
    verify(fieldStatusService, never()).setMostRecentFieldStatus(any(), any());
  }
}