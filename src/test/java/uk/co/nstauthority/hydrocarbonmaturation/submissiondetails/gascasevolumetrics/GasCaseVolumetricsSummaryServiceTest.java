package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class GasCaseVolumetricsSummaryServiceTest {

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @InjectMocks
  private GasCaseVolumetricsSummaryService gasCaseVolumetricsSummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

  @Test
  void getGasCaseVolumetricsSummaryView_emptySummaries() {
    var emptySummary = ProbabilityDistributionSummaryView.empty("empty");

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(submissionDetail)))
        .thenReturn(emptySummary);

    var gasCaseVolumetricsSummaryView = gasCaseVolumetricsSummaryService.getGasCaseVolumetricsSummaryView(
        submissionDetail);

    assertThat(gasCaseVolumetricsSummaryView)
        .extracting(
            ProbabilityDistributionsFormSummaryView::probabilityDistributionSummaryViews)
        .isEqualTo(
            List.of(
                emptySummary,
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
        "20", "20", "unit", "", "comments");

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(submissionDetail)))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_PROSPECT,
            submissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_PROSPECT,
            submissionDetail))
        .thenReturn(testSummary);

    var gasCaseVolumetricsSummaryView = gasCaseVolumetricsSummaryService.getGasCaseVolumetricsSummaryView(
        submissionDetail);

    assertThat(gasCaseVolumetricsSummaryView)
        .extracting(
            ProbabilityDistributionsFormSummaryView::probabilityDistributionSummaryViews)
        .isEqualTo(
            List.of(
                testSummary,
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
        "20", "20", "unit", "", "comments");
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(discoverySubmissionDetail)))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY,
            discoverySubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY,
            discoverySubmissionDetail))
        .thenReturn(testSummary);

    var gasCaseVolumetricsSummaryView = gasCaseVolumetricsSummaryService.getGasCaseVolumetricsSummaryView(
        discoverySubmissionDetail);

    assertThat(gasCaseVolumetricsSummaryView)
        .extracting(
            ProbabilityDistributionsFormSummaryView::probabilityDistributionSummaryViews)
        .isEqualTo(
            List.of(
                testSummary,
                testSummary,
                testSummary,
                testSummary,
                testSummary)
        );
  }
}