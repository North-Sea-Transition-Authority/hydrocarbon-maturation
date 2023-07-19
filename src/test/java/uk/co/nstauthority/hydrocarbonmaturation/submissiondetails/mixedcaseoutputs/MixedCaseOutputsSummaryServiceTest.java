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

@ExtendWith(MockitoExtension.class)
class MixedCaseOutputSummaryServiceTest {

  @Mock
  private MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @InjectMocks
  private MixedCaseOutputsSummaryService mixedCaseOutputsSummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final MixedCaseOutput mixedCaseOutput = new MixedCaseOutput();

  @BeforeEach
  void setUp() {
    mixedCaseOutput.setSubmissionDetail(submissionDetail);
  }

  @Test
  void getMixedCaseOutputSummaryFromSubmissionDetail_whenPresent_fullSummary() {
    var emptySummary = ProbabilityDistributionSummaryView.empty("empty");

    when(mixedCaseOutputsPersistenceService.getMixedCaseOutput(submissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(submissionDetail)))
        .thenReturn(emptySummary);

    mixedCaseOutput.setHasAssociatedGasRecoverableResources(true);
    mixedCaseOutput.setRiskedOilEquivalentRecoverableMean(BigDecimal.ONE);

    var view = mixedCaseOutputsSummaryService.getMixedCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
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
  void getMixedCaseOutputSummaryFromSubmissionDetail_whenPresent_notEmptySummary_whenProspect() {
    var testSummary = new ProbabilityDistributionSummaryView(
         "test", "type", "20",
        "20", "20", "20", "20", "20", "20", "20", "20", "20",
        "20", "", "20", "unit", "comments");

    when(mixedCaseOutputsPersistenceService.getMixedCaseOutput(submissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT,
            submissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT,
            submissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail))
        .thenReturn(testSummary);

    mixedCaseOutput.setHasAssociatedGasRecoverableResources(true);
    mixedCaseOutput.setRiskedOilEquivalentRecoverableMean(BigDecimal.ZERO);

    var view = mixedCaseOutputsSummaryService.getMixedCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
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
  void getMixedCaseOutputSummaryFromSubmissionDetail_whenPresent_notEmptySummary_whenDiscovery() {
    var testSummary = new ProbabilityDistributionSummaryView(
        "test", "type", "20",
        "20", "20", "20", "20", "20", "20", "20", "20", "20",
        "20", "", "20", "unit", "comments");
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    mixedCaseOutput.setSubmissionDetail(discoverySubmissionDetail);

    when(mixedCaseOutputsPersistenceService.getMixedCaseOutput(discoverySubmissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_DISCOVERY,
            discoverySubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
            discoverySubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            discoverySubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_DISCOVERY,
            discoverySubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            discoverySubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
            discoverySubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
            discoverySubmissionDetail))
        .thenReturn(testSummary);

    mixedCaseOutput.setHasAssociatedGasRecoverableResources(true);
    mixedCaseOutput.setRiskedOilEquivalentRecoverableMean(BigDecimal.ZERO);

    var view = mixedCaseOutputsSummaryService.getMixedCaseOutputsSummaryFromSubmissionDetail(discoverySubmissionDetail);
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
  void getMixedCaseOutputSummaryFromSubmissionDetail_whenNotPresent_notEmptySummary() {
    when(mixedCaseOutputsPersistenceService.getMixedCaseOutput(submissionDetail))
        .thenReturn(Optional.empty());

    var view = mixedCaseOutputsSummaryService.getMixedCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
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