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

  private DecimalInputValidator prospectSpecificSourceValidator;
  private DecimalInputValidator prospectSpecificReservoirValidator;
  private DecimalInputValidator prospectSpecificTopSealValidator;
  private DecimalInputValidator prospectSpecificTrapValidator;
  private DecimalInputValidator prospectSpecificMigrationValidator;

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

    prospectSpecificSourceValidator.isOptional().validate();
    prospectSpecificReservoirValidator.isOptional().validate();
    prospectSpecificTopSealValidator.isOptional().validate();
    prospectSpecificTrapValidator.isOptional().validate();
    prospectSpecificMigrationValidator.isOptional().validate();

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

    prospectSpecificSourceValidator.validate();
    prospectSpecificReservoirValidator.validate();
    prospectSpecificTopSealValidator.validate();
    prospectSpecificTrapValidator.validate();
    prospectSpecificMigrationValidator.validate();

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

    prospectSpecificSourceValidator = PercentageValidationUtils.configurePercentageValidator(
        form.getProspectSpecificChanceOfSuccess().getChanceOfSuccessDetailsForm().getSourcePercentage(),
        "prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.sourcePercentage.decimalInputValue",
        "The prospect specific source", errors);

    prospectSpecificReservoirValidator = PercentageValidationUtils.configurePercentageValidator(
        form.getProspectSpecificChanceOfSuccess().getChanceOfSuccessDetailsForm().getReservoirPercentage(),
        "prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.reservoirPercentage.decimalInputValue",
        "The prospect specific reservoir", errors);

    prospectSpecificTopSealValidator = PercentageValidationUtils.configurePercentageValidator(
        form.getProspectSpecificChanceOfSuccess().getChanceOfSuccessDetailsForm().getTopSealPercentage(),
        "prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.topSealPercentage.decimalInputValue",
        "The prospect specific top seal", errors);

    prospectSpecificTrapValidator = PercentageValidationUtils.configurePercentageValidator(
        form.getProspectSpecificChanceOfSuccess().getTrapPercentage(),
        "prospectSpecificChanceOfSuccess.trapPercentage.decimalInputValue",
        "The prospect specific trap", errors);

    prospectSpecificMigrationValidator = PercentageValidationUtils.configurePercentageValidator(
        form.getProspectSpecificChanceOfSuccess().getMigrationPercentage(),
        "prospectSpecificChanceOfSuccess.migrationPercentage.decimalInputValue",
        "The prospect specific migration", errors);
  }

}
