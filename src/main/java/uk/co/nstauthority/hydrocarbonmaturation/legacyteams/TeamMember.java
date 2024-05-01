package uk.co.nstauthority.hydrocarbonmaturation.legacyteams;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamRole;

public record TeamMember(WebUserAccountId wuaId, TeamView teamView, Set<TeamRole> roles) implements Serializable {

  @Serial
  private static final long serialVersionUID = 5589243123900587906L;
}
