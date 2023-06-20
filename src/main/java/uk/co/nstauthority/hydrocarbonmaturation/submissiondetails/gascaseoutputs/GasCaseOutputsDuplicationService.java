package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

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
class GasCaseOutputsDuplicationService implements SubmissionSectionDuplicationService {
  private final GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;

  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  GasCaseOutputsDuplicationService(GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService,
                                   ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
                                   SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.gasCaseOutputsPersistenceService = gasCaseOutputsPersistenceService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    if (!newSubmissionDetail.isDiscoverySubmission() && !newSubmissionDetail.isProspectSubmission()) {
      return;
    }

    gasCaseOutputsPersistenceService.getGasCaseOutput(oldSubmissionDetail)
        .ifPresent(gasCaseOutput ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, gasCaseOutput,
                GasCaseOutput.class)
        );

    if (oldSubmissionDetail.hasSameResourceType(newSubmissionDetail)) {
      probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldSubmissionDetail, Set.of(
              ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE,
              ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES,
              ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES,
              ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES
          ))
          .forEach(probabilityDistribution ->
              submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution,
                  ProbabilityDistribution.class));
    }
  }
}
