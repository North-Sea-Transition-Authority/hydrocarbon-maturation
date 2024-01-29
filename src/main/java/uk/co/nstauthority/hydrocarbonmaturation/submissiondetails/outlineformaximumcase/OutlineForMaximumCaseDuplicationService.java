package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@Service
class OutlineForMaximumCaseDuplicationService implements SubmissionSectionDuplicationService {
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  private final SubmissionSupportingDocumentService supportingDocumentService;
  private final OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;

  @Autowired
  OutlineForMaximumCaseDuplicationService(SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService,
                                          SubmissionSupportingDocumentService supportingDocumentService,
                                          OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService) {
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
    this.outlineForMaximumCasePersistenceService = outlineForMaximumCasePersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    supportingDocumentService.copyUploadedFiles(
        oldSubmissionDetail,
        newSubmissionDetail,
        FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE
    );

    outlineForMaximumCasePersistenceService.getOutlineForMaximumCaseBySubmissionDetail(oldSubmissionDetail).ifPresent(
        outlineForMaximumCase -> submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
            newSubmissionDetail,
            outlineForMaximumCase,
            OutlineForMaximumCase.class
        )
    );
  }



}
