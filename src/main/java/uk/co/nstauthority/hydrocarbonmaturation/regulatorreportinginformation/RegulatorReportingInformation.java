package uk.co.nstauthority.hydrocarbonmaturation.regulatorreportinginformation;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroup;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;

@Entity
@Table(name = "regulator_reporting_information")
@Audited
public class RegulatorReportingInformation {

  @Id
  @UuidGenerator
  private UUID id;

  @JoinColumn(name = "resource_group_id")
  @ManyToOne
  private ResourceGroup resourceGroup;

  private String resourceName;

  @Enumerated(EnumType.STRING)
  private HydrocarbonsDiscovered hydrocarbonCase;

  @Enumerated(EnumType.STRING)
  private OpportunityType opportunityType;

  private String otherTypeOfOpportunity;

  private Integer wellId;

  private String wellReference;

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getId() {
    return id;
  }

  public ResourceGroup getResourceGroup() {
    return resourceGroup;
  }

  public void setResourceGroup(
      ResourceGroup resourceGroup) {
    this.resourceGroup = resourceGroup;
  }

  public String getResourceName() {
    return resourceName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public HydrocarbonsDiscovered getHydrocarbonCase() {
    return hydrocarbonCase;
  }

  public void setHydrocarbonCase(
      HydrocarbonsDiscovered hydrocarbonCase) {
    this.hydrocarbonCase = hydrocarbonCase;
  }

  public OpportunityType getOpportunityType() {
    return opportunityType;
  }

  public void setOpportunityType(
      OpportunityType opportunityType) {
    this.opportunityType = opportunityType;
  }

  public String getOtherTypeOfOpportunity() {
    return otherTypeOfOpportunity;
  }

  public void setOtherTypeOfOpportunity(String otherTypeOfOpportunity) {
    this.otherTypeOfOpportunity = otherTypeOfOpportunity;
  }

  public Integer getWellId() {
    return wellId;
  }

  public void setWellId(Integer wellId) {
    this.wellId = wellId;
  }

  public String getWellReference() {
    return wellReference;
  }

  public void setWellReference(String wellReference) {
    this.wellReference = wellReference;
  }
}
