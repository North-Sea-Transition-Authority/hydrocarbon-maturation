package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class OutlineForMaximumCaseUploadFormService implements SubmissionFormService {
  private final OutlineForMaximumCaseUploadFormValidator outlineForMaximumCaseFormValidator;
  private final SubmissionSupportingDocumentService supportingDocumentService;

  @Autowired
  public OutlineForMaximumCaseUploadFormService(OutlineForMaximumCaseUploadFormValidator outlineForMaximumCaseFormValidator,
                                                SubmissionSupportingDocumentService supportingDocumentService) {
    this.outlineForMaximumCaseFormValidator = outlineForMaximumCaseFormValidator;
    this.supportingDocumentService = supportingDocumentService;
  }

  OutlineForMaximumCaseUploadForm getOutlineForMaximumCaseUploadForm(SubmissionDetail submissionDetail) {
    var outlineForMaximumCaseFileUploads = supportingDocumentService.getUploadedFileForms(
        submissionDetail, FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE);

    var form = new OutlineForMaximumCaseUploadForm();
    form.setOutlineForMaximumCase(outlineForMaximumCaseFileUploads);
    return form;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType,
                                SubmissionDetail submissionDetail) {
    outlineForMaximumCaseFormValidator
        .validate(form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }
}
