package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;

@Component
abstract class AbstractIndustryPermissionManagement {

  private final IndustryTeamService industryTeamService;

  @Autowired
  AbstractIndustryPermissionManagement(IndustryTeamService industryTeamService) {
    this.industryTeamService = industryTeamService;
  }

  Team getIndustryTeam(TeamId teamId) {
    return industryTeamService.getTeam(teamId)
        .orElseThrow(() -> new RemiEntityNotFoundException(
            "No industry team with ID %s found".formatted(teamId.uuid())
        ));
  }
}
