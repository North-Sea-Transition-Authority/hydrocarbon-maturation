package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.DrillDetail;

@Entity
@Table(name = "dry_well_details")
class DryWellDetail extends DrillDetail {

  @Enumerated(EnumType.STRING)
  private DryHoleType dryHoleType;

  private String dryHoleDescription;

  private String dryHoleScope;

  private String dryHoleComments;

  private BigDecimal dryHoleCost;

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

  public BigDecimal getDryHoleCost() {
    return dryHoleCost;
  }

  public void setDryHoleCost(BigDecimal dryHoleCost) {
    this.dryHoleCost = dryHoleCost;
  }
}
