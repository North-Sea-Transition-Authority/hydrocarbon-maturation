package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.onshore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;

@Component
class AbstractOnshoreAreaPermissionManagement {

  private final OnshoreAreaTeamService onshoreAreaTeamService;

  @Autowired
  public AbstractOnshoreAreaPermissionManagement(OnshoreAreaTeamService onshoreAreaTeamService) {
    this.onshoreAreaTeamService = onshoreAreaTeamService;
  }

  protected LegacyTeam getAreaTeam(TeamId teamId) {
    return onshoreAreaTeamService.getTeam(teamId)
        .orElseThrow(() -> new RemiEntityNotFoundException(
            "No area team with ID %s found".formatted(teamId.uuid())
        ));
  }
}
