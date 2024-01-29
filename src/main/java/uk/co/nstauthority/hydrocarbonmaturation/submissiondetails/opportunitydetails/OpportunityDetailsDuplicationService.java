package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@Service
class OpportunityDetailsDuplicationService implements SubmissionSectionDuplicationService {
  private final OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;
  private final SubmissionSupportingDocumentService supportingDocumentService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  private final EntityManager entityManager;

  @Autowired
  OpportunityDetailsDuplicationService(OpportunityDetailsPersistenceService opportunityDetailsPersistenceService,
                                       SubmissionSupportingDocumentService supportingDocumentService,
                                       SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService,
                                       EntityManager entityManager) {
    this.opportunityDetailsPersistenceService = opportunityDetailsPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
    this.entityManager = entityManager;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    opportunityDetailsPersistenceService.getOpportunityDetail(oldSubmissionDetail)
        .ifPresent(opportunityDetail -> {
          var newOpportunityDetail = submissionDuplicationPersistenceService
              .duplicateChildAndReplaceParent(newSubmissionDetail, opportunityDetail, OpportunityDetail.class);
          if (newSubmissionDetail.isDryWellOrDiscoverySubmission()) {
            setOpportunityType(newOpportunityDetail, newSubmissionDetail);
          }
        });

    Set.of(
        FileDocumentType.MAP_OF_OPPORTUNITY,
        FileDocumentType.REPRESENTATIVE_SEISMIC_SECTION,
        FileDocumentType.GEOLOGICAL_CROSS_SECTION
    ).forEach(fileDocumentType ->
        supportingDocumentService.copyUploadedFiles(
            oldSubmissionDetail,
            newSubmissionDetail,
            fileDocumentType
        )
    );
  }

  @Transactional
  public void setOpportunityType(OpportunityDetail opportunityDetail, SubmissionDetail newSubmissionDetail) {
    if (newSubmissionDetail.isDiscoverySubmission()) {
      opportunityDetail.setOpportunityType(OpportunityType.DISCOVERY);
    } else if (newSubmissionDetail.isDryWellSubmission()) {
      opportunityDetail.setOpportunityType(OpportunityType.DRY_WELL);
    }
    entityManager.persist(opportunityDetail);
  }
}
