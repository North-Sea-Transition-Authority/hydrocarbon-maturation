package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics;

import java.util.Objects;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class MixedCaseVolumetricsDuplicationService implements SubmissionSectionDuplicationService {
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  MixedCaseVolumetricsDuplicationService(ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
                                         SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    if (!Objects.equals(newSubmissionDetail.getSubmissionType(), SubmissionType.PROSPECT_UPDATE)) {
      return;
    }

    probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldSubmissionDetail, Set.of(
            ProbabilityDistributionFieldInstance.MIXED_FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY,
            ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION,
            ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION,
            ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
            ProbabilityDistributionFieldInstance.MIXED_GAS_EXPANSION_FACTOR,
            ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
            ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR,
            ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR
        ))
        .forEach(probabilityDistribution ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution,
                ProbabilityDistribution.class));
  }
}
