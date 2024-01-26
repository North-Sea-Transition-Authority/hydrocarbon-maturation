package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import java.math.BigDecimal;
import java.util.stream.Stream;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class PlannedSanctionedWellsFormValidator implements SubmissionValidator {
  private Boolean isTotalNumberOfPlannedSanctionedWellsNumeric;
  private DecimalInput totalNumberOfPlannedSanctionedWells;

  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var plannedSanctionedWellsForm = (PlannedSanctionedWellsForm) form;

    validateTotalCapex(plannedSanctionedWellsForm, errors, ValidationType.PARTIAL);
    validateWellTypesAndSum(plannedSanctionedWellsForm, errors, ValidationType.PARTIAL);
    validateHorizontalWells(plannedSanctionedWellsForm, errors, ValidationType.PARTIAL);
    validateHighPressureTemperatureWells(plannedSanctionedWellsForm, errors, ValidationType.PARTIAL);
    validatePpfgWells(plannedSanctionedWellsForm, errors, ValidationType.PARTIAL);
    validateWellboreStability(plannedSanctionedWellsForm, errors, ValidationType.PARTIAL);
    validateFaultCrossings(plannedSanctionedWellsForm, errors, ValidationType.PARTIAL);
    validateExpectedLosses(plannedSanctionedWellsForm, errors, ValidationType.PARTIAL);
    validateExpectedDrillingChallenges(plannedSanctionedWellsForm, errors, ValidationType.PARTIAL);
    validateStimulationTypes(plannedSanctionedWellsForm, errors, ValidationType.PARTIAL);
    validateExpectedFormationDamage(plannedSanctionedWellsForm, errors, ValidationType.PARTIAL);
    validateSpecialisedDrillingEquipment(plannedSanctionedWellsForm, errors, ValidationType.PARTIAL);
    validateMultilateralWells(plannedSanctionedWellsForm, errors, ValidationType.PARTIAL);
    validateHoleCompletionTypes(plannedSanctionedWellsForm, errors, ValidationType.PARTIAL);
    validatePlannedCompletionTypes(plannedSanctionedWellsForm, errors, ValidationType.PARTIAL);
    validateArtificialLiftMechanisms(plannedSanctionedWellsForm, errors, ValidationType.PARTIAL);
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var plannedSanctionedWellsForm = (PlannedSanctionedWellsForm) form;

    validateTotalCapex(plannedSanctionedWellsForm, errors, ValidationType.FULL);
    validateWellTypesAndSum(plannedSanctionedWellsForm, errors, ValidationType.FULL);
    validateHorizontalWells(plannedSanctionedWellsForm, errors, ValidationType.FULL);
    validateHighPressureTemperatureWells(plannedSanctionedWellsForm, errors, ValidationType.FULL);
    validatePpfgWells(plannedSanctionedWellsForm, errors, ValidationType.FULL);
    validateWellboreStability(plannedSanctionedWellsForm, errors, ValidationType.FULL);
    validateFaultCrossings(plannedSanctionedWellsForm, errors, ValidationType.FULL);
    validateExpectedLosses(plannedSanctionedWellsForm, errors, ValidationType.FULL);
    validateExpectedDrillingChallenges(plannedSanctionedWellsForm, errors, ValidationType.FULL);
    validateStimulationTypes(plannedSanctionedWellsForm, errors, ValidationType.FULL);
    validateExpectedFormationDamage(plannedSanctionedWellsForm, errors, ValidationType.FULL);
    validateHeavyOil(plannedSanctionedWellsForm, errors, ValidationType.FULL);
    validateSpecialisedDrillingEquipment(plannedSanctionedWellsForm, errors, ValidationType.FULL);
    validateMultilateralWells(plannedSanctionedWellsForm, errors, ValidationType.FULL);
    validateHoleCompletionTypes(plannedSanctionedWellsForm, errors, ValidationType.FULL);
    validatePlannedCompletionTypes(plannedSanctionedWellsForm, errors, ValidationType.FULL);
    validateArtificialLiftMechanisms(plannedSanctionedWellsForm, errors, ValidationType.FULL);
  }

  private void validateTotalCapex(PlannedSanctionedWellsForm plannedSanctionedWellsForm,
                                  Errors errors,
                                  ValidationType validationType) {
    DecimalInputValidator validator;
    if (ValidationType.PARTIAL.equals(validationType)) {
      validator = DecimalInputValidator.configureOptionalValidator(
          errors,
          "totalCapex.decimalInputValue",
          "Total CAPEX for all planned sanctioned wells",
          plannedSanctionedWellsForm.getTotalCapex());
    } else if (ValidationType.FULL.equals(validationType)) {
      validator = DecimalInputValidator.configureValidator(
          errors,
          "totalCapex.decimalInputValue",
          "Total CAPEX for all planned sanctioned wells",
          plannedSanctionedWellsForm.getTotalCapex());
    } else {
      return;
    }
    validator
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO)
        .validate();
  }

  private void validateWellTypesAndSum(PlannedSanctionedWellsForm plannedSanctionedWellsForm,
                                       Errors errors,
                                       ValidationType validationType) {

    totalNumberOfPlannedSanctionedWells = plannedSanctionedWellsForm.getTotalNumberOfPlannedSanctionedWells();
    isTotalNumberOfPlannedSanctionedWellsNumeric = isPositiveWholeNumberInputValid(
        errors,
        "totalNumberOfPlannedSanctionedWells.decimalInputValue",
        "Total number of planned sanctioned wells",
        totalNumberOfPlannedSanctionedWells,
        validationType
    );
    if (plannedSanctionedWellsForm.getTotalNumberOfPlannedSanctionedWells().getDecimalInputValue() == null) {
      // for fields that require total planned sanctioned wells, if partial validation then it will be set true when null.
      isTotalNumberOfPlannedSanctionedWellsNumeric = false;
    }

    var numberOfInjectors = plannedSanctionedWellsForm.getNumberOfInjectors();
    var isNumberOfInjectorsValid = isPositiveWholeNumberInputValid(
        errors,
        "numberOfInjectors.decimalInputValue",
        "Number of injectors",
        numberOfInjectors,
        validationType
    );

    var numberOfProducers = plannedSanctionedWellsForm.getNumberOfProducers();
    var isNumberOfProducersValid = isPositiveWholeNumberInputValid(
        errors,
        "numberOfProducers.decimalInputValue",
        "Number of producers",
        numberOfProducers,
        validationType
    );

    var numberOfMonitors = plannedSanctionedWellsForm.getNumberOfMonitors();
    var isNumberOfMonitorsValid = isPositiveWholeNumberInputValid(
        errors,
        "numberOfMonitors.decimalInputValue",
        "Number of monitors",
        numberOfMonitors,
        validationType
    );

    var allValid = isTotalNumberOfPlannedSanctionedWellsNumeric
        && isNumberOfInjectorsValid
        && isNumberOfProducersValid
        && isNumberOfMonitorsValid;
    var allNotNull =
        ObjectUtils.allNotNull(totalNumberOfPlannedSanctionedWells, numberOfInjectors, numberOfProducers, numberOfMonitors);

    if (allValid && allNotNull) {
      var sum = (Integer) Stream.of(numberOfInjectors, numberOfProducers, numberOfMonitors)
          .map(DecimalInput::createIntegerOrNull).mapToInt(Integer::intValue).sum();
      if (!sum.equals(totalNumberOfPlannedSanctionedWells.createIntegerOrNull())) {
        errors.rejectValue(
            "totalNumberOfPlannedSanctionedWells.decimalInputValue",
            "totalNumberOfPlannedSanctionedWells.decimalInputValue.invalidTotals",
            "The combined number of injectors, producers and monitors must be equal to " +
                "the total number of planned sanctioned wells");

        errors.rejectValue(
            "numberOfInjectors.decimalInputValue",
            "numberOfInjectors.decimalInputValue.invalidTotals",
            "");
        errors.rejectValue("numberOfProducers.decimalInputValue",
            "numberOfProducers.decimalInputValue.invalidTotals",
            "");
        errors.rejectValue("numberOfMonitors.decimalInputValue",
            "numberOfMonitors.decimalInputValue.invalidTotals",
            "");
      }
    }
  }

  private void validateHorizontalWells(PlannedSanctionedWellsForm plannedSanctionedWellsForm,
                                       Errors errors,
                                       ValidationType validationType) {
    if (ValidationType.FULL.equals(validationType) && plannedSanctionedWellsForm.getAnyHorizontalWells() == null) {
      ValidationUtils.rejectIfEmpty(
          errors,
          "anyHorizontalWells",
          "anyHorizontalWells.required",
          "Select if there are any horizontal wells"
      );
    }

    if (BooleanUtils.isTrue(plannedSanctionedWellsForm.getAnyHorizontalWells())) {
      var isHorizontalWellsValid = isPositiveWholeNumberAndLessThanPlannedSanctionedWells(
          errors,
          "numberOfHorizontalWells.decimalInputValue",
          "Number of horizontal wells",
          plannedSanctionedWellsForm.getNumberOfHorizontalWells(),
          validationType
      );

      // check less than number of horizontal wells
      if (BooleanUtils.isTrue(isHorizontalWellsValid)) {
        isPositiveWholeNumberAndLessThanMaxValue(
            errors,
            "numberOfHorizontalWellsWithErd.decimalInputValue",
            "Number of horizontal wells with extended reach drilling",
            plannedSanctionedWellsForm.getNumberOfHorizontalWellsWithErd(),
            plannedSanctionedWellsForm.getNumberOfHorizontalWells(),
            validationType
        );
      } else if (plannedSanctionedWellsForm.getNumberOfHorizontalWellsWithErd().getDecimalInputValue() != null) {
        // if no valid horizontal wells just check it is valid and less than max possible (planned sanctioned wells)
        isPositiveWholeNumberAndLessThanPlannedSanctionedWells(
            errors,
            "numberOfHorizontalWellsWithErd.decimalInputValue",
            "Number of horizontal wells with extended reach drilling",
            plannedSanctionedWellsForm.getNumberOfHorizontalWellsWithErd(),
            validationType
        );
      }
    }
  }

  private void validateHighPressureTemperatureWells(PlannedSanctionedWellsForm plannedSanctionedWellsForm,
                                                    Errors errors,
                                                    ValidationType validationType) {
    if (ValidationType.FULL.equals(validationType)
        && plannedSanctionedWellsForm.getAnyHighPressureTemperatureWells() == null) {
      ValidationUtils.rejectIfEmpty(
          errors,
          "anyHighPressureTemperatureWells",
          "anyHighPressureTemperatureWells.required",
          "Select if there are any high pressure or high temperature wells"
      );
    }

    if (BooleanUtils.isTrue(plannedSanctionedWellsForm.getAnyHighPressureTemperatureWells())) {
      isPositiveWholeNumberAndLessThanPlannedSanctionedWells(
          errors,
          "numberOfHighPressureTemperatureWells.decimalInputValue",
          "Number of high pressure or high temperature wells",
          plannedSanctionedWellsForm.getNumberOfHighPressureTemperatureWells(),
          validationType
      );
    }
  }

  private void validatePpfgWells(PlannedSanctionedWellsForm plannedSanctionedWellsForm,
                                 Errors errors,
                                 ValidationType validationType) {
    if (ValidationType.FULL.equals(validationType)
        && plannedSanctionedWellsForm.getAnyWellsWithinNarrowOrTightPpfgWindow() == null) {
      ValidationUtils.rejectIfEmpty(
          errors,
          "anyWellsWithinNarrowOrTightPpfgWindow",
          "anyWellsWithinNarrowOrTightPpfgWindow.required",
          "Select if there are any wells within a narrow or tight pore pressure fracture gradient (PPFG) window"
      );
    }

    if (BooleanUtils.isTrue(plannedSanctionedWellsForm.getAnyWellsWithinNarrowOrTightPpfgWindow())) {
      isPositiveWholeNumberAndLessThanPlannedSanctionedWells(
          errors,
          "numberOfWellsWithinNarrowOrTightPpfgWindow.decimalInputValue",
          "Number of wells within a narrow or tight pore pressure fracture gradient (PPFG) window",
          plannedSanctionedWellsForm.getNumberOfWellsWithinNarrowOrTightPpfgWindow(),
          validationType
      );

      if (ValidationType.FULL.equals(validationType)
          && plannedSanctionedWellsForm.getIsManagedPressureDrillingUtilized() == null) {
        errors.rejectValue(
            "isManagedPressureDrillingUtilized",
            "isManagedPressureDrillingUtilized.required",
            "Select if managed pressure drilling (MPD) is utilised"
        );
      }
    }
  }

  private void validateWellboreStability(PlannedSanctionedWellsForm plannedSanctionedWellsForm,
                                         Errors errors,
                                         ValidationType validationType) {
    if (ValidationType.FULL.equals(validationType)
        && plannedSanctionedWellsForm.getAnyWellboreStabilityChallenges() == null) {
      ValidationUtils.rejectIfEmpty(
          errors,
          "anyWellboreStabilityChallenges",
          "anyWellboreStabilityChallenges.required",
          "Select if there are any stability challenges with any of the wells"
      );
    }

    if (BooleanUtils.isTrue(plannedSanctionedWellsForm.getAnyWellboreStabilityChallenges())) {
      isPositiveWholeNumberAndLessThanPlannedSanctionedWells(
          errors,
          "numberOfWellboreStabilityChallenges.decimalInputValue",
          "Number of wells with wellbore stability challenges",
          plannedSanctionedWellsForm.getNumberOfWellboreStabilityChallenges(),
          validationType
      );

      if (ValidationType.FULL.equals(validationType)) {
        ValidationUtils.rejectIfEmptyOrWhitespace(
            errors,
            "stabilityChallengesInformation",
            "stabilityChallengesInformation.required",
            "Provide information on the stability challenges"
        );
      }
    }
  }

  private void validateFaultCrossings(PlannedSanctionedWellsForm plannedSanctionedWellsForm,
                                      Errors errors,
                                      ValidationType validationType) {
    if (ValidationType.FULL.equals(validationType)
        && plannedSanctionedWellsForm.getAnyWellsExpectedToCrossFaults() == null) {
      ValidationUtils.rejectIfEmpty(
          errors,
          "anyWellsExpectedToCrossFaults",
          "anyWellsExpectedToCrossFaults.required",
          "Select if any of the wells are expected to cross a fault"
      );
    }

    if (BooleanUtils.isTrue(plannedSanctionedWellsForm.getAnyWellsExpectedToCrossFaults())) {
      isPositiveWholeNumberAndLessThanPlannedSanctionedWells(
          errors,
          "numberOfWellsExpectedToCrossFaults.decimalInputValue",
          "Number of wells expected to cross a fault",
          plannedSanctionedWellsForm.getNumberOfWellsExpectedToCrossFaults(),
          validationType
      );

      if (ValidationType.FULL.equals(validationType)
          && (plannedSanctionedWellsForm.getExpectedFaults() == null
          || plannedSanctionedWellsForm.getExpectedFaults().isEmpty())) {
        errors.rejectValue(
            "expectedFaults",
            "expectedFaults.required",
            "Select where faults are expected"
        );
      }
    }
  }

  private void validateExpectedLosses(PlannedSanctionedWellsForm plannedSanctionedWellsForm,
                                      Errors errors,
                                      ValidationType validationType) {
    if (ValidationType.FULL.equals(validationType)
        && plannedSanctionedWellsForm.getAnyExpectedLossesFromDrilling() == null) {
      ValidationUtils.rejectIfEmpty(
          errors,
          "anyExpectedLossesFromDrilling",
          "anyExpectedLossesFromDrilling.required",
          "Select if you expect to incur losses in drilling these wells"
      );
    }

    if (BooleanUtils.isTrue(plannedSanctionedWellsForm.getAnyExpectedLossesFromDrilling())
        && ValidationType.FULL.equals(validationType)) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "whyLossesExpected",
          "whyLossesExpected.required",
          "Provide information on why losses are expected"
      );
    }
  }

  private void validateExpectedDrillingChallenges(PlannedSanctionedWellsForm plannedSanctionedWellsForm,
                                                  Errors errors,
                                                  ValidationType validationType) {
    if (ValidationType.FULL.equals(validationType)
        && plannedSanctionedWellsForm.getAnyExpectedDrillingChallenges() == null) {
      ValidationUtils.rejectIfEmpty(
          errors,
          "anyExpectedDrillingChallenges",
          "anyExpectedDrillingChallenges.required",
          "Select if you expect to encounter any drilling challenges"
      );
    }

    if (BooleanUtils.isTrue(plannedSanctionedWellsForm.getAnyExpectedDrillingChallenges())
        && ValidationType.FULL.equals(validationType)) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "drillingChallengesMitigations",
          "drillingChallengesMitigations.required",
          "Provide the mitigations you have in place"
      );
    }
  }

  private void validateStimulationTypes(PlannedSanctionedWellsForm plannedSanctionedWellsForm,
                                        Errors errors,
                                        ValidationType validationType) {
    if (ValidationType.FULL.equals(validationType)) {
      var stimulationTypeRequired = plannedSanctionedWellsForm.getStimulationTypeRequired();
      if (stimulationTypeRequired == null) {
        errors.rejectValue(
            "stimulationTypeRequired",
            "stimulationTypeRequired.required",
            "Select the stimulation type required."
        );
      } else if (stimulationTypeRequired != StimulationType.NO_WELL_STIMULATION) {
        var numberOfWellsRequiringStimulation =
            plannedSanctionedWellsForm.getStimulationTypeDecimalInputHashMap().get(stimulationTypeRequired);

        isPositiveWholeNumberAndLessThanPlannedSanctionedWells(
            errors,
            "stimulationTypeDecimalInputHashMap[%s].decimalInputValue".formatted(stimulationTypeRequired),
            "Number of wells requiring stimulation type",
            numberOfWellsRequiringStimulation,
            validationType
        );
      }
    }
  }

  private void validateExpectedFormationDamage(PlannedSanctionedWellsForm plannedSanctionedWellsForm,
                                               Errors errors,
                                               ValidationType validationType) {
    if (ValidationType.FULL.equals(validationType)
        && plannedSanctionedWellsForm.getIsExpectingFormationDamage() == null) {
      ValidationUtils.rejectIfEmpty(
          errors,
          "isExpectingFormationDamage",
          "isExpectingFormationDamage.required",
          "Select if you envisage any formation damage"
      );
    }

    if (BooleanUtils.isTrue(plannedSanctionedWellsForm.getIsExpectingFormationDamage())
        && ValidationType.FULL.equals(validationType)) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "expectedFormationDamage",
          "expectedFormationDamage.required",
          "Provide the envisaged formation damage"
      );
    }
  }

  private void validateHeavyOil(PlannedSanctionedWellsForm plannedSanctionedWellsForm,
                                Errors errors,
                                ValidationType validationType) {
    if (ValidationType.FULL.equals(validationType)
        && plannedSanctionedWellsForm.getAnyHeavyOil() == null) {
      ValidationUtils.rejectIfEmpty(
          errors,
          "anyHeavyOil",
          "anyHeavyOil.required",
          "Select if there is any heavy oil with an API below 20 degrees expected"
      );
    }
  }

  private void validateSpecialisedDrillingEquipment(PlannedSanctionedWellsForm plannedSanctionedWellsForm,
                                                     Errors errors,
                                                     ValidationType validationType) {
    if (ValidationType.FULL.equals(validationType)
        && plannedSanctionedWellsForm.getAnySpecialisedDrillingEquipmentRequired() == null) {
      ValidationUtils.rejectIfEmpty(
          errors,
          "anySpecialisedDrillingEquipmentRequired",
          "anySpecialisedDrillingEquipmentRequired.required",
          "Select if any specialised drilling equipment or service is required"
      );
    }

    if (BooleanUtils.isTrue(plannedSanctionedWellsForm.getAnySpecialisedDrillingEquipmentRequired())
        && ValidationType.FULL.equals(validationType)) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "specialisedDrillingEquipmentRequired",
          "specialisedDrillingEquipmentRequired.required",
          "Provide the specialised drilling equipment required"
      );
    }
  }

  private void validateMultilateralWells(PlannedSanctionedWellsForm plannedSanctionedWellsForm,
                                         Errors errors,
                                         ValidationType validationType) {
    if (ValidationType.FULL.equals(validationType)
        && plannedSanctionedWellsForm.getAnyMultilateralWells() == null) {
      ValidationUtils.rejectIfEmpty(
          errors,
          "anyMultilateralWells",
          "anyMultilateralWells.required",
          "Select if there are any multilateral wells"
      );
    }

    if (BooleanUtils.isTrue(plannedSanctionedWellsForm.getAnyMultilateralWells())) {
      isPositiveWholeNumberAndLessThanPlannedSanctionedWells(
          errors,
          "numberOfMultilateralWells.decimalInputValue",
          "Number of multilateral wells",
          plannedSanctionedWellsForm.getNumberOfMultilateralWells(),
          validationType
      );
    }
  }

  private void validateHoleCompletionTypes(PlannedSanctionedWellsForm plannedSanctionedWellsForm,
                                           Errors errors,
                                           ValidationType validationType) {
    var firstHoleCompletionTypeId = "selectedHoleCompletionTypes[%s]"
        .formatted(DisplayableEnumOptionUtil.getFirstDisplayable(HoleCompletionType.class));
    // check at least one selection has been made
    var isNoSelectionMade = plannedSanctionedWellsForm.getSelectedHoleCompletionTypes()
        .values().stream().allMatch(BooleanUtils::isFalse);
    if (ValidationType.FULL.equals(validationType)
        && BooleanUtils.isTrue(isNoSelectionMade)) {
      errors.rejectValue(
          firstHoleCompletionTypeId,
          "%s.required".formatted(firstHoleCompletionTypeId),
          "Select the hole completion types"
      );
    } else if (BooleanUtils.isFalse(isNoSelectionMade)) {
      var sumOfTypes = 0;
      var areAllValidNumeric = true;
      for (var entry : plannedSanctionedWellsForm.getSelectedHoleCompletionTypes().entrySet()) {
        var holeCompletionType = entry.getKey();
        var numberOfWells = plannedSanctionedWellsForm.getNumberOfHoleCompletionTypes().get(holeCompletionType);
        var isValidNumeric = isPositiveWholeNumberAndLessThanPlannedSanctionedWells(
            errors,
            "numberOfHoleCompletionTypes[%s].decimalInputValue".formatted(holeCompletionType),
            "Number of wells with hole completion type '%s'".formatted(holeCompletionType.getDisplayName()),
            numberOfWells,
            validationType
        );

        if (BooleanUtils.isTrue(isValidNumeric)) {
          sumOfTypes += numberOfWells.createIntegerOrNull();
        } else {
          areAllValidNumeric = false;
        }
      }

      if (BooleanUtils.isTrue(areAllValidNumeric) && BooleanUtils.isTrue(isTotalNumberOfPlannedSanctionedWellsNumeric)
          && sumOfTypes != totalNumberOfPlannedSanctionedWells.createIntegerOrNull()) {
        errors.rejectValue(
            firstHoleCompletionTypeId,
            "%s.invalidTotals".formatted(firstHoleCompletionTypeId),
            "The combined number of wells across all hole completion types must be equal to the total number " +
                "of planned sanctioned wells (%d)".formatted(totalNumberOfPlannedSanctionedWells.createIntegerOrNull())
        );
      }
    }
  }

  private void validatePlannedCompletionTypes(PlannedSanctionedWellsForm plannedSanctionedWellsForm,
                                              Errors errors,
                                              ValidationType validationType) {
    var firstPlannedCompletionTypeId = "selectedPlannedCompletionTypes[%s]"
        .formatted(DisplayableEnumOptionUtil.getFirstDisplayable(PlannedCompletionType.class));

    // check at least one selection has been made
    var isNoSelectionMade = plannedSanctionedWellsForm.getSelectedPlannedCompletionTypes()
        .values().stream().allMatch(BooleanUtils::isFalse);
    if (ValidationType.FULL.equals(validationType)
        && BooleanUtils.isTrue(isNoSelectionMade)) {
      errors.rejectValue(
          firstPlannedCompletionTypeId,
          "%s.required".formatted(firstPlannedCompletionTypeId),
          "Select the planned completion types"
      );
    } else if (BooleanUtils.isFalse(isNoSelectionMade)) {
      var sumOfTypes = 0;
      var areAllValidNumeric = true;
      for (var entry : plannedSanctionedWellsForm.getSelectedPlannedCompletionTypes().entrySet()) {
        var plannedCompletionType = entry.getKey();
        if (PlannedCompletionType.OTHER == plannedCompletionType && ValidationType.FULL.equals(validationType)) {
          ValidationUtils.rejectIfEmptyOrWhitespace(
              errors,
              "otherPlannedCompletionType",
              "otherPlannedCompletionType.required",
              "Provide the other planned completion type"
          );
        }

        var numberOfWells = plannedSanctionedWellsForm.getNumberOfPlannedCompletionTypes().get(plannedCompletionType);
        var isValidNumeric = isPositiveWholeNumberAndLessThanPlannedSanctionedWells(
            errors,
            "numberOfPlannedCompletionTypes[%s].decimalInputValue".formatted(plannedCompletionType),
            "Number of wells with planned completion type '%s'".formatted(plannedCompletionType.getDisplayName()),
            numberOfWells,
            validationType
        );

        if (BooleanUtils.isTrue(isValidNumeric)) {
          sumOfTypes += numberOfWells.createIntegerOrNull();
        } else {
          areAllValidNumeric = false;
        }
      }

      if (BooleanUtils.isTrue(areAllValidNumeric) && BooleanUtils.isTrue(isTotalNumberOfPlannedSanctionedWellsNumeric)
          && sumOfTypes != totalNumberOfPlannedSanctionedWells.createIntegerOrNull()) {
        errors.rejectValue(
            firstPlannedCompletionTypeId,
            "%s.invalidTotals".formatted(firstPlannedCompletionTypeId),
            "The combined number of wells across all planned completion types must be equal to the total number " +
                "of planned sanctioned wells (%d)".formatted(totalNumberOfPlannedSanctionedWells.createIntegerOrNull())
        );
      }
    }
  }

  private void validateArtificialLiftMechanisms(PlannedSanctionedWellsForm plannedSanctionedWellsForm,
                                              Errors errors,
                                              ValidationType validationType) {
    var firstArtificialLiftMechanismId = "selectedArtificialLiftMechanisms[%s]"
        .formatted(DisplayableEnumOptionUtil.getFirstDisplayable(ArtificialLiftMechanism.class));

    // check at least one selection has been made
    var isNoSelectionMade = plannedSanctionedWellsForm.getSelectedArtificialLiftMechanisms()
        .values().stream().allMatch(BooleanUtils::isFalse);
    if (ValidationType.FULL.equals(validationType)
        && BooleanUtils.isTrue(isNoSelectionMade)) {
      errors.rejectValue(
          firstArtificialLiftMechanismId,
          "%s.required"
              .formatted(firstArtificialLiftMechanismId),
          "Select the artificial lift mechanisms"
      );
    } else if (BooleanUtils.isFalse(isNoSelectionMade)) {
      var sumOfTypes = 0;
      var areAllValidNumeric = true;
      for (var entry : plannedSanctionedWellsForm.getSelectedArtificialLiftMechanisms().entrySet()) {
        var artificialLiftMechanism = entry.getKey();
        if (ArtificialLiftMechanism.OTHER.equals(artificialLiftMechanism) && ValidationType.FULL.equals(validationType)) {
          ValidationUtils.rejectIfEmptyOrWhitespace(
              errors,
              "otherArtificialLiftMechanism",
              "otherArtificialLiftMechanism.required",
              "Provide the other artificial lift mechanism"
          );
        }

        // selected none and other options
        if (ArtificialLiftMechanism.NONE.equals(artificialLiftMechanism)
            && plannedSanctionedWellsForm.getSelectedArtificialLiftMechanisms().size() > 1) {
          errors.rejectValue(
              firstArtificialLiftMechanismId,
              "%s.invalidSelection".formatted(firstArtificialLiftMechanismId),
              "If no artificial lift mechanisms were used, do not select any other options"
          );
        }

        // check number of wells for options that have it
        if (!ArtificialLiftMechanism.NONE.equals(artificialLiftMechanism)) {
          var numberOfWells = plannedSanctionedWellsForm.getNumberOfArtificialLiftMechanisms().get(artificialLiftMechanism);
          var isValidNumeric = isPositiveWholeNumberAndLessThanPlannedSanctionedWells(
              errors,
              "numberOfArtificialLiftMechanisms[%s].decimalInputValue".formatted(artificialLiftMechanism),
              "Number of wells with artificial mechanism '%s'".formatted(artificialLiftMechanism.getDisplayName()),
              numberOfWells,
              validationType
          );

          if (BooleanUtils.isTrue(isValidNumeric)) {
            sumOfTypes += numberOfWells.createIntegerOrNull();
          } else {
            areAllValidNumeric = false;
          }
        } else {
          // avoid total check if no lift mechanisms selected
          areAllValidNumeric = false;
        }
      }

      if (BooleanUtils.isTrue(areAllValidNumeric)
          && BooleanUtils.isTrue(isTotalNumberOfPlannedSanctionedWellsNumeric)
          && sumOfTypes != totalNumberOfPlannedSanctionedWells.createIntegerOrNull()) {
        errors.rejectValue(
            firstArtificialLiftMechanismId,
            "%s.invalidTotals".formatted(firstArtificialLiftMechanismId),
            "The combined number of wells across all artificial lift mechanisms must be equal to the total number " +
                "of planned sanctioned wells (%d)".formatted(totalNumberOfPlannedSanctionedWells.createIntegerOrNull())
        );
      }
    }
  }

  /*
   * Check if input is greater than or equal to 0 and a valid whole number
   */
  private Boolean isPositiveWholeNumberInputValid(Errors errors,
                                                  String fieldId,
                                                  String errorPrefix,
                                                  DecimalInput decimalInput,
                                                  ValidationType validationType) {
    var numberOfErrors = errors.getAllErrors().size();

    if (ValidationType.PARTIAL.equals(validationType)) {
      DecimalInputValidator.configureOptionalValidator(
              errors,
              fieldId,
              errorPrefix,
              decimalInput)
          .setMaxDecimalPlaces(0)
          .setMinValue(BigDecimal.ZERO)
          .validate();
    } else if (ValidationType.FULL.equals(validationType)) {
      DecimalInputValidator.configureValidator(
              errors,
              fieldId,
              errorPrefix,
              decimalInput)
          .setMaxDecimalPlaces(0)
          .setMinValue(BigDecimal.ZERO)
          .validate();
    }
    return (errors.getAllErrors().size() == numberOfErrors);
  }

  /*
   * Check if input is greater than or equal to 0, and is a valid whole number. If maxValue is valid then check constraint
   */
  private Boolean isPositiveWholeNumberAndLessThanPlannedSanctionedWells(Errors errors,
                                                                         String fieldId,
                                                                         String errorPrefix,
                                                                         DecimalInput validatedValue,
                                                                         ValidationType validationType) {
    return isPositiveWholeNumberAndLessThanMaxValue(
        errors,
        fieldId,
        errorPrefix,
        validatedValue,
        totalNumberOfPlannedSanctionedWells,
        validationType
    );
  }


  /*
   * Check if input is greater than or equal to 0, and is a valid whole number. If maxValue is valid then check constraint
   */
  private Boolean isPositiveWholeNumberAndLessThanMaxValue(Errors errors,
                                                           String fieldId,
                                                           String errorPrefix,
                                                           DecimalInput validatedValue,
                                                           DecimalInput maxValue,
                                                           ValidationType validationType) {
    var numberOfErrors = errors.getAllErrors().size();

    DecimalInputValidator inputValidator;
    if (ValidationType.PARTIAL.equals(validationType)) {
      inputValidator = DecimalInputValidator.configureOptionalValidator(
          errors,
          fieldId,
          errorPrefix,
          validatedValue);
    } else if (ValidationType.FULL.equals(validationType)) {
      inputValidator = DecimalInputValidator.configureValidator(
          errors,
          fieldId,
          errorPrefix,
          validatedValue);
    } else {
      return false;
    }

    inputValidator
        .setMaxDecimalPlaces(0)
        .setMinValue(BigDecimal.ONE)
        .setMaxValue(maxValue.createBigDecimalOrNull());
    inputValidator.validate();

    return errors.getAllErrors().size() == numberOfErrors;
  }
}
