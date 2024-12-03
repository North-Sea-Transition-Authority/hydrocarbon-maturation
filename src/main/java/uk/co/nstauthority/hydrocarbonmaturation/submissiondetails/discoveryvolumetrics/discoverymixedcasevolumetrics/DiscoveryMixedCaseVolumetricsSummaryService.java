package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverymixedcasevolumetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class DiscoveryMixedCaseVolumetricsSummaryService implements SummarySectionService<ReservoirSubmissionDetail> {

  public static final String OIL_AND_GAS_CASE_OUTPUTS = "Oil and gas case volumetrics";
  public static final int OIL_AND_GAS_CASE_OUTPUTS_DISPLAY_ORDER = 60;

  private final GeneralReservoirInformationService generalReservoirInformationService;
  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final DiscoveryMixedCaseVolumetricsPersistenceService discoveryMixedCaseVolumetricsPersistenceService;

  @Autowired
  public DiscoveryMixedCaseVolumetricsSummaryService(
      GeneralReservoirInformationService generalReservoirInformationService,
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
      DiscoveryMixedCaseVolumetricsPersistenceService discoveryMixedCaseVolumetricsPersistenceService) {
    this.generalReservoirInformationService = generalReservoirInformationService;
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.discoveryMixedCaseVolumetricsPersistenceService = discoveryMixedCaseVolumetricsPersistenceService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail, User user) {
    if (!reservoirSubmissionDetail.getSubmissionDetail().isDiscoverySubmission()) {
      return Optional.empty();
    }

    var hydrocarbonsDiscovered = generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail);
    if (hydrocarbonsDiscovered.isEmpty()
        || hydrocarbonsDiscovered.get().getHydrocarbonsDiscovered() != HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      return Optional.empty();
    }
    var summaryItems = List.of(getMixedCaseOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail));
    return Optional.of(new SummarySection(OIL_AND_GAS_CASE_OUTPUTS_DISPLAY_ORDER, summaryItems, ""));
  }

  private SummaryItem getMixedCaseOutputsSummaryFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail
  ) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);

    discoveryMixedCaseVolumetricsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail)
        .ifPresent(mixedCaseOutput -> {
          if (BooleanUtils.isTrue(mixedCaseOutput.getHasAssociatedCondensateRecoverableResources())) {
            listOfInstances.add(
                ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY);
          }
          if (BooleanUtils.isTrue(mixedCaseOutput.getUseIndustryStandardConversion())) {
            listOfInstances.remove(ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);
            listOfInstances.add(
                ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY);
          }
        });

    var tableView = probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    );

    return SummaryItem.withCard(OIL_AND_GAS_CASE_OUTPUTS, SummaryCard.probabilitySummaryCard(tableView));
  }
}