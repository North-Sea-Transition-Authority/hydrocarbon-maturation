package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals;

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
    when(discoveryGasCaseVolumetricTotalsService.getGasCaseVolumetricTotal(submissionDetail)).thenReturn(Optional.empty());
    assertThat(discoveryGasCaseVolumetricTotalsSummaryService.getGasCaseVolumetricTotalsSummaryFromSubmissionDetail(submissionDetail))
        .extracting(
            DiscoveryGasCaseVolumetricTotalsSummaryView::hasAssociatedCondensateRecoverableResources,
            DiscoveryGasCaseVolumetricTotalsSummaryView::probabilityDistributionSummaryViews
        )
        .containsExactly(
            "",
            List.of(
                ProbabilityDistributionSummaryView.empty("Gas initially in place"),
                ProbabilityDistributionSummaryView.empty("Gas recoverable resources"),
                ProbabilityDistributionSummaryView.empty("Associated condensate recoverable resources"),
                ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources")
            )
        );
  }

  @Test
  void getGasCaseVolumetricTotalsSummaryFromSubmissionDetail() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var gasCaseVolumetricTotal = new GasCaseVolumetricTotal();
    gasCaseVolumetricTotal.setSubmissionDetail(submissionDetail);
    gasCaseVolumetricTotal.setHasAssociatedCondensateRecoverableResources(true);
    when(discoveryGasCaseVolumetricTotalsService.getGasCaseVolumetricTotal(submissionDetail)).thenReturn(Optional.of(
        gasCaseVolumetricTotal
    ));
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY);
    var probabilityDistributionSummaryView1 = ProbabilityDistributionSummaryView.empty("test1");
    var probabilityDistributionSummaryView2 = ProbabilityDistributionSummaryView.empty("test2");
    var probabilityDistributionSummaryViews = List.of(probabilityDistributionSummaryView1, probabilityDistributionSummaryView2);
    when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
        listOfInstances,
        submissionDetail,
        null
    )).thenReturn(probabilityDistributionSummaryViews);
    assertThat(discoveryGasCaseVolumetricTotalsSummaryService.getGasCaseVolumetricTotalsSummaryFromSubmissionDetail(submissionDetail))
        .extracting(
            DiscoveryGasCaseVolumetricTotalsSummaryView::hasAssociatedCondensateRecoverableResources,
            DiscoveryGasCaseVolumetricTotalsSummaryView::probabilityDistributionSummaryViews
        )
        .containsExactly(
            BooleanUtil.yesNoFromBoolean(gasCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources()),
            probabilityDistributionSummaryViews
        );
  }
}
