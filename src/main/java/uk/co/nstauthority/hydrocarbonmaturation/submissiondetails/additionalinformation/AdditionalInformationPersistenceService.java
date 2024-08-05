package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@Service
public class AdditionalInformationPersistenceService {

  private final AdditionalInformationRepository additionalInformationRepository;
  private final SubmissionSupportingDocumentService supportingDocumentService;

  @Autowired
  public AdditionalInformationPersistenceService(AdditionalInformationRepository additionalInformationRepository,
                                                 SubmissionSupportingDocumentService supportingDocumentService) {
    this.additionalInformationRepository = additionalInformationRepository;
    this.supportingDocumentService = supportingDocumentService;
  }

  @Transactional
  public void saveAdditionalInformation(AdditionalInformationForm form, SubmissionDetail submissionDetail) {
    var additionalInformation = getAdditionalInformation(submissionDetail)
        .orElse(new AdditionalInformation());

    additionalInformation.setSubmissionDetail(submissionDetail);
    additionalInformation.setHasAdditionalComments(form.getProvidingAdditionalComments());
    additionalInformation.setHasAdditionalDocuments(form.getProvidingAdditionalDocuments());

    if (BooleanUtils.isTrue(form.getProvidingAdditionalComments())) {
      additionalInformation.setAdditionalComments(form.getAdditionalComments());
    } else {
      additionalInformation.setAdditionalComments(null);
    }

    if (!BooleanUtils.isTrue(form.getProvidingAdditionalDocuments())) {
      supportingDocumentService.getUploadedFiles(
          submissionDetail, FileDocumentType.ADDITIONAL_DOCUMENT)
          .forEach(file -> supportingDocumentService.deleteFile(file.getId()));
    }

    supportingDocumentService
        .saveDocuments(submissionDetail, form.getAdditionalDocuments(), FileDocumentType.ADDITIONAL_DOCUMENT);
    additionalInformationRepository.save(additionalInformation);
  }

  Optional<AdditionalInformation> getAdditionalInformation(SubmissionDetail submissionDetail) {
    return additionalInformationRepository.findBySubmissionDetail(submissionDetail);
  }
}
