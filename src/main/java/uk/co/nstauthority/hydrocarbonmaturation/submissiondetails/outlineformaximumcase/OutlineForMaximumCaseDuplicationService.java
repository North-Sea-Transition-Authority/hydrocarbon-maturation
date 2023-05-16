package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import java.util.Set;
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

  @Autowired
  OutlineForMaximumCaseDuplicationService(SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService,
                                          SupportingDocumentService supportingDocumentService) {
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    supportingDocumentService.getSupportingDocumentsForTypes(
        oldSubmissionDetail,
        Set.of(
            SupportingDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE
        )
    ).forEach(supportingDocument ->
        submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
            newSubmissionDetail,
            supportingDocument,
            SupportingDocument.class
        ));
  }
}
