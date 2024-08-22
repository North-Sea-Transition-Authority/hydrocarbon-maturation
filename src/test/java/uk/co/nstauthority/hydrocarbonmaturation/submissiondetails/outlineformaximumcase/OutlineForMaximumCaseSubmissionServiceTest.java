package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

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
class OutlineForMaximumCaseSubmissionServiceTest {
  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private OutlineForMaximumCaseUploadFormService outlineForMaximumCaseUploadFormService;
  @Mock
  private OutlineForMaximumCaseVerifyFormService outlineForMaximumCaseVerifyFormService;

  @InjectMocks
  private OutlineForMaximumCaseSubmissionService outlineForMaximumCaseSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var uploadForm = new OutlineForMaximumCaseUploadForm();
    var verifyForm = new OutlineForMaximumCaseVerifyForm();
    var emptyBindingResult = ReverseRouter.emptyBindingResult();

    when(outlineForMaximumCaseUploadFormService.getOutlineForMaximumCaseUploadForm(submissionDetail)).thenReturn(uploadForm);

    when(outlineForMaximumCaseUploadFormService.validate(eq(uploadForm), any(BindingResult.class), eq(ValidationType.FULL), eq(submissionDetail), eq(USER)))
        .thenReturn(emptyBindingResult);

    when(outlineForMaximumCaseVerifyFormService.getOutlineForMaximumCaseVerifyForm(submissionDetail)).thenReturn(verifyForm);

    when(outlineForMaximumCaseVerifyFormService.validate(eq(verifyForm), any(BindingResult.class), eq(ValidationType.FULL), eq(submissionDetail), eq(USER)))
        .thenReturn(emptyBindingResult);

    assertTrue(outlineForMaximumCaseSubmissionService.isSectionSubmittable(submissionDetail, USER));

  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var form = new OutlineForMaximumCaseUploadForm();
    var bindingResultWithError = ReverseRouter.emptyBindingResult();
    bindingResultWithError.addError(new FieldError("object", "field", "message"));

    when(outlineForMaximumCaseUploadFormService.getOutlineForMaximumCaseUploadForm(submissionDetail)).thenReturn(form);

    when(outlineForMaximumCaseUploadFormService.validate(eq(form), any(BindingResult.class), eq(ValidationType.FULL), eq(submissionDetail), eq(USER)))
        .thenReturn(bindingResultWithError);

    assertFalse(outlineForMaximumCaseSubmissionService.isSectionSubmittable(submissionDetail, USER));
  }
}