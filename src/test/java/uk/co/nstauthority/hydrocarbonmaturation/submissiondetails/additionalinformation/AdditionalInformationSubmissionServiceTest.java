package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class AdditionalInformationSubmissionServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private AdditionalInformationFormService additionalInformationFormService;

  @InjectMocks
  private AdditionalInformationSubmissionService additionalInformationSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var form = new AdditionalInformationForm();

    var emptyBindingResult = ReverseRouter.emptyBindingResult();

    when(additionalInformationFormService.getAdditionalInformationForm(submissionDetail)).thenReturn(form);

    when(additionalInformationFormService.validate(eq(form), any(BindingResult.class),
        eq(ValidationType.FULL), eq(submissionDetail), eq(USER)))
        .thenReturn(emptyBindingResult);

    assertTrue(additionalInformationSubmissionService.isSectionSubmittable(submissionDetail, USER));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var form = new AdditionalInformationForm();
    var bindingResultWithError = ReverseRouter.emptyBindingResult();
    bindingResultWithError.addError(new FieldError("object", "field", "message"));

    when(additionalInformationFormService.getAdditionalInformationForm(submissionDetail)).thenReturn(form);

    when(additionalInformationFormService.validate(eq(form), any(BindingResult.class),
        eq(ValidationType.FULL), eq(submissionDetail), eq(USER)))
        .thenReturn(bindingResultWithError);

    assertFalse(additionalInformationSubmissionService.isSectionSubmittable(submissionDetail, USER));
  }
}