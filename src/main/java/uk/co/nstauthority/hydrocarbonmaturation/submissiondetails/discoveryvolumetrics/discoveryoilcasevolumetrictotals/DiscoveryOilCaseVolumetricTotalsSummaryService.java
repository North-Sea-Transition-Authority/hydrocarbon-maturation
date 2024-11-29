package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals;

import java.util.ArrayList;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrictotals.OilCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class DiscoveryOilCaseVolumetricTotalsSummaryService {

  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final DiscoveryOilCaseVolumetricTotalsService discoveryOilCaseVolumetricTotalsService;

  @Autowired
  public DiscoveryOilCaseVolumetricTotalsSummaryService(
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
      DiscoveryOilCaseVolumetricTotalsService discoveryOilCaseVolumetricTotalsService
  ) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.discoveryOilCaseVolumetricTotalsService = discoveryOilCaseVolumetricTotalsService;
  }

  public DiscoveryOilCaseVolumetricTotalsSummaryView createSummaryView(SubmissionDetail submissionDetail) {
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();

    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY);

    var useIndustryStandardConversion = discoveryOilCaseVolumetricTotalsService.getOilCaseVolumetricTotal(submissionDetail)
        .map(OilCaseVolumetricTotal::getUseIndustryStandardConversion)
        .orElse(null);

    var useIndustryStandardConversionString = BooleanUtil.yesNoFromBoolean(useIndustryStandardConversion, "");

    if (BooleanUtils.isTrue(useIndustryStandardConversion)) {
      listOfInstances.add(
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY);
    } else {
      listOfInstances.add(
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);
    }

    var tableView = probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances,
        submissionDetail,
        null
    );

    return new DiscoveryOilCaseVolumetricTotalsSummaryView(
        useIndustryStandardConversionString,
        tableView
    );
  }
}
