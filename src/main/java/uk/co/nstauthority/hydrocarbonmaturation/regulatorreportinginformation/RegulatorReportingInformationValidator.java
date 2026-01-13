package uk.co.nstauthority.hydrocarbonmaturation.regulatorreportinginformation;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;

@Service
public class RegulatorReportingInformationValidator {

  boolean isValid(RegulatorReportingInformationForm regulatorReportingInformationForm,
                  Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "resourceName",
        "resourceName.required",
        "Enter the resource name"
    );

    ValidationUtils.rejectIfEmpty(
        errors,
        "hydrocarbonCase",
        "hydrocarbonCase.required",
        "Select the hydrocarbon case"
    );

    ValidationUtils.rejectIfEmpty(
        errors,
        "opportunityType",
        "opportunityType.required",
        "Select the opportunity type"
    );

    if (regulatorReportingInformationForm.getOpportunityType() != null
        && regulatorReportingInformationForm.getOpportunityType().equals(OpportunityType.OTHER)) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "otherTypeOfOpportunity",
          "otherTypeOfOpportunity.required",
          "Enter the other opportunity type"
      );
    }

    return !errors.hasErrors();
  }
}
