package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;

@Component
abstract class AbstractRegulatorPermissionManagement {

  private final RegulatorTeamService regulatorTeamService;

  @Autowired
  AbstractRegulatorPermissionManagement(RegulatorTeamService regulatorTeamService) {
    this.regulatorTeamService = regulatorTeamService;
  }

  LegacyTeam getRegulatorTeam(TeamId teamId) {
    return regulatorTeamService.getTeam(teamId)
        .orElseThrow(() -> new RemiEntityNotFoundException(
            "No regulator team with ID %s found".formatted(teamId.uuid())
        ));
  }
}
