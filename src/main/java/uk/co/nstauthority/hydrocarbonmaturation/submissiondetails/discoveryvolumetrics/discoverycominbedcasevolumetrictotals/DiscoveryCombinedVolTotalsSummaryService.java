package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class DiscoveryCombinedVolTotalsSummaryService {

  public static final List<ProbabilityDistributionFieldInstance> ALL_INSTANCES = List.of(
      ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
      ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
      ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
      ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
      ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
      ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
      ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
  );

  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final DiscoveryCombinedCaseVolTotalsService discoveryCombinedCaseVolTotalsService;

  @Autowired
  public DiscoveryCombinedVolTotalsSummaryService(
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
      DiscoveryCombinedCaseVolTotalsService discoveryCombinedCaseVolTotalsService) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.discoveryCombinedCaseVolTotalsService = discoveryCombinedCaseVolTotalsService;
  }

  public DiscoveryCombinedCaseVolTotalsSummaryView getCombinedCaseVolumetricTotalsSummaryFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    var summaryBehaviour = probabilityDistributionSummaryService.getSummaryBehaviour(ALL_INSTANCES);
    var combinedCaseVolumetricTotal = discoveryCombinedCaseVolTotalsService
        .getCombinedCaseVolumetricTotal(submissionDetail);

    return combinedCaseVolumetricTotal.map(this::createSummaryViewFromCombinedCaseVolumetricTotal)
        .orElse(DiscoveryCombinedCaseVolTotalsSummaryView.empty(summaryBehaviour));
  }

  private DiscoveryCombinedCaseVolTotalsSummaryView createSummaryViewFromCombinedCaseVolumetricTotal(
      CombinedCaseVolumetricTotal combinedCaseVolumetricTotal) {
    var listOfInstances = getBaseInstances();
    var submissionDetail = combinedCaseVolumetricTotal.getSubmissionDetail();

    var hasAssociatedCondensateRecoverableResources = BooleanUtil.yesNoFromBoolean(
        combinedCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources(), "");
    if (BooleanUtils.isTrue(combinedCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources())) {
      listOfInstances.add(
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY);
    }
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);

    var tableView = probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances,
        submissionDetail,
        null
    );
    return new DiscoveryCombinedCaseVolTotalsSummaryView(hasAssociatedCondensateRecoverableResources, tableView);
  }

  private List<ProbabilityDistributionFieldInstance> getBaseInstances() {
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();

    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY);

    return listOfInstances;
  }
}