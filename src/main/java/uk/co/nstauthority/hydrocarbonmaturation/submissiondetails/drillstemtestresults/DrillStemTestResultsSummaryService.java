package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class DrillStemTestResultsSummaryService implements SummarySectionService<ReservoirSubmissionDetail> {
  public static final int WELL_TEST_RESULTS_DISPLAY_ORDER = 110;
  public static final String WELL_TEST_RESULT = "Well test result";

  public static final String TEST_NUMBER = "Test number";
  public static final String FORMATION_NAME = "Formation name";
  public static final String TOP_DEPTH = "Top depth";
  public static final String BOTTOM_DEPTH = "Bottom depth";
  public static final String BHFP = "Bhfp";
  public static final String WHFP = "Whfp";
  public static final String FLOW_DURATION = "Flow duration";
  public static final String GAS_FLOWRATE = "Gas flowrate";
  public static final String OIL_FLOWRATE = "Oil flowrate";
  public static final String WATER_FLOWRATE = "Water flowrate";
  public static final String COMMENTS_AND_INTERPRETATION = "Comments and interpretation";

  static final String HOURS = "hours";
  static final String PSIA = "psia";
  static final String M_TVDSS = "m tvdss";
  static final String BPD = "bpd";
  static final String MMSCFPD = "mmscfpd";

  private final DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;
  private final DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  @Autowired
  public DrillStemTestResultsSummaryService(DrillStemTestResultPersistenceService drillStemTestResultPersistenceService,
                                            DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService) {
    this.drillStemTestResultPersistenceService = drillStemTestResultPersistenceService;
    this.drillStemTestInitialPersistenceService = drillStemTestInitialPersistenceService;
  }

  public DrillStemTestSummaryView getDrillStemTestSummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return drillStemTestInitialPersistenceService
        .getDrillStemTestByReservoirSubmissionDetail(reservoirSubmissionDetail)
        .map(drillStemTest -> new DrillStemTestSummaryView(
            getDrillStemTestResultSummaryViewsFromDrillStemTest(drillStemTest)))
        .orElse(DrillStemTestSummaryView.empty());
  }

  public List<DrillStemTestResultSummaryView> getDrillStemTestResultSummaryViewsFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var drillStemTest = drillStemTestInitialPersistenceService
        .getDrillStemTestByReservoirSubmissionDetailOrThrow(reservoirSubmissionDetail);
    var drillStemTestResultViews = new ArrayList<DrillStemTestResultSummaryView>();
    var drillStemTestResults =
        drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(drillStemTest);
    for (int i = 0; i < drillStemTestResults.size(); i++) {
      drillStemTestResultViews.add(
          createSummaryViewFromDrillStemTestResult(
              drillStemTestResults.get(i), DrillStemTestResultSummaryMode.EDIT, i + 1));
    }
    return drillStemTestResultViews;
  }

  public DrillStemTestResultSummaryView createSummaryViewFromDrillStemTestResult(
      DrillStemTestResult drillStemTestResult, DrillStemTestResultSummaryMode summaryMode, Integer resultCounter) {
    var reservoirSubmissionDetail = drillStemTestResult.getDrillStemTest().getReservoirSubmissionDetail();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    return new DrillStemTestResultSummaryView(
        StringUtils.defaultIfBlank(drillStemTestResult.getTestNumber(), resultCounter.toString()),
        StringUtils.defaultIfBlank(drillStemTestResult.getFormationName(), ""),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getTopDepth(), M_TVDSS),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getBottomDepth(), M_TVDSS),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getBhfp(), PSIA),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getWhfp(), PSIA),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getFlowDuration(), HOURS),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getGasFlowrate(), MMSCFPD),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getOilFlowrate(), BPD),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getWaterFlowrate(), BPD),
        StringUtils.defaultIfBlank(drillStemTestResult.getCommentsInterpretation(), ""),
        summaryMode,
        ReverseRouter.route(on(DrillStemTestResultController.class)
            .getDrillStemTestResultPage(
               submissionDetail.getId(),
                reservoirSubmissionDetail.getId(),
                drillStemTestResult.getId())),
        ReverseRouter.route(on(DrillStemTestDeleteController.class)
            .getDrillStemTestResultDeleteConfirmationPage(
                submissionDetail.getId(),
                reservoirSubmissionDetail.getId(),
                drillStemTestResult.getId()))
    );
  }

  private List<DrillStemTestResultSummaryView> getDrillStemTestResultSummaryViewsFromDrillStemTest(
      DrillStemTest drillStemTest) {
    var drillStemTestResultViews = new ArrayList<DrillStemTestResultSummaryView>();
    var drillStemTestResults =
        drillStemTestResultPersistenceService.getDrillStemTestResultsByDrillStemTest(drillStemTest);
    for (int i = 0; i < drillStemTestResults.size(); i++) {
      drillStemTestResultViews.add(
          createSummaryViewFromDrillStemTestResult(
              drillStemTestResults.get(i), DrillStemTestResultSummaryMode.VIEW, i + 1));
    }
    return drillStemTestResultViews;
  }

  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var summaryItems = getAllWellTestResultsSummaries(reservoirSubmissionDetail);

    // do not show summary section if there are no result cards
    if (summaryItems.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(new SummarySection(WELL_TEST_RESULTS_DISPLAY_ORDER, summaryItems, ""));
  }

  private List<SummaryItem> getAllWellTestResultsSummaries(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return drillStemTestResultPersistenceService.getDrillStemTestResultsByReservoirSubmissionDetail(reservoirSubmissionDetail)
        .stream()
        .map(this::getWellTestResultsSummary)
        .toList();
  }

  private SummaryItem getWellTestResultsSummary(DrillStemTestResult drillStemTestResult) {
    var summaryDataView = new SummaryDataView(new ArrayList<>());

    summaryDataView.addKeyValue(
        TEST_NUMBER,
        StringUtils.defaultIfBlank(drillStemTestResult.getTestNumber(), ""));
    summaryDataView.addKeyValue(
        FORMATION_NAME,
        StringUtils.defaultIfBlank(drillStemTestResult.getFormationName(), ""));
    summaryDataView.addKeyValue(
        TOP_DEPTH,
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getTopDepth(), M_TVDSS));
    summaryDataView.addKeyValue(
        BOTTOM_DEPTH,
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getBottomDepth(), M_TVDSS));
    summaryDataView.addKeyValue(
        BHFP,
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getBhfp(), PSIA));
    summaryDataView.addKeyValue(
        WHFP,
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getWhfp(), PSIA));
    summaryDataView.addKeyValue(
        FLOW_DURATION,
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getFlowDuration(), HOURS));
    summaryDataView.addKeyValue(
        GAS_FLOWRATE,
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getGasFlowrate(), MMSCFPD));
    summaryDataView.addKeyValue(
        OIL_FLOWRATE,
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getOilFlowrate(), BPD));
    summaryDataView.addKeyValue(
        WATER_FLOWRATE,
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getWaterFlowrate(), BPD));
    summaryDataView.addKeyValue(
        COMMENTS_AND_INTERPRETATION,
        StringUtils.defaultIfBlank(drillStemTestResult.getCommentsInterpretation(), ""));

    var cardTitle = "%s %s"
        .formatted(WELL_TEST_RESULT, StringUtils.defaultIfBlank(drillStemTestResult.getTestNumber(), ""));
    return SummaryItem.withCard(cardTitle, SummaryCard.simpleSummaryCard(summaryDataView));
  }
}
