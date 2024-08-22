package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties;

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
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class FieldTrapPropertiesSubmissionServiceTest {

  @Mock
  private FieldTrapPropertiesFormService fieldTrapPropertiesFormService;

  @Mock
  private FieldTrapPropertiesFormValidator fieldTrapPropertiesFormValidator;

  @InjectMocks
  private FieldTrapPropertiesSubmissionService fieldTrapPropertiesSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    var form = new FieldTrapPropertiesForm();

    when(fieldTrapPropertiesFormService.submissionDetailToForm(submissionDetail)).thenReturn(form);
    when(fieldTrapPropertiesFormValidator.isValid(
        any(FieldTrapPropertiesForm.class), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
    ).thenReturn(true);

    assertTrue(fieldTrapPropertiesSubmissionService.isSectionSubmittable(submissionDetail, null));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();

    var form = new FieldTrapPropertiesForm();
    var bindingResultWithError = ReverseRouter.emptyBindingResult();
    bindingResultWithError.addError(new FieldError("object", "field", "message"));

    when(fieldTrapPropertiesFormService.submissionDetailToForm(submissionDetail)).thenReturn(form);
    when(fieldTrapPropertiesFormValidator.isValid(
        any(FieldTrapPropertiesForm.class), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
    ).thenReturn(false);

    assertFalse(fieldTrapPropertiesSubmissionService.isSectionSubmittable(submissionDetail, null));
  }

  @Test
  void isSectionSubmittable_whenNotFieldSubmission_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    assertTrue(fieldTrapPropertiesSubmissionService.isSectionSubmittable(submissionDetail, null));
  }
}