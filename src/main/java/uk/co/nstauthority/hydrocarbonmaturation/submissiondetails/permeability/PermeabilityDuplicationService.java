package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

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
class PermeabilityDuplicationService implements ReservoirSectionDuplicationService {
  private final PermeabilityPersistenceService permeabilityPersistenceService;
  private final ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Autowired
  PermeabilityDuplicationService(PermeabilityPersistenceService permeabilityPersistenceService,
                                 ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService,
                                 ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.permeabilityPersistenceService = permeabilityPersistenceService;
    this.reservoirDuplicationPersistenceService = reservoirDuplicationPersistenceService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateReservoirSection(ReservoirSubmissionDetail oldReservoirSubmissionDetail,
                                        ReservoirSubmissionDetail newReservoirSubmissionDetail) {
    permeabilityPersistenceService.getPermeabilityDetail(oldReservoirSubmissionDetail)
        .ifPresent(permeabilityDetail ->
            reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(newReservoirSubmissionDetail,
                permeabilityDetail,
                PermeabilityDetail.class)
        );

    var oldSubmissionDetail = oldReservoirSubmissionDetail.getSubmissionDetail();
    var newSubmissionDetail = newReservoirSubmissionDetail.getSubmissionDetail();
    var resourceType = oldSubmissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.FIELD && oldSubmissionDetail.hasSameResourceType(newSubmissionDetail)) {
      var applicableInstances = Set.of(
          ProbabilityDistributionFieldInstance.FIELD_PERMEABILITY,
          ProbabilityDistributionFieldInstance.FIELD_HORIZONTAL_PERMEABILITY,
          ProbabilityDistributionFieldInstance.FIELD_VERTICAL_PERMEABILITY
      );

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
