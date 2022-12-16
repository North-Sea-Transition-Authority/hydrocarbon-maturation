package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class DrillStemTestSubmissionServiceTest {

  @Mock
  private DrillStemTestFormService dryWellDetailsFormService;

  @InjectMocks
  private DrillStemTestSubmissionService dryWellDetailsSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    var form = new DrillStemTestInitialForm();
    var emptyBindingResult = ReverseRouter.emptyBindingResult();

    when(dryWellDetailsFormService.getDrillStemTestInitialForm(submissionDetail))
        .thenReturn(form);
    when(dryWellDetailsFormService.validate(eq(form), any(BindingResult.class), eq(ValidationType.FULL)))
        .thenReturn(emptyBindingResult);

    assertTrue(dryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    var form = new DrillStemTestInitialForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult.addError(new ObjectError("", ""));

    when(dryWellDetailsFormService.getDrillStemTestInitialForm(submissionDetail))
        .thenReturn(form);
    when(dryWellDetailsFormService.validate(eq(form), any(BindingResult.class), eq(ValidationType.FULL)))
        .thenReturn(bindingResult);

    assertFalse(dryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail));
  }
}