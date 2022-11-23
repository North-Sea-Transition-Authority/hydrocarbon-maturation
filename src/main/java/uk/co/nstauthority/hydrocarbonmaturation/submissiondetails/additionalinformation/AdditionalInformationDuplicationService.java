package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocument;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;

@Service
class AdditionalInformationDuplicationService implements SubmissionSectionDuplicationService {
  private final AdditionalInformationPersistenceService additionalInformationPersistenceService;
  private final SupportingDocumentService supportingDocumentService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  AdditionalInformationDuplicationService(AdditionalInformationPersistenceService chanceOfSuccessPersistenceService,
                                          SupportingDocumentService supportingDocumentService,
                                          SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.additionalInformationPersistenceService = chanceOfSuccessPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    additionalInformationPersistenceService.getAdditionalInformation(oldSubmissionDetail)
        .ifPresent(additionalInformation ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, additionalInformation,
                AdditionalInformation.class)
        );

    supportingDocumentService.getSupportingDocumentsForType(oldSubmissionDetail, SupportingDocumentType.ADDITIONAL_DOCUMENT)
        .forEach(supportingDocument ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, supportingDocument,
                SupportingDocument.class));
  }
}
