package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.northernoffshore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;

@Component
class AbstractNorthernOffshoreAreaPermissionManagement {

  private final NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService;

  @Autowired
  public AbstractNorthernOffshoreAreaPermissionManagement(
      NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService) {
    this.northernOffshoreAreaTeamService = northernOffshoreAreaTeamService;
  }

  protected LegacyTeam getAreaTeam(TeamId teamId) {
    return northernOffshoreAreaTeamService.getTeam(teamId)
        .orElseThrow(() -> new RemiEntityNotFoundException(
            "No area team with ID %s found".formatted(teamId.uuid())
        ));
  }
}
