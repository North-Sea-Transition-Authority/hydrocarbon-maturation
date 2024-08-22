package uk.co.nstauthority.hydrocarbonmaturation.teams;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

public class TeamTestUtil {

  private TeamTestUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static Builder Builder() {
    return new Builder();
  }

  public static class Builder {

    private UUID uuid = UUID.randomUUID();
    private uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType teamType = uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType.REGULATOR;
    private String displayName = "Team A";
    private String scopeId = "1";
    private String scopeType = "ORGGRP";

    public uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil.Builder withId(UUID uuid) {
      this.uuid = uuid;
      return this;
    }

    public uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil.Builder withTeamType(TeamType teamType) {
      this.teamType = teamType;
      return this;
    }

    public uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil.Builder withDisplayName(String displayName) {
      this.displayName = displayName;
      return this;
    }

    public uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil.Builder withScopeId(String scopeId) {
      this.scopeId = scopeId;
      return this;
    }

    public uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil.Builder withScopeType(String scopeType) {
      this.scopeType = scopeType;
      return this;
    }

    public Team buildStaticTeam() {
      var team = new Team(uuid);
      team.setTeamType(teamType);
      team.setName(displayName);
      return team;
    }

    public Team buildScopedTeam() {
      var team = new Team(uuid);
      team.setTeamType(teamType);
      team.setName(displayName);
      team.setScopeId(scopeId);
      team.setScopeType(scopeType);
      return team;
    }
  }
}
