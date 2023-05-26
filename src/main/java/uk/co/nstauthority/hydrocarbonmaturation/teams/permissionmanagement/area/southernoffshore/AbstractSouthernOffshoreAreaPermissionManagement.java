package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.southernoffshore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;

@Component
class AbstractSouthernOffshoreAreaPermissionManagement {

  private final SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService;

  @Autowired
  public AbstractSouthernOffshoreAreaPermissionManagement(
      SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService) {
    this.southernOffshoreAreaTeamService = southernOffshoreAreaTeamService;
  }

  protected Team getAreaTeam(TeamId teamId) {
    return southernOffshoreAreaTeamService.getTeam(teamId)
        .orElseThrow(() -> new RemiEntityNotFoundException(
            "No area team with ID %s found".formatted(teamId.uuid())
        ));
  }
}
