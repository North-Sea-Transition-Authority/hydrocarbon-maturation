package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

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
public class GasCaseOutputsDuplicationService implements ReservoirSectionDuplicationService {
  private final GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Autowired
  GasCaseOutputsDuplicationService(GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService,
                                   ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
                                   ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService) {
    this.gasCaseOutputsPersistenceService = gasCaseOutputsPersistenceService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.reservoirDuplicationPersistenceService = reservoirDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateReservoirSection(ReservoirSubmissionDetail oldReservoirSubmissionDetail,
                                         ReservoirSubmissionDetail newReservoirSubmissionDetail) {
    var oldSubmissionDetail = oldReservoirSubmissionDetail.getSubmissionDetail();
    var newSubmissionDetail = newReservoirSubmissionDetail.getSubmissionDetail();

    if (!newSubmissionDetail.isDiscoverySubmission() && !newSubmissionDetail.isProspectSubmission()) {
      return;
    }

    gasCaseOutputsPersistenceService.getGasCaseOutput(oldReservoirSubmissionDetail)
        .ifPresent(gasCaseOutput ->
            reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(newReservoirSubmissionDetail, gasCaseOutput,
                GasCaseOutput.class)
        );

    if (oldSubmissionDetail.hasSameResourceType(newSubmissionDetail)) {
      var applicableInstances = Set.of(
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT,
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT,
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
      );
      var resourceType = oldSubmissionDetail.getSubmission().getResource().getType();
      if (resourceType == ResourceType.DISCOVERY) {
        applicableInstances = Set.of(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
            ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
        );
      }
      probabilityDistributionPersistenceService
          .getAllProbabilityDistributionsForInstances(oldReservoirSubmissionDetail, applicableInstances)
          .forEach(probabilityDistribution ->
              reservoirDuplicationPersistenceService
                  .duplicateChildAndReplaceParent(newReservoirSubmissionDetail, probabilityDistribution,
                      ProbabilityDistribution.class));

      probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(
          newReservoirSubmissionDetail, applicableInstances).forEach(
            probabilityDistribution -> probabilityDistribution.setSubmissionDetail(newSubmissionDetail)
      );
    }
  }
}
