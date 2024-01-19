package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
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
    return gasCaseOutput.map(this::createSummaryViewFromGasCaseOutput)
        .orElse(GasCaseOutputsSummaryView.empty());
  }

  private GasCaseOutputsSummaryView createSummaryViewFromGasCaseOutput(GasCaseOutput gasCaseOutput) {
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    var reservoirSubmissionDetail = gasCaseOutput.getReservoirSubmissionDetail();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var hasAssociatedGasRecoverableResources = BooleanUtil.yesNoFromBoolean(
        gasCaseOutput.getHasAssociatedCondensateRecoverableResources(), "");

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT);

      listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT);

      if (gasCaseOutput.getHasAssociatedCondensateRecoverableResources() == null
          || gasCaseOutput.getHasAssociatedCondensateRecoverableResources()) {
        listOfInstances.add(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT);
      }

      listOfInstances.add(
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT);
    } else if (resourceType == ResourceType.DISCOVERY) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY);

      listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY);

      if (gasCaseOutput.getHasAssociatedCondensateRecoverableResources() == null
          || gasCaseOutput.getHasAssociatedCondensateRecoverableResources()) {
        listOfInstances.add(
            ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY);
      }

      listOfInstances.add(
          ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);
    }

    var listOfSummaryViews = new ArrayList<ProbabilityDistributionSummaryView>();
    listOfInstances.forEach(instance -> listOfSummaryViews.add(
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            instance, submissionDetail, reservoirSubmissionDetail)));
    return new GasCaseOutputsSummaryView(hasAssociatedGasRecoverableResources, listOfSummaryViews);
  }

  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail) {
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

    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY);

    gasCaseOutputsPersistenceService.getGasCaseOutput(reservoirSubmissionDetail)
        .ifPresent(gasCaseOutput -> {
          if (BooleanUtils.isTrue(gasCaseOutput.getHasAssociatedCondensateRecoverableResources()))  {
            listOfInstances.add(
                ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY);
          }
        });

    listOfInstances.add(ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);

    var listOfSummaryViews =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            listOfInstances, submissionDetail, reservoirSubmissionDetail);

    return SummaryItem.withCard(GAS_VOLUMETRICS, SummaryCard.probabilitySummaryCard(listOfSummaryViews));

  }
}