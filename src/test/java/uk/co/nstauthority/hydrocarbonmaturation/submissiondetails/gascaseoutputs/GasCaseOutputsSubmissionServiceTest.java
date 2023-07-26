package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

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
class GasCaseOutputsSubmissionServiceTest {

  @Mock
  private GasCaseOutputsFormService gasCaseOutputsFormService;

  @InjectMocks
  private GasCaseOutputsSubmissionService gasCaseOutputsSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new GasCaseOutputsForm();

    var emptyBindingResult = ReverseRouter.emptyBindingResult();

    when(gasCaseOutputsFormService.getGasCaseOutputsForm(reservoirSubmissionDetail)).thenReturn(form);
    when(gasCaseOutputsFormService.validate(
        eq(form), any(BindingResult.class), eq(ValidationType.FULL), eq(submissionDetail)))
        .thenReturn(emptyBindingResult);

    assertTrue(gasCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new GasCaseOutputsForm();
    var bindingResultWithError = ReverseRouter.emptyBindingResult();
    bindingResultWithError.addError(new FieldError("object", "field", "message"));

    when(gasCaseOutputsFormService.getGasCaseOutputsForm(reservoirSubmissionDetail)).thenReturn(form);
    when(gasCaseOutputsFormService.validate(
        eq(form), any(BindingResult.class), eq(ValidationType.FULL), eq(submissionDetail)))
        .thenReturn(bindingResultWithError);

    assertFalse(gasCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }
}