package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
class OilCaseVolumetricParametersDuplicationService implements ReservoirSectionDuplicationService {
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Autowired
  OilCaseVolumetricParametersDuplicationService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService) {
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

    if (oldSubmissionDetail.hasSameResourceType(newSubmissionDetail)) {
      var applicableInstances = new HashSet<>(
          Set.of(ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD));
      var resourceType = oldSubmissionDetail.getSubmission().getResource().getType();
      if (resourceType == ResourceType.PROSPECT) {
        applicableInstances.addAll(List.of(
            ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_PROSPECT,
            ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_PROSPECT,
            ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_PROSPECT));
      } else if (resourceType == ResourceType.DISCOVERY) {
        applicableInstances.addAll(List.of(
            ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_DISCOVERY,
            ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_DISCOVERY,
            ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_DISCOVERY));
      }
      probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldReservoirSubmissionDetail,
              applicableInstances)
          .forEach(probabilityDistribution ->
              reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(newReservoirSubmissionDetail,
                  probabilityDistribution,
                  ProbabilityDistribution.class));

      probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(
          newReservoirSubmissionDetail, applicableInstances).forEach(
            probabilityDistribution -> probabilityDistribution.setSubmissionDetail(newSubmissionDetail)
      );
    }
  }
}
