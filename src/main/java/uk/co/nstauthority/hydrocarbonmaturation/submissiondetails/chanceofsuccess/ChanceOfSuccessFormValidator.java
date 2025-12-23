package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PercentageValidationUtils;

@Service
class ChanceOfSuccessFormValidator extends PartialFullFormValidator {

  private DecimalInputValidator playSourceValidator;
  private DecimalInputValidator playReservoirValidator;
  private DecimalInputValidator playTopSealValidator;

  private DecimalInputValidator prospectSpecificTrapPresenceValidator;
  private DecimalInputValidator prospectSpecificTrapEffectivenessValidator;
  private DecimalInputValidator prospectSpecificSourcePresenceValidator;
  private DecimalInputValidator prospectSpecificSourceEffectivenessValidator;
  private DecimalInputValidator prospectSpecificReservoirPresenceValidator;
  private DecimalInputValidator prospectSpecificReservoirEffectivenessValidator;

  private static final String GEOPHYSICALLY_MODIFIED_PERCENTAGE_ERROR_MESSAGE_PREFIX
      = "The geophysically-modified chance of success";

  private static final String WELL_MODIFIED_PERCENTAGE_ERROR_MESSAGE_PREFIX =
      "The well-modified chance of success";

  @Override
  public boolean supports(Class<?> clazz) {
    return ChanceOfSuccessForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    setupDecimalInputValidators((ChanceOfSuccessForm) target, errors);
    super.validate(target, errors, validationHints);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    playSourceValidator.isOptional().validate();
    playReservoirValidator.isOptional().validate();
    playTopSealValidator.isOptional().validate();

    prospectSpecificTrapPresenceValidator.isOptional().validate();
    prospectSpecificTrapEffectivenessValidator.isOptional().validate();
    prospectSpecificSourcePresenceValidator.isOptional().validate();
    prospectSpecificSourceEffectivenessValidator.isOptional().validate();
    prospectSpecificReservoirPresenceValidator.isOptional().validate();
    prospectSpecificReservoirEffectivenessValidator.isOptional().validate();

    var chanceOfSuccessForm = (ChanceOfSuccessForm) form;

    if (chanceOfSuccessForm.getGeophysicallyModified() != null && chanceOfSuccessForm.getGeophysicallyModified()) {
      PercentageValidationUtils.configurePercentageValidator(chanceOfSuccessForm.getGeophysicallyModifiedPercentage(),
          "geophysicallyModifiedPercentage.decimalInputValue",
              GEOPHYSICALLY_MODIFIED_PERCENTAGE_ERROR_MESSAGE_PREFIX,
              errors
          )
          .isOptional()
          .validate();
    }

    if (chanceOfSuccessForm.getRelatedToSpecificWellLocation() != null
        && chanceOfSuccessForm.getRelatedToSpecificWellLocation()) {

      PercentageValidationUtils.configurePercentageValidator(chanceOfSuccessForm.getWellModifiedPercentage(),
          "wellModifiedPercentage.decimalInputValue",
          WELL_MODIFIED_PERCENTAGE_ERROR_MESSAGE_PREFIX,
              errors
          )
          .isOptional()
          .validate();
    }
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    playSourceValidator.validate();
    playReservoirValidator.validate();
    playTopSealValidator.validate();

    prospectSpecificTrapPresenceValidator.validate();
    prospectSpecificTrapEffectivenessValidator.validate();
    prospectSpecificSourcePresenceValidator.validate();
    prospectSpecificSourceEffectivenessValidator.validate();
    prospectSpecificReservoirPresenceValidator.validate();
    prospectSpecificReservoirEffectivenessValidator.validate();

    var chanceOfSuccessForm = (ChanceOfSuccessForm) form;

    ValidationUtils.rejectIfEmpty(errors, "geophysicallyModified", "geophysicallyModified.required",
        "Select yes if there is a geophysically-modified chance of success");

    if (chanceOfSuccessForm.getGeophysicallyModified() != null && chanceOfSuccessForm.getGeophysicallyModified()) {
      PercentageValidationUtils.configurePercentageValidator(chanceOfSuccessForm.getGeophysicallyModifiedPercentage(),
              "geophysicallyModifiedPercentage.decimalInputValue", GEOPHYSICALLY_MODIFIED_PERCENTAGE_ERROR_MESSAGE_PREFIX, errors)
          .validate();
    }

    ValidationUtils.rejectIfEmpty(errors, "relatedToSpecificWellLocation", "relatedToSpecificWellLocation.required",
        "Select yes if the chance of success is quoted in relation to a specific well location");

    if (chanceOfSuccessForm.getRelatedToSpecificWellLocation() != null
        && chanceOfSuccessForm.getRelatedToSpecificWellLocation()) {

      PercentageValidationUtils.configurePercentageValidator(chanceOfSuccessForm.getWellModifiedPercentage(),
          "wellModifiedPercentage.decimalInputValue", WELL_MODIFIED_PERCENTAGE_ERROR_MESSAGE_PREFIX, errors)
          .validate();
    }
  }

  private void setupDecimalInputValidators(ChanceOfSuccessForm form, Errors errors) {
    playSourceValidator = PercentageValidationUtils.configurePercentageValidator(
        form.getPlayChanceOfSuccess().getSourcePercentage(),
        "playChanceOfSuccess.sourcePercentage.decimalInputValue", "The play source", errors);

    playReservoirValidator = PercentageValidationUtils.configurePercentageValidator(
        form.getPlayChanceOfSuccess().getReservoirPercentage(),
        "playChanceOfSuccess.reservoirPercentage.decimalInputValue", "The play reservoir", errors);

    playTopSealValidator = PercentageValidationUtils.configurePercentageValidator(
        form.getPlayChanceOfSuccess().getTopSealPercentage(),
        "playChanceOfSuccess.topSealPercentage.decimalInputValue", "The play top seal", errors);

    prospectSpecificTrapPresenceValidator = PercentageValidationUtils.configurePercentageValidator(
        form.getProspectSpecificChanceOfSuccess().getTrapPresence(),
        "prospectSpecificChanceOfSuccess.trapPresence.decimalInputValue",
        "The prospect specific trap presence", errors);

    prospectSpecificTrapEffectivenessValidator = PercentageValidationUtils.configurePercentageValidator(
        form.getProspectSpecificChanceOfSuccess().getTrapEffectiveness(),
        "prospectSpecificChanceOfSuccess.trapEffectiveness.decimalInputValue",
        "The prospect specific trap effectiveness", errors);

    prospectSpecificSourcePresenceValidator = PercentageValidationUtils.configurePercentageValidator(
        form.getProspectSpecificChanceOfSuccess().getSourcePresence(),
        "prospectSpecificChanceOfSuccess.sourcePresence.decimalInputValue",
        "The prospect specific source presence", errors);

    prospectSpecificSourceEffectivenessValidator = PercentageValidationUtils.configurePercentageValidator(
        form.getProspectSpecificChanceOfSuccess().getSourceEffectiveness(),
        "prospectSpecificChanceOfSuccess.sourceEffectiveness.decimalInputValue",
        "The prospect specific source effectiveness", errors);

    prospectSpecificReservoirPresenceValidator = PercentageValidationUtils.configurePercentageValidator(
        form.getProspectSpecificChanceOfSuccess().getReservoirPresence(),
        "prospectSpecificChanceOfSuccess.reservoirPresence.decimalInputValue",
        "The prospect specific reservoir presence", errors);

    prospectSpecificReservoirEffectivenessValidator = PercentageValidationUtils.configurePercentageValidator(
        form.getProspectSpecificChanceOfSuccess().getReservoirEffectiveness(),
        "prospectSpecificChanceOfSuccess.reservoirEffectiveness.decimalInputValue",
        "The prospect specific reservoir effectiveness", errors);
  }

}
