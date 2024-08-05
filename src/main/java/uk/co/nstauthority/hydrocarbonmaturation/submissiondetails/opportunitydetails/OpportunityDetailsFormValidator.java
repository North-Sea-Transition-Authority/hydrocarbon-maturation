package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidationUtils;

@Service
public class OpportunityDetailsFormValidator extends PartialFullFormValidator {

  private SubmissionDetail submissionDetail;

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (SubmissionForm) target;
    var hints = SubmissionFormValidationUtils
        .extractValidatorHints(validationHints);
    submissionDetail = hints.getSubmissionDetail();
    var validationType = hints.getValidationType();

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return OpportunityDetailsForm.class.equals(clazz);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    var opportunityDetailsForm = (OpportunityDetailsForm) form;

    var mapOfOpportunity = opportunityDetailsForm.getMapOfOpportunity();
    var mapName = submissionDetail.isDiscoverySubmission() ? "map of discovery" : "map of opportunity";
    FileValidationUtil.validator()
        .withMaximumNumberOfFiles(1, "Upload at most one %s".formatted(mapName))
        .isPartiallyValidated()
        .validate(errors, mapOfOpportunity, "mapOfOpportunity");

    var representativeSeismicSection = opportunityDetailsForm.getRepresentativeSeismicSection();
    FileValidationUtil.validator()
        .withMaximumNumberOfFiles(1, "Upload at most one representative seismic section")
        .isPartiallyValidated()
        .validate(errors, representativeSeismicSection, "representativeSeismicSection");

    var geologicalCrossSection = opportunityDetailsForm.getGeologicalCrossSection();
    FileValidationUtil.validator()
        .withMaximumNumberOfFiles(1, "Upload at most one geological cross section")
        .isPartiallyValidated()
        .validate(errors, geologicalCrossSection, "geologicalCrossSection");
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    if (submissionDetail.isProspectSubmission()) {
      ValidationUtils.rejectIfEmpty(errors, "opportunityType", "opportunityType.required",
          "Select an opportunity type");
    }
    var opportunityDetailsForm = (OpportunityDetailsForm) form;

    if (Objects.equals(opportunityDetailsForm.getOpportunityType(), OpportunityType.OTHER)) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "otherTypeOfOpportunity", "otherTypeOfOpportunity.required",
          "Enter the other opportunity type");
    }

    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "keyEvaluationWorkCarriedOut", "keyEvaluationWorkCarriedOut.required",
        "Enter the key evaluation work being carried out for this opportunity");

    var mapOfOpportunity = opportunityDetailsForm.getMapOfOpportunity();
    var mapName = submissionDetail.isDiscoverySubmission() ? "map of discovery" : "map of opportunity";
    FileValidationUtil.validator()
        .withMaximumNumberOfFiles(1, "Upload at most one %s".formatted(mapName))
        .withMinimumNumberOfFiles(1, "Upload a %s".formatted(mapName))
        .validate(errors, mapOfOpportunity, "mapOfOpportunity");

    var representativeSeismicSection = opportunityDetailsForm.getRepresentativeSeismicSection();
    FileValidationUtil.validator()
        .withMaximumNumberOfFiles(1, "Upload at most one representative seismic section")
        .withMinimumNumberOfFiles(1, "Upload a representative seismic section")
        .validate(errors, representativeSeismicSection, "representativeSeismicSection");

    var geologicalCrossSection = opportunityDetailsForm.getGeologicalCrossSection();
    FileValidationUtil.validator()
        .withMaximumNumberOfFiles(1, "Upload at most one geological cross section")
        .validate(errors, geologicalCrossSection, "geologicalCrossSection");
  }
}
