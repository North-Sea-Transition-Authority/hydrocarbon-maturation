package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadUtils;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFileView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class OutlineForMaximumCaseUploadFormService implements SubmissionFormService {
  public static final String ALLOWED_EXTENSIONS = ".zip";
  private final OutlineForMaximumCaseUploadFormValidator outlineForMaximumCaseFormValidator;
  private final SupportingDocumentService supportingDocumentService;

  @Autowired
  public OutlineForMaximumCaseUploadFormService(OutlineForMaximumCaseUploadFormValidator outlineForMaximumCaseFormValidator,
                                                SupportingDocumentService supportingDocumentService) {
    this.outlineForMaximumCaseFormValidator = outlineForMaximumCaseFormValidator;
    this.supportingDocumentService = supportingDocumentService;
  }

  OutlineForMaximumCaseUploadForm getOutlineForMaximumCaseUploadForm(SubmissionDetail submissionDetail) {
    var outlineForMaximumCaseFileUploads = supportingDocumentService.getFileUploadFormListForSubmissionAndType(
        submissionDetail, SupportingDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE);

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

  String getAllowedExtensions() {
    return ALLOWED_EXTENSIONS;
  }

  List<UploadedFileView> getOutlineForMaximumCaseUploads(OutlineForMaximumCaseUploadForm opportunityDetailsForm) {
    return supportingDocumentService.getUploadedFileViewList(
        FileUploadUtils.getUploadIdList(opportunityDetailsForm.getOutlineForMaximumCase())
    );
  }
}
