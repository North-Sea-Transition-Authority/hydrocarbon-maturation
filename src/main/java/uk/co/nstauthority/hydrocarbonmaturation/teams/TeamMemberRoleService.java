package uk.co.nstauthority.hydrocarbonmaturation.teams;

import java.util.ArrayList;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.AddedToTeamEventPublisher;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RemovedFromTeamEventPublisher;

@Service
public class TeamMemberRoleService {

  private final TeamMemberRoleRepository teamMemberRoleRepository;

  private final AddedToTeamEventPublisher addedToTeamEventPublisher;

  private final UserDetailService userDetailService;

  private final RemovedFromTeamEventPublisher removedFromTeamEventPublisher;

  @Autowired
  public TeamMemberRoleService(TeamMemberRoleRepository teamMemberRoleRepository,
                               AddedToTeamEventPublisher addedToTeamEventPublisher,
                               UserDetailService userDetailService,
                               RemovedFromTeamEventPublisher removedFromTeamEventPublisher) {
    this.teamMemberRoleRepository = teamMemberRoleRepository;
    this.addedToTeamEventPublisher = addedToTeamEventPublisher;
    this.userDetailService = userDetailService;
    this.removedFromTeamEventPublisher = removedFromTeamEventPublisher;
  }

  @Transactional
  public void addUserTeamRoles(Team team, EnergyPortalUserDto userToAdd, Set<String> roles) {
    updateUserTeamRoles(team, new WebUserAccountId(userToAdd.webUserAccountId()), roles);

    addedToTeamEventPublisher.publish(
        new TeamId(team.getUuid()),
        new WebUserAccountId(userToAdd.webUserAccountId()),
        roles,
        userDetailService.getUserDetail()
    );
  }

  @Transactional
  public void updateUserTeamRoles(Team team, WebUserAccountId wuaId, Set<String> roles) {
    // Clear user's existing roles
    teamMemberRoleRepository.deleteAllByTeamAndWuaId(team, wuaId.id());

    var teamMemberRoles = new ArrayList<TeamMemberRole>();

    // Create new roles based on role selection
    roles.forEach(role -> {
      var teamMemberRole = new TeamMemberRole();
      teamMemberRole.setTeam(team);
      teamMemberRole.setWuaId(wuaId.id());
      teamMemberRole.setRole(role);
      teamMemberRoles.add(teamMemberRole);
    });

    teamMemberRoleRepository.saveAll(teamMemberRoles);
  }

  @Transactional
  public void removeMemberFromTeam(Team team, TeamMember teamMember) {
    teamMemberRoleRepository.deleteAllByTeamAndWuaId(team, teamMember.wuaId().id());
    removedFromTeamEventPublisher.publish(teamMember, userDetailService.getUserDetail());
  }

}
