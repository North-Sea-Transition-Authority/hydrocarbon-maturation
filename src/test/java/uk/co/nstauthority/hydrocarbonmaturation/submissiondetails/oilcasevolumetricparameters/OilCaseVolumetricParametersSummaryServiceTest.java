package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters;

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
class OilCaseVolumetricParametersSummaryServiceTest {

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private OilCaseVolumetricParametersSummaryService oilCaseVolumetricParametersSummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void getOilCaseVolumetricsSummaryView_emptySummaries() {
    var emptySummary = ProbabilityDistributionSummaryView.empty("empty");

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(submissionDetail), eq(reservoirSubmissionDetail)))
        .thenReturn(emptySummary);

    var oilCaseVolumetricsSummaryView = oilCaseVolumetricParametersSummaryService.getOilCaseVolumetricsSummaryView(
        reservoirSubmissionDetail);

    assertThat(oilCaseVolumetricsSummaryView)
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
  void getOilCaseVolumetricsSummaryView_whenProspect_filledSummaries() {
    var testSummary = new ProbabilityDistributionSummaryView(
        "test", "type", "20",
        "20", "20", "20", "20", "20", "20", "20", "20", "20",
        "20", "", "20", "unit", "comments");

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(submissionDetail), eq(reservoirSubmissionDetail)))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_PROSPECT,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_PROSPECT,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_PROSPECT,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(testSummary);

    var oilCaseVolumetricsSummaryView = oilCaseVolumetricParametersSummaryService.getOilCaseVolumetricsSummaryView(
        reservoirSubmissionDetail);

    assertThat(oilCaseVolumetricsSummaryView)
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
  void getOilCaseVolumetricsSummaryView_whenDiscovery_filledSummaries() {
    var testSummary = new ProbabilityDistributionSummaryView(
        "test", "type", "20",
        "20", "20", "20", "20", "20", "20", "20", "20", "20",
        "20", "", "20", "unit", "comments");
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(discoverySubmissionDetail), eq(discoveryReservoirSubmissionDetail)))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_DISCOVERY,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_DISCOVERY,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_DISCOVERY,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(testSummary);

    var oilCaseVolumetricsSummaryView = oilCaseVolumetricParametersSummaryService.getOilCaseVolumetricsSummaryView(
        discoveryReservoirSubmissionDetail);

    assertThat(oilCaseVolumetricsSummaryView)
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
  void getSummarySection_whenNotOilCase() {
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(false);

    assertThat(oilCaseVolumetricParametersSummaryService.getSummarySection(reservoirSubmissionDetail))
        .isEmpty();
  }

  @ParameterizedTest
  @EnumSource(
      value = HydrocarbonsDiscovered.class,
      names = {"OIL_ONLY", "MIXED_OIL_AND_GAS"},
      mode = EnumSource.Mode.INCLUDE
  )
  void getSummarySection_whenOilCase() {
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(true);

    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_DISCOVERY);

    var probabilityDistributionSummaryView1 = ProbabilityDistributionSummaryView.empty("test1");
    var probabilityDistributionSummaryView2 = ProbabilityDistributionSummaryView.empty("test2");
    List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews = List.of(
        probabilityDistributionSummaryView1,
        probabilityDistributionSummaryView2
    );

    when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityDistributionSummaryViews);

    var summarySection = oilCaseVolumetricParametersSummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), OilCaseVolumetricParametersSummaryService.OIL_CASE_VOLUMETRIC_PARAMETERS_DISPLAY_ORDER);
    var summaryCard = SummaryCard.probabilitySummaryCard(
        probabilityDistributionSummaryViews
    );
    var summaryItem = SummaryItem.withCard(OilCaseVolumetricParametersSummaryService.OIL_CASE_VOLUMETRIC_PARAMETERS, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }
}