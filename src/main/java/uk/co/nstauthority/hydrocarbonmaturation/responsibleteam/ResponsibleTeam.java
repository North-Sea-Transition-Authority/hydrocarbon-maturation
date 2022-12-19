package uk.co.nstauthority.hydrocarbonmaturation.responsibleteam;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Entity
@Table(name = "responsible_teams")
class ResponsibleTeam {

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

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
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
}
