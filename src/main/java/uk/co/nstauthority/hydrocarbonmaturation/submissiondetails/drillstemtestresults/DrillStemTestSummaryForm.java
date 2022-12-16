package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class DrillStemTestSummaryForm implements SubmissionForm {

  private DrillStemTestSummaryOptions drillStemTestSummaryOption;

  public DrillStemTestSummaryOptions getDrillStemTestSummaryOption() {
    return drillStemTestSummaryOption;
  }

  public void setDrillStemTestSummaryOption(
      DrillStemTestSummaryOptions drillStemTestSummaryOption) {
    this.drillStemTestSummaryOption = drillStemTestSummaryOption;
  }
}
