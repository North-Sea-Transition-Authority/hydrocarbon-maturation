package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@Service
class OutlineForMaximumCaseDuplicationService implements SubmissionSectionDuplicationService {
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  private final SubmissionSupportingDocumentService supportingDocumentService;
  private final OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;
  private final OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService;

  @Autowired
  OutlineForMaximumCaseDuplicationService(SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService,
                                          SubmissionSupportingDocumentService supportingDocumentService,
                                          OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService,
                                          OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService) {
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
    this.outlineForMaximumCasePersistenceService = outlineForMaximumCasePersistenceService;
    this.outlineForMaximumCaseDocumentTypeService = outlineForMaximumCaseDocumentTypeService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    var documentType = outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(oldSubmissionDetail);
    supportingDocumentService.copyUploadedFiles(
        oldSubmissionDetail,
        newSubmissionDetail,
        documentType
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
