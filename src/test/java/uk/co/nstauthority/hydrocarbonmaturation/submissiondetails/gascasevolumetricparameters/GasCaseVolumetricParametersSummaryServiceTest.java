package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummaryTestUtil.assertSummarySection;

import java.util.ArrayList;
import java.util.List;
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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;

@ExtendWith(MockitoExtension.class)
class GasCaseVolumetricParametersSummaryServiceTest {

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private GasCaseVolumetricParametersSummaryService gasCaseVolumetricParametersSummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void getGasCaseVolumetricsSummaryView_emptySummaries() {
    var emptySummary = ProbabilityDistributionSummaryView.empty("empty");

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(submissionDetail), eq(reservoirSubmissionDetail)))
        .thenReturn(emptySummary);

    var gasCaseVolumetricsSummaryView = gasCaseVolumetricParametersSummaryService.getGasCaseVolumetricsSummaryView(
        reservoirSubmissionDetail);

    assertThat(gasCaseVolumetricsSummaryView)
        .extracting(
            ProbabilityDistributionsFormSummaryView::probabilityDistributionSummaryViews)
        .isEqualTo(
            List.of(
                emptySummary,
                emptySummary,
                emptySummary,
                emptySummary)
        );
  }

  @Test
  void getGasCaseVolumetricsSummaryView_whenProspect_filledSummaries() {
    var testSummary = new ProbabilityDistributionSummaryView(
        "test", "type", "20",
        "20", "20", "20", "20", "20", "20", "20", "20", "20",
        "20", "20", "unit", "", "5", "5", "5", "comments");

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(submissionDetail), eq(reservoirSubmissionDetail)))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_PROSPECT,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_PROSPECT,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(testSummary);

    var gasCaseVolumetricsSummaryView = gasCaseVolumetricParametersSummaryService.getGasCaseVolumetricsSummaryView(
        reservoirSubmissionDetail);

    assertThat(gasCaseVolumetricsSummaryView)
        .extracting(
            ProbabilityDistributionsFormSummaryView::probabilityDistributionSummaryViews)
        .isEqualTo(
            List.of(
                testSummary,
                testSummary,
                testSummary,
                testSummary)
        );
  }

  @Test
  void getGasCaseVolumetricsSummaryView_whenDiscovery_filledSummaries() {
    var testSummary = new ProbabilityDistributionSummaryView(
        "test", "type", "20",
        "20", "20", "20", "20", "20", "20", "20", "20", "20",
        "20", "20", "unit", "", "5", "5", "5", "comments");
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(discoverySubmissionDetail), eq(discoveryReservoirSubmissionDetail)))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(testSummary);

    var gasCaseVolumetricsSummaryView = gasCaseVolumetricParametersSummaryService.getGasCaseVolumetricsSummaryView(
        discoveryReservoirSubmissionDetail);

    assertThat(gasCaseVolumetricsSummaryView)
        .extracting(
            ProbabilityDistributionsFormSummaryView::probabilityDistributionSummaryViews)
        .isEqualTo(
            List.of(
                testSummary,
                testSummary,
                testSummary,
                testSummary)
        );
  }

  @Test
  void getSummarySection_whenNotGasCase() {
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(false);

    assertThat(gasCaseVolumetricParametersSummaryService.getSummarySection(reservoirSubmissionDetail))
        .isEmpty();
  }

  @ParameterizedTest
  @EnumSource(
      value = HydrocarbonsDiscovered.class,
      names = {"GAS_ONLY", "MIXED_OIL_AND_GAS"},
      mode = EnumSource.Mode.INCLUDE
  )
  void getSummarySection_whenValidCase() {
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(true);

    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR);

    var probabilityDistributionSummaryView1 = ProbabilityDistributionSummaryView.empty("test1");
    var probabilityDistributionSummaryView2 = ProbabilityDistributionSummaryView.empty("test2");
    List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews = List.of(
        probabilityDistributionSummaryView1,
        probabilityDistributionSummaryView2
    );

    when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityDistributionSummaryViews);

    var summarySection = gasCaseVolumetricParametersSummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), GasCaseVolumetricParametersSummaryService.GAS_CASE_VOLUMETRIC_PARAMETERS_DISPLAY_ORDER);
    var summaryCard = SummaryCard.probabilitySummaryCard(
        probabilityDistributionSummaryViews
    );
    var summaryItem = SummaryItem.withCard(GasCaseVolumetricParametersSummaryService.GAS_CASE_VOLUMETRIC_PARAMETERS, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }
}