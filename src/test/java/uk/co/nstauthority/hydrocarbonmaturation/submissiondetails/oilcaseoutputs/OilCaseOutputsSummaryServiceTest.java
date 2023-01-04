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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class OilCaseOutputSummaryServiceTest {
  @Mock
  private OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @InjectMocks
  private OilCaseOutputsSummaryService oilCaseOutputsSummaryService;

  private SubmissionDetail submissionDetail;
  private OilCaseOutput oilCaseOutput;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    oilCaseOutput = new OilCaseOutput();
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
  void getOilCaseOutputSummaryFromSubmissionDetail_whenPresent_notEmptySummary() {
    var testSummary = new ProbabilityDistributionSummaryView(
        "test", "type", "20",
        "20", "20", "20",
        "20", "20", "20",
        "20", "", "20", "unit");

    when(oilCaseOutputsPersistenceService.getOilCaseOutput(submissionDetail))
        .thenReturn(Optional.of(oilCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(submissionDetail)))
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