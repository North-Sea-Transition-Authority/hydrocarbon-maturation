package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.offshore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;

@Component
class AbstractOffshoreAreaPermissionManagement {

  private final OffshoreAreaTeamService offshoreAreaTeamService;

  @Autowired
  public AbstractOffshoreAreaPermissionManagement(OffshoreAreaTeamService offshoreAreaTeamService) {
    this.offshoreAreaTeamService = offshoreAreaTeamService;
  }

  protected Team getAreaTeam(TeamId teamId) {
    return offshoreAreaTeamService.getTeam(teamId)
        .orElseThrow(() -> new RemiEntityNotFoundException(
            "No area team with ID %s found".formatted(teamId.uuid())
        ));
  }
}
