package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpapromotedvolumes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class FdpaPromotedVolumesSubmissionServiceTest {

  @Mock
  private FdpaPromotedVolumesFormValidator formValidator;

  @Mock
  private FdpaPromotedVolumesService volumesService;

  @InjectMocks
  private FdpaPromotedVolumesSubmissionService submissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = new SubmissionDetail();
    var form = new FdpaPromotedVolumesForm();

    when(volumesService.submissionDetailToForm(submissionDetail))
        .thenReturn(form);
    when(formValidator.isValid(eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(true);

    assertThat(submissionService.isSectionSubmittable(submissionDetail))
        .isTrue();
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = new SubmissionDetail();
    var form = new FdpaPromotedVolumesForm();

    when(volumesService.submissionDetailToForm(submissionDetail))
        .thenReturn(form);
    when(formValidator.isValid(eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(false);

    assertThat(submissionService.isSectionSubmittable(submissionDetail))
        .isFalse();
  }
}
