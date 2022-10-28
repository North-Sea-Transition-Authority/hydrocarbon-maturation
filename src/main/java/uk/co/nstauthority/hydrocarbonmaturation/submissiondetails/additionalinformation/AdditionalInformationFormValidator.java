package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import java.util.List;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadDetail;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadValidationUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class AdditionalInformationFormValidator implements SmartValidator, SubmissionFormValidator {

  @Override
  public void validate(Object target, Errors errors, Object... validationHints) {
    var form = (AdditionalInformationForm) target;
    var validationType = (ValidationType) validationHints[0];

    switch (validationType) {
      case FULL -> validateFull(form, errors);
      case PARTIAL -> validatePartial(form, errors);
      default -> throw new ActionNotAllowedException("Validation type must be either full or partial");
    }
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return AdditionalInformationForm.class.equals(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    throw new ActionNotAllowedException("Incorrect parameters provided for validation");
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    // no fields require partial validation
  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var additionalInformationForm = (AdditionalInformationForm) form;

    ValidationUtils.rejectIfEmpty(errors,
        "providingAdditionalComments",
        "providingAdditionalComments.required",
        "Select whether you have additional comments");

    if (BooleanUtils.isTrue(additionalInformationForm.getProvidingAdditionalComments())) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors,
          "additionalComments",
          "additionalComments.required",
          "Enter any additional comments"
      );
    }

    ValidationUtils.rejectIfEmpty(errors,
        "providingAdditionalDocuments",
        "providingAdditionalDocuments.required",
        "Select whether you have additional documents");

    List<FileUploadDetail> additionalDocuments = additionalInformationForm.getAdditionalDocuments();
    if (BooleanUtils.isTrue(additionalInformationForm.getProvidingAdditionalDocuments())
        && additionalDocuments.isEmpty()) {
      errors.rejectValue("additionalDocuments", "additionalDocuments.required", "Upload an additional document");
    } else if (BooleanUtils.isTrue(additionalInformationForm.getProvidingAdditionalDocuments())) {
      FileUploadValidationUtils.rejectIfFileDescriptionsAreEmptyOrWhitespace(errors, additionalDocuments, "additionalDocuments");
    }
  }
}
