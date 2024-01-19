package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

@ExtendWith(MockitoExtension.class)
class DrillStemTestResultsSummaryServiceTest {

  @Mock
  private DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;

  @Mock
  private DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  @InjectMocks
  private DrillStemTestResultsSummaryService drillStemTestResultsSummaryService;

  private SubmissionDetail submissionDetail;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  private DrillStemTest drillStemTest;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createDiscoveryFromProspectSubmissionDetail();
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    drillStemTest = new DrillStemTest();
    drillStemTest.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    drillStemTest.setDrillStemTestSummaryOption(DrillStemTestSummaryOptions.NO);
  }

  @Test
  void getDrillStemTestSummaryViewFromSubmissionDetail_whenNotPresent_emptySummary() {
    when(drillStemTestInitialPersistenceService.getDrillStemTestByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var result = drillStemTestResultsSummaryService.getDrillStemTestSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    assertThat(result).isEqualTo(DrillStemTestSummaryView.empty());
  }


  @Test
  void getDrillStemTestResultSummaryViewsFromSubmissionDetail_whenPresent_noStemResults_emptyList() {
    when(drillStemTestInitialPersistenceService.getDrillStemTestByReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail))
        .thenReturn(drillStemTest);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(drillStemTest))
        .thenReturn(List.of());

    var result = drillStemTestResultsSummaryService.getDrillStemTestResultSummaryViewsFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    assertThat(result).isEqualTo(List.of());
  }

  @Test
  void getDrillStemTestSummaryViewFromSubmissionDetail_whenPresent_oneStemResult_filledSummary() {
    when(drillStemTestInitialPersistenceService.getDrillStemTestByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(drillStemTest));

    var drillStemTestResult = DrillStemTestResultFormServiceTest.getDrillStemTestResult(drillStemTest);

    when(drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(drillStemTest))
        .thenReturn(List.of(drillStemTestResult));
    var result = drillStemTestResultsSummaryService.getDrillStemTestSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

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
            "1 m tvdss",
            "10 m tvdss",
            "1 psia",
            "2 psia",
            "10 hours",
            "10 mmscfpd",
            "10 bpd",
            "5 bpd",
            "comment",
            DrillStemTestResultSummaryMode.VIEW,
            ReverseRouter.route(on(DrillStemTestResultController.class)
                .getDrillStemTestResultPage(submissionDetail.getId(), reservoirSubmissionDetail.getId(), drillStemTestResult.getId())),
            ReverseRouter.route(on(DrillStemTestDeleteController.class)
                .getDrillStemTestResultDeleteConfirmationPage(submissionDetail.getId(), reservoirSubmissionDetail.getId(), drillStemTestResult.getId()))
        );
  }

  @Test
  void getDrillStemTestSummaryViewFromSubmissionDetail_whenPresent_twoStemResults_filledSummary() {
    when(drillStemTestInitialPersistenceService.getDrillStemTestByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(drillStemTest));

    var drillStemTestResult = DrillStemTestResultFormServiceTest.getDrillStemTestResult(drillStemTest);
    var drillStemTestResult2 = new DrillStemTestResult();
    drillStemTestResult2.setId(UUID.randomUUID());
    drillStemTestResult2.setDrillStemTest(drillStemTest);
    when(drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(drillStemTest))
        .thenReturn(List.of(drillStemTestResult, drillStemTestResult2));
    var result = drillStemTestResultsSummaryService.getDrillStemTestSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

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
            "1 m tvdss",
            "10 m tvdss",
            "1 psia",
            "2 psia",
            "10 hours",
            "10 mmscfpd",
            "10 bpd",
            "5 bpd",
            "comment",
            DrillStemTestResultSummaryMode.VIEW,
            ReverseRouter.route(on(DrillStemTestResultController.class)
                .getDrillStemTestResultPage(submissionDetail.getId(), reservoirSubmissionDetail.getId(), drillStemTestResult.getId())),
            ReverseRouter.route(on(DrillStemTestDeleteController.class)
                .getDrillStemTestResultDeleteConfirmationPage(submissionDetail.getId(), reservoirSubmissionDetail.getId(), drillStemTestResult.getId()))
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
                .getDrillStemTestResultPage(submissionDetail.getId(), reservoirSubmissionDetail.getId(), drillStemTestResult2.getId())),
            ReverseRouter.route(on(DrillStemTestDeleteController.class)
                .getDrillStemTestResultDeleteConfirmationPage(submissionDetail.getId(), reservoirSubmissionDetail.getId(), drillStemTestResult2.getId()))
        );
  }

  @Test
  void getSummarySection_noDrillStemTest_assertEmptySummary() {
    when(drillStemTestResultPersistenceService.getDrillStemTestResultsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(List.of());

    assertThat(drillStemTestResultsSummaryService.getSummarySection(reservoirSubmissionDetail))
        .isEmpty();
  }

  @Test
  void getSummarySection_oneDrillStemTestResult_assertBuiltCorrectly() {
    var drillStemTestResult = new DrillStemTestResult();
    drillStemTestResult.setFormationName("Formation name");
    drillStemTestResult.setTopDepth(BigDecimal.TEN);
    drillStemTestResult.setBottomDepth(BigDecimal.ONE);
    drillStemTestResult.setBhfp(10);
    drillStemTestResult.setWhfp(5);
    drillStemTestResult.setFlowDuration(BigDecimal.ZERO);
    drillStemTestResult.setGasFlowrate(BigDecimal.valueOf(50));
    drillStemTestResult.setOilFlowrate(BigDecimal.valueOf(100));
    drillStemTestResult.setWaterFlowrate(BigDecimal.valueOf(150));
    drillStemTestResult.setCommentsInterpretation("My perspective");
    drillStemTestResult.setTestNumber("1");

    when(drillStemTestResultPersistenceService.getDrillStemTestResultsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(List.of(drillStemTestResult));

    var summarySectionOptional = drillStemTestResultsSummaryService.getSummarySection(reservoirSubmissionDetail);
    assertThat(summarySectionOptional)
        .isPresent()
        .get()
        .extracting(
            SummarySection::displayOrder,
            SummarySection::summaryText
        ).containsExactly(
            DrillStemTestResultsSummaryService.WELL_TEST_RESULTS_DISPLAY_ORDER,
            ""
        );

    var summaryItems = summarySectionOptional.get().summaryItems();
    assertThat(summaryItems)
        .hasSize(1)
        .extracting(SummaryItem::displayName)
        .contains("Well test result 1");

    var summaryDataView = new SummaryDataView(new ArrayList<>());

    summaryDataView.addKeyValue(
        DrillStemTestResultsSummaryService.TEST_NUMBER,
        drillStemTestResult.getTestNumber());
    summaryDataView.addKeyValue(
        DrillStemTestResultsSummaryService.FORMATION_NAME,
        drillStemTestResult.getFormationName());
    summaryDataView.addKeyValue(
        DrillStemTestResultsSummaryService.TOP_DEPTH,
        drillStemTestResult.getTopDepth() + " m tvdss");
    summaryDataView.addKeyValue(
        DrillStemTestResultsSummaryService.BOTTOM_DEPTH,
        drillStemTestResult.getBottomDepth() + " m tvdss");
    summaryDataView.addKeyValue(
        DrillStemTestResultsSummaryService.BHFP,
        drillStemTestResult.getBhfp() + " psia");
    summaryDataView.addKeyValue(
        DrillStemTestResultsSummaryService.WHFP,
        drillStemTestResult.getWhfp() + " psia");
    summaryDataView.addKeyValue(
        DrillStemTestResultsSummaryService.FLOW_DURATION,
        drillStemTestResult.getFlowDuration() + " hours");
    summaryDataView.addKeyValue(
        DrillStemTestResultsSummaryService.GAS_FLOWRATE,
        drillStemTestResult.getGasFlowrate() + " mmscfpd");
    summaryDataView.addKeyValue(
        DrillStemTestResultsSummaryService.OIL_FLOWRATE,
        drillStemTestResult.getOilFlowrate() + " bpd");
    summaryDataView.addKeyValue(
        DrillStemTestResultsSummaryService.WATER_FLOWRATE,
        drillStemTestResult.getWaterFlowrate() + " bpd");
    summaryDataView.addKeyValue(
        DrillStemTestResultsSummaryService.COMMENTS_AND_INTERPRETATION,
          drillStemTestResult.getCommentsInterpretation());

    var expectedSummaryCard = new SummaryCard(
        null,
        SummaryCardType.SIMPLE_SUMMARY,
        summaryDataView
    );
    assertThat(summaryItems.get(0).summaryCards())
        .hasSize(1);

    assertThat(summaryItems.get(0).summaryCards().get(0))
        .usingRecursiveComparison()
        .isEqualTo(expectedSummaryCard);
  }

  @Test
  void getSummarySection_twoDrillStemTestResults_assertBothBuiltCorrectly() {
    var drillStemTestResult1 = new DrillStemTestResult();
    drillStemTestResult1.setFormationName("Formation name 1");
    drillStemTestResult1.setTopDepth(BigDecimal.valueOf(1));
    drillStemTestResult1.setBottomDepth(BigDecimal.valueOf(1));
    drillStemTestResult1.setBhfp(1);
    drillStemTestResult1.setWhfp(1);
    drillStemTestResult1.setFlowDuration(BigDecimal.valueOf(1));
    drillStemTestResult1.setGasFlowrate(BigDecimal.valueOf(1));
    drillStemTestResult1.setOilFlowrate(BigDecimal.valueOf(1));
    drillStemTestResult1.setWaterFlowrate(BigDecimal.valueOf(1));
    drillStemTestResult1.setCommentsInterpretation("My perspective 1");
    drillStemTestResult1.setTestNumber("1");

    var drillStemTestResult2 = new DrillStemTestResult();
    drillStemTestResult2.setFormationName("Formation name 2");
    drillStemTestResult2.setTopDepth(BigDecimal.valueOf(2));
    drillStemTestResult2.setBottomDepth(BigDecimal.valueOf(2));
    drillStemTestResult2.setBhfp(2);
    drillStemTestResult2.setWhfp(2);
    drillStemTestResult2.setFlowDuration(BigDecimal.valueOf(2));
    drillStemTestResult2.setGasFlowrate(BigDecimal.valueOf(2));
    drillStemTestResult2.setOilFlowrate(BigDecimal.valueOf(2));
    drillStemTestResult2.setWaterFlowrate(BigDecimal.valueOf(2));
    drillStemTestResult2.setCommentsInterpretation("My perspective 2");
    drillStemTestResult2.setTestNumber("2");

    when(drillStemTestResultPersistenceService.getDrillStemTestResultsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(List.of(drillStemTestResult1, drillStemTestResult2));

    var summarySectionOptional = drillStemTestResultsSummaryService.getSummarySection(reservoirSubmissionDetail);
    assertThat(summarySectionOptional)
        .isPresent()
        .get()
        .extracting(
            SummarySection::displayOrder,
            SummarySection::summaryText
        ).containsExactly(
            DrillStemTestResultsSummaryService.WELL_TEST_RESULTS_DISPLAY_ORDER,
            ""
        );

    var summaryItems = summarySectionOptional.get().summaryItems();
    assertThat(summaryItems)
        .hasSize(2)
        .extracting(SummaryItem::displayName)
        .containsExactly(
            "Well test result 1",
             "Well test result 2"
        );

    var summaryDataView1 = new SummaryDataView(new ArrayList<>());
    summaryDataView1.addKeyValue(
        DrillStemTestResultsSummaryService.TEST_NUMBER,
        drillStemTestResult1.getTestNumber());
    summaryDataView1.addKeyValue(
        DrillStemTestResultsSummaryService.FORMATION_NAME,
        drillStemTestResult1.getFormationName());
    summaryDataView1.addKeyValue(
        DrillStemTestResultsSummaryService.TOP_DEPTH,
        drillStemTestResult1.getTopDepth() + " m tvdss");
    summaryDataView1.addKeyValue(
        DrillStemTestResultsSummaryService.BOTTOM_DEPTH,
        drillStemTestResult1.getBottomDepth() + " m tvdss");
    summaryDataView1.addKeyValue(
        DrillStemTestResultsSummaryService.BHFP,
        drillStemTestResult1.getBhfp() + " psia");
    summaryDataView1.addKeyValue(
        DrillStemTestResultsSummaryService.WHFP,
        drillStemTestResult1.getWhfp() + " psia");
    summaryDataView1.addKeyValue(
        DrillStemTestResultsSummaryService.FLOW_DURATION,
        drillStemTestResult1.getFlowDuration() + " hours");
    summaryDataView1.addKeyValue(
        DrillStemTestResultsSummaryService.GAS_FLOWRATE,
        drillStemTestResult1.getGasFlowrate() + " mmscfpd");
    summaryDataView1.addKeyValue(
        DrillStemTestResultsSummaryService.OIL_FLOWRATE,
        drillStemTestResult1.getOilFlowrate() + " bpd");
    summaryDataView1.addKeyValue(
        DrillStemTestResultsSummaryService.WATER_FLOWRATE,
        drillStemTestResult1.getWaterFlowrate() + " bpd");
    summaryDataView1.addKeyValue(
        DrillStemTestResultsSummaryService.COMMENTS_AND_INTERPRETATION,
        drillStemTestResult1.getCommentsInterpretation());

    var summaryDataView2 = new SummaryDataView(new ArrayList<>());
    summaryDataView2.addKeyValue(
        DrillStemTestResultsSummaryService.TEST_NUMBER,
        drillStemTestResult2.getTestNumber());
    summaryDataView2.addKeyValue(
        DrillStemTestResultsSummaryService.FORMATION_NAME,
        drillStemTestResult2.getFormationName());
    summaryDataView2.addKeyValue(
        DrillStemTestResultsSummaryService.TOP_DEPTH,
        drillStemTestResult2.getTopDepth() + " m tvdss");
    summaryDataView2.addKeyValue(
        DrillStemTestResultsSummaryService.BOTTOM_DEPTH,
        drillStemTestResult2.getBottomDepth() + " m tvdss");
    summaryDataView2.addKeyValue(
        DrillStemTestResultsSummaryService.BHFP,
        drillStemTestResult2.getBhfp() + " psia");
    summaryDataView2.addKeyValue(
        DrillStemTestResultsSummaryService.WHFP,
        drillStemTestResult2.getWhfp() + " psia");
    summaryDataView2.addKeyValue(
        DrillStemTestResultsSummaryService.FLOW_DURATION,
        drillStemTestResult2.getFlowDuration() + " hours");
    summaryDataView2.addKeyValue(
        DrillStemTestResultsSummaryService.GAS_FLOWRATE,
        drillStemTestResult2.getGasFlowrate() + " mmscfpd");
    summaryDataView2.addKeyValue(
        DrillStemTestResultsSummaryService.OIL_FLOWRATE,
        drillStemTestResult2.getOilFlowrate() + " bpd");
    summaryDataView2.addKeyValue(
        DrillStemTestResultsSummaryService.WATER_FLOWRATE,
        drillStemTestResult2.getWaterFlowrate() + " bpd");
    summaryDataView2.addKeyValue(
        DrillStemTestResultsSummaryService.COMMENTS_AND_INTERPRETATION,
        drillStemTestResult2.getCommentsInterpretation());

    var expectedSummaryCard1 = new SummaryCard(
        null,
        SummaryCardType.SIMPLE_SUMMARY,
        summaryDataView1
    );
    var expectedSummaryCard2 = new SummaryCard(
        null,
        SummaryCardType.SIMPLE_SUMMARY,
        summaryDataView2
    );

    assertThat(summaryItems.get(0).summaryCards())
        .hasSize(1);
    assertThat(summaryItems.get(1).summaryCards())
        .hasSize(1);

    assertThat(summaryItems.get(0).summaryCards().get(0))
        .usingRecursiveComparison()
        .isEqualTo(expectedSummaryCard1);
    assertThat(summaryItems.get(1).summaryCards().get(0))
        .usingRecursiveComparison()
        .isEqualTo(expectedSummaryCard2);
  }
}