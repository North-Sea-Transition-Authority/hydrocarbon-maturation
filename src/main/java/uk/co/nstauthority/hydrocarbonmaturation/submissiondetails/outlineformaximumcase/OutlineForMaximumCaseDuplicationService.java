package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

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
class OutlineForMaximumCaseDuplicationService implements SubmissionSectionDuplicationService {
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  private final SupportingDocumentService supportingDocumentService;
  private final OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;

  @Autowired
  OutlineForMaximumCaseDuplicationService(SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService,
                                          SupportingDocumentService supportingDocumentService,
                                          OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService) {
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
    this.outlineForMaximumCasePersistenceService = outlineForMaximumCasePersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    supportingDocumentService.getSupportingDocumentsForType(
        oldSubmissionDetail,
        SupportingDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE
    ).ifPresent(supportingDocument ->
        submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
            newSubmissionDetail,
            supportingDocument,
            SupportingDocument.class
        ));

    outlineForMaximumCasePersistenceService.getOutlineForMaximumCaseBySubmissionDetail(oldSubmissionDetail).ifPresent(
        outlineForMaximumCase -> submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
            newSubmissionDetail,
            outlineForMaximumCase,
            OutlineForMaximumCase.class
        )
    );
  }
}
