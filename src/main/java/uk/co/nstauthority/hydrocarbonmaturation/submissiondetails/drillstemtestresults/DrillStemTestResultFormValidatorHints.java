package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import java.util.List;

public class DrillStemTestResultFormValidatorHints {

  private final List<String> drillStemTestTestNumbers;

  public DrillStemTestResultFormValidatorHints(List<String> drillStemTestTestNumbers) {
    this.drillStemTestTestNumbers = drillStemTestTestNumbers;
  }

  public List<String> getDrillStemTestTestNumbers() {
    return drillStemTestTestNumbers;
  }
}
