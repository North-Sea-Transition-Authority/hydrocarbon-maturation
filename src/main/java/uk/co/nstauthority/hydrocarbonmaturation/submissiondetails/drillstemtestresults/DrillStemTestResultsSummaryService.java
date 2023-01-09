package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class DrillStemTestResultsSummaryService {

  private final DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;
  private final DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService;

  @Autowired
  public DrillStemTestResultsSummaryService(DrillStemTestResultPersistenceService drillStemTestResultPersistenceService,
                                            DrillStemTestInitialPersistenceService drillStemTestInitialPersistenceService) {
    this.drillStemTestResultPersistenceService = drillStemTestResultPersistenceService;
    this.drillStemTestInitialPersistenceService = drillStemTestInitialPersistenceService;
  }

  public DrillStemTestSummaryView getDrillStemTestSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return drillStemTestInitialPersistenceService
        .getDrillStemTestBySubmissionDetail(submissionDetail)
        .map(drillStemTest -> new DrillStemTestSummaryView(
            getDrillStemTestSummaryOptionDisplay(drillStemTest),
            getDrillStemTestResultSummaryViewsFromDrillStemTest(drillStemTest)))
        .orElse(DrillStemTestSummaryView.empty());
  }

  public List<DrillStemTestResultSummaryView> getDrillStemTestResultSummaryViewsFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    var drillStemTest = drillStemTestInitialPersistenceService.getDrillStemTestBySubmissionDetailOrThrow(submissionDetail);
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
    final String hours = "hours";
    final String psia = "psia";
    final String ftTvdss = "ft tvdss";
    final String bpd = "bpd";
    return new DrillStemTestResultSummaryView(
        resultCounter.toString(),
        StringUtils.defaultIfBlank(drillStemTestResult.getFormationName(), ""),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getTopDepth(), ftTvdss),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getBottomDepth(), ftTvdss),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getBhfp(), psia),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getWhfp(), psia),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getFlowDuration(), hours),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getGasFlowrate(), bpd),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getOilFlowrate(), bpd),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getWaterFlowrate(), bpd),
        StringUtils.defaultIfBlank(drillStemTestResult.getCommentsInterpretation(), ""),
        summaryMode,
        ReverseRouter.route(on(DrillStemTestResultController.class)
            .getDrillStemTestResultPage(
                drillStemTestResult.getDrillStemTest().getSubmissionDetail().getId(), drillStemTestResult.getId())),
        ReverseRouter.route(on(DrillStemTestDeleteController.class)
            .getDrillStemTestResultDeleteConfirmationPage(
                drillStemTestResult.getDrillStemTest().getSubmissionDetail().getId(), drillStemTestResult.getId()))
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

  private String getDrillStemTestSummaryOptionDisplay(DrillStemTest drillStemTest) {
    return drillStemTest.getDrillStemTestSummaryOption() != null
        ? drillStemTest.getDrillStemTestSummaryOption().getDisplayName()
        : "";
  }
}
