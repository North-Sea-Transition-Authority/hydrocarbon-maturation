package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldoilvolumetricparameters.FieldOilVolumetricParametersService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class OilCaseVolumetricParametersSubmissionServiceTest {

  @Mock
  private OilCaseVolumetricParametersFormService oilCaseVolumetricParametersFormService;

  @Mock
  private FieldOilVolumetricParametersService fieldOilVolumetricParametersService;

  @Mock
  private OilCaseVolumetricParametersFormValidator oilCaseVolumetricParametersFormValidator;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private OilCaseVolumetricParametersSubmissionService oilCaseVolumetricParametersSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new OilCaseVolumetricParametersForm();

    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(true);
    when(oilCaseVolumetricParametersFormService.getOilCaseVolumetricsForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(oilCaseVolumetricParametersFormValidator.isValid(
        eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(true);

    assertTrue(oilCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new OilCaseVolumetricParametersForm();

    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(true);
    when(oilCaseVolumetricParametersFormService.getOilCaseVolumetricsForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(oilCaseVolumetricParametersFormValidator.isValid(
        eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(false);

    assertFalse(oilCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenField_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new OilCaseVolumetricParametersForm();

    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(true);
    when(fieldOilVolumetricParametersService.getOilVolumetricsForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(oilCaseVolumetricParametersFormValidator.isValid(
        eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(true);

    assertTrue(oilCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenField_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new OilCaseVolumetricParametersForm();

    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(true);
    when(fieldOilVolumetricParametersService.getOilVolumetricsForm(reservoirSubmissionDetail))
        .thenReturn(form);
    when(oilCaseVolumetricParametersFormValidator.isValid(
        eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(false);

    assertFalse(oilCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotApplicable_thenTrue() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(false);
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail))
        .thenReturn(false);
    assertTrue(oilCaseVolumetricParametersSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }
}