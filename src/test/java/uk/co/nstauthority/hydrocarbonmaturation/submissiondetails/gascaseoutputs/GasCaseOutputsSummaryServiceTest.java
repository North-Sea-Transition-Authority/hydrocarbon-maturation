package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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
class GasCaseOutputSummaryServiceTest {
  @Mock
  private GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @InjectMocks
  private GasCaseOutputsSummaryService gasCaseOutputsSummaryService;

  private SubmissionDetail submissionDetail;
  private GasCaseOutput gasCaseOutput;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    gasCaseOutput = new GasCaseOutput();
    gasCaseOutput.setSubmissionDetail(submissionDetail);
  }

  @Test
  void getGasCaseOutputSummaryFromSubmissionDetail_whenPresent_fullSummary() {
    var emptySummary = ProbabilityDistributionSummaryView.empty("empty");

    when(gasCaseOutputsPersistenceService.getGasCaseOutput(submissionDetail))
        .thenReturn(Optional.of(gasCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(submissionDetail)))
        .thenReturn(emptySummary);

    gasCaseOutput.setHasAssociatedCondensateRecoverableResources(true);

    var view = gasCaseOutputsSummaryService.getGasCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            GasCaseOutputsSummaryView::hasAssociatedCondensateRecoverableResources,
            GasCaseOutputsSummaryView::probabilityDistributionSummaryViews)
        .containsExactly(
            "Yes",
            List.of(
                emptySummary,
                emptySummary,
                emptySummary,
                emptySummary)
        );
  }

  @Test
  void getGasCaseOutputSummaryFromSubmissionDetail_whenPresent_notEmptySummary() {
    var testSummary = new ProbabilityDistributionSummaryView(
        "test", "type", "20",
        "20", "20", "20",
        "20", "20", "20",
        "20", "20", "10", "unit");

    when(gasCaseOutputsPersistenceService.getGasCaseOutput(submissionDetail))
        .thenReturn(Optional.of(gasCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(submissionDetail)))
        .thenReturn(testSummary);

    gasCaseOutput.setHasAssociatedCondensateRecoverableResources(true);

    var view = gasCaseOutputsSummaryService.getGasCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            GasCaseOutputsSummaryView::hasAssociatedCondensateRecoverableResources,
            GasCaseOutputsSummaryView::probabilityDistributionSummaryViews)
        .containsExactly(
            "Yes",
            List.of(
                testSummary,
                testSummary,
                testSummary,
                testSummary)
        );
  }

  @Test
  void getGasCaseOutputSummaryFromSubmissionDetail_whenNotPresent_notEmptySummary() {
    when(gasCaseOutputsPersistenceService.getGasCaseOutput(submissionDetail))
        .thenReturn(Optional.empty());

    var view = gasCaseOutputsSummaryService.getGasCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            GasCaseOutputsSummaryView::hasAssociatedCondensateRecoverableResources,
            GasCaseOutputsSummaryView::probabilityDistributionSummaryViews)
        .containsExactly(
            "",
            List.of(ProbabilityDistributionSummaryView.empty("Gas in place"),
                ProbabilityDistributionSummaryView.empty("Gas recoverable resources"),
                ProbabilityDistributionSummaryView.empty("Associated condensate recoverable resources"),
                ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources"))
        );
  }
}