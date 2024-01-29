package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class ReservoirPropertiesDuplicationService implements ReservoirSectionDuplicationService {
  private final ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Autowired
  ReservoirPropertiesDuplicationService(ReservoirPropertiesPersistenceService chanceOfSuccessPersistenceService,
                                        ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
                                        ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService) {
    this.reservoirPropertiesPersistenceService = chanceOfSuccessPersistenceService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.reservoirDuplicationPersistenceService = reservoirDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateReservoirSection(ReservoirSubmissionDetail oldReservoirSubmissionDetail,
                                        ReservoirSubmissionDetail newReservoirSubmissionDetail) {
    var oldSubmissionDetail = oldReservoirSubmissionDetail.getSubmissionDetail();
    var newSubmissionDetail = newReservoirSubmissionDetail.getSubmissionDetail();

    reservoirPropertiesPersistenceService.getReservoirProperty(oldReservoirSubmissionDetail)
        .ifPresent(reservoirProperty ->
            reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(newReservoirSubmissionDetail,
                reservoirProperty,
                ReservoirProperty.class)
        );

    var applicableInstances = new HashSet<ProbabilityDistributionFieldInstance>();

    var resourceType = newSubmissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      applicableInstances.addAll(List.of(
          ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT,
          ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT,
          ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT,
          ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT,
          ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT));

      if (newSubmissionDetail.isProspectSubmission()) {
        applicableInstances.addAll(List.of(
            ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT,
            ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT_PROSPECT));
      } else if (newSubmissionDetail.isSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE)
          && newSubmissionDetail.hasSameSubmissionType(oldSubmissionDetail)) {
        applicableInstances.addAll(List.of(
            ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL,
            ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY));
      }
    }

    if (resourceType == ResourceType.DISCOVERY) {
      applicableInstances.addAll(List.of(
          ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY,
          ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY,
          ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY,
          ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY,
          ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY,
          ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY,
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY,
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY,
          ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY));
    } else if (resourceType == ResourceType.FIELD) {
      applicableInstances.addAll(List.of(
          ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD,
          ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_FIELD,
          ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_FIELD,
          ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_FIELD,
          ProbabilityDistributionFieldInstance.NET_TO_GROSS_FIELD,
          ProbabilityDistributionFieldInstance.NET_POROSITY_FIELD,
          ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_FIELD,
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_FIELD,
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_FIELD,
          ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_OIL_CONTACT_FIELD,
          ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_WATER_CONTACT_FIELD,
          ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_OIL_WATER_CONTACT_FIELD,
          ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_OIL_CONTACT_FIELD,
          ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_WATER_CONTACT_FIELD,
          ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_OIL_WATER_CONTACT_FIELD
      ));
    }

    if (oldSubmissionDetail.hasSameResourceType(newSubmissionDetail)) {
      probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(
              oldReservoirSubmissionDetail,
              applicableInstances
          )
          .forEach(probabilityDistribution -> {
            //this ensures the correct probability field instance is saved, for the new submission type,
            //since the question is mandatory for dry hole submissions the not calculated option is not copied forward
            //unless its an update submission, then all information for dry wells is copied forward
            if (probabilityDistribution.getInstance() == ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT
                && newSubmissionDetail.isDiscoverySubmission()) {
              if (probabilityDistribution.getDistributionType() == ProbabilityDistributionType.NOT_CALCULATED) {
                return;
              }
              var newProbabilityDistribution =
                  reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(
                      newReservoirSubmissionDetail, probabilityDistribution, ProbabilityDistribution.class);
              newProbabilityDistribution.setInstance(
                  ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY);
            } else {
              reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(newReservoirSubmissionDetail,
                  probabilityDistribution,
                  ProbabilityDistribution.class);
            }
          });
    }
  }
}
