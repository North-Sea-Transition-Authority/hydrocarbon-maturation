package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;

@Service
class AdditionalInformationFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return AdditionalInformationForm.class.equals(clazz);
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

    List<UploadedFileForm> additionalDocuments = additionalInformationForm.getAdditionalDocuments();
    if (BooleanUtils.isTrue(additionalInformationForm.getProvidingAdditionalDocuments())) {
      FileValidationUtil.validator()
          .withMinimumNumberOfFiles(1, "Upload additional documents")
          .validate(errors, additionalDocuments, "additionalDocuments");
    }
  }
}
