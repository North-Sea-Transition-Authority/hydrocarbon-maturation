package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class FieldReservoirVolumetricsDuplicationService implements ReservoirSectionDuplicationService {

  private final GeneralReservoirInformationService generalReservoirInformationService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Autowired
  public FieldReservoirVolumetricsDuplicationService(
      GeneralReservoirInformationService generalReservoirInformationService,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService) {

    this.generalReservoirInformationService = generalReservoirInformationService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.reservoirDuplicationPersistenceService = reservoirDuplicationPersistenceService;
  }

  @Override
  public void duplicateReservoirSection(ReservoirSubmissionDetail oldReservoirSubmissionDetail,
                                        ReservoirSubmissionDetail newReservoirSubmissionDetail) {
    var oldSubmissionDetail = oldReservoirSubmissionDetail.getSubmissionDetail();
    var newSubmissionDetail = newReservoirSubmissionDetail.getSubmissionDetail();

    if (!newSubmissionDetail.isFieldSubmission()) {
      return;
    }

    if (oldSubmissionDetail.hasSameResourceType(newSubmissionDetail)) {
      var applicableInstances = new java.util.HashSet<ProbabilityDistributionFieldInstance>(Set.of());
      var newReservoirHydrocarbons = generalReservoirInformationService
          .getReservoirHydrocarbons(newReservoirSubmissionDetail)
          .orElse(new ReservoirHydrocarbons());
      if (generalReservoirInformationService.isOilOnly(newReservoirHydrocarbons)
          || generalReservoirInformationService.isCombinedOilAndGas(newReservoirHydrocarbons)) {
        applicableInstances.add(ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD);
      } else if (generalReservoirInformationService.isGasOnly(newReservoirHydrocarbons)) {
        applicableInstances.add(ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD);
      }

      if (generalReservoirInformationService.isGasOnly(newReservoirHydrocarbons)
          || generalReservoirInformationService.isCombinedOilAndGas(newReservoirHydrocarbons)) {
        applicableInstances.add(ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD);
      }

      probabilityDistributionPersistenceService
          .getAllProbabilityDistributionsForInstances(oldReservoirSubmissionDetail, applicableInstances)
          .forEach(probabilityDistribution -> reservoirDuplicationPersistenceService
              .duplicateChildAndReplaceParent(
                  newReservoirSubmissionDetail,
                  probabilityDistribution,
                  ProbabilityDistribution.class)
          );

      probabilityDistributionPersistenceService
          .getAllProbabilityDistributionsForInstances(newReservoirSubmissionDetail, applicableInstances)
          .forEach(probabilityDistribution -> probabilityDistribution.setSubmissionDetail(newSubmissionDetail));
    }
  }
}
