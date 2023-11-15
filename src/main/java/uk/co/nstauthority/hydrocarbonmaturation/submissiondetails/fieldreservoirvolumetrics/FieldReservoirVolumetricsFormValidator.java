package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ReservoirSubmissionValidator;

@Service
public class FieldReservoirVolumetricsFormValidator implements ReservoirSubmissionValidator {
  private final GeneralReservoirInformationService generalReservoirInformationService;

  public FieldReservoirVolumetricsFormValidator(GeneralReservoirInformationService generalReservoirInformationService) {
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var fieldVolumetricsForm = (FieldReservoirVolumetricsForm) form;

    var reservoirHydrocarbons = generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)
        .orElse(new ReservoirHydrocarbons());
    if (generalReservoirInformationService.isOilOnly(reservoirHydrocarbons)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)) {
      var oilInPlace = fieldVolumetricsForm.getOilInPlace();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          oilInPlace,
          errors,
          "oilInPlace",
          "Oil in place",
          ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
      );
    }

    if (generalReservoirInformationService.isGasOnly(reservoirHydrocarbons)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)) {
      var gasInPlace = fieldVolumetricsForm.getGasInPlace();
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          gasInPlace,
          errors,
          "gasInPlace",
          "Gas in place",
          ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
      );
    }
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var fieldVolumetricsForm = (FieldReservoirVolumetricsForm) form;

    var reservoirHydrocarbons = generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)
        .orElse(new ReservoirHydrocarbons());

    if (generalReservoirInformationService.isOilOnly(reservoirHydrocarbons)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)) {
      var oilInPlace = fieldVolumetricsForm.getOilInPlace();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          oilInPlace,
          errors,
          "oilInPlace",
          "Oil in place",
          ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
      );
    }

    if (generalReservoirInformationService.isGasOnly(reservoirHydrocarbons)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)) {
      var gasInPlace = fieldVolumetricsForm.getGasInPlace();
      ProbabilityDistributionValidationUtil.validateCustomFull(
          gasInPlace,
          errors,
          "gasInPlace",
          "Gas in place",
          ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
      );
    }
  }
}
