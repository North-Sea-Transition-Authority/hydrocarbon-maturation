package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.WellDetailsForm;

class DryWellDetailsForm implements SubmissionForm {

  private WellDetailsForm drillDetailsForm = new WellDetailsForm();
  private DryHoleType dryHoleType;
  private String dryHoleDescription;
  private String dryHoleScope;
  private String dryHoleComments;
  private DecimalInput dryHoleCost = new DecimalInput();

  public DryHoleType getDryHoleType() {
    return dryHoleType;
  }

  public void setDryHoleType(
      DryHoleType dryHoleType) {
    this.dryHoleType = dryHoleType;
  }

  public String getDryHoleDescription() {
    return dryHoleDescription;
  }

  public void setDryHoleDescription(String dryHoleDescription) {
    this.dryHoleDescription = dryHoleDescription;
  }

  public String getDryHoleScope() {
    return dryHoleScope;
  }

  public void setDryHoleScope(String dryHoleScope) {
    this.dryHoleScope = dryHoleScope;
  }

  public String getDryHoleComments() {
    return dryHoleComments;
  }

  public void setDryHoleComments(String dryHoleComments) {
    this.dryHoleComments = dryHoleComments;
  }

  public DecimalInput getDryHoleCost() {
    return dryHoleCost;
  }

  public void setDryHoleCost(DecimalInput dryHoleCost) {
    this.dryHoleCost = dryHoleCost;
  }

  public WellDetailsForm getDrillDetailsForm() {
    return drillDetailsForm;
  }

  public void setDrillDetailsForm(
      WellDetailsForm drillDetailsForm) {
    this.drillDetailsForm = drillDetailsForm;
  }
}
