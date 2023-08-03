package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

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
class PermeabilitySubmissionServiceTest {

  @Mock
  private PermeabilityFormService permeabilityFormService;

  @Mock
  private PermeabilityFormValidator permeabilityFormValidator;

  @InjectMocks
  private PermeabilitySubmissionService permeabilitySubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new PermeabilityForm();
    var emptyBindingResult = ReverseRouter.emptyBindingResult();

    when(permeabilityFormService.getPermeabilityForm(reservoirSubmissionDetail)).thenReturn(form);

    when(permeabilityFormValidator.isValid(eq(form), any(BindingResult.class), any(), eq(ValidationType.FULL))).thenReturn(true);

    assertTrue(permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail));

  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new PermeabilityForm();
    var bindingResultWithError = ReverseRouter.emptyBindingResult();
    bindingResultWithError.addError(new FieldError("object", "field", "message"));

    when(permeabilityFormService.getPermeabilityForm(reservoirSubmissionDetail)).thenReturn(form);

    when(permeabilityFormValidator.isValid(eq(form), any(BindingResult.class), any(), eq(ValidationType.FULL))).thenReturn(false);

    assertFalse(permeabilitySubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }
}