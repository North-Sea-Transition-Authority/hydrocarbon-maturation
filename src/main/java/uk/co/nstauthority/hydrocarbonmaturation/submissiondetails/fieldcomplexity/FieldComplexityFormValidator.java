package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionValidator;

@Service
public class FieldComplexityFormValidator implements SubmissionValidator {
  @Override
  public void validatePartial(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {
    // not needed
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors, SubmissionDetail submissionDetail) {

    ValidationUtils.rejectIfEmpty(
        errors,
        "viscosityRange",
        "viscosityRange.required",
        "Provide the viscosity"
    );
    ValidationUtils.rejectIfEmpty(
        errors,
        "verticalHeterogeneityRange",
        "verticalHeterogeneityRange.required",
        "Provide the vertical heterogeneity"
    );
    ValidationUtils.rejectIfEmpty(
        errors,
        "arealDensityRange",
        "arealDensityRange.required",
        "Provide the areal density"
    );
    ValidationUtils.rejectIfEmpty(
        errors,
        "structuralComplexityRange",
        "structuralComplexityRange.required",
        "Provide the structural complexity"
    );
  }
}
