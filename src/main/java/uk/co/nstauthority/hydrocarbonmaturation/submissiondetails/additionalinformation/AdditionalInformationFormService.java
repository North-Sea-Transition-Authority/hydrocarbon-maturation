package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadUtils;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFileView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class AdditionalInformationFormService implements SubmissionFormService {

  private final AdditionalInformationFormValidator additionalInformationFormValidator;
  private final AdditionalInformationPersistenceService additionalInformationPersistenceService;
  private final SupportingDocumentService supportingDocumentService;

  @Autowired
  public AdditionalInformationFormService(AdditionalInformationFormValidator additionalInformationFormValidator,
                                          AdditionalInformationPersistenceService additionalInformationPersistenceService,
                                          SupportingDocumentService supportingDocumentService) {
    this.additionalInformationFormValidator = additionalInformationFormValidator;
    this.additionalInformationPersistenceService = additionalInformationPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
  }

  AdditionalInformationForm getAdditionalInformationForm(SubmissionDetail submissionDetail) {
    var additionalDocumentsFileUploads = supportingDocumentService.getFileUploadFormListForSubmissionAndType(
        submissionDetail, SupportingDocumentType.ADDITIONAL_DOCUMENT);

    return additionalInformationPersistenceService.getAdditionalInformation(submissionDetail)
        .map(additionalInformation -> additionalInformationEntityToForm(additionalInformation, additionalDocumentsFileUploads))
        .orElseGet(() -> {
          var form = new AdditionalInformationForm();
          form.setAdditionalDocuments(additionalDocumentsFileUploads);
          return form;
        });
  }

  public List<UploadedFileView> getAdditionalDocumentUploads(AdditionalInformationForm additionalInformationForm) {
    var additionalDocumentUploadIdList = FileUploadUtils.getUploadIdList(additionalInformationForm.getAdditionalDocuments());
    return supportingDocumentService.getUploadedFileViewList(additionalDocumentUploadIdList);
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    additionalInformationFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }

  private AdditionalInformationForm additionalInformationEntityToForm(AdditionalInformation additionalInformation,
                                                                      List<FileUploadForm> additionalDocumentUploads) {
    var form = new AdditionalInformationForm();

    form.setProvidingAdditionalComments(additionalInformation.getHasAdditionalComments());
    form.setProvidingAdditionalDocuments(additionalInformation.getHasAdditionalDocuments());

    if (BooleanUtils.isTrue(additionalInformation.getHasAdditionalDocuments())) {
      form.setAdditionalDocuments(additionalDocumentUploads);
    }

    form.setAdditionalComments(additionalInformation.getAdditionalComments());

    return form;
  }
}
