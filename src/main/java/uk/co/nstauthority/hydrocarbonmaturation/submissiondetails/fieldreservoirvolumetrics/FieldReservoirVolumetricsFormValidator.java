package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics.FieldReservoirVolumetricsFormService.INITIALLY_IN_PLACE_QUESTION_SUBMISSION_TYPES;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
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
          "Oil initially in place",
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
          "Gas initially in place",
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
          "Oil initially in place",
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
          "Gas initially in place",
          ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
      );
    }

    var submissionType = reservoirSubmissionDetail.getSubmissionDetail().getSubmissionType();
    if (INITIALLY_IN_PLACE_QUESTION_SUBMISSION_TYPES.contains(submissionType)) {
      ValidationUtils.rejectIfEmpty(
          errors,
          "initiallyInPlaceInformationCorrect",
          "initiallyInPlaceInformationCorrect.required",
          "Select if the reservoir initially in place information is correct"
      );
    }
  }
}
