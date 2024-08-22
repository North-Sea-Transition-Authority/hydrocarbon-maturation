package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

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
class OpportunityDetailsSubmissionServiceTest {
  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private OpportunityDetailsFormService opportunityDetailsFormService;

  @InjectMocks
  private OpportunityDetailsSubmissionService opportunityDetailsSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var form = new OpportunityDetailsForm();
    var emptyBindingResult = ReverseRouter.emptyBindingResult();

    when(opportunityDetailsFormService.getOpportunityDetailsForm(submissionDetail)).thenReturn(form);

    when(opportunityDetailsFormService.validate(
        eq(form), any(BindingResult.class), eq(ValidationType.FULL), eq(submissionDetail), any()))
        .thenReturn(emptyBindingResult);

    assertTrue(opportunityDetailsSubmissionService.isSectionSubmittable(submissionDetail, USER));

  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var form = new OpportunityDetailsForm();
    var bindingResultWithError = ReverseRouter.emptyBindingResult();
    bindingResultWithError.addError(new FieldError("object", "field", "message"));

    when(opportunityDetailsFormService.getOpportunityDetailsForm(submissionDetail)).thenReturn(form);

    when(opportunityDetailsFormService.validate(
        eq(form), any(BindingResult.class), eq(ValidationType.FULL), eq(submissionDetail), any()))
        .thenReturn(bindingResultWithError);

    assertFalse(opportunityDetailsSubmissionService.isSectionSubmittable(submissionDetail, USER));
  }
}