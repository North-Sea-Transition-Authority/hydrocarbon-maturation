package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

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
class GeneralFluidPropertySubmissionServiceTest {

  @Mock
  private GeneralFluidPropertyFormService generalFluidPropertyFormService;

  @Mock
  private GeneralFluidPropertyFormValidator generalFluidPropertyFormValidator;

  @InjectMocks
  private GeneralFluidPropertySubmissionService generalFluidPropertySubmissionService;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  private GeneralFluidPropertyForm form;

  @BeforeEach
  void setUp() {
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    form = new GeneralFluidPropertyForm();
  }

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    when(generalFluidPropertyFormService.getGeneralFluidPropertyForm(reservoirSubmissionDetail)).thenReturn(form);
    when(generalFluidPropertyFormValidator.isValid(eq(form), any(BindingResult.class), eq(reservoirSubmissionDetail), eq(ValidationType.FULL)))
        .thenReturn(true);

    assertTrue(generalFluidPropertySubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    when(generalFluidPropertyFormService.getGeneralFluidPropertyForm(reservoirSubmissionDetail)).thenReturn(form);
    when(generalFluidPropertyFormValidator.isValid(eq(form), any(BindingResult.class), eq(reservoirSubmissionDetail), eq(ValidationType.FULL)))
        .thenReturn(false);

    assertFalse(generalFluidPropertySubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }
}