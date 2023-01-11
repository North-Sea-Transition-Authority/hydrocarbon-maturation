package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
public class TeamTypeService {
  private final RegulatorTeamService regulatorTeamService;

  @Autowired
  TeamTypeService(RegulatorTeamService regulatorTeamService) {
    this.regulatorTeamService = regulatorTeamService;
  }

  public boolean isIndustryAccessManagerInRegulatorTeam(ServiceUserDetail user) {
    return regulatorTeamService.getRegulatorTeamForUser(user)
        .filter(regulatorTeam -> {
          var teamId = TeamId.valueOf(regulatorTeam.getUuid());
          return regulatorTeamService.isIndustryAccessManager(teamId, user);
        })
        .isPresent();
  }
}
