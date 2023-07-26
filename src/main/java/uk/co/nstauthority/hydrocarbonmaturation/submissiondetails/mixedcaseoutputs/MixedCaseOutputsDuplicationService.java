package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class MixedCaseOutputsDuplicationService implements ReservoirSectionDuplicationService {
  private final MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService;

  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Autowired
  MixedCaseOutputsDuplicationService(MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService,
                                     ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
                                     ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService) {
    this.mixedCaseOutputsPersistenceService = mixedCaseOutputsPersistenceService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.reservoirDuplicationPersistenceService = reservoirDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateReservoirSection(ReservoirSubmissionDetail oldReservoirSubmissionDetail,
                                        ReservoirSubmissionDetail newReservoirSubmissionDetail) {
    var oldSubmissionDetail = oldReservoirSubmissionDetail.getSubmissionDetail();
    var newSubmissionDetail = newReservoirSubmissionDetail.getSubmissionDetail();

    if (!newSubmissionDetail.isProspectSubmission() && !newSubmissionDetail.isDiscoverySubmission()) {
      return;
    }

    mixedCaseOutputsPersistenceService.getMixedCaseOutput(oldReservoirSubmissionDetail)
        .ifPresent(mixedCaseOutput ->
            reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(newReservoirSubmissionDetail, mixedCaseOutput,
                MixedCaseOutput.class)
        );

    if (oldSubmissionDetail.hasSameResourceType(newSubmissionDetail)) {
      var applicableInstances = Set.of(
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT,
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT,
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT,
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT,
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
      );
      var resourceType = oldSubmissionDetail.getSubmission().getResource().getType();
      if (resourceType == ResourceType.DISCOVERY) {
        applicableInstances = Set.of(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_DISCOVERY,
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_DISCOVERY,
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
        );
      }

      probabilityDistributionPersistenceService
          .getAllProbabilityDistributionsForInstances(oldReservoirSubmissionDetail, applicableInstances)
          .forEach(probabilityDistribution ->
              reservoirDuplicationPersistenceService
                  .duplicateChildAndReplaceParent(newReservoirSubmissionDetail, probabilityDistribution,
                      ProbabilityDistribution.class));
    }
  }
}
