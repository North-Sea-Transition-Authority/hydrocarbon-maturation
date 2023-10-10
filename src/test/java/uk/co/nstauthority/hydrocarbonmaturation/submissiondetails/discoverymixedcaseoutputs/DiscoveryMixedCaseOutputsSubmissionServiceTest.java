package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverymixedcaseoutputs;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class DiscoveryMixedCaseOutputsSubmissionServiceTest {

  @Mock
  private DiscoveryMixedCaseOutputsFormService discoveryMixedCaseOutputsFormService;

  @Mock
  private DiscoveryMixedCaseOutputsFormValidator discoveryMixedCaseOutputsFormValidator;

  @InjectMocks
  private DiscoveryMixedCaseOutputsSubmissionService discoveryMixedCaseOutputsSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new MixedCaseOutputsForm();

    var emptyBindingResult = ReverseRouter.emptyBindingResult();

    when(discoveryMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail)).thenReturn(form);
    when(discoveryMixedCaseOutputsFormValidator.isValid(
        any(MixedCaseOutputsForm.class), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
    ).thenReturn(true);

    assertTrue(discoveryMixedCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new MixedCaseOutputsForm();
    var bindingResultWithError = ReverseRouter.emptyBindingResult();
    bindingResultWithError.addError(new FieldError("object", "field", "message"));

    when(discoveryMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail)).thenReturn(form);
    when(discoveryMixedCaseOutputsFormValidator.isValid(
        any(MixedCaseOutputsForm.class), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
    ).thenReturn(false);

    assertFalse(discoveryMixedCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }
}