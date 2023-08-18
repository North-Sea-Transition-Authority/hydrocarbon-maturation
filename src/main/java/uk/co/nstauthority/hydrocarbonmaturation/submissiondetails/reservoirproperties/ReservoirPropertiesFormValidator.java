package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ReservoirSubmissionValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class ReservoirPropertiesFormValidator implements ReservoirSubmissionValidator {

  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public ReservoirPropertiesFormValidator(GeneralReservoirInformationService generalReservoirInformationService) {
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  @Override
  public boolean isValid(SubmissionForm form,
                         Errors errors,
                         ReservoirSubmissionDetail reservoirSubmissionDetail,
                         ValidationType validationType) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var hydrocarbonsDiscovered = generalReservoirInformationService
        .getReservoirHydrocarbons(reservoirSubmissionDetail).orElse(new ReservoirHydrocarbons())
        .getHydrocarbonsDiscovered();

    switch (validationType) {
      case FULL -> validateFull(form, errors, submissionDetail.getSubmissionType(), hydrocarbonsDiscovered);
      case PARTIAL -> validatePartial(form, errors, submissionDetail.getSubmissionType(), hydrocarbonsDiscovered);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }

    return !errors.hasErrors();
  }

  private void validatePartial(SubmissionForm form,
                              Errors errors,
                              SubmissionType submissionType,
                              HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    var reservoirForm = ((ReservoirPropertiesForm) form);

    var averageNetPayThicknessFormField = "averageNetPayThickness";
    if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
      ProbabilityDistributionValidationUtil.validatePartial(
          reservoirForm.getAverageNetPayThickness(),
          errors,
          averageNetPayThicknessFormField,
          "Average net reservoir thickness distribution used"
      );
    } else {
      ProbabilityDistributionValidationUtil.validatePartial(
          reservoirForm.getAverageNetPayThickness(),
          errors,
          averageNetPayThicknessFormField,
          "Average net pay thickness distribution used"
      );
    }

    ProbabilityDistributionValidationUtil.validatePartial(
        reservoirForm.getGrossReservoirThickness(),
        errors,
        "grossReservoirThickness",
        "Gross reservoir thickness distribution used"
    );

    ProbabilityDistributionValidationUtil.validatePartial(
        reservoirForm.getGrossRockVolume(),
        errors,
        "grossRockVolume",
        "Gross rock volume (GRV) distribution used"
    );

    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS
        || submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
      ProbabilityDistributionValidationUtil.validatePartialPercentage(
          reservoirForm.getPercentOfGrvOilBearing(),
          errors,
          "percentOfGrvOilBearing",
          "Percentage of GRV which is oil bearing distribution used"
      );
    }

    ProbabilityDistributionValidationUtil.validatePartialPercentage(
        reservoirForm.getNetToGross(),
        errors,
        "netToGross",
        "Net to gross distribution used"
    );

    ProbabilityDistributionValidationUtil.validatePartialPercentage(
        reservoirForm.getNetPorosity(),
        errors,
        "netPorosity",
        "Net porosity distribution used"
    );

    if (submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.PROSPECT_UPDATE
        || submissionType == SubmissionType.RELINQUISHMENT_PROSPECT) {
      ProbabilityDistributionValidationUtil.validatePartial(
          reservoirForm.getDepthToHydrocarbonContact(),
          errors,
          "depthToHydrocarbonContact",
          "Depth to hydrocarbon contact distribution used"
      );
    }

    if (submissionType == SubmissionType.CREATE_DISCOVERY
        || submissionType == SubmissionType.UPDATE_DISCOVERY
        || submissionType == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT) {
      validatePartiallyDepthToFields(errors, reservoirForm, hydrocarbonsDiscovered);
    }
  }

  private void validateFull(SubmissionForm form,
                           Errors errors,
                           SubmissionType submissionType,
                           HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    ReservoirPropertiesForm reservoirPropertiesForm = (ReservoirPropertiesForm) form;
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "formationName", "formationName.required", "Enter a formation name");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors,
        "ageStageOrSeries",
        "ageStageOrSeries.required",
        "Enter an age stage or series");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "groupName", "groupName.required", "Enter a group name");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lithology", "lithology.required", "Enter a lithology");
    if (reservoirPropertiesForm.getLithology() == ReservoirLithology.OTHER) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lithologyOther", "lithologyOther.required", "Enter an other lithology");
    }

    if (submissionType == SubmissionType.CREATE_DISCOVERY
        || submissionType == SubmissionType.UPDATE_DISCOVERY
        || submissionType == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT
        || submissionType == SubmissionType.RELINQUISHMENT_DISCOVERY) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "reservoirFacies",
          "reservoirFacies.required", "Enter the reservoir facies");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "reservoirConnectivity",
          "reservoirConnectivity.required", "Enter the reservoir connectivity");
    }

    var reservoirForm = ((ReservoirPropertiesForm) form);

    var averageNetPayThicknessFormField = "averageNetPayThickness";
    if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
      ValidationUtils.rejectIfEmpty(errors, "averageNetPayThickness.probabilityDistributionType",
          "averageNetPayThickness.probabilityDistributionType.required",
          "Select the average net reservoir thickness distribution used");

      ProbabilityDistributionValidationUtil.validateFull(
          reservoirForm.getAverageNetPayThickness(),
          errors,
          averageNetPayThicknessFormField,
          "Average net reservoir thickness distribution used"
      );
    } else {
      ValidationUtils.rejectIfEmpty(errors, "averageNetPayThickness.probabilityDistributionType",
          "averageNetPayThickness.probabilityDistributionType.required",
          "Select the average net reservoir thickness distribution used");

      ProbabilityDistributionValidationUtil.validateFull(
          reservoirForm.getAverageNetPayThickness(),
          errors,
          averageNetPayThicknessFormField,
          "Average net pay thickness distribution used"
      );
    }

    ValidationUtils.rejectIfEmpty(errors, "grossReservoirThickness.probabilityDistributionType",
        "grossReservoirThickness.probabilityDistributionType.required",
        "Select the gross reservoir thickness distribution used");

    ProbabilityDistributionValidationUtil.validateFull(
        reservoirForm.getGrossReservoirThickness(),
        errors,
        "grossReservoirThickness",
        "Gross reservoir thickness distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "grossRockVolume.probabilityDistributionType",
        "grossRockVolume.probabilityDistributionType.required",
        "Select the gross rock volume distribution used");

    ProbabilityDistributionValidationUtil.validateFull(
        reservoirForm.getGrossRockVolume(),
        errors,
        "grossRockVolume",
        "Gross rock volume (GRV) distribution used"
    );

    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS
        || submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
      ValidationUtils.rejectIfEmpty(errors, "percentOfGrvOilBearing.probabilityDistributionType",
          "percentOfGrvOilBearing.probabilityDistributionType.required",
          "Select the percentage of GRV which is oil bearing distribution used");

      ProbabilityDistributionValidationUtil.validateFullPercentage(
          reservoirForm.getPercentOfGrvOilBearing(),
          errors,
          "percentOfGrvOilBearing",
          "Percentage of GRV which is oil bearing distribution used"
      );
    }

    ValidationUtils.rejectIfEmpty(errors, "netToGross.probabilityDistributionType",
        "netToGross.probabilityDistributionType.required",
        "Select net-to-gross distribution used");

    ProbabilityDistributionValidationUtil.validateFullPercentage(
        reservoirForm.getNetToGross(),
        errors,
        "netToGross",
        "Net to gross distribution used"
    );

    ValidationUtils.rejectIfEmpty(errors, "netPorosity.probabilityDistributionType",
        "netPorosity.probabilityDistributionType.required",
        "Select the net porosity distribution used");

    ProbabilityDistributionValidationUtil.validateFullPercentage(
        reservoirForm.getNetPorosity(),
        errors,
        "netPorosity",
        "Net porosity distribution used"
    );

    if (submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.PROSPECT_UPDATE
        || submissionType == SubmissionType.RELINQUISHMENT_PROSPECT) {
      ValidationUtils.rejectIfEmpty(errors, "depthToHydrocarbonContact.probabilityDistributionType",
          "depthToHydrocarbonContact.probabilityDistributionType.required",
          "Select the depth to hydrocarbon contact distribution used");

      ProbabilityDistributionValidationUtil.validateFull(
          reservoirForm.getDepthToHydrocarbonContact(),
          errors,
          "depthToHydrocarbonContact",
          "Depth to hydrocarbon contact distribution used"
      );
    } else if (submissionType == SubmissionType.CREATE_DISCOVERY
        || submissionType == SubmissionType.UPDATE_DISCOVERY
        || submissionType == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT
        || submissionType == SubmissionType.RELINQUISHMENT_DISCOVERY) {
      validateFullyDepthToFields(errors, reservoirForm, hydrocarbonsDiscovered);
    }
  }

  private void validatePartiallyDepthToFields(Errors errors,
                                              ReservoirPropertiesForm reservoirForm,
                                              HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY
        || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getDepthToOilWaterContact(),
          errors,
          "depthToOilWaterContact",
          "Depth to oil water contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY
        || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getDepthToGasWaterContact(),
          errors,
          "depthToGasWaterContact",
          "Depth to gas water contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getDepthToGasOilContact(),
          errors,
          "depthToGasOilContact",
          "Depth to gas oil contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT
      );
    }
  }

  private void validateFullyDepthToFields(Errors errors,
                                          ReservoirPropertiesForm reservoirForm,
                                          HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY
        || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getDepthToOilWaterContact(),
          errors,
          "depthToOilWaterContact",
          "Depth to oil water contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY
        || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getDepthToGasWaterContact(),
          errors,
          "depthToGasWaterContact",
          "Depth to gas water contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getDepthToGasOilContact(),
          errors,
          "depthToGasOilContact",
          "Depth to gas oil contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT
      );
    }
  }
}
