package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Entity
@Table(name = "regulator_managed_information")
public class RegulatorManagedInformation {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "resource_id")
  private Resource resource;

  @Enumerated(EnumType.STRING)
  @Column(name = "responsible_team")
  private ResponsibleTeamType responsibleTeamType;

  @Enumerated(EnumType.STRING)
  private GeographicalArea geographicalArea;

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
}
