package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;

@Component
abstract class AbstractRegulatorPermissionManagement {

  private final RegulatorTeamService regulatorTeamService;

  @Autowired
  AbstractRegulatorPermissionManagement(RegulatorTeamService regulatorTeamService) {
    this.regulatorTeamService = regulatorTeamService;
  }

  Team getRegulatorTeam(TeamId teamId) {
    return regulatorTeamService.getTeam(teamId)
        .orElseThrow(() -> new RemiEntityNotFoundException(
            "No regulator team with ID %s found".formatted(teamId.uuid())
        ));
  }
}
