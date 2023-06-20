package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

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
class MixedCaseOutputsDuplicationService implements SubmissionSectionDuplicationService {
  private final MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService;

  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  MixedCaseOutputsDuplicationService(MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService,
                                     ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
                                     SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.mixedCaseOutputsPersistenceService = mixedCaseOutputsPersistenceService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    if (!newSubmissionDetail.isProspectSubmission() && !newSubmissionDetail.isDiscoverySubmission()) {
      return;
    }

    mixedCaseOutputsPersistenceService.getMixedCaseOutput(oldSubmissionDetail)
        .ifPresent(mixedCaseOutput ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, mixedCaseOutput,
                MixedCaseOutput.class)
        );

    if (oldSubmissionDetail.hasSameResourceType(newSubmissionDetail)) {
      probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldSubmissionDetail, Set.of(
              ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE,
              ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES,
              ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES,
              ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE,
              ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES,
              ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES,
              ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES
          ))
          .forEach(probabilityDistribution ->
              submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution,
                  ProbabilityDistribution.class));
    }
  }
}
