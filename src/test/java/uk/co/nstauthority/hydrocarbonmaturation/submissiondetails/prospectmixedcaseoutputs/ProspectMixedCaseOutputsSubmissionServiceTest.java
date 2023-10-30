package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.prospectmixedcaseoutputs;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class ProspectMixedCaseOutputsSubmissionServiceTest {

  @Mock
  private ProspectMixedCaseOutputsFormService prospectMixedCaseOutputsFormService;

  @Mock
  private ProspectMixedCaseOutputsFormValidator prospectMixedCaseOutputsFormValidator;

  @InjectMocks
  private ProspectMixedCaseOutputsSubmissionService prospectMixedCaseOutputsSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new MixedCaseOutputsForm();

    var emptyBindingResult = ReverseRouter.emptyBindingResult();

    when(prospectMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail)).thenReturn(form);
    when(prospectMixedCaseOutputsFormValidator.isValid(
        any(MixedCaseOutputsForm.class), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
    ).thenReturn(true);

    assertTrue(prospectMixedCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new MixedCaseOutputsForm();
    var bindingResultWithError = ReverseRouter.emptyBindingResult();
    bindingResultWithError.addError(new FieldError("object", "field", "message"));

    when(prospectMixedCaseOutputsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail)).thenReturn(form);
    when(prospectMixedCaseOutputsFormValidator.isValid(
        any(MixedCaseOutputsForm.class), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
    ).thenReturn(false);

    assertFalse(prospectMixedCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotApplicable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    assertTrue(prospectMixedCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }
}