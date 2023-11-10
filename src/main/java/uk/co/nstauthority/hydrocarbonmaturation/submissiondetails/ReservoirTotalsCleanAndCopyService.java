package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals.DiscoveryCombinedCaseVolTotalsService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals.DiscoveryGasCaseVolumetricTotalsService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverymixedcasevolumetrics.DiscoveryMixedCaseVolumetricsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TotalVolumesTaskListSectionService;

@Service
public class ReservoirTotalsCleanAndCopyService {

  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final GeneralReservoirInformationService generalReservoirInformationService;
  private final DiscoveryGasCaseVolumetricTotalsService discoveryGasCaseVolumetricTotalsService;
  private final GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;
  private final DiscoveryMixedCaseVolumetricsPersistenceService discoveryMixedCaseVolumetricsPersistenceService;
  private final DiscoveryCombinedCaseVolTotalsService discoveryCombinedCaseVolTotalsService;
  private final TotalVolumesTaskListSectionService totalVolumesTaskListSectionService;

  @Autowired
  public ReservoirTotalsCleanAndCopyService(
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      GeneralReservoirInformationService generalReservoirInformationService,
      DiscoveryGasCaseVolumetricTotalsService discoveryGasCaseVolumetricTotalsService,
      GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService,
      DiscoveryMixedCaseVolumetricsPersistenceService discoveryMixedCaseVolumetricsPersistenceService,
      DiscoveryCombinedCaseVolTotalsService discoveryCombinedCaseVolTotalsService,
      TotalVolumesTaskListSectionService totalVolumesTaskListSectionService) {

    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.generalReservoirInformationService = generalReservoirInformationService;
    this.discoveryGasCaseVolumetricTotalsService = discoveryGasCaseVolumetricTotalsService;
    this.gasCaseOutputsPersistenceService = gasCaseOutputsPersistenceService;
    this.discoveryMixedCaseVolumetricsPersistenceService = discoveryMixedCaseVolumetricsPersistenceService;
    this.discoveryCombinedCaseVolTotalsService = discoveryCombinedCaseVolTotalsService;
    this.totalVolumesTaskListSectionService = totalVolumesTaskListSectionService;
  }


  /** Clean any previously entered totals (can happen if more than one reservoir existed originally).
   * If there is only a single reservoir at the point of submission
   * then copy the probability information from that into the totals for the submission.
   * @param submissionDetail the submission detail being submitted
   */
  public void cleanAndCopyDiscoveryVolumetricTotals(SubmissionDetail submissionDetail) {
    if (!submissionDetail.isDiscoverySubmission()) {
      return;
    }

    var reservoirSubmissionDetails =
        reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail);
    var totals = List.of(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
    );

    if (reservoirSubmissionDetails.size() == 1) {
      // if single reservoir,  copy into totals
      // clean any previous totals saved first
      probabilityDistributionPersistenceService.deleteProbabilityDistributions(totals, submissionDetail);
      discoveryGasCaseVolumetricTotalsService.deleteGasCaseVolumetricTotalsForSubmissionDetail(submissionDetail);
      discoveryCombinedCaseVolTotalsService.deleteCombinedCaseVolumetricTotalsForSubmissionDetail(submissionDetail);

      var reservoirSubmissionDetail = reservoirSubmissionDetails.get(0);
      copyOil(reservoirSubmissionDetail, submissionDetail);
      copyGas(reservoirSubmissionDetail, submissionDetail);
      copyCommon(reservoirSubmissionDetail, submissionDetail);
    }  else if (totalVolumesTaskListSectionService.isGasCaseOnly(reservoirSubmissionDetails)) {
      // if gas only, clean any oil totals saved
      deleteOilOnlyDiscoveryTotals(submissionDetail);
    } else if (totalVolumesTaskListSectionService.isOilCaseOnly(reservoirSubmissionDetails)) {
      // if oil only, clean any gas totals saved
      deleteGasOnlyDiscoveryTotals(submissionDetail);
    }
    // combined case currently requires all totals so do not delete any totals
  }

  private void copyOil(ReservoirSubmissionDetail reservoirSubmissionDetail, SubmissionDetail submissionDetail) {
    if (generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)) {
      probabilityDistributionPersistenceService.copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          submissionDetail
      );
      probabilityDistributionPersistenceService.copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
      probabilityDistributionPersistenceService.copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
    }
  }

  private void copyGas(ReservoirSubmissionDetail reservoirSubmissionDetail, SubmissionDetail submissionDetail) {
    if (generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)) {
      probabilityDistributionPersistenceService.copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
          submissionDetail
      );
      probabilityDistributionPersistenceService.copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );

      Boolean hasAssociatedCondensate = null;
      if (generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)) {
        var gasCaseOutput = gasCaseOutputsPersistenceService.getGasCaseOutput(reservoirSubmissionDetail);
        hasAssociatedCondensate = gasCaseOutput
            .map(GasCaseOutput::getHasAssociatedCondensateRecoverableResources)
            .orElse(false);
        if (BooleanUtils.isTrue(hasAssociatedCondensate)) {
          var gasCaseVolumetricTotal = new GasCaseVolumetricTotal();
          gasCaseVolumetricTotal.setSubmissionDetail(submissionDetail);
          gasCaseVolumetricTotal.setHasAssociatedCondensateRecoverableResources(true);
          discoveryGasCaseVolumetricTotalsService.saveDiscoveryGasCaseVolumetricTotals(gasCaseVolumetricTotal);
        }

      } else if (generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)) {
        var mixedCaseOutput = discoveryMixedCaseVolumetricsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail);
        hasAssociatedCondensate = mixedCaseOutput
            .map(MixedCaseOutput::getHasAssociatedCondensateRecoverableResources)
            .orElse(false);
        if (BooleanUtils.isTrue(hasAssociatedCondensate)) {
          var combinedCaseVolumetricTotal = new CombinedCaseVolumetricTotal();
          combinedCaseVolumetricTotal.setSubmissionDetail(submissionDetail);
          combinedCaseVolumetricTotal.setHasAssociatedCondensateRecoverableResources(true);
          discoveryCombinedCaseVolTotalsService.saveDiscoveryCombinedCaseVolumetricTotals(combinedCaseVolumetricTotal);
        }
      }

      if (BooleanUtils.isTrue(hasAssociatedCondensate)) {
        probabilityDistributionPersistenceService.copyProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
            reservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
            submissionDetail
        );
      }
    }
  }

  private void copyCommon(ReservoirSubmissionDetail reservoirSubmissionDetail, SubmissionDetail submissionDetail) {
    probabilityDistributionPersistenceService.copyProbabilityDistribution(
        ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
        submissionDetail
    );
  }

  private void deleteOilOnlyDiscoveryTotals(SubmissionDetail submissionDetail) {
    List<ProbabilityDistributionFieldInstance> totals = List.of(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    );

    probabilityDistributionPersistenceService.deleteProbabilityDistributions(totals, submissionDetail);
  }

  private void deleteGasOnlyDiscoveryTotals(SubmissionDetail submissionDetail) {
    List<ProbabilityDistributionFieldInstance> totals = List.of(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    );

    probabilityDistributionPersistenceService.deleteProbabilityDistributions(totals, submissionDetail);
  }
}
