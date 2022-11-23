package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

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
class OpportunityDetailsDuplicationService implements SubmissionSectionDuplicationService {
  private final OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;
  private final SupportingDocumentService supportingDocumentService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  OpportunityDetailsDuplicationService(OpportunityDetailsPersistenceService opportunityDetailsPersistenceService,
                                       SupportingDocumentService supportingDocumentService,
                                       SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.opportunityDetailsPersistenceService = opportunityDetailsPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    opportunityDetailsPersistenceService.getOpportunityDetail(oldSubmissionDetail)
        .ifPresent(opportunityDetail ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, opportunityDetail,
                OpportunityDetail.class));

    supportingDocumentService.getSupportingDocumentsForType(oldSubmissionDetail, SupportingDocumentType.MAP_OF_OPPORTUNITY)
        .forEach(supportingDocument ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, supportingDocument,
                SupportingDocument.class));
  }
}
