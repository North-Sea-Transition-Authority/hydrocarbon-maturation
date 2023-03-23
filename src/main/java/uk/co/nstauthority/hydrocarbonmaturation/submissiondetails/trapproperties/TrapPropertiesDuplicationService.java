package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocument;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;

@Service
class TrapPropertiesDuplicationService implements SubmissionSectionDuplicationService {
  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final SupportingDocumentService supportingDocumentService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  TrapPropertiesDuplicationService(TrapPropertiesPersistenceService trapPropertiesPersistenceService,
                                   ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
                                   SupportingDocumentService supportingDocumentService,
                                   SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    trapPropertiesPersistenceService.getTrapProperty(oldSubmissionDetail)
        .ifPresent(trapProperty ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, trapProperty,
                TrapProperty.class)
        );

    supportingDocumentService.getSupportingDocumentsForTypes(
        oldSubmissionDetail,
        Set.of(SupportingDocumentType.PROSPECT_DEPTH_MAP)
    ).forEach(supportingDocument -> submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
        newSubmissionDetail,
        supportingDocument,
        SupportingDocument.class
    ));

    probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldSubmissionDetail, Set.of(
            ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA,
            ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_PROSPECT_DISCOVERY,
            ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT_DISCOVERY
        ))
        .forEach(probabilityDistribution ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution,
                ProbabilityDistribution.class));
  }
}
