package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

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
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GasFluidPropertySubmissionServiceTest {

  @Mock
  private GasFluidPropertyFormService gasFluidPropertyFormService;

  @InjectMocks
  private GasFluidPropertySubmissionService gasFluidPropertySubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var form = new GasFluidPropertyForm();
    var emptyBindingResult = ReverseRouter.emptyBindingResult();

    when(gasFluidPropertyFormService.getGasFluidPropertyForm(submissionDetail)).thenReturn(form);
    when(gasFluidPropertyFormService.validate(eq(form), any(BindingResult.class), eq(ValidationType.FULL)))
        .thenReturn(emptyBindingResult);

    assertTrue(gasFluidPropertySubmissionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var form = new GasFluidPropertyForm();
    var bindingResultWithError = ReverseRouter.emptyBindingResult();
    bindingResultWithError.addError(new FieldError("object", "field", "message"));

    when(gasFluidPropertyFormService.getGasFluidPropertyForm(submissionDetail)).thenReturn(form);
    when(gasFluidPropertyFormService.validate(eq(form), any(BindingResult.class), eq(ValidationType.FULL)))
        .thenReturn(bindingResultWithError);

    assertFalse(gasFluidPropertySubmissionService.isSectionSubmittable(submissionDetail));
  }
}