package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
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
class ReservoirPropertiesSummaryServiceTest {

  @Mock
  private ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @InjectMocks
  private ReservoirPropertiesSummaryService reservoirPropertiesSummaryService;

  private SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

  @Test
  void getReservoirPropertiesSummaryFromSubmissionDetail_noReservoirProperty() {
    var emptySummary = ProbabilityDistributionSummaryView.empty("empty");

    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail))
        .thenReturn(Optional.empty());
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(submissionDetail)))
        .thenReturn(emptySummary);

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromSubmissionDetail(submissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::formationName,
            ReservoirPropertiesSummaryView::ageStageOrSeries,
            ReservoirPropertiesSummaryView::lithology,
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews)
        .containsExactly(
            "",
            "",
            "",
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
  void getReservoirPropertiesSummaryFromSubmissionDetail_reservoirProperty() {
    var testSummary = new ProbabilityDistributionSummaryView(
        "test", "type", "20",
        "20", "20", "20",
        "20", "20", "20",
        "20", "20", "unit");

    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setFormationName("formation");
    reservoirProperty.setLithology("lithology");
    reservoirProperty.setAgeStage("ageStage");
    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(submissionDetail)))
        .thenReturn(testSummary);

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromSubmissionDetail(
        submissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::formationName,
            ReservoirPropertiesSummaryView::ageStageOrSeries,
            ReservoirPropertiesSummaryView::lithology,
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews)
        .containsExactly(
            "formation",
            "ageStage",
            "lithology",
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
}