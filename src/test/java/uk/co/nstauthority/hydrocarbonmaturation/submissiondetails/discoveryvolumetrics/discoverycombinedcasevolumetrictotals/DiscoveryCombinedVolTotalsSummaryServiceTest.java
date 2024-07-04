package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycombinedcasevolumetrictotals;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals.DiscoveryCombinedCaseVolTotalsService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals.DiscoveryCombinedCaseVolTotalsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals.DiscoveryCombinedVolTotalsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@ExtendWith(MockitoExtension.class)
class DiscoveryCombinedVolTotalsSummaryServiceTest {

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private DiscoveryCombinedCaseVolTotalsService discoveryCombinedCaseVolTotalsService;

  @InjectMocks
  private DiscoveryCombinedVolTotalsSummaryService discoveryCombinedVolTotalsSummaryService;

  @Test
  void getGasCaseVolumetricTotalsSummaryFromSubmissionDetail_noVolumetricTotals() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var summaryBehaviour = new SummaryBehaviour();
    when(discoveryCombinedCaseVolTotalsService.getCombinedCaseVolumetricTotal(submissionDetail))
        .thenReturn(Optional.empty());
    when(probabilityDistributionSummaryService.getSummaryBehaviour(
        DiscoveryCombinedVolTotalsSummaryService.ALL_INSTANCES))
        .thenReturn(summaryBehaviour);
    assertThat(discoveryCombinedVolTotalsSummaryService.getCombinedCaseVolumetricTotalsSummaryFromSubmissionDetail(
        submissionDetail))
        .extracting(
            DiscoveryCombinedCaseVolTotalsSummaryView::hasAssociatedCondensateRecoverableResources,
            DiscoveryCombinedCaseVolTotalsSummaryView::tableView
        )
        .containsExactly(
            "",
            new ProbabilityDistributionTableView(
                summaryBehaviour,
                List.of(
                    ProbabilityDistributionSummaryView.empty("Oil initially in place"),
                    ProbabilityDistributionSummaryView.empty("Oil recoverable resources"),
                    ProbabilityDistributionSummaryView.empty("Associated gas recoverable resources"),
                    ProbabilityDistributionSummaryView.empty("Gas initially in place"),
                    ProbabilityDistributionSummaryView.empty("Gas recoverable resources"),
                    ProbabilityDistributionSummaryView.empty("Associated condensate recoverable resources"),
                    ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources")
                )
            )
        );
  }

  @Test
  void getCombinedCaseVolumetricTotalsSummaryFromSubmissionDetail_hasAssociatedCondensate() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();

    var combinedCaseVolumetricTotal = new CombinedCaseVolumetricTotal();
    combinedCaseVolumetricTotal.setSubmissionDetail(submissionDetail);
    combinedCaseVolumetricTotal.setHasAssociatedCondensateRecoverableResources(true);

    var tableView = ProbabilityDistributionTableView.empty();

    when(discoveryCombinedCaseVolTotalsService.getCombinedCaseVolumetricTotal(submissionDetail))
        .thenReturn(Optional.of(combinedCaseVolumetricTotal));
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        DiscoveryCombinedVolTotalsSummaryService.ALL_INSTANCES, submissionDetail, null))
        .thenReturn(tableView);

    assertThat(discoveryCombinedVolTotalsSummaryService.getCombinedCaseVolumetricTotalsSummaryFromSubmissionDetail(
        submissionDetail))
        .extracting(
            DiscoveryCombinedCaseVolTotalsSummaryView::hasAssociatedCondensateRecoverableResources,
            DiscoveryCombinedCaseVolTotalsSummaryView::tableView
        )
        .containsExactly(
            BooleanUtil.yesNoFromBoolean(combinedCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources()),
            tableView
        );
  }

  @Test
  void getCombinedCaseVolumetricTotalsSummaryFromSubmissionDetail_doesntHaveAssociatedCondensate() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();

    var combinedCaseVolumetricTotal = new CombinedCaseVolumetricTotal();
    combinedCaseVolumetricTotal.setSubmissionDetail(submissionDetail);
    combinedCaseVolumetricTotal.setHasAssociatedCondensateRecoverableResources(false);

    when(discoveryCombinedCaseVolTotalsService.getCombinedCaseVolumetricTotal(submissionDetail)).thenReturn(Optional.of(
        combinedCaseVolumetricTotal
    ));

    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);

    var tableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, null))
        .thenReturn(tableView);

    assertThat(discoveryCombinedVolTotalsSummaryService.getCombinedCaseVolumetricTotalsSummaryFromSubmissionDetail(
        submissionDetail))
        .extracting(
            DiscoveryCombinedCaseVolTotalsSummaryView::hasAssociatedCondensateRecoverableResources,
            DiscoveryCombinedCaseVolTotalsSummaryView::tableView
        )
        .containsExactly(
            BooleanUtil.yesNoFromBoolean(combinedCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources()),
            tableView
        );
  }
}