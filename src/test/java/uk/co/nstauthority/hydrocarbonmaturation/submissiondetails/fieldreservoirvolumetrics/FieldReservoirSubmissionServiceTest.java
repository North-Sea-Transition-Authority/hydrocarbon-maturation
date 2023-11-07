package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class FieldReservoirSubmissionServiceTest {

  @Mock
  private FieldReservoirVolumetricsFormValidator fieldReservoirVolumetricsFormValidator;

  @Mock
  private FieldReservoirVolumetricsFormService fieldReservoirVolumetricsFormService;

  @InjectMocks
  private FieldReservoirSubmissionService fieldReservoirSubmissionService;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
  }

  @Test
  void isSectionSubmittable_formValid_assertTrue() {
    when(fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail))
        .thenReturn(new FieldReservoirVolumetricsForm());
    when(fieldReservoirVolumetricsFormValidator.isValid(
        any(FieldReservoirVolumetricsForm.class),
        any(BindingResult.class),
        eq(reservoirSubmissionDetail),
        eq(ValidationType.FULL)))
        .thenReturn(true);

    assertTrue(fieldReservoirSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_formInvalid_assertFalse() {
    when(fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail))
        .thenReturn(new FieldReservoirVolumetricsForm());
    when(fieldReservoirVolumetricsFormValidator.isValid(
        any(FieldReservoirVolumetricsForm.class),
        any(BindingResult.class),
        eq(reservoirSubmissionDetail),
        eq(ValidationType.FULL)))
        .thenReturn(false);

    assertFalse(fieldReservoirSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }
}