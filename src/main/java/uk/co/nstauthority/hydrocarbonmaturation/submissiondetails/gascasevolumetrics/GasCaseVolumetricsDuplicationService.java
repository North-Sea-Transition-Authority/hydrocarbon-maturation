package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

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

@Service
class GasCaseVolumetricsDuplicationService implements SubmissionSectionDuplicationService {
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  GasCaseVolumetricsDuplicationService(ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
                                       SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    if (!newSubmissionDetail.isDiscoverySubmission() && !newSubmissionDetail.isProspectSubmission()) {
      return;
    }

    probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldSubmissionDetail, Set.of(
            ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION,
            ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR,
            ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
            ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR
        ))
        .forEach(probabilityDistribution ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution,
                ProbabilityDistribution.class));
  }
}
