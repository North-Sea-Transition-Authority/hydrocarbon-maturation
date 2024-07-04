package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverymixedcasevolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummaryTestUtil.assertSummarySection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;

@ExtendWith(MockitoExtension.class)
class DiscoveryMixedCaseVolumetricsSummaryServiceTest {

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @Mock
  private DiscoveryMixedCaseVolumetricsPersistenceService discoveryMixedCaseVolumetricsPersistenceService;

  @InjectMocks
  private DiscoveryMixedCaseVolumetricsSummaryService discoveryMixedCaseVolumetricsSummaryService;

  @Test
  void getSummarySection_whenMixedCase_withNoAssociatedCondensate() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        SubmissionType.CREATE_DISCOVERY);
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)).thenReturn(
        Optional.of(reservoirHydrocarbons)
    );
    var mixedCaseOutput = mock(MixedCaseOutput.class);
    when(discoveryMixedCaseVolumetricsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));
    when(mixedCaseOutput.getHasAssociatedCondensateRecoverableResources())
        .thenReturn(false);
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);

    var tableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail))
        .thenReturn(tableView);

    var summarySection = discoveryMixedCaseVolumetricsSummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(),
        DiscoveryMixedCaseVolumetricsSummaryService.OIL_AND_GAS_CASE_OUTPUTS_DISPLAY_ORDER);

    var summaryCard = SummaryCard.probabilitySummaryCard(tableView);
    var summaryItem = SummaryItem.withCard(DiscoveryMixedCaseVolumetricsSummaryService.OIL_AND_GAS_CASE_OUTPUTS,
        summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @Test
  void getSummarySection_whenMixedCase_notAnsweredAssociatedCondensate_assertProperties() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        SubmissionType.CREATE_DISCOVERY);
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)).thenReturn(
        Optional.of(reservoirHydrocarbons)
    );

    when(discoveryMixedCaseVolumetricsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);

    var tableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail))
        .thenReturn(tableView);

    var summarySection = discoveryMixedCaseVolumetricsSummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(),
        DiscoveryMixedCaseVolumetricsSummaryService.OIL_AND_GAS_CASE_OUTPUTS_DISPLAY_ORDER);
    var summaryCard = SummaryCard.probabilitySummaryCard(
        tableView
    );
    var summaryItem = SummaryItem.withCard(DiscoveryMixedCaseVolumetricsSummaryService.OIL_AND_GAS_CASE_OUTPUTS,
        summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @Test
  void getSummarySection_whenMixedCase_withAssociatedCondensate() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        SubmissionType.CREATE_DISCOVERY);
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)).thenReturn(
        Optional.of(reservoirHydrocarbons)
    );
    var mixedCaseOutput = mock(MixedCaseOutput.class);
    when(discoveryMixedCaseVolumetricsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));
    when(mixedCaseOutput.getHasAssociatedCondensateRecoverableResources())
        .thenReturn(true);

    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(
        ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);

    var tableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail))
        .thenReturn(tableView);

    var summarySection = discoveryMixedCaseVolumetricsSummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(),
        DiscoveryMixedCaseVolumetricsSummaryService.OIL_AND_GAS_CASE_OUTPUTS_DISPLAY_ORDER);

    var summaryCard = SummaryCard.probabilitySummaryCard(tableView);
    var summaryItem = SummaryItem.withCard(DiscoveryMixedCaseVolumetricsSummaryService.OIL_AND_GAS_CASE_OUTPUTS,
        summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @ParameterizedTest
  @EnumSource(value = HydrocarbonsDiscovered.class, names = {"GAS_ONLY", "OIL_ONLY"})
  void getSummarySection_notMixedCase(HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        SubmissionType.CREATE_DISCOVERY);
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    reservoirHydrocarbons.setHydrocarbonsDiscovered(hydrocarbonsDiscovered);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)).thenReturn(
        Optional.of(reservoirHydrocarbons)
    );

    var summarySection = discoveryMixedCaseVolumetricsSummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isEmpty();
  }

  @Test
  void getSummarySection_noHydrocarbonsDiscovered() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        SubmissionType.CREATE_DISCOVERY);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)).thenReturn(
        Optional.empty()
    );

    var summarySection = discoveryMixedCaseVolumetricsSummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("allNonDiscoverySubmissionTypes")
  void getSummarySection_nonDiscoverySubmission_assertEmpty(SubmissionType submissionType) {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionType);

    var summarySection = discoveryMixedCaseVolumetricsSummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isEmpty();
  }

  private static Stream<Arguments> allNonDiscoverySubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isDiscoverySubmission(submissionType))
        .map(Arguments::of);
  }
}
