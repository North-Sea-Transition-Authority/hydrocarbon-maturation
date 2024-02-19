package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetricparameters;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class MixedCaseVolumetricParametersSubmissionServiceTest {

  @Mock
  private MixedCaseVolumetricParametersFormService mixedCaseVolumetricParametersFormService;

  @Mock
  private MixedCaseVolumetricParametersFormValidator mixedCaseVolumetricParametersFormValidator;

  @InjectMocks
  private MixedCaseVolumetricParametersSubmissionService mixedCaseVolumetricParametersSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new MixedCaseVolumetricParametersForm();

    var emptyBindingResult = ReverseRouter.emptyBindingResult();

    when(mixedCaseVolumetricParametersFormService.getMixedCaseVolumetricsForm(reservoirSubmissionDetail)).thenReturn(form);

    when(mixedCaseVolumetricParametersFormValidator.isValid(
        eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)
    )).thenReturn(true);

    assertTrue(mixedCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new MixedCaseVolumetricParametersForm();
    var bindingResultWithError = ReverseRouter.emptyBindingResult();
    bindingResultWithError.addError(new FieldError("object", "field", "message"));

    when(mixedCaseVolumetricParametersFormService.getMixedCaseVolumetricsForm(reservoirSubmissionDetail))
        .thenReturn(form);

    when(mixedCaseVolumetricParametersFormValidator.isValid(
        eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)
    )).thenReturn(false);

    assertFalse(mixedCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }
}