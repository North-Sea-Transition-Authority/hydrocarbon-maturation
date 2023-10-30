package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class OilCaseOutputsSubmissionServiceTest {

  @Mock
  private OilCaseOutputsFormService oilCaseOutputsFormService;

  @Mock
  private OilCaseOutputsFormValidator oilCaseOutputsFormValidator;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private OilCaseOutputsSubmissionService oilCaseOutputsSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new OilCaseOutputsForm();

    var emptyBindingResult = ReverseRouter.emptyBindingResult();

    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(true);
    when(oilCaseOutputsFormService.getOilCaseOutputsForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(oilCaseOutputsFormValidator.isValid(
        eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(true);

    assertTrue(oilCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new OilCaseOutputsForm();
    var bindingResultWithError = ReverseRouter.emptyBindingResult();
    bindingResultWithError.addError(new FieldError("object", "field", "message"));

    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(true);
    when(oilCaseOutputsFormService.getOilCaseOutputsForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(oilCaseOutputsFormValidator.isValid(
        eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(false);

    assertFalse(oilCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotApplicable_thenTrue() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(false);
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail))
        .thenReturn(false);
    assertTrue(oilCaseOutputsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }
}