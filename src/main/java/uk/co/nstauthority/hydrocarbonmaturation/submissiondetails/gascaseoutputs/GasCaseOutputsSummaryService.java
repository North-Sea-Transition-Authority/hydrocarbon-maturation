package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class GasCaseOutputsSummaryService implements SummarySectionService<ReservoirSubmissionDetail> {

  public static final String GAS_VOLUMETRICS = "Gas case volumetrics";
  public static final int GAS_VOLUMETRICS_DISPLAY_ORDER = 60;

  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;
  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public GasCaseOutputsSummaryService(ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
                                      GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService,
                                      GeneralReservoirInformationService generalReservoirInformationService) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.gasCaseOutputsPersistenceService = gasCaseOutputsPersistenceService;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  public GasCaseOutputsSummaryView getGasCaseOutputsSummaryFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var gasCaseOutput = gasCaseOutputsPersistenceService.getGasCaseOutput(reservoirSubmissionDetail);
    var instances = getInstances(reservoirSubmissionDetail);

    gasCaseOutput.ifPresent(caseOutput ->
        filterInstances(instances, reservoirSubmissionDetail.getSubmissionDetail(), caseOutput));

    var summaryBehaviour = probabilityDistributionSummaryService.getSummaryBehaviour(instances);

    return gasCaseOutput.map(this::createSummaryViewFromGasCaseOutput)
        .orElse(GasCaseOutputsSummaryView.empty(summaryBehaviour));
  }

  private GasCaseOutputsSummaryView createSummaryViewFromGasCaseOutput(GasCaseOutput gasCaseOutput) {
    var reservoirSubmissionDetail = gasCaseOutput.getReservoirSubmissionDetail();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var hasAssociatedGasRecoverableResources = BooleanUtil.yesNoFromBoolean(
        gasCaseOutput.getHasAssociatedCondensateRecoverableResources(), "");
    var useIndustryStandardConversion = BooleanUtil.yesNoFromBoolean(gasCaseOutput.getUseIndustryStandardConversion(), "");

    var listOfInstances = getInstances(reservoirSubmissionDetail);

    filterInstances(listOfInstances, submissionDetail, gasCaseOutput);

    var tableView = probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances,
        submissionDetail,
        reservoirSubmissionDetail
    );
    return new GasCaseOutputsSummaryView(hasAssociatedGasRecoverableResources, useIndustryStandardConversion, tableView);
  }

  private void filterInstances(
      List<ProbabilityDistributionFieldInstance> instances,
      SubmissionDetail submissionDetail,
      GasCaseOutput gasCaseOutput
  ) {
    if (submissionDetail.isProspectSubmission()) {
      if (BooleanUtils.isTrue(gasCaseOutput.getHasAssociatedCondensateRecoverableResources())) {
        instances.add(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT);
      }

      if (BooleanUtils.isTrue(gasCaseOutput.getUseIndustryStandardConversion())) {
        instances.remove(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT);
        instances.add(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_PROSPECT);
      }
    }

    if (submissionDetail.isDiscoverySubmission()) {
      if (BooleanUtils.isTrue(gasCaseOutput.getHasAssociatedCondensateRecoverableResources())) {
        instances.add(ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY);
      }

      if (BooleanUtils.isTrue(gasCaseOutput.getUseIndustryStandardConversion())) {
        instances.remove(ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);
        instances.add(
            ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY);
      }
    }
  }

  private static List<ProbabilityDistributionFieldInstance> getInstances(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    var resourceType = reservoirSubmissionDetail.getSubmissionDetail().getSubmission().getResource().getType();

    if (resourceType == ResourceType.PROSPECT) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT);
      listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT);
      listOfInstances.add(
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT);
    } else if (resourceType == ResourceType.DISCOVERY) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY);
      listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
      listOfInstances.add(ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);
    }

    return listOfInstances;
  }

  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail, User user) {
    if (!reservoirSubmissionDetail.getSubmissionDetail().isDiscoverySubmission()
        || !generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)) {
      return Optional.empty();
    }

    var summaryItems = List.of(
        getGasVolumetricsSummaryDiscovery(reservoirSubmissionDetail)
    );

    return Optional.of(new SummarySection(GAS_VOLUMETRICS_DISPLAY_ORDER, summaryItems, ""));
  }

  private SummaryItem getGasVolumetricsSummaryDiscovery(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var listOfInstances = getInstances(reservoirSubmissionDetail);

    gasCaseOutputsPersistenceService.getGasCaseOutput(reservoirSubmissionDetail)
        .ifPresent(gasCaseOutput -> filterInstances(listOfInstances, submissionDetail, gasCaseOutput));

    var tableView = probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    );

    return SummaryItem.withCard(GAS_VOLUMETRICS, SummaryCard.probabilitySummaryCard(tableView));

  }
}