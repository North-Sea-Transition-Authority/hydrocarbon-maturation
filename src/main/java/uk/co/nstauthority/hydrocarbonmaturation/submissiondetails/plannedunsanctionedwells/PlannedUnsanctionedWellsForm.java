package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

public class PlannedUnsanctionedWellsForm implements SubmissionForm {
  private DecimalInput totalNumberOfPlannedUnsanctionedWells = new DecimalInput();

  private DecimalInput totalCapex = new DecimalInput();

  public DecimalInput getTotalNumberOfPlannedUnsanctionedWells() {
    return totalNumberOfPlannedUnsanctionedWells;
  }

  public void setTotalNumberOfPlannedUnsanctionedWells(DecimalInput totalNumberOfPlannedUnsanctionedWells) {
    this.totalNumberOfPlannedUnsanctionedWells = totalNumberOfPlannedUnsanctionedWells;
  }

  public DecimalInput getTotalCapex() {
    return totalCapex;
  }

  public void setTotalCapex(DecimalInput totalCapex) {
    this.totalCapex = totalCapex;
  }
}
