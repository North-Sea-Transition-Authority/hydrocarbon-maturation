package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class MixedCaseOutputSummaryServiceTest {

  @Mock
  private MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @InjectMocks
  private MixedCaseOutputsSummaryService mixedCaseOutputsSummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
  private final MixedCaseOutput mixedCaseOutput = new MixedCaseOutput();

  @BeforeEach
  void setUp() {
    mixedCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getMixedCaseOutputsSummaryFromReservoirSubmissionDetail_whenPresent_fullSummary() {
    var emptySummary = ProbabilityDistributionSummaryView.empty("empty");

    when(mixedCaseOutputsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(submissionDetail), eq(reservoirSubmissionDetail)))
        .thenReturn(emptySummary);

    mixedCaseOutput.setHasAssociatedGasRecoverableResources(true);
    mixedCaseOutput.setRiskedOilEquivalentRecoverableMean(BigDecimal.ONE);

    var view = mixedCaseOutputsSummaryService.getMixedCaseOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
    assertThat(view)
        .extracting(
            MixedCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            MixedCaseOutputsSummaryView::riskedOilEquivalentRecoverableMean,
            MixedCaseOutputsSummaryView::probabilityDistributionSummaryViews)
        .containsExactly(
            "Yes",
            "1",
            List.of(
                emptySummary,
                emptySummary,
                emptySummary,
                emptySummary,
                emptySummary,
                emptySummary,
                emptySummary)
        );
  }

  @Test
  void getMixedCaseOutputsSummaryFromReservoirSubmissionDetail_whenPresent_notEmptySummary_whenProspect() {
    var testSummary = new ProbabilityDistributionSummaryView(
         "test", "type", "20",
        "20", "20", "20", "20", "20", "20", "20", "20", "20",
        "20", "", "20", "unit", "comments");

    when(mixedCaseOutputsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(testSummary);

    mixedCaseOutput.setHasAssociatedGasRecoverableResources(true);
    mixedCaseOutput.setRiskedOilEquivalentRecoverableMean(BigDecimal.ZERO);

    var view = mixedCaseOutputsSummaryService.getMixedCaseOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
    assertThat(view)
        .extracting(
            MixedCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            MixedCaseOutputsSummaryView::riskedOilEquivalentRecoverableMean,
            MixedCaseOutputsSummaryView::probabilityDistributionSummaryViews)
        .containsExactly(
            "Yes",
            "0",
            List.of(
                testSummary,
                testSummary,
                testSummary,
                testSummary,
                testSummary,
                testSummary,
                testSummary)
        );
  }

  @Test
  void getMixedCaseOutputsSummaryFromReservoirSubmissionDetail_whenPresent_notEmptySummary_whenDiscovery() {
    var testSummary = new ProbabilityDistributionSummaryView(
        "test", "type", "20",
        "20", "20", "20", "20", "20", "20", "20", "20", "20",
        "20", "", "20", "unit", "comments");
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);
    mixedCaseOutput.setReservoirSubmissionDetail(discoveryReservoirSubmissionDetail);

    when(mixedCaseOutputsPersistenceService.getMixedCaseOutput(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_DISCOVERY,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_DISCOVERY,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(testSummary);

    mixedCaseOutput.setHasAssociatedGasRecoverableResources(true);
    mixedCaseOutput.setRiskedOilEquivalentRecoverableMean(BigDecimal.ZERO);

    var view = mixedCaseOutputsSummaryService.getMixedCaseOutputsSummaryFromReservoirSubmissionDetail(discoveryReservoirSubmissionDetail);
    assertThat(view)
        .extracting(
            MixedCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            MixedCaseOutputsSummaryView::riskedOilEquivalentRecoverableMean,
            MixedCaseOutputsSummaryView::probabilityDistributionSummaryViews)
        .containsExactly(
            "Yes",
            "0",
            List.of(
                testSummary,
                testSummary,
                testSummary,
                testSummary,
                testSummary,
                testSummary,
                testSummary)
        );
  }

  @Test
  void getMixedCaseOutputsSummaryFromReservoirSubmissionDetail_whenNotPresent_notEmptySummary() {
    when(mixedCaseOutputsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var view = mixedCaseOutputsSummaryService.getMixedCaseOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
    assertThat(view)
        .extracting(
            MixedCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            MixedCaseOutputsSummaryView::riskedOilEquivalentRecoverableMean,
            MixedCaseOutputsSummaryView::probabilityDistributionSummaryViews)
        .containsExactly(
            "",
            "",
            List.of(
                ProbabilityDistributionSummaryView.empty("Oil in place"),
                ProbabilityDistributionSummaryView.empty("Oil recoverable resources"),
                ProbabilityDistributionSummaryView.empty("Associated gas recoverable resources"),
                ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources"),
                ProbabilityDistributionSummaryView.empty("Gas in place"),
                ProbabilityDistributionSummaryView.empty("Gas recoverable resources"),
                ProbabilityDistributionSummaryView.empty("Associated condensate recoverable resources"),
                ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources"))
        );
  }
}