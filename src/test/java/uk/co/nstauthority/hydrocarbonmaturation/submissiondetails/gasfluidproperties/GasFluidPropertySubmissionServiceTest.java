package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

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
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GasFluidPropertySubmissionServiceTest {

  @Mock
  private GasFluidPropertyFormService gasFluidPropertyFormService;

  @Mock
  private GasFluidPropertyFormValidator gasFluidPropertyFormValidator;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private GasFluidPropertySubmissionService gasFluidPropertySubmissionService;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;
  private GasFluidPropertyForm gasFluidPropertyForm;

  @BeforeEach
  void setUp() {
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    gasFluidPropertyForm = new GasFluidPropertyForm();
  }

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(true);
    when(gasFluidPropertyFormService.getGasFluidPropertyForm(reservoirSubmissionDetail))
        .thenReturn(gasFluidPropertyForm);
    when(gasFluidPropertyFormValidator.isValid(eq(gasFluidPropertyForm), any(BindingResult.class), eq(reservoirSubmissionDetail), eq(ValidationType.FULL)))
        .thenReturn(true);

    assertTrue(gasFluidPropertySubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var bindingResultWithError = ReverseRouter.emptyBindingResult();
    bindingResultWithError.addError(new FieldError("object", "field", "message"));

    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(true);
    when(gasFluidPropertyFormService.getGasFluidPropertyForm(reservoirSubmissionDetail)).thenReturn(gasFluidPropertyForm);
    when(gasFluidPropertyFormValidator.isValid(eq(gasFluidPropertyForm), any(BindingResult.class), eq(reservoirSubmissionDetail), eq(ValidationType.FULL)))
        .thenReturn(false);

    assertFalse(gasFluidPropertySubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotApplicable_thenTrue() {
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(false);
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail))
        .thenReturn(false);
    assertTrue(gasFluidPropertySubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }
}