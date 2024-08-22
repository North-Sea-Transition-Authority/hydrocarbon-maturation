package uk.co.nstauthority.hydrocarbonmaturation.teams;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

public class TeamRoleTestUtil {

  private TeamRoleTestUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static TeamRoleTestUtil.TeamRoleBuilder Builder() {
    return new TeamRoleTestUtil.TeamRoleBuilder();
  }

  public static class TeamRoleBuilder {

    private UUID uuid = UUID.randomUUID();
    private long wuaId = 123L;
    private Role role = Role.MANAGE_TEAM;
    private Team team = TeamTestUtil.Builder().buildStaticTeam();

    public TeamRoleTestUtil.TeamRoleBuilder withId(UUID id) {
      this.uuid = id;
      return this;
    }

    public TeamRoleTestUtil.TeamRoleBuilder withWuaId(long wuaId) {
      this.wuaId = wuaId;
      return this;
    }

    public TeamRoleTestUtil.TeamRoleBuilder withTeam(Team team) {
      this.team = team;
      return this;
    }

    public TeamRoleTestUtil.TeamRoleBuilder withRole(Role role) {
      this.role = role;
      return this;
    }

    public TeamRole build() {
      return new TeamRole(uuid, team, role, wuaId);
    }

  }
}
