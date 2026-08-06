package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.EnumUtils;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StandardLithology;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ReservoirSubmissionValidator;

@Service
class ReservoirPropertiesFormValidator implements ReservoirSubmissionValidator {

  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public ReservoirPropertiesFormValidator(GeneralReservoirInformationService generalReservoirInformationService) {
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  @Override
  public void validatePartial(SubmissionForm form,
                              Errors errors,
                              ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var hydrocarbonsDiscovered = generalReservoirInformationService
        .getReservoirHydrocarbons(reservoirSubmissionDetail).orElse(new ReservoirHydrocarbons())
        .getHydrocarbonsDiscovered();

    var submissionType = reservoirSubmissionDetail.getSubmissionDetail().getSubmissionType();

    var reservoirForm = ((ReservoirPropertiesForm) form);

    var averageNetPayThicknessFormField = "averageNetPayThickness";
    if (SubmissionDetailUtil.isFieldSubmission(submissionType)) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getAverageNetPayThickness(),
          errors,
          averageNetPayThicknessFormField,
          "Average net pay thickness",
          ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD
      );
    } else if (SubmissionDetailUtil.isDiscoverySubmission(submissionType)
        || SubmissionDetailUtil.isProspectSubmission(submissionType)) {
      ProbabilityDistributionValidationUtil.validatePartial(
          reservoirForm.getAverageNetPayThickness(),
          errors,
          averageNetPayThicknessFormField,
          "Average net pay thickness"
      );
    }

    if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
      ProbabilityDistributionValidationUtil.validatePartial(
          reservoirForm.getAverageNetReservoirThickness(),
          errors,
          "averageNetReservoirThickness",
          "Average net reservoir thickness"
      );
    }

    if (SubmissionDetailUtil.isFieldSubmission(submissionType)) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getGrossReservoirThickness(),
          errors,
          "grossReservoirThickness",
          "Gross reservoir thickness",
          ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_FIELD
      );
    } else {
      ProbabilityDistributionValidationUtil.validatePartial(
          reservoirForm.getGrossReservoirThickness(),
          errors,
          "grossReservoirThickness",
          "Gross reservoir thickness"
      );
    }

    if (SubmissionDetailUtil.isFieldSubmission(submissionType)) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getGrossRockVolume(),
          errors,
          "grossRockVolume",
          "Gross rock volume (GRV)",
          ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_FIELD
      );
    } else {
      ProbabilityDistributionValidationUtil.validatePartial(
          reservoirForm.getGrossRockVolume(),
          errors,
          "grossRockVolume",
          "Gross rock volume (GRV)"
      );
    }

    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS
        || submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
      if (SubmissionDetailUtil.isFieldSubmission(submissionType)) {
        ProbabilityDistributionValidationUtil.validateCustomPartialPercentage(
            reservoirForm.getPercentOfGrvOilBearing(),
            errors,
            "percentOfGrvOilBearing",
            "Percentage of GRV which is oil bearing",
            ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_FIELD
        );
      } else {
        ProbabilityDistributionValidationUtil.validatePartialPercentage(
            reservoirForm.getPercentOfGrvOilBearing(),
            errors,
            "percentOfGrvOilBearing",
            "Percentage of GRV which is oil bearing"
        );
      }
    }

    if (SubmissionDetailUtil.isFieldSubmission(submissionType)) {
      ProbabilityDistributionValidationUtil.validateCustomPartialPercentage(
          reservoirForm.getNetToGross(),
          errors,
          "netToGross",
          "Net to gross",
          ProbabilityDistributionFieldInstance.NET_TO_GROSS_FIELD
      );
    } else {
      ProbabilityDistributionValidationUtil.validatePartialPercentage(
          reservoirForm.getNetToGross(),
          errors,
          "netToGross",
          "Net to gross"
      );
    }

    if (SubmissionDetailUtil.isFieldSubmission(submissionType)) {
      ProbabilityDistributionValidationUtil.validateCustomPartialPercentage(
          reservoirForm.getNetPorosity(),
          errors,
          "netPorosity",
          "Net porosity",
          ProbabilityDistributionFieldInstance.NET_POROSITY_FIELD
      );
    } else {
      ProbabilityDistributionValidationUtil.validatePartialPercentage(
          reservoirForm.getNetPorosity(),
          errors,
          "netPorosity",
          "Net porosity"
      );
    }

    if (submissionType == SubmissionType.CREATE_PROSPECT
        || submissionType == SubmissionType.PROSPECT_UPDATE
        || submissionType == SubmissionType.RELINQUISHMENT_PROSPECT) {
      ProbabilityDistributionValidationUtil.validatePartial(
          reservoirForm.getDepthToHydrocarbonContact(),
          errors,
          "depthToHydrocarbonContact",
          "Depth to hydrocarbon contact"
      );
    }

    if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE
        || SubmissionDetailUtil.isDiscoverySubmission(submissionType)) {
      validatePartiallyDiscoveryDepthTo(errors, reservoirForm, hydrocarbonsDiscovered);
    } else if (SubmissionDetailUtil.isFieldSubmission(submissionType)) {
      if (submissionType == SubmissionType.CREATE_COP) {
        validatePartiallyFieldOriginalDepth(errors, reservoirForm, hydrocarbonsDiscovered);
        validatePartiallyFieldDepthToMoved(errors, reservoirForm, hydrocarbonsDiscovered);
      } else {
        validatePartiallyFieldDepthTo(errors, reservoirForm, hydrocarbonsDiscovered);
      }
    }
  }

  @Override
  public void validateFull(SubmissionForm form,
                            Errors errors,
                            ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionType = reservoirSubmissionDetail.getSubmissionDetail().getSubmissionType();

    ReservoirPropertiesForm reservoirPropertiesForm = (ReservoirPropertiesForm) form;
    ValidationUtils.rejectIfEmptyOrWhitespace(errors,
        "ageStageOrSeries",
        "ageStageOrSeries.required",
        "Enter the reservoir age");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "groupName", "groupName.required", "Enter the group name");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "formationName", "formationName.required",
            "Enter the formation name");
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lithology", "lithology.required", "Select a lithology");
    if (reservoirPropertiesForm.getLithology() == StandardLithology.OTHER) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lithologyOther", "lithologyOther.required",
          "Enter thr other lithology");
    }

    if (SubmissionDetailUtil.isDiscoverySubmission(submissionType) || SubmissionDetailUtil.isFieldSubmission(submissionType)) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "reservoirFacies",
          "reservoirFacies.required", "Enter the reservoir facies");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "reservoirConnectivity",
          "reservoirConnectivity.required", "Enter the reservoir connectivity");
    }

    var reservoirForm = ((ReservoirPropertiesForm) form);

    var averageNetPayThicknessFormField = "averageNetPayThickness";
    if (SubmissionDetailUtil.isFieldSubmission(submissionType)) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getAverageNetPayThickness(),
          errors,
          averageNetPayThicknessFormField,
          "Average net pay thickness",
          ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD
      );
    } else if (SubmissionDetailUtil.isDiscoverySubmission(submissionType)
        || SubmissionDetailUtil.isProspectSubmission(submissionType)) {
      ProbabilityDistributionValidationUtil.validateRequiredDistributionType(
          errors,
          averageNetPayThicknessFormField,
          "average net pay thickness"
      );
      ProbabilityDistributionValidationUtil.validateFull(
          reservoirForm.getAverageNetPayThickness(),
          errors,
          averageNetPayThicknessFormField,
          "Average net pay thickness"
      );
    }

    if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
      var averageNetReservoirThicknessFormField = "averageNetReservoirThickness";
      ProbabilityDistributionValidationUtil.validateRequiredDistributionType(
          errors,
          averageNetReservoirThicknessFormField,
          "average net reservoir thickness"
      );
      ProbabilityDistributionValidationUtil.validateFull(
          reservoirForm.getAverageNetReservoirThickness(),
          errors,
          averageNetReservoirThicknessFormField,
          "Average net reservoir thickness"
      );
    }

    if (SubmissionDetailUtil.isFieldSubmission(submissionType)) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getGrossReservoirThickness(),
          errors,
          "grossReservoirThickness",
          "Gross reservoir thickness",
          ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_FIELD
      );
    } else {
      ValidationUtils.rejectIfEmpty(errors, "grossReservoirThickness.probabilityDistributionType",
          "grossReservoirThickness.probabilityDistributionType.required",
          "Select the gross reservoir thickness distribution used");
      ProbabilityDistributionValidationUtil.validateFull(
          reservoirForm.getGrossReservoirThickness(),
          errors,
          "grossReservoirThickness",
          "Gross reservoir thickness"
      );
    }

    ValidationUtils.rejectIfEmpty(errors, "grossRockVolume.probabilityDistributionType",
        "grossRockVolume.probabilityDistributionType.required",
        "Select the gross rock volume distribution used");

    if (SubmissionDetailUtil.isFieldSubmission(submissionType)) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getGrossRockVolume(),
          errors,
          "grossRockVolume",
          "Gross rock volume (GRV)",
          ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_FIELD
      );
    } else {
      ProbabilityDistributionValidationUtil.validateFull(
          reservoirForm.getGrossRockVolume(),
          errors,
          "grossRockVolume",
          "Gross rock volume (GRV)"
      );
    }

    var hydrocarbonsDiscovered = generalReservoirInformationService
        .getReservoirHydrocarbons(reservoirSubmissionDetail).orElse(new ReservoirHydrocarbons())
        .getHydrocarbonsDiscovered();

    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS
        || submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
      ValidationUtils.rejectIfEmpty(errors, "percentOfGrvOilBearing.probabilityDistributionType",
          "percentOfGrvOilBearing.probabilityDistributionType.required",
          "Select the percentage of GRV which is oil bearing distribution used");

      if (SubmissionDetailUtil.isFieldSubmission(submissionType)) {
        ProbabilityDistributionValidationUtil.validateCustomFullPercentage(
            reservoirForm.getPercentOfGrvOilBearing(),
            errors,
            "percentOfGrvOilBearing",
            "Percentage of GRV which is oil bearing",
            ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_FIELD
        );
      } else {
        ProbabilityDistributionValidationUtil.validateFullPercentage(
            reservoirForm.getPercentOfGrvOilBearing(),
            errors,
            "percentOfGrvOilBearing",
            "Percentage of GRV which is oil bearing"
        );
      }
    }

    ValidationUtils.rejectIfEmpty(errors, "netToGross.probabilityDistributionType",
        "netToGross.probabilityDistributionType.required",
        "Select net-to-gross distribution used");

    if (SubmissionDetailUtil.isFieldSubmission(submissionType)) {
      ProbabilityDistributionValidationUtil.validateCustomFullPercentage(
          reservoirForm.getNetToGross(),
          errors,
          "netToGross",
          "Net to gross",
          ProbabilityDistributionFieldInstance.NET_TO_GROSS_FIELD
      );
    } else {
      ProbabilityDistributionValidationUtil.validateFullPercentage(
          reservoirForm.getNetToGross(),
          errors,
          "netToGross",
          "Net to gross"
      );
    }

    ValidationUtils.rejectIfEmpty(errors, "netPorosity.probabilityDistributionType",
        "netPorosity.probabilityDistributionType.required",
        "Select the net porosity distribution used");

    if (SubmissionDetailUtil.isFieldSubmission(submissionType)) {
      ProbabilityDistributionValidationUtil.validateCustomFullPercentage(
          reservoirForm.getNetPorosity(),
          errors,
          "netPorosity",
          "Net porosity",
          ProbabilityDistributionFieldInstance.NET_POROSITY_FIELD
      );
    } else {
      ProbabilityDistributionValidationUtil.validateFullPercentage(
          reservoirForm.getNetPorosity(),
          errors,
          "netPorosity",
          "Net porosity"
      );
    }

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
          "Depth to hydrocarbon contact"
      );
    }

    if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
      validateFullyDiscoveryDepthTo(errors, reservoirForm, hydrocarbonsDiscovered);
    } else if (SubmissionDetailUtil.isFieldSubmission(submissionType)) {
      if (submissionType == SubmissionType.CREATE_COP) {
        validateFullyFieldOriginalDepth(errors, reservoirForm, hydrocarbonsDiscovered);
        validateFullyFieldDepthToMoved(errors, reservoirForm, hydrocarbonsDiscovered);
      } else {
        validateFullyFieldDepthTo(errors, reservoirForm, hydrocarbonsDiscovered);
      }
    }

    if (SubmissionDetailUtil.isDiscoverySubmission(submissionType)) {
      validateFullyDiscoveryDepthTo(errors, reservoirForm, hydrocarbonsDiscovered);
    } else if (SubmissionDetailUtil.isFieldSubmission(submissionType)) {
      DecimalInputValidator.configureOptionalValidator(
              errors,
              "fracturePressure.decimalInputValue",
              "The fracture pressure",
              reservoirPropertiesForm.getFracturePressure())
          .setMinValue(BigDecimal.ZERO)
          .setMaxDecimalPlaces(0)
          .validate();


      var depositionalSettingName = reservoirPropertiesForm.getDepositionalSetting();
      if (depositionalSettingName == null) {
        errors.rejectValue(
            "depositionalSetting",
            "depositionalSetting.required",
            "Select a depositional setting");
      } else {
        var depositionalSetting = EnumUtils.getEnumValue(DepositionalSetting.class, depositionalSettingName);

        if (depositionalSetting.hasDepositionalEnvironments()) {
          var depositionalEnvironmentName = reservoirPropertiesForm.getDepositionalEnvironmentMap().get(depositionalSetting);
          if (depositionalEnvironmentName == null) {
            ValidationUtils.rejectIfEmpty(
                errors,
                "depositionalEnvironmentMap[" + depositionalSetting + "]",
                "depositionalEnvironment.required",
                "Select a depositional environment"
            );
          }
        }
      }

      ValidationUtils.rejectIfEmpty(
          errors,
          "isLateralBarriersBaffles",
          "isLateralBarriersBaffles.required",
          "Select if there any lateral barriers or baffles"
      );

      if (reservoirPropertiesForm.getIsLateralBarriersBaffles() != null
          && reservoirPropertiesForm.getIsLateralBarriersBaffles()) {
        var lateralBarriersBafflesDetail = reservoirPropertiesForm.getLateralBarriersBafflesDetail();
        if (lateralBarriersBafflesDetail.isEmpty()) {
          errors.rejectValue(
              "lateralBarriersBafflesDetail",
              "lateralBarriersBafflesDetail.required",
              "Select at least one lateral barrier or baffle"
          );
        }
      }

      ValidationUtils.rejectIfEmpty(
          errors,
          "isVerticalBarriersBaffles",
          "isVerticalBarriersBaffles.required",
          "Select if there any vertical barriers or baffles"
      );

      if (reservoirPropertiesForm.getIsVerticalBarriersBaffles() != null
          && reservoirPropertiesForm.getIsVerticalBarriersBaffles()) {
        var verticalBarriersBafflesDetail = reservoirPropertiesForm.getVerticalBarriersBafflesDetail();
        if (verticalBarriersBafflesDetail.isEmpty()) {
          errors.rejectValue(
              "verticalBarriersBafflesDetail",
              "verticalBarriersBafflesDetail.required",
              "Select at least one vertical barrier or baffle"
          );
        }
      }
    }
  }

  private void validatePartiallyFieldDepthTo(Errors errors,
                                             ReservoirPropertiesForm reservoirForm,
                                             HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY
        || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getDepthToOilWaterContact(),
          errors,
          "depthToOilWaterContact",
          "Depth to oil-water contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_FIELD
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getDepthToGasWaterContact(),
          errors,
          "depthToGasWaterContact",
          "Depth to gas-water contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_FIELD
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getDepthToGasOilContact(),
          errors,
          "depthToGasOilContact",
          "Depth to gas-oil contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_FIELD
      );
    }
  }

  private void validatePartiallyDiscoveryDepthTo(Errors errors,
                                                 ReservoirPropertiesForm reservoirForm,
                                                 HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY
        || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getDepthToOilWaterContact(),
          errors,
          "depthToOilWaterContact",
          "Depth to oil-water contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getDepthToGasWaterContact(),
          errors,
          "depthToGasWaterContact",
          "Depth to gas-water contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getDepthToGasOilContact(),
          errors,
          "depthToGasOilContact",
          "Depth to gas-oil contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY
      );
    }
  }

  private void validateFullyFieldDepthTo(Errors errors,
                                         ReservoirPropertiesForm reservoirForm,
                                         HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY
        || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getDepthToOilWaterContact(),
          errors,
          "depthToOilWaterContact",
          "Depth to oil-water contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_FIELD
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getDepthToGasWaterContact(),
          errors,
          "depthToGasWaterContact",
          "Depth to gas-water contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_FIELD
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getDepthToGasOilContact(),
          errors,
          "depthToGasOilContact",
          "Depth to gas-oil contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_FIELD
      );
    }
  }

  private void validateFullyDiscoveryDepthTo(Errors errors,
                                             ReservoirPropertiesForm reservoirForm,
                                             HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY
        || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateRequiredDistributionType(
          errors,
          "depthToOilWaterContact",
          "depth to oil-water contact"
      );
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getDepthToOilWaterContact(),
          errors,
          "depthToOilWaterContact",
          "Depth to oil-water contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY) {
      ProbabilityDistributionValidationUtil.validateRequiredDistributionType(
          errors,
          "depthToGasWaterContact",
          "depth to gas-water contact"
      );
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getDepthToGasWaterContact(),
          errors,
          "depthToGasWaterContact",
          "Depth to gas-water contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateRequiredDistributionType(
          errors,
          "depthToGasOilContact",
          "depth to gas-oil contact"
      );
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getDepthToGasOilContact(),
          errors,
          "depthToGasOilContact",
          "Depth to gas-oil contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY
      );
    }
  }

  private void validateFullyFieldOriginalDepth(Errors errors,
                                               ReservoirPropertiesForm reservoirForm,
                                               HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY
        || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getOriginalDepthToOilWaterContact(),
          errors,
          "originalDepthToOilWaterContact",
          "Original depth to oil-water contact",
          ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_OIL_WATER_CONTACT_FIELD
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getOriginalDepthToGasWaterContact(),
          errors,
          "originalDepthToGasWaterContact",
          "Original depth to gas-water contact",
          ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_WATER_CONTACT_FIELD
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getOriginalDepthToGasOilContact(),
          errors,
          "originalDepthToGasOilContact",
          "Original depth to gas-oil contact",
          ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_OIL_CONTACT_FIELD
      );
    }
  }

  private void validatePartiallyFieldOriginalDepth(Errors errors,
                                                   ReservoirPropertiesForm reservoirForm,
                                                   HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY
        || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getOriginalDepthToOilWaterContact(),
          errors,
          "originalDepthToOilWaterContact",
          "Original depth to oil-water contact",
          ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_OIL_WATER_CONTACT_FIELD
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getOriginalDepthToGasWaterContact(),
          errors,
          "originalDepthToGasWaterContact",
          "Original depth to gas-water contact",
          ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_WATER_CONTACT_FIELD
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getOriginalDepthToGasOilContact(),
          errors,
          "originalDepthToGasOilContact",
          "Original depth to gas-oil contact",
          ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_OIL_CONTACT_FIELD
      );
    }
  }

  private void validateFullyFieldDepthToMoved(Errors errors,
                                              ReservoirPropertiesForm reservoirForm,
                                              HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY
        || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getDepthToMovedOilWaterContact(),
          errors,
          "depthToMovedOilWaterContact",
          "Depth to moved oil-water contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_OIL_WATER_CONTACT_FIELD
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getDepthToMovedGasWaterContact(),
          errors,
          "depthToMovedGasWaterContact",
          "Depth to moved gas-water contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_WATER_CONTACT_FIELD
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomFull(
          reservoirForm.getDepthToMovedGasOilContact(),
          errors,
          "depthToMovedGasOilContact",
          "Depth to moved gas-oil contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_OIL_CONTACT_FIELD
      );
    }
  }

  private void validatePartiallyFieldDepthToMoved(Errors errors,
                                                  ReservoirPropertiesForm reservoirForm,
                                                  HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY
        || hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getDepthToMovedOilWaterContact(),
          errors,
          "depthToMovedOilWaterContact",
          "Depth to moved oil-water contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_OIL_WATER_CONTACT_FIELD
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getDepthToMovedGasWaterContact(),
          errors,
          "depthToMovedGasWaterContact",
          "Depth to moved gas-water contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_WATER_CONTACT_FIELD
      );
    }
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      ProbabilityDistributionValidationUtil.validateCustomPartial(
          reservoirForm.getDepthToMovedGasOilContact(),
          errors,
          "depthToMovedGasOilContact",
          "Depth to moved gas-oil contact",
          ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_OIL_CONTACT_FIELD
      );
    }
  }
}

