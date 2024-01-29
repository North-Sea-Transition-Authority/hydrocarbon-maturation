package uk.co.nstauthority.hydrocarbonmaturation.teams;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "team_member_roles")
public class TeamMemberRole {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID uuid;

  @JoinColumn(name = "team_id")
  @ManyToOne
  private Team team;

  private Long wuaId;

  private String role;

  protected TeamMemberRole() {
  }

  TeamMemberRole(UUID uuid) {
    this.uuid = uuid;
  }

  public UUID getUuid() {
    return uuid;
  }

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public Long getWuaId() {
    return wuaId;
  }

  public void setWuaId(Long wuaId) {
    this.wuaId = wuaId;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
