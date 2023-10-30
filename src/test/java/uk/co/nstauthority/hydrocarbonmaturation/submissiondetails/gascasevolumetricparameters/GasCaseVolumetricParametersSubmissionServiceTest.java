package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldgasvolumetricparameters.FieldGasVolumetricParametersService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GasCaseVolumetricParametersSubmissionServiceTest {

  @Mock
  private GasCaseVolumetricParametersFormService gasCaseVolumetricParametersFormService;

  @Mock
  private GasCaseVolumetricParametersFormValidator gasCaseVolumetricParametersFormValidator;

  @Mock
  private FieldGasVolumetricParametersService fieldGasVolumetricParametersService;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private GasCaseVolumetricParametersSubmissionService gasCaseVolumetricParametersSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new GasCaseVolumetricParametersForm();

    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(true);
    when(gasCaseVolumetricParametersFormService.getGasCaseVolumetricsForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(gasCaseVolumetricParametersFormValidator.isValid(eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(true);

    assertTrue(gasCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new GasCaseVolumetricParametersForm();
    var bindingResultWithError = ReverseRouter.emptyBindingResult();
    bindingResultWithError.addError(new FieldError("object", "field", "message"));

    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(true);
    when(gasCaseVolumetricParametersFormService.getGasCaseVolumetricsForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(gasCaseVolumetricParametersFormValidator.isValid(eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(false);

    assertFalse(gasCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenField_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new GasCaseVolumetricParametersForm();

    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(true);
    when(fieldGasVolumetricParametersService.getGasVolumetricsForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(gasCaseVolumetricParametersFormValidator.isValid(
        eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(true);

    assertTrue(gasCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenField_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new GasCaseVolumetricParametersForm();

    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(true);
    when(fieldGasVolumetricParametersService.getGasVolumetricsForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(gasCaseVolumetricParametersFormValidator.isValid(
        eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(false);

    assertFalse(gasCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotApplicable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail))
        .thenReturn(false);
    assertTrue(gasCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }
}