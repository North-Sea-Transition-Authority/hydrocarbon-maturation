package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverymixedcasevolumetrics;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class DiscoveryMixedCaseVolumetricsSubmissionServiceTest {

  @Mock
  private DiscoveryMixedCaseVolumetricsFormService discoveryMixedCaseVolumetricsFormService;

  @Mock
  private DiscoveryMixedCaseVolumetricsFormValidator discoveryMixedCaseVolumetricsFormValidator;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private DiscoveryMixedCaseVolumetricsSubmissionService discoveryMixedCaseVolumetricsSubmissionService;

  @Test
  void isSectionSubmittable_whenSubmittable_thenTrue() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new MixedCaseOutputsForm();

    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail))
        .thenReturn(true);
    when(discoveryMixedCaseVolumetricsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail)).thenReturn(form);
    when(discoveryMixedCaseVolumetricsFormValidator.isValid(
        any(MixedCaseOutputsForm.class), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
    ).thenReturn(true);

    assertTrue(discoveryMixedCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotSubmittable_thenFalse() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var form = new MixedCaseOutputsForm();
    var bindingResultWithError = ReverseRouter.emptyBindingResult();
    bindingResultWithError.addError(new FieldError("object", "field", "message"));

    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail))
        .thenReturn(true);
    when(discoveryMixedCaseVolumetricsFormService.getMixedCaseOutputsForm(reservoirSubmissionDetail)).thenReturn(form);
    when(discoveryMixedCaseVolumetricsFormValidator.isValid(
        any(MixedCaseOutputsForm.class), any(Errors.class), eq(submissionDetail), eq(ValidationType.FULL))
    ).thenReturn(false);

    assertFalse(discoveryMixedCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }

  @Test
  void isSectionSubmittable_whenNotApplicable_thenTrue() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail))
        .thenReturn(false);
    assertTrue(discoveryMixedCaseVolumetricsSubmissionService.isSectionSubmittable(reservoirSubmissionDetail));
  }
}