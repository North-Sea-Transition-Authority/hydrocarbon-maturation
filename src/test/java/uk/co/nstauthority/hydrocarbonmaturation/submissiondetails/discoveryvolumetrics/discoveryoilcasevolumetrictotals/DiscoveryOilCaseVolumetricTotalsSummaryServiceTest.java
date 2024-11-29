package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrictotals.OilCaseVolumetricTotal;

@ExtendWith(MockitoExtension.class)
class DiscoveryOilCaseVolumetricTotalsSummaryServiceTest {

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private DiscoveryOilCaseVolumetricTotalsService discoveryOilCaseVolumetricTotalsService;

  @InjectMocks
  private DiscoveryOilCaseVolumetricTotalsSummaryService discoveryOilCaseVolumetricTotalsSummaryService;

  @Test
  void getOilCaseVolumetricTotalsSummaryFromSubmissionDetail() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);

    when(discoveryOilCaseVolumetricTotalsService.getOilCaseVolumetricTotal(submissionDetail)).thenReturn(Optional.empty());

    var tableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, null))
        .thenReturn(tableView);

    assertThat(discoveryOilCaseVolumetricTotalsSummaryService.createSummaryView(
        submissionDetail))
        .extracting(
            DiscoveryOilCaseVolumetricTotalsSummaryView::useIndustryStandardConversion,
            DiscoveryOilCaseVolumetricTotalsSummaryView::tableView
        )
        .containsExactly(
            "",
            tableView
        );
  }

  @Test
  void getOilCaseVolumetricTotalsSummaryFromSubmissionDetail_useIndustryStandardConversion() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY);

    var oilCaseVolumetricTotal = new OilCaseVolumetricTotal();
    oilCaseVolumetricTotal.setUseIndustryStandardConversion(true);

    when(discoveryOilCaseVolumetricTotalsService.getOilCaseVolumetricTotal(submissionDetail))
        .thenReturn(Optional.of(oilCaseVolumetricTotal));

    var tableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, null))
        .thenReturn(tableView);

    assertThat(discoveryOilCaseVolumetricTotalsSummaryService.createSummaryView(
        submissionDetail))
        .extracting(
            DiscoveryOilCaseVolumetricTotalsSummaryView::useIndustryStandardConversion,
            DiscoveryOilCaseVolumetricTotalsSummaryView::tableView
        )
        .containsExactly(
            "Yes",
            tableView
        );
  }
}
