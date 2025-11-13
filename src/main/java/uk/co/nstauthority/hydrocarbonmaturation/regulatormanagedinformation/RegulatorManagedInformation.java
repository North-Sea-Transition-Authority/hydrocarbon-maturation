package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import jakarta.persistence.Column;
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
import org.hibernate.envers.NotAudited;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;

@Entity
@Table(name = "regulator_managed_information")
@Audited
public class RegulatorManagedInformation {

  @Id
  @UuidGenerator
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "resource_id")
  @NotAudited
  private Resource resource;

  @Enumerated(EnumType.STRING)
  @Column(name = "responsible_team")
  private ResponsibleTeamType responsibleTeamType;

  @Enumerated(EnumType.STRING)
  private GeographicalArea geographicalArea;

  private String resourceName;

  private Boolean linkedToShelvedCopField;

  private Integer wellId;

  private String wellReference;

  @Enumerated(EnumType.STRING)
  private HydrocarbonsDiscovered hydrocarbonCase;

  public UUID getId() {
    return id;
  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

  public ResponsibleTeamType getResponsibleTeamType() {
    return responsibleTeamType;
  }

  public void setResponsibleTeamType(
      ResponsibleTeamType responsibleTeamType) {
    this.responsibleTeamType = responsibleTeamType;
  }

  public GeographicalArea getGeographicalArea() {
    return geographicalArea;
  }

  public void setGeographicalArea(
      GeographicalArea geographicalArea) {
    this.geographicalArea = geographicalArea;
  }

  public String getResourceName() {
    return resourceName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public Boolean getLinkedToShelvedCopField() {
    return linkedToShelvedCopField;
  }

  public void setLinkedToShelvedCopField(Boolean linkedToShelvedCopField) {
    this.linkedToShelvedCopField = linkedToShelvedCopField;
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

  public HydrocarbonsDiscovered getHydrocarbonCase() {
    return hydrocarbonCase;
  }

  public void setHydrocarbonCase(
      HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    this.hydrocarbonCase = hydrocarbonsDiscovered;
  }
}
