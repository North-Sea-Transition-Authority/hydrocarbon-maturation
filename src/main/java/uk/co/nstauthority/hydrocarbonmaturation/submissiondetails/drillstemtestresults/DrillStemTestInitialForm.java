package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class DrillStemTestInitialForm implements SubmissionForm {

  private Boolean hasDrillStemTestResult;

  public Boolean getHasDrillStemTestResult() {
    return hasDrillStemTestResult;
  }

  public void setHasDrillStemTestResult(Boolean hasDrillStemTestResult) {
    this.hasDrillStemTestResult = hasDrillStemTestResult;
  }
}
