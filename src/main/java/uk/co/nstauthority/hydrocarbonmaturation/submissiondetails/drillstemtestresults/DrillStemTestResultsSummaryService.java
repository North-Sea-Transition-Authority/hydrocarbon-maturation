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
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class DrillStemTestResultsSummaryService implements SummarySectionService<ReservoirSubmissionDetail> {

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

    final String hours = "hours";
    final String psia = "psia";
    final String mTvdss = "m tvdss";
    final String bpd = "bpd";
    final String mmscfpd = "mmscfpd";
    return new DrillStemTestResultSummaryView(
        StringUtils.defaultIfBlank(drillStemTestResult.getTestNumber(), resultCounter.toString()),
        StringUtils.defaultIfBlank(drillStemTestResult.getFormationName(), ""),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getTopDepth(), mTvdss),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getBottomDepth(), mTvdss),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getBhfp(), psia),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getWhfp(), psia),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getFlowDuration(), hours),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getGasFlowrate(), mmscfpd),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getOilFlowrate(), bpd),
        NumberUtil.getNumberStringWithUnitsOrEmpty(drillStemTestResult.getWaterFlowrate(), bpd),
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

  private String getDrillStemTestSummaryOptionDisplay(DrillStemTest drillStemTest) {
    return drillStemTest.getDrillStemTestSummaryOption() != null
        ? drillStemTest.getDrillStemTestSummaryOption().getDisplayName()
        : "";
  }

  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail source) {
    // TODO HM-816 - add summary section for drill stem test result
    return Optional.empty();
  }
}
