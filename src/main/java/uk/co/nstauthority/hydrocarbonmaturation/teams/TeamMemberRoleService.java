package uk.co.nstauthority.hydrocarbonmaturation.teams;

import java.util.ArrayList;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.fivium.digital.energyportalteamaccesslibrary.team.EnergyPortalAccessService;
import uk.co.fivium.digital.energyportalteamaccesslibrary.team.InstigatingWebUserAccountId;
import uk.co.fivium.digital.energyportalteamaccesslibrary.team.ResourceType;
import uk.co.fivium.digital.energyportalteamaccesslibrary.team.TargetWebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.access.EnergyPortalAccessApiConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.AddedToTeamEventPublisher;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RemovedFromTeamEventPublisher;

@Service
public class TeamMemberRoleService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TeamMemberRoleService.class);

  private final TeamMemberRoleRepository teamMemberRoleRepository;

  private final AddedToTeamEventPublisher addedToTeamEventPublisher;

  private final UserDetailService userDetailService;

  private final RemovedFromTeamEventPublisher removedFromTeamEventPublisher;

  private final EnergyPortalAccessApiConfiguration energyPortalAccessApiConfiguration;

  private final EnergyPortalAccessService energyPortalAccessService;

  @Autowired
  public TeamMemberRoleService(TeamMemberRoleRepository teamMemberRoleRepository,
                               AddedToTeamEventPublisher addedToTeamEventPublisher,
                               UserDetailService userDetailService,
                               RemovedFromTeamEventPublisher removedFromTeamEventPublisher,
                               EnergyPortalAccessApiConfiguration energyPortalAccessApiConfiguration,
                               EnergyPortalAccessService energyPortalAccessService) {
    this.teamMemberRoleRepository = teamMemberRoleRepository;
    this.addedToTeamEventPublisher = addedToTeamEventPublisher;
    this.userDetailService = userDetailService;
    this.removedFromTeamEventPublisher = removedFromTeamEventPublisher;
    this.energyPortalAccessApiConfiguration = energyPortalAccessApiConfiguration;
    this.energyPortalAccessService = energyPortalAccessService;
  }

  @Transactional
  public void addUserTeamRoles(Team team, EnergyPortalUserDto userToAdd, Set<String> roles) {
    var targetUser = new WebUserAccountId(userToAdd.webUserAccountId());
    var instigationUser = new WebUserAccountId(userDetailService.getUserDetail().wuaId());
    updateUserTeamRoles(team, new WebUserAccountId(userToAdd.webUserAccountId()), roles);

    var resourceType = energyPortalAccessApiConfiguration.resourceType();

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Adding user with WUA_ID %s to %s".formatted(userToAdd.webUserAccountId(), resourceType));
    }

    energyPortalAccessService.addUserToAccessTeam(
        new ResourceType(resourceType),
        new TargetWebUserAccountId(targetUser.id()),
        new InstigatingWebUserAccountId(instigationUser.id())
    );

    addedToTeamEventPublisher.publish(
        team.toTeamId(),
        targetUser,
        roles,
        instigationUser
    );
  }

  @Transactional
  public void updateUserTeamRoles(Team team, WebUserAccountId webUserAccountId, Set<String> roles) {
    var wuaId = webUserAccountId.id();
    // Clear user's existing roles
    teamMemberRoleRepository.deleteAllByTeamAndWuaId(team, wuaId);

    var teamMemberRoles = new ArrayList<TeamMemberRole>();

    // Create new roles based on role selection
    roles.forEach(role -> {
      var teamMemberRole = new TeamMemberRole();
      teamMemberRole.setTeam(team);
      teamMemberRole.setWuaId(wuaId);
      teamMemberRole.setRole(role);
      teamMemberRoles.add(teamMemberRole);
    });

    teamMemberRoleRepository.saveAll(teamMemberRoles);
  }

  @Transactional
  public void removeMemberFromTeam(Team team, TeamMember teamMember) {
    var removedUserId = new WebUserAccountId(teamMember.wuaId().id());
    var instigationUser = new WebUserAccountId(userDetailService.getUserDetail().wuaId());
    teamMemberRoleRepository.deleteAllByTeamAndWuaId(team, removedUserId.id());

    var resourceType = energyPortalAccessApiConfiguration.resourceType();

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Removing user with WUA_ID %s from %s".formatted(removedUserId, resourceType));
    }

    if (!teamMemberRoleRepository.existsByWuaId(removedUserId.id())) {
      energyPortalAccessService.removeUserFromAccessTeam(
          new ResourceType(resourceType),
          new TargetWebUserAccountId(removedUserId.id()),
          new InstigatingWebUserAccountId(instigationUser.id())
      );
    }

    removedFromTeamEventPublisher.publish(teamMember, instigationUser);
  }

}
