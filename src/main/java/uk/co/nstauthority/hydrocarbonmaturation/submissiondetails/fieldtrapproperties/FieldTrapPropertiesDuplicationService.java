package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;

@Service
public class FieldTrapPropertiesDuplicationService implements SubmissionSectionDuplicationService {

  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  @Autowired
  public FieldTrapPropertiesDuplicationService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService,
      TrapPropertiesPersistenceService trapPropertiesPersistenceService) {

    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
  }


  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    if (!newSubmissionDetail.isFieldSubmission()) {
      return;
    }

    var applicableInstances = Set.of(
        ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_FIELD,
        ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_FIELD,
        ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_FIELD
    );
    probabilityDistributionPersistenceService
        .getAllProbabilityDistributionsForInstances(oldSubmissionDetail, applicableInstances)
        .forEach(probabilityDistribution ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
                newSubmissionDetail, probabilityDistribution, ProbabilityDistribution.class
            )
        );

    trapPropertiesPersistenceService.getTrapProperty(oldSubmissionDetail)
        .ifPresent(trapProperty ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, trapProperty,
                TrapProperty.class)
        );
  }
}
