package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.industry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;

@Component
abstract class AbstractIndustryPermissionManagement {

  private final IndustryTeamService industryTeamService;

  @Autowired
  AbstractIndustryPermissionManagement(IndustryTeamService industryTeamService) {
    this.industryTeamService = industryTeamService;
  }

  LegacyTeam getIndustryTeam(TeamId teamId) {
    return industryTeamService.getTeam(teamId)
        .orElseThrow(() -> new RemiEntityNotFoundException(
            "No industry team with ID %s found".formatted(teamId.uuid())
        ));
  }
}
