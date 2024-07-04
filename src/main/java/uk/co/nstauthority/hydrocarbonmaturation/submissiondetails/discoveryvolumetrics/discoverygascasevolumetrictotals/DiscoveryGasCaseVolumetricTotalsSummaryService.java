package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class DiscoveryGasCaseVolumetricTotalsSummaryService {

  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final DiscoveryGasCaseVolumetricTotalsService discoveryGasCaseVolumetricTotalsService;

  @Autowired
  public DiscoveryGasCaseVolumetricTotalsSummaryService(
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
      DiscoveryGasCaseVolumetricTotalsService discoveryGasCaseVolumetricTotalsService) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.discoveryGasCaseVolumetricTotalsService = discoveryGasCaseVolumetricTotalsService;
  }

  public DiscoveryGasCaseVolumetricTotalsSummaryView getGasCaseVolumetricTotalsSummaryFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    var instances = getInstances();
    instances.add(
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY);

    var summaryBehaviour = probabilityDistributionSummaryService.getSummaryBehaviour(instances);
    var gasCaseOutput = discoveryGasCaseVolumetricTotalsService.getGasCaseVolumetricTotal(submissionDetail);

    return gasCaseOutput.map(this::createSummaryViewFromGasCaseVolumetricTotal)
        .orElse(DiscoveryGasCaseVolumetricTotalsSummaryView.empty(summaryBehaviour));
  }

  private DiscoveryGasCaseVolumetricTotalsSummaryView createSummaryViewFromGasCaseVolumetricTotal(
      GasCaseVolumetricTotal gasCaseVolumetricTotal) {
    var listOfInstances = getInstances();
    var submissionDetail = gasCaseVolumetricTotal.getSubmissionDetail();
    var hasAssociatedGasRecoverableResources = BooleanUtil.yesNoFromBoolean(
        gasCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources(), "");

    if (BooleanUtils.isTrue(gasCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources())) {
      listOfInstances.add(
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY);
    }

    var tableView = probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, null
    );
    return new DiscoveryGasCaseVolumetricTotalsSummaryView(hasAssociatedGasRecoverableResources, tableView);
  }

  private List<ProbabilityDistributionFieldInstance> getInstances() {
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();

    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);

    return listOfInstances;
  }
}