package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class HydrocarbonCaseSummaryService {
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @Autowired
  public HydrocarbonCaseSummaryService(HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService) {
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
  }

  public HydrocarbonCaseSummaryView getHydrocarbonSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var hydrocarbonCase = hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail);
    return hydrocarbonCase.map(this::createSummaryViewFromHydrocarbonCase)
        .orElse(HydrocarbonCaseSummaryView.empty());
  }

  private HydrocarbonCaseSummaryView createSummaryViewFromHydrocarbonCase(HydrocarbonCaseDetail hydrocarbonCaseDetail) {
    var oilOnly = BooleanUtil.yesNoFromBoolean(hydrocarbonCaseDetail.getOilOnly(), "No");
    var oilOnlyProbability = hydrocarbonCaseDetail.getOilOnlyProbability() != null
        ? hydrocarbonCaseDetail.getOilOnlyProbability().toString() + "%" : "";
    var oilOnlyComments = hydrocarbonCaseDetail.getOilOnlyComments() != null
        ? hydrocarbonCaseDetail.getOilOnlyComments() : "";
    var gasOnly = BooleanUtil.yesNoFromBoolean(hydrocarbonCaseDetail.getGasOnly(), "No");
    var gasOnlyProbability = hydrocarbonCaseDetail.getGasOnlyProbability() != null
        ? hydrocarbonCaseDetail.getGasOnlyProbability().toString() + "%" : "";
    var gasOnlyComments = hydrocarbonCaseDetail.getGasOnlyComments() != null
        ? hydrocarbonCaseDetail.getGasOnlyComments() : "";
    var oilAndGas = BooleanUtil.yesNoFromBoolean(hydrocarbonCaseDetail.getOilAndGas(), "No");
    var oilAndGasProbability = hydrocarbonCaseDetail.getOilAndGasProbability() != null
        ? hydrocarbonCaseDetail.getOilAndGasProbability().toString() + "%" : "";
    var oilAndGasComments = hydrocarbonCaseDetail.getOilAndGasComments() != null
        ? hydrocarbonCaseDetail.getOilAndGasComments() : "";

    if (isNothingChecked(oilOnly, gasOnly, oilAndGas)) {
      gasOnly = "";
      oilOnly = "";
      oilAndGas = "";
    }

    return new HydrocarbonCaseSummaryView(
        oilOnly,
        oilOnlyProbability,
        oilOnlyComments,
        gasOnly,
        gasOnlyProbability,
        gasOnlyComments,
        oilAndGas,
        oilAndGasProbability,
        oilAndGasComments);
  }

  private boolean isNothingChecked(String oilOnly, String gasOnly, String oilAndGas) {
    return oilOnly.equals("No") && gasOnly.equals("No") && oilAndGas.equals("No");
  }
}
