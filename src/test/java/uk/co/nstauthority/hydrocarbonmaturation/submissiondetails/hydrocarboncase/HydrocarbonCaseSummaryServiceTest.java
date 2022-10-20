package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class HydrocarbonCaseSummaryServiceTest {

  @Mock
  private HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @InjectMocks
  private HydrocarbonCaseSummaryService hydrocarbonCaseSummaryService;

  private SubmissionDetail submissionDetail;
  private HydrocarbonCaseDetail hydrocarbonCaseDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    hydrocarbonCaseDetail = new HydrocarbonCaseDetail();
    hydrocarbonCaseDetail.setSubmissionDetail(submissionDetail);
  }

  @Test
  void getHydrocarbonCaseSummaryFromSubmissionDetail_whenPresent_fullSummary() {
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail))
        .thenReturn(Optional.of(hydrocarbonCaseDetail));
    hydrocarbonCaseDetail.setGasOnly(true);
    hydrocarbonCaseDetail.setGasOnlyComments("there is a lot of gas here");
    hydrocarbonCaseDetail.setGasOnlyProbability(BigDecimal.valueOf(5.0));
    hydrocarbonCaseDetail.setOilOnly(true);
    hydrocarbonCaseDetail.setOilOnlyComments("there is a lot of oil here");
    hydrocarbonCaseDetail.setOilOnlyProbability(BigDecimal.valueOf(25.0));
    hydrocarbonCaseDetail.setOilAndGas(true);
    hydrocarbonCaseDetail.setOilAndGasComments("there is a lot of oil and gas here");
    hydrocarbonCaseDetail.setOilAndGasProbability(BigDecimal.valueOf(55.0));

    var view = hydrocarbonCaseSummaryService.getHydrocarbonSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            HydrocarbonCaseSummaryView::gasOnly,
            HydrocarbonCaseSummaryView::gasOnlyProbability,
            HydrocarbonCaseSummaryView::gasOnlyComments,
            HydrocarbonCaseSummaryView::oilOnly,
            HydrocarbonCaseSummaryView::oilOnlyProbability,
            HydrocarbonCaseSummaryView::oilOnlyComments,
            HydrocarbonCaseSummaryView::oilAndGas,
            HydrocarbonCaseSummaryView::oilAndGasProbability,
            HydrocarbonCaseSummaryView::oilAndGasComments)
        .containsExactly(
            "Yes",
            "5.0%",
            "there is a lot of gas here",
            "Yes",
            "25.0%",
            "there is a lot of oil here",
            "Yes",
            "55.0%",
            "there is a lot of oil and gas here"
        );
  }

  @Test
  void getHydrocarbonCaseSummaryFromSubmissionDetail_whenNotPresent_EmptySummary() {
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var view = hydrocarbonCaseSummaryService.getHydrocarbonSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            HydrocarbonCaseSummaryView::gasOnly,
            HydrocarbonCaseSummaryView::gasOnlyProbability,
            HydrocarbonCaseSummaryView::gasOnlyComments,
            HydrocarbonCaseSummaryView::oilOnly,
            HydrocarbonCaseSummaryView::oilOnlyProbability,
            HydrocarbonCaseSummaryView::oilOnlyComments,
            HydrocarbonCaseSummaryView::oilAndGas,
            HydrocarbonCaseSummaryView::oilAndGasProbability,
            HydrocarbonCaseSummaryView::oilAndGasComments)
        .containsExactly(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        );
  }

  @Test
  void getHydrocarbonCaseSummaryFromSubmissionDetail_whenPresentEmpty_EmptySummary() {
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail))
        .thenReturn(Optional.of(hydrocarbonCaseDetail));

    var view = hydrocarbonCaseSummaryService.getHydrocarbonSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            HydrocarbonCaseSummaryView::gasOnly,
            HydrocarbonCaseSummaryView::gasOnlyProbability,
            HydrocarbonCaseSummaryView::gasOnlyComments,
            HydrocarbonCaseSummaryView::oilOnly,
            HydrocarbonCaseSummaryView::oilOnlyProbability,
            HydrocarbonCaseSummaryView::oilOnlyComments,
            HydrocarbonCaseSummaryView::oilAndGas,
            HydrocarbonCaseSummaryView::oilAndGasProbability,
            HydrocarbonCaseSummaryView::oilAndGasComments)
        .containsExactly(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        );
  }
}