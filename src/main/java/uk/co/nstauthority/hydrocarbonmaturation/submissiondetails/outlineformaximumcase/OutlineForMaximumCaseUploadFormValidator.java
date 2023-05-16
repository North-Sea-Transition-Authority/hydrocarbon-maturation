package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileValidationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.PartialFullFormValidator;

@Service
class OutlineForMaximumCaseUploadFormValidator extends PartialFullFormValidator {

  @Override
  public boolean supports(Class<?> clazz) {
    return OutlineForMaximumCaseUploadForm.class.equals(clazz);
  }

  @Override
  public void validatePartial(SubmissionForm form, Errors errors) {
    //no fields require partial validation

  }

  @Override
  public void validateFull(SubmissionForm form, Errors errors) {
    var outlineForMaximumCaseForm = (OutlineForMaximumCaseUploadForm) form;

    var outlineForMaximumCase = outlineForMaximumCaseForm.getOutlineForMaximumCase();
    FileValidationUtil.validator()
        .withMaximumNumberOfFiles(1, "Upload at most one outline for maximum case")
        .withMinimumNumberOfFiles(1, "Upload an outline for maximum case")
        .validate(errors, outlineForMaximumCase, "outlineForMaximumCase");
  }
}
