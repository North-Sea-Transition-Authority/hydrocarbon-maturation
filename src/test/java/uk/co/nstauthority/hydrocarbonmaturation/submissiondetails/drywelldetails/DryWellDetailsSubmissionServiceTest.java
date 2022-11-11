package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

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
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class DryWellDetailsSubmissionServiceTest {

  @Mock
  private DryWellDetailsFormService dryWellDetailsFormService;

  @InjectMocks
  private DryWellDetailsSubmissionService dryWellDetailsSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    var form = new DryWellDetailsForm();
    var emptyBindingResult = ReverseRouter.emptyBindingResult();

    when(dryWellDetailsFormService.getDryWellDetailsForm(submissionDetail))
        .thenReturn(form);
    when(dryWellDetailsFormService.validate(eq(form), any(BindingResult.class), eq(ValidationType.FULL)))
        .thenReturn(emptyBindingResult);

    assertTrue(dryWellDetailsSubmissionService.isSectionSubmittable(submissionDetail));
  }
}