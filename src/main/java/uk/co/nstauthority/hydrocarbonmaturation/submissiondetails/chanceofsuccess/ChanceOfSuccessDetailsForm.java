package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import uk.co.nstauthority.hydrocarbonmaturation.numberinput.DecimalInput;

public class ChanceOfSuccessDetailsForm {

  private DecimalInput sourcePercentage;
  private String sourceComments;
  private DecimalInput reservoirPercentage;
  private String reservoirComments;
  private DecimalInput topSealPercentage;
  private String topSealComments;

  public DecimalInput getSourcePercentage() {
    return sourcePercentage;
  }

  public void setSourcePercentage(DecimalInput sourcePercentage) {
    this.sourcePercentage = sourcePercentage;
  }

  public String getSourceComments() {
    return sourceComments;
  }

  public void setSourceComments(String sourceComments) {
    this.sourceComments = sourceComments;
  }

  public DecimalInput getReservoirPercentage() {
    return reservoirPercentage;
  }

  public void setReservoirPercentage(DecimalInput reservoirPercentage) {
    this.reservoirPercentage = reservoirPercentage;
  }

  public String getReservoirComments() {
    return reservoirComments;
  }

  public void setReservoirComments(String reservoirComments) {
    this.reservoirComments = reservoirComments;
  }

  public DecimalInput getTopSealPercentage() {
    return topSealPercentage;
  }

  public void setTopSealPercentage(DecimalInput topSealPercentage) {
    this.topSealPercentage = topSealPercentage;
  }

  public String getTopSealComments() {
    return topSealComments;
  }

  public void setTopSealComments(String topSealComments) {
    this.topSealComments = topSealComments;
  }
}
