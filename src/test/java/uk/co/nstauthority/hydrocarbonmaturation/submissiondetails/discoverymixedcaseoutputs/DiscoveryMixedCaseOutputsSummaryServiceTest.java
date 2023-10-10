package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverymixedcaseoutputs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummaryTestUtil.assertSummarySection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;

@ExtendWith(MockitoExtension.class)
class DiscoveryMixedCaseOutputsSummaryServiceTest {

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private DiscoveryMixedCaseOutputsSummaryService discoveryMixedCaseOutputsSummaryService;

  @Test
  void getSummarySection_whenMixedCase() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)).thenReturn(
        Optional.of(reservoirHydrocarbons)
    );
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);

    var probabilityDistributionSummaryView1 = ProbabilityDistributionSummaryView.empty("test1");
    var probabilityDistributionSummaryView2 = ProbabilityDistributionSummaryView.empty("test2");
    List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews = List.of(
        probabilityDistributionSummaryView1,
        probabilityDistributionSummaryView2
    );

    when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityDistributionSummaryViews);

    var summarySection = discoveryMixedCaseOutputsSummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), DiscoveryMixedCaseOutputsSummaryService.OIL_AND_GAS_CASE_OUTPUTS_DISPLAY_ORDER);
    var summaryCard = SummaryCard.probabilitySummaryCard(
        probabilityDistributionSummaryViews
    );
    var summaryItem = SummaryItem.withCard(DiscoveryMixedCaseOutputsSummaryService.OIL_AND_GAS_CASE_OUTPUTS, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @ParameterizedTest
  @EnumSource(value = HydrocarbonsDiscovered.class, names = {"GAS_ONLY", "OIL_ONLY"})
  void getSummarySection_notMixedCase(HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    reservoirHydrocarbons.setHydrocarbonsDiscovered(hydrocarbonsDiscovered);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)).thenReturn(
        Optional.of(reservoirHydrocarbons)
    );

    var summarySection = discoveryMixedCaseOutputsSummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isEmpty();
  }

  @Test
  void getSummarySection_noHydrocarbonsDiscovered() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)).thenReturn(
        Optional.empty()
    );

    var summarySection = discoveryMixedCaseOutputsSummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isEmpty();
  }
}
