package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class SourceAndSealSubmissionServiceTest {

  @Mock
  private SourceAndSealFormService sourceAndSealFormService;

  @Mock
  private SourceAndSealFormValidator sourceAndSealFormValidator;

  @InjectMocks
  private SourceAndSealSubmissionService sourceAndSealSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var form = new SourceAndSealForm();

    when(sourceAndSealFormService.getSourceAndSealForm(submissionDetail)).thenReturn(form);

    when(sourceAndSealFormValidator.isValid(eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(true);

    assertTrue(sourceAndSealSubmissionService.isSectionSubmittable(submissionDetail));

  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var form = new SourceAndSealForm();

    when(sourceAndSealFormService.getSourceAndSealForm(submissionDetail)).thenReturn(form);

    when(sourceAndSealFormValidator.isValid(eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(false);

    assertFalse(sourceAndSealSubmissionService.isSectionSubmittable(submissionDetail));
  }
}