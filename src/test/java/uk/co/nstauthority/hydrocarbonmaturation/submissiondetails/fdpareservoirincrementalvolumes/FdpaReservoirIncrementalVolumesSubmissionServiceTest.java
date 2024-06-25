package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpareservoirincrementalvolumes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class FdpaReservoirIncrementalVolumesSubmissionServiceTest {
  
  @Mock
  private FdpaReservoirIncrementalVolumesFormValidator formValidator;

  @Mock
  private FdpaReservoirIncrementalVolumesService volumesService;

  @InjectMocks
  private FdpaReservoirIncrementalVolumesSubmissionService submissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();
    var form = new FdpaReservoirIncrementalVolumesForm();

    when(volumesService.submissionDetailToForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(formValidator.isValid(eq(form), any(BindingResult.class), eq(reservoirSubmissionDetail), eq(ValidationType.FULL)))
        .thenReturn(true);

    assertThat(submissionService.isSectionSubmittable(reservoirSubmissionDetail))
        .isTrue();
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();
    var form = new FdpaReservoirIncrementalVolumesForm();

    when(volumesService.submissionDetailToForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(formValidator.isValid(eq(form), any(BindingResult.class), eq(reservoirSubmissionDetail), eq(ValidationType.FULL)))
        .thenReturn(false);

    assertThat(submissionService.isSectionSubmittable(reservoirSubmissionDetail))
        .isFalse();
  }
}