package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.HashSet;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class ReservoirPropertiesDuplicationService implements SubmissionSectionDuplicationService {
  private final ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  ReservoirPropertiesDuplicationService(ReservoirPropertiesPersistenceService chanceOfSuccessPersistenceService,
                                        ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
                                        SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.reservoirPropertiesPersistenceService = chanceOfSuccessPersistenceService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    reservoirPropertiesPersistenceService.getReservoirProperty(oldSubmissionDetail)
        .ifPresent(reservoirProperty ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, reservoirProperty,
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
            ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT));
      } else if (newSubmissionDetail.isDryWellSubmission()
          && oldSubmissionDetail.hasSameSubmissionType(newSubmissionDetail)) {
        applicableInstances.addAll(List.of(
            ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL,
            ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT));
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
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT,
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT,
          ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT));
    }

    if (oldSubmissionDetail.hasSameResourceType(newSubmissionDetail)) {
      probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(
              oldSubmissionDetail,
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
                  submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
                      newSubmissionDetail, probabilityDistribution, ProbabilityDistribution.class);
              newProbabilityDistribution.setInstance(
                  ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY);
            } else {
              submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail,
                  probabilityDistribution,
                  ProbabilityDistribution.class);
            }
          });
    }
  }
}
