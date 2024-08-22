package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics;

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
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class FieldGasCaseVolumetricTotalsSubmissionServiceTest {
  private static final User USER = UserTestUtil.Builder().build();
  @Mock
  FieldGasCaseVolumetricTotalsService fieldGasCaseVolumetricTotalsService;

  @Mock
  FieldGasCaseVolumetricTotalsFormValidator fieldGasCaseVolumetricTotalsFormValidator;

  @InjectMocks
  FieldGasCaseVolumetricTotalsSubmissionService fieldGasCaseVolumetricTotalsSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    var form = new GasCaseOutputsForm();

    when(fieldGasCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail)).thenReturn(form);
    when(fieldGasCaseVolumetricTotalsFormValidator.isValid(
        any(GasCaseOutputsForm.class), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
    ).thenReturn(true);

    assertTrue(fieldGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();

    var form = new GasCaseOutputsForm();
    var bindingResultWithError = ReverseRouter.emptyBindingResult();
    bindingResultWithError.addError(new FieldError("object", "field", "message"));

    when(fieldGasCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail)).thenReturn(form);
    when(fieldGasCaseVolumetricTotalsFormValidator.isValid(
        any(GasCaseOutputsForm.class), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
    ).thenReturn(false);

    assertFalse(fieldGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER));
  }

  @Test
  void isSectionSubmittable_whenNotFieldSubmission_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    assertTrue(fieldGasCaseVolumetricTotalsSubmissionService.isSectionSubmittable(submissionDetail, USER));
  }
}