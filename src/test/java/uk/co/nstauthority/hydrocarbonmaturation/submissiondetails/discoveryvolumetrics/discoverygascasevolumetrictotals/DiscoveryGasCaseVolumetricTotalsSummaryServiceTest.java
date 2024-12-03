package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.SummaryBehaviour;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@ExtendWith(MockitoExtension.class)
class DiscoveryGasCaseVolumetricTotalsSummaryServiceTest {

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private DiscoveryGasCaseVolumetricTotalsService discoveryGasCaseVolumetricTotalsService;

  @InjectMocks
  private DiscoveryGasCaseVolumetricTotalsSummaryService discoveryGasCaseVolumetricTotalsSummaryService;

  @Test
  void getGasCaseVolumetricTotalsSummaryFromSubmissionDetail_noVolumetricTotals() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();

    var listOfInstances = List.of(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
    );

    var summaryBehaviour = new SummaryBehaviour();
    when(probabilityDistributionSummaryService.getSummaryBehaviour(listOfInstances))
        .thenReturn(summaryBehaviour);
    when(discoveryGasCaseVolumetricTotalsService.getGasCaseVolumetricTotal(submissionDetail))
        .thenReturn(Optional.empty());

    assertThat(discoveryGasCaseVolumetricTotalsSummaryService.getGasCaseVolumetricTotalsSummaryFromSubmissionDetail(
        submissionDetail))
        .extracting(
            DiscoveryGasCaseVolumetricTotalsSummaryView::hasAssociatedCondensateRecoverableResources,
            DiscoveryGasCaseVolumetricTotalsSummaryView::useIndustryStandardConversion,
            DiscoveryGasCaseVolumetricTotalsSummaryView::tableView
        )
        .containsExactly(
            "",
            "",
            new ProbabilityDistributionTableView(
                summaryBehaviour,
                List.of(
                    ProbabilityDistributionSummaryView.empty("Gas initially in place"),
                    ProbabilityDistributionSummaryView.empty("Gas recoverable resources"),
                    ProbabilityDistributionSummaryView.empty("Associated condensate recoverable resources"),
                    ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources")
                )
            )
        );
  }

  @Test
  void getGasCaseVolumetricTotalsSummaryFromSubmissionDetail() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();

    var gasCaseVolumetricTotal = new GasCaseVolumetricTotal();
    gasCaseVolumetricTotal.setSubmissionDetail(submissionDetail);
    gasCaseVolumetricTotal.setHasAssociatedCondensateRecoverableResources(true);
    gasCaseVolumetricTotal.setUseIndustryStandardConversion(true);
    when(discoveryGasCaseVolumetricTotalsService.getGasCaseVolumetricTotal(submissionDetail))
        .thenReturn(Optional.of(gasCaseVolumetricTotal));

    var listOfInstances = List.of(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY
    );

    var tableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, null))
        .thenReturn(tableView);

    assertThat(discoveryGasCaseVolumetricTotalsSummaryService.getGasCaseVolumetricTotalsSummaryFromSubmissionDetail(
        submissionDetail))
        .extracting(
            DiscoveryGasCaseVolumetricTotalsSummaryView::hasAssociatedCondensateRecoverableResources,
            DiscoveryGasCaseVolumetricTotalsSummaryView::useIndustryStandardConversion,
            DiscoveryGasCaseVolumetricTotalsSummaryView::tableView
        )
        .containsExactly(
            BooleanUtil.yesNoFromBoolean(gasCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources()),
            BooleanUtil.yesNoFromBoolean(gasCaseVolumetricTotal.getUseIndustryStandardConversion()),
            tableView
        );
  }
}
