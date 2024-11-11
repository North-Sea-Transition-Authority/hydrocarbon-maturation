package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInputValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class PlannedUnsanctionedWellsFormValidator implements SubmissionValidator {


  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var plannedUnsanctionedWellsForm = (PlannedUnsanctionedWellsForm) form;

    validateTotalNumberOfPlannedUnsanctionedWells(
        errors,
        "totalNumberOfPlannedUnsanctionedWells.decimalInputValue",
        "Total number of planned unsanctioned wells",
        plannedUnsanctionedWellsForm.getTotalNumberOfPlannedUnsanctionedWells(),
        ValidationType.PARTIAL
    );

    validateCapex(
        errors,
        "totalCapex.decimalInputValue",
        "Total CAPEX",
        plannedUnsanctionedWellsForm.getTotalCapex(),
        ValidationType.PARTIAL
    );
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    var plannedUnsanctionedWellsForm = (PlannedUnsanctionedWellsForm) form;

    validateTotalNumberOfPlannedUnsanctionedWells(
        errors,
        "totalNumberOfPlannedUnsanctionedWells.decimalInputValue",
        "Total number of planned unsanctioned wells",
        plannedUnsanctionedWellsForm.getTotalNumberOfPlannedUnsanctionedWells(),
        ValidationType.FULL
    );

    validateCapex(
        errors,
        "totalCapex.decimalInputValue",
        "Total CAPEX",
        plannedUnsanctionedWellsForm.getTotalCapex(),
        ValidationType.FULL
    );
  }

  private void validateTotalNumberOfPlannedUnsanctionedWells(Errors errors,
                                                             String fieldId,
                                                             String errorPrefix,
                                                             DecimalInput validatedValue,
                                                             ValidationType validationType) {
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
      return;
    }

    inputValidator
        .setMaxDecimalPlaces(0)
        .setMinValue(BigDecimal.ZERO);
    inputValidator.validate();
  }

  private void validateCapex(Errors errors,
                                           String fieldId,
                                           String errorPrefix,
                                           DecimalInput validatedValue,
                                           ValidationType validationType) {
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
      return;
    }

    inputValidator
        .setMaxDecimalPlaces(2)
        .setMinValue(BigDecimal.ZERO);
    inputValidator.validate();
  }
}
