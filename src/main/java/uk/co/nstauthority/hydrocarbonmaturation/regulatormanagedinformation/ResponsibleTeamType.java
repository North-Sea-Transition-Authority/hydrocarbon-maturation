package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import jakarta.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

public enum ResponsibleTeamType implements Displayable {
  EXPLORATION("Exploration team", TeamType.REGULATOR, Role.EXPLORATION),
  NORTHERN_NORTH_SEA_WEST_OF_SHETLAND("Northern North Sea and West of Shetland team",
      TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM),
  CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA("Central North Sea, Southern North Sea and East Irish Sea team",
      TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM),
  ONSHORE("Onshore team", TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM);

  private final String displayName;
  private final TeamType teamType;
  private final Role role;


  ResponsibleTeamType(String displayName, TeamType teamType, Role role) {
    this.displayName = displayName;
    this.teamType = teamType;
    this.role = role;
  }

  public String getDisplayName() {
    return displayName;
  }

  public TeamType getTeamType() {
    return teamType;
  }

  public Role getRole() {
    return role;
  }

  public static ResponsibleTeamType fromName(@Nullable String name) {
    if (name == null) {
      return null;
    }

    return valueOf(name);
  }

  public static List<ResponsibleTeamType> getResponsibleTeamTypesForTeamsAndRoles(Map<TeamType, Role> teamTypeRoleMap) {
    return Arrays.stream(ResponsibleTeamType.values())
        .filter(r -> teamTypeRoleMap.containsKey(r.teamType))
        .filter(r -> teamTypeRoleMap.containsValue(r.role))
        .toList();
  }
}
