package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class DrillStemTestResultsSummaryServiceTest {

  @Mock
  private DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;

  @Mock
  private DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  @InjectMocks
  private DrillStemTestResultsSummaryService drillStemTestResultsSummaryService;

  private SubmissionDetail submissionDetail;

  private DrillStemTest drillStemTest;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    drillStemTest = new DrillStemTest();
    drillStemTest.setSubmissionDetail(submissionDetail);
    drillStemTest.setDrillStemTestSummaryOption(DrillStemTestSummaryOptions.NO);
  }

  @Test
  void getDrillStemTestSummaryViewFromSubmissionDetail_whenNotPresent_emptySummary() {
    when(drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var result = drillStemTestResultsSummaryService.getDrillStemTestSummaryViewFromSubmissionDetail(submissionDetail);

    assertThat(result).isEqualTo(DrillStemTestSummaryView.empty());
  }


  @Test
  void getDrillStemTestResultSummaryViewsFromSubmissionDetail_whenPresent_noStemResults_emptyList() {
    when(drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetailOrThrow(submissionDetail))
        .thenReturn(drillStemTest);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(drillStemTest))
        .thenReturn(List.of());

    var result = drillStemTestResultsSummaryService.getDrillStemTestResultSummaryViewsFromSubmissionDetail(submissionDetail);

    assertThat(result).isEqualTo(List.of());
  }

  @Test
  void getDrillStemTestSummaryViewFromSubmissionDetail_whenPresent_oneStemResult_filledSummary() {
    when(drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(drillStemTest));

    var drillStemTestResult = DrillStemTestResultFormServiceTest.getDrillStemTestResult(drillStemTest);

    when(drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(drillStemTest))
        .thenReturn(List.of(drillStemTestResult));
    var result = drillStemTestResultsSummaryService.getDrillStemTestSummaryViewFromSubmissionDetail(submissionDetail);

    assertThat(result.drillStemTestResultSummaryViews()).hasSize(1);

    assertThat(result.drillStemTestResultSummaryViews().get(0))
        .extracting(
            DrillStemTestResultSummaryView::testNumber,
            DrillStemTestResultSummaryView::formationName,
            DrillStemTestResultSummaryView::topDepth,
            DrillStemTestResultSummaryView::bottomDepth,
            DrillStemTestResultSummaryView::bhfp,
            DrillStemTestResultSummaryView::whfp,
            DrillStemTestResultSummaryView::flowDuration,
            DrillStemTestResultSummaryView::gasFlowrate,
            DrillStemTestResultSummaryView::oilFlowrate,
            DrillStemTestResultSummaryView::waterFlowrate,
            DrillStemTestResultSummaryView::commentsAndInterpretation,
            DrillStemTestResultSummaryView::summaryMode,
            DrillStemTestResultSummaryView::changeUrl,
            DrillStemTestResultSummaryView::deleteUrl)
        .containsExactly(
            "1a",
            "formation",
            "1 ft tvdss",
            "10 ft tvdss",
            "1 psia",
            "2 psia",
            "10 hours",
            "10 bpd",
            "10 bpd",
            "5 bpd",
            "comment",
            DrillStemTestResultSummaryMode.VIEW,
            ReverseRouter.route(on(DrillStemTestResultController.class)
                .getDrillStemTestResultPage(submissionDetail.getId(), drillStemTestResult.getId())),
            ReverseRouter.route(on(DrillStemTestDeleteController.class)
                .getDrillStemTestResultDeleteConfirmationPage(submissionDetail.getId(), drillStemTestResult.getId()))
        );
  }

  @Test
  void getDrillStemTestSummaryViewFromSubmissionDetail_whenPresent_twoStemResults_filledSummary() {
    when(drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(drillStemTest));

    var drillStemTestResult = DrillStemTestResultFormServiceTest.getDrillStemTestResult(drillStemTest);
    var drillStemTestResult2 = new DrillStemTestResult();
    drillStemTestResult2.setId(UUID.randomUUID());
    drillStemTestResult2.setDrillStemTest(drillStemTest);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(drillStemTest))
        .thenReturn(List.of(drillStemTestResult, drillStemTestResult2));
    var result = drillStemTestResultsSummaryService.getDrillStemTestSummaryViewFromSubmissionDetail(submissionDetail);

    assertThat(result.drillStemTestResultSummaryViews()).hasSize(2);

    assertThat(result.drillStemTestResultSummaryViews().get(0))
        .extracting(
            DrillStemTestResultSummaryView::testNumber,
            DrillStemTestResultSummaryView::formationName,
            DrillStemTestResultSummaryView::topDepth,
            DrillStemTestResultSummaryView::bottomDepth,
            DrillStemTestResultSummaryView::bhfp,
            DrillStemTestResultSummaryView::whfp,
            DrillStemTestResultSummaryView::flowDuration,
            DrillStemTestResultSummaryView::gasFlowrate,
            DrillStemTestResultSummaryView::oilFlowrate,
            DrillStemTestResultSummaryView::waterFlowrate,
            DrillStemTestResultSummaryView::commentsAndInterpretation,
            DrillStemTestResultSummaryView::summaryMode,
            DrillStemTestResultSummaryView::changeUrl,
            DrillStemTestResultSummaryView::deleteUrl)
        .containsExactly(
            "1a",
            "formation",
            "1 ft tvdss",
            "10 ft tvdss",
            "1 psia",
            "2 psia",
            "10 hours",
            "10 bpd",
            "10 bpd",
            "5 bpd",
            "comment",
            DrillStemTestResultSummaryMode.VIEW,
            ReverseRouter.route(on(DrillStemTestResultController.class)
                .getDrillStemTestResultPage(submissionDetail.getId(), drillStemTestResult.getId())),
            ReverseRouter.route(on(DrillStemTestDeleteController.class)
                .getDrillStemTestResultDeleteConfirmationPage(submissionDetail.getId(), drillStemTestResult.getId()))
        );

    assertThat(result.drillStemTestResultSummaryViews().get(1))
        .extracting(
            DrillStemTestResultSummaryView::testNumber,
            DrillStemTestResultSummaryView::formationName,
            DrillStemTestResultSummaryView::topDepth,
            DrillStemTestResultSummaryView::bottomDepth,
            DrillStemTestResultSummaryView::bhfp,
            DrillStemTestResultSummaryView::whfp,
            DrillStemTestResultSummaryView::flowDuration,
            DrillStemTestResultSummaryView::gasFlowrate,
            DrillStemTestResultSummaryView::oilFlowrate,
            DrillStemTestResultSummaryView::waterFlowrate,
            DrillStemTestResultSummaryView::commentsAndInterpretation,
            DrillStemTestResultSummaryView::summaryMode,
            DrillStemTestResultSummaryView::changeUrl,
            DrillStemTestResultSummaryView::deleteUrl)
        .containsExactly(
            "2",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            DrillStemTestResultSummaryMode.VIEW,
            ReverseRouter.route(on(DrillStemTestResultController.class)
                .getDrillStemTestResultPage(submissionDetail.getId(), drillStemTestResult2.getId())),
            ReverseRouter.route(on(DrillStemTestDeleteController.class)
                .getDrillStemTestResultDeleteConfirmationPage(submissionDetail.getId(), drillStemTestResult2.getId()))
        );
  }
}