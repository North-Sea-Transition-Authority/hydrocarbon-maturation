package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.DrillDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;

@Entity
@Table(name = "discovery_well_details")
public class DiscoveryWellDetail extends DrillDetail implements SubmissionDetailChildEntity {

  @Enumerated(EnumType.STRING)
  private DiscoveryStatus discoveryStatus;

  private String chanceOfCommercialityComments;

  private BigDecimal totalWellCost;

  private BigDecimal successRelatedCosts;

  public DiscoveryStatus getDiscoveryStatus() {
    return discoveryStatus;
  }

  public void setDiscoveryStatus(
      DiscoveryStatus discoveryStatus) {
    this.discoveryStatus = discoveryStatus;
  }

  public String getChanceOfCommercialityComments() {
    return chanceOfCommercialityComments;
  }

  public void setChanceOfCommercialityComments(String chanceOfCommercialityComments) {
    this.chanceOfCommercialityComments = chanceOfCommercialityComments;
  }

  public BigDecimal getTotalWellCost() {
    return totalWellCost;
  }

  public void setTotalWellCost(BigDecimal totalWellCost) {
    this.totalWellCost = totalWellCost;
  }

  public BigDecimal getSuccessRelatedCosts() {
    return successRelatedCosts;
  }

  public void setSuccessRelatedCosts(BigDecimal successRelatedCosts) {
    this.successRelatedCosts = successRelatedCosts;
  }
}
