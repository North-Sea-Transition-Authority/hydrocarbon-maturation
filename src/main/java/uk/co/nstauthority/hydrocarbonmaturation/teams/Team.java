package uk.co.nstauthority.hydrocarbonmaturation.teams;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "teams")
public class Team {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID uuid;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private TeamType teamType;

  private String displayName;

  private Integer organisationGroupId;

  public Team() {
  }

  public Team(UUID uuid) {
    this.uuid = uuid;
  }

  public UUID getUuid() {
    return uuid;
  }

  public TeamType getTeamType() {
    return teamType;
  }

  public void setTeamType(TeamType teamType) {
    this.teamType = teamType;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public Integer getOrganisationGroupId() {
    return organisationGroupId;
  }

  public void setOrganisationGroupId(Integer organisationGroupId) {
    this.organisationGroupId = organisationGroupId;
  }

  public TeamId toTeamId() {
    return new TeamId(uuid);
  }
}
