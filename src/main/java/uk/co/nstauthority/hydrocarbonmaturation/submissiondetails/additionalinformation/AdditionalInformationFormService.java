package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class AdditionalInformationFormService implements SubmissionFormService {

  private final AdditionalInformationFormValidator additionalInformationFormValidator;
  private final AdditionalInformationPersistenceService additionalInformationPersistenceService;
  private final SubmissionSupportingDocumentService supportingDocumentService;

  @Autowired
  public AdditionalInformationFormService(AdditionalInformationFormValidator additionalInformationFormValidator,
                                          AdditionalInformationPersistenceService additionalInformationPersistenceService,
                                          SubmissionSupportingDocumentService supportingDocumentService) {
    this.additionalInformationFormValidator = additionalInformationFormValidator;
    this.additionalInformationPersistenceService = additionalInformationPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
  }

  AdditionalInformationForm getAdditionalInformationForm(SubmissionDetail submissionDetail) {
    var additionalDocumentsFileUploads = supportingDocumentService.getUploadedFileForms(
        submissionDetail, FileDocumentType.ADDITIONAL_DOCUMENT);

    return additionalInformationPersistenceService.getAdditionalInformation(submissionDetail)
        .map(additionalInformation -> additionalInformationEntityToForm(additionalInformation, additionalDocumentsFileUploads))
        .orElseGet(() -> {
          var form = new AdditionalInformationForm();
          form.setAdditionalDocuments(additionalDocumentsFileUploads);
          return form;
        });
  }

  @Override
  public BindingResult validate(SubmissionForm form,
                                BindingResult bindingResult,
                                ValidationType validationType,
                                SubmissionDetail submissionDetail) {
    additionalInformationFormValidator.validate(
        form,
        bindingResult,
        new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }

  private AdditionalInformationForm additionalInformationEntityToForm(AdditionalInformation additionalInformation,
                                                                      List<UploadedFileForm> additionalDocumentUploads) {
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
