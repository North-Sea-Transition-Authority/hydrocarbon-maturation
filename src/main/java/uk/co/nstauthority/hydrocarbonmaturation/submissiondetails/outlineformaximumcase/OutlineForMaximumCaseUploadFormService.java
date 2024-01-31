package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class OutlineForMaximumCaseUploadFormService implements SubmissionFormService {
  private final OutlineForMaximumCaseUploadFormValidator outlineForMaximumCaseFormValidator;
  private final SubmissionSupportingDocumentService submissionSupportingDocumentService;
  private final OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService;

  @Autowired
  public OutlineForMaximumCaseUploadFormService(
      OutlineForMaximumCaseUploadFormValidator outlineForMaximumCaseFormValidator,
      SubmissionSupportingDocumentService submissionSupportingDocumentService,
      OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService
  ) {
    this.outlineForMaximumCaseFormValidator = outlineForMaximumCaseFormValidator;
    this.submissionSupportingDocumentService = submissionSupportingDocumentService;
    this.outlineForMaximumCaseDocumentTypeService = outlineForMaximumCaseDocumentTypeService;
  }

  OutlineForMaximumCaseUploadForm getOutlineForMaximumCaseUploadForm(SubmissionDetail submissionDetail) {
    var documentType = outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(submissionDetail);
    var outlineForMaximumCaseFileUploads =
        submissionSupportingDocumentService.getUploadedFileForms(submissionDetail, documentType);

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
