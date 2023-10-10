package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverymixedcaseoutputs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
public class DiscoveryMixedCaseOutputsSummaryService implements SummarySectionService<ReservoirSubmissionDetail> {

  public static final String OIL_AND_GAS_CASE_OUTPUTS = "Oil and gas case outputs (resources)";
  public static final int OIL_AND_GAS_CASE_OUTPUTS_DISPLAY_ORDER = 60;

  private final GeneralReservoirInformationService generalReservoirInformationService;
  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Autowired
  public DiscoveryMixedCaseOutputsSummaryService(
      GeneralReservoirInformationService generalReservoirInformationService,
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService) {
    this.generalReservoirInformationService = generalReservoirInformationService;
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail) {
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
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);

    var listOfSummaryViews =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            listOfInstances, submissionDetail, reservoirSubmissionDetail
        );

    var summaryCard = SummaryCard.probabilitySummaryCard(
        listOfSummaryViews
    );
    return SummaryItem.withCard(OIL_AND_GAS_CASE_OUTPUTS, summaryCard);
  }
}