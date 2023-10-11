package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

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
class OilFluidPropertySubmissionServiceTest {

  @Mock
  private OilFluidPropertyFormService oilFluidPropertyFormService;

  @Mock
  private OilFluidPropertyFormValidator oilFluidPropertyFormValidator;

  @InjectMocks
  private OilFluidPropertySubmissionService oilFluidPropertySubmissionService;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;
  private OilFluidPropertyForm oilFluidPropertyForm;

  @BeforeEach
  void setUp() {
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    oilFluidPropertyForm = new OilFluidPropertyForm();
  }

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    when(oilFluidPropertyFormService.getOilFluidPropertyForm(reservoirSubmissionDetail)).thenReturn(
        oilFluidPropertyForm);
    when(oilFluidPropertyFormValidator.isValid(eq(oilFluidPropertyForm), any(BindingResult.class), eq(reservoirSubmissionDetail), eq(ValidationType.FULL)))
        .thenReturn(true);

    assertTrue(oilFluidPropertySubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    when(oilFluidPropertyFormService.getOilFluidPropertyForm(reservoirSubmissionDetail)).thenReturn(oilFluidPropertyForm);
    when(oilFluidPropertyFormValidator.isValid(eq(oilFluidPropertyForm), any(BindingResult.class), eq(reservoirSubmissionDetail), eq(ValidationType.FULL)))
        .thenReturn(false);

    assertFalse(oilFluidPropertySubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }
}