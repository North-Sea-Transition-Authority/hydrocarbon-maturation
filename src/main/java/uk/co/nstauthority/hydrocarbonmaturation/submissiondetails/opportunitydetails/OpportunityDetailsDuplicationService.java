package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.Set;
import javax.persistence.EntityManager;
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
  private final EntityManager entityManager;

  @Autowired
  OpportunityDetailsDuplicationService(OpportunityDetailsPersistenceService opportunityDetailsPersistenceService,
                                       SupportingDocumentService supportingDocumentService,
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

    supportingDocumentService.getSupportingDocumentsForTypes(
        oldSubmissionDetail,
        Set.of(
            SupportingDocumentType.MAP_OF_OPPORTUNITY,
            SupportingDocumentType.REPRESENTATIVE_SEISMIC_SECTION,
            SupportingDocumentType.GEOLOGICAL_CROSS_SECTION
        )
    ).forEach(supportingDocument ->
        submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
            newSubmissionDetail,
            supportingDocument,
            SupportingDocument.class
        ));
  }

  @Transactional
  void setOpportunityType(OpportunityDetail opportunityDetail, SubmissionDetail newSubmissionDetail) {
    if (newSubmissionDetail.isDiscoverySubmission()) {
      opportunityDetail.setOpportunityType(OpportunityType.DISCOVERY);
    } else if (newSubmissionDetail.isDryWellSubmission()) {
      opportunityDetail.setOpportunityType(OpportunityType.DRY_WELL);
    }
    entityManager.persist(opportunityDetail);
  }
}
