package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class ReservoirPropertiesSubmissionServiceTest {

  @Mock
  private ReservoirPropertiesFormService reservoirPropertiesFormService;

  @Mock
  private ReservoirPropertiesFormValidator reservoirPropertiesFormValidator;

  @InjectMocks
  private ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new ReservoirPropertiesForm();

    when(reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail)).thenReturn(form);

    when(reservoirPropertiesFormValidator.isValid(eq(form), any(BindingResult.class), eq(reservoirSubmissionDetail), eq(ValidationType.FULL)))
        .thenReturn(true);

    assertTrue(reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));

  }


  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new ReservoirPropertiesForm();

    when(reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail)).thenReturn(form);

    when(reservoirPropertiesFormValidator.isValid(eq(form), any(BindingResult.class), eq(reservoirSubmissionDetail), eq(ValidationType.FULL)))
        .thenReturn(false);

    assertFalse(reservoirPropertiesSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));

  }
}