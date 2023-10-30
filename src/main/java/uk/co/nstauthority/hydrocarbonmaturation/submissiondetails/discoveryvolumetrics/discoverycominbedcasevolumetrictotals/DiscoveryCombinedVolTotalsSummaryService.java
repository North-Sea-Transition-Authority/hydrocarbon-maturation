package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals;

import java.util.ArrayList;
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
    var combinedCaseVolumetricTotal =
        discoveryCombinedCaseVolTotalsService.getCombinedCaseVolumetricTotal(submissionDetail);
    return combinedCaseVolumetricTotal.map(this::createSummaryViewFromCombinedCaseVolumetricTotal)
        .orElse(DiscoveryCombinedCaseVolTotalsSummaryView.empty());
  }

  private DiscoveryCombinedCaseVolTotalsSummaryView createSummaryViewFromCombinedCaseVolumetricTotal(
      CombinedCaseVolumetricTotal combinedCaseVolumetricTotal) {
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    var submissionDetail = combinedCaseVolumetricTotal.getSubmissionDetail();

    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    var hasAssociatedCondensateRecoverableResources = BooleanUtil.yesNoFromBoolean(
        combinedCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources(), "");
    if (BooleanUtils.isTrue(combinedCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources())) {
      listOfInstances.add(
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY);
    }
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);

    var listOfSummaryViews =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            listOfInstances,
            submissionDetail,
            null
        );
    return new DiscoveryCombinedCaseVolTotalsSummaryView(hasAssociatedCondensateRecoverableResources, listOfSummaryViews);
  }
}