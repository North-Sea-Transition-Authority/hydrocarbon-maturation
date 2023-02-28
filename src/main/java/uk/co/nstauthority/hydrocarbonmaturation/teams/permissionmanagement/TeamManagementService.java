package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
public class TeamManagementService {
  private final RegulatorTeamService regulatorTeamService;
  private final TeamService teamService;
  private final TeamMemberService teamMemberService;

  @Autowired
  TeamManagementService(RegulatorTeamService regulatorTeamService, TeamService teamService, TeamMemberService teamMemberService) {
    this.regulatorTeamService = regulatorTeamService;
    this.teamService = teamService;
    this.teamMemberService = teamMemberService;
  }

  public boolean isIndustryAccessManagerInRegulatorTeam(ServiceUserDetail user) {
    return regulatorTeamService.getRegulatorTeamForUser(user)
        .filter(regulatorTeam -> regulatorTeamService.isIndustryAccessManager(regulatorTeam.toTeamId(), user))
        .isPresent();
  }

  public boolean isMemberOfTeamOrIndustryAccessManager(TeamId teamId, ServiceUserDetail user) {

    return teamMemberService.isMemberOfTeam(teamId, user)
        || isIndustryAccessManagerInRegulatorTeam(user);
  }

  List<TeamView> getTeamsThatUserHasAccessTo(ServiceUserDetail user) {
    if (isIndustryAccessManagerInRegulatorTeam(user)) {
      return teamsToTeamViews(teamService.getAllTeams());
    }

    return teamsToTeamViews(teamService.getTeamsThatUserBelongsTo(user));
  }

  private List<TeamView> teamsToTeamViews(List<Team> teams) {
    return teams.stream()
        .map(TeamView::fromTeam)
        .sorted(TeamView.sort())
        .toList();
  }
}
