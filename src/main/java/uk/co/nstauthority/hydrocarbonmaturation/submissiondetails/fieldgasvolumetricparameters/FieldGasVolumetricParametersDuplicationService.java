package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldgasvolumetricparameters;

import java.util.HashSet;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class FieldGasVolumetricParametersDuplicationService implements ReservoirSectionDuplicationService {

  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Autowired
  FieldGasVolumetricParametersDuplicationService(
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

    if (!newSubmissionDetail.isFieldSubmission()) {
      return;
    }

    if (oldSubmissionDetail.hasSameResourceType(newSubmissionDetail)) {
      var applicableInstances = new HashSet<>(
          Set.of(
              ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD_CONSTANT,
              ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR_FIELD,
              ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_FIELD,
              ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_FIELD)
      );
      probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(
              oldReservoirSubmissionDetail, applicableInstances)
          .forEach(probabilityDistribution ->
              reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(newReservoirSubmissionDetail,
                  probabilityDistribution,
                  ProbabilityDistribution.class));

      //set new submission detail against the new probability items
      probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(
          newReservoirSubmissionDetail, applicableInstances).forEach(probabilityDistribution ->
          probabilityDistribution.setSubmissionDetail(newSubmissionDetail)
      );
    }
  }
}
