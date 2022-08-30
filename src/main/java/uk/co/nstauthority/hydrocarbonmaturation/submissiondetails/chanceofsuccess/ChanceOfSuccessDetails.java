package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;

@Embeddable
@MappedSuperclass
class ChanceOfSuccessDetails {

  private String sourcePercentage;
  private String sourceComments;
  private String reservoirPercentage;
  private String reservoirComments;
  private String topSealPercentage;
  private String topSealComments;

  public String getSourcePercentage() {
    return sourcePercentage;
  }

  public void setSourcePercentage(String sourcePercentage) {
    this.sourcePercentage = sourcePercentage;
  }

  public String getSourceComments() {
    return sourceComments;
  }

  public void setSourceComments(String sourceComments) {
    this.sourceComments = sourceComments;
  }

  public String getReservoirPercentage() {
    return reservoirPercentage;
  }

  public void setReservoirPercentage(String reservoirPercentage) {
    this.reservoirPercentage = reservoirPercentage;
  }

  public String getReservoirComments() {
    return reservoirComments;
  }

  public void setReservoirComments(String reservoirComments) {
    this.reservoirComments = reservoirComments;
  }

  public String getTopSealPercentage() {
    return topSealPercentage;
  }

  public void setTopSealPercentage(String topSealPercentage) {
    this.topSealPercentage = topSealPercentage;
  }

  public String getTopSealComments() {
    return topSealComments;
  }

  public void setTopSealComments(String topSealComments) {
    this.topSealComments = topSealComments;
  }
}
