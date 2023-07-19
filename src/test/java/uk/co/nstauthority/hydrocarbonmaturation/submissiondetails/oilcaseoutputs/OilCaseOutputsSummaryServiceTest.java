package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

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
class OilCaseOutputsSummaryServiceTest {
  @Mock
  private OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @InjectMocks
  private OilCaseOutputsSummaryService oilCaseOutputsSummaryService;

  private final SubmissionDetail submissionDetail= SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final OilCaseOutput oilCaseOutput = new OilCaseOutput();

  @BeforeEach
  void setUp() {
    oilCaseOutput.setSubmissionDetail(submissionDetail);
  }

  @Test
  void getOilCaseOutputSummaryFromSubmissionDetail_whenPresent_fullSummary() {
    var emptySummary = ProbabilityDistributionSummaryView.empty("empty");

    when(oilCaseOutputsPersistenceService.getOilCaseOutput(submissionDetail))
        .thenReturn(Optional.of(oilCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(submissionDetail)))
        .thenReturn(emptySummary);

    oilCaseOutput.setHasAssociatedGasRecoverableResources(true);
    oilCaseOutput.setRiskedOilEquivalentRecoverableMean(BigDecimal.ONE);

    var view = oilCaseOutputsSummaryService.getOilCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            OilCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            OilCaseOutputsSummaryView::riskedOilEquivalentRecoverableMean,
            OilCaseOutputsSummaryView::probabilityDistributionSummaryViews)
        .containsExactly(
            "Yes",
            oilCaseOutput.getRiskedOilEquivalentRecoverableMean().toString(),
            List.of(
                emptySummary,
                emptySummary,
                emptySummary,
                emptySummary)
        );
  }

  @Test
  void getOilCaseOutputSummaryFromSubmissionDetail_whenPresent_notEmptySummary_whenProspect() {
    var testSummary = new ProbabilityDistributionSummaryView(
        "test", "type", "20",
        "20", "20", "20", "20", "20", "20", "20", "20", "20",
        "20", "", "20", "unit", "comments");

    when(oilCaseOutputsPersistenceService.getOilCaseOutput(submissionDetail))
        .thenReturn(Optional.of(oilCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT,
            submissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail))
        .thenReturn(testSummary);

    oilCaseOutput.setHasAssociatedGasRecoverableResources(true);
    oilCaseOutput.setRiskedOilEquivalentRecoverableMean(BigDecimal.ONE);

    var view = oilCaseOutputsSummaryService.getOilCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            OilCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            OilCaseOutputsSummaryView::riskedOilEquivalentRecoverableMean,
            OilCaseOutputsSummaryView::probabilityDistributionSummaryViews)
        .containsExactly(
            "Yes",
            oilCaseOutput.getRiskedOilEquivalentRecoverableMean().toString(),
            List.of(
                testSummary,
                testSummary,
                testSummary,
                testSummary)
        );
  }

  @Test
  void getOilCaseOutputSummaryFromSubmissionDetail_whenPresent_notEmptySummary_whenDiscovery() {
    var testSummary = new ProbabilityDistributionSummaryView(
        "test", "type", "20",
        "20", "20", "20", "20", "20", "20", "20", "20", "20",
        "20", "", "20", "unit", "comments");
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    oilCaseOutput.setSubmissionDetail(discoverySubmissionDetail);

    when(oilCaseOutputsPersistenceService.getOilCaseOutput(submissionDetail))
        .thenReturn(Optional.of(oilCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
            discoverySubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
            discoverySubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            discoverySubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
            discoverySubmissionDetail))
        .thenReturn(testSummary);

    oilCaseOutput.setHasAssociatedGasRecoverableResources(true);
    oilCaseOutput.setRiskedOilEquivalentRecoverableMean(BigDecimal.ONE);

    var view = oilCaseOutputsSummaryService.getOilCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            OilCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            OilCaseOutputsSummaryView::riskedOilEquivalentRecoverableMean,
            OilCaseOutputsSummaryView::probabilityDistributionSummaryViews)
        .containsExactly(
            "Yes",
            oilCaseOutput.getRiskedOilEquivalentRecoverableMean().toString(),
            List.of(
                testSummary,
                testSummary,
                testSummary,
                testSummary)
        );
  }

  @Test
  void getOilCaseOutputSummaryFromSubmissionDetail_whenNotPresent_notEmptySummary() {
    when(oilCaseOutputsPersistenceService.getOilCaseOutput(submissionDetail))
        .thenReturn(Optional.empty());

    var view = oilCaseOutputsSummaryService.getOilCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            OilCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            OilCaseOutputsSummaryView::riskedOilEquivalentRecoverableMean,
            OilCaseOutputsSummaryView::probabilityDistributionSummaryViews)
        .containsExactly(
            "",
            "",
            List.of(ProbabilityDistributionSummaryView.empty("Oil in place"),
                ProbabilityDistributionSummaryView.empty("Oil recoverable resources"),
                ProbabilityDistributionSummaryView.empty("Associated-gas recoverable resources"),
                ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources"))
        );
  }
}