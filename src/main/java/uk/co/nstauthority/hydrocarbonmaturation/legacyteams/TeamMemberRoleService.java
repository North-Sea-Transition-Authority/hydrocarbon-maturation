package uk.co.nstauthority.hydrocarbonmaturation.legacyteams;

import java.util.ArrayList;
import java.util.List;
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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.access.EnergyPortalAccessApiConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.AddedToTeamEventPublisher;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RemovedFromTeamEventPublisher;

@Service
public class TeamMemberRoleService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TeamMemberRoleService.class);

  private final TeamMemberRoleRepository teamMemberRoleRepository;

  private final AddedToTeamEventPublisher addedToTeamEventPublisher;

  private final UserService userService;

  private final RemovedFromTeamEventPublisher removedFromTeamEventPublisher;

  private final EnergyPortalAccessApiConfiguration energyPortalAccessApiConfiguration;

  private final EnergyPortalAccessService energyPortalAccessService;

  @Autowired
  public TeamMemberRoleService(TeamMemberRoleRepository teamMemberRoleRepository,
                               AddedToTeamEventPublisher addedToTeamEventPublisher,
                               UserService userService,
                               RemovedFromTeamEventPublisher removedFromTeamEventPublisher,
                               EnergyPortalAccessApiConfiguration energyPortalAccessApiConfiguration,
                               EnergyPortalAccessService energyPortalAccessService) {
    this.teamMemberRoleRepository = teamMemberRoleRepository;
    this.addedToTeamEventPublisher = addedToTeamEventPublisher;
    this.userService = userService;
    this.removedFromTeamEventPublisher = removedFromTeamEventPublisher;
    this.energyPortalAccessApiConfiguration = energyPortalAccessApiConfiguration;
    this.energyPortalAccessService = energyPortalAccessService;
  }

  @Transactional
  public void addUserTeamRoles(LegacyTeam legacyTeam, EnergyPortalUserDto userToAdd, Set<String> roles) {
    var targetUser = new WebUserAccountId(userToAdd.webUserAccountId());
    var instigationUser = new WebUserAccountId(userService.getUser().wuaId());
    updateUserTeamRoles(legacyTeam, new WebUserAccountId(userToAdd.webUserAccountId()), roles);

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
        legacyTeam.toTeamId(),
        targetUser,
        roles,
        instigationUser
    );
  }

  @Transactional
  public void updateUserTeamRoles(LegacyTeam legacyTeam, WebUserAccountId webUserAccountId, Set<String> roles) {
    var wuaId = webUserAccountId.id();
    // Clear user's existing roles
    teamMemberRoleRepository.deleteAllByTeamAndWuaId(legacyTeam, wuaId);

    var teamMemberRoles = new ArrayList<TeamMemberRole>();

    // Create new roles based on role selection
    roles.forEach(role -> {
      var teamMemberRole = new TeamMemberRole();
      teamMemberRole.setTeam(legacyTeam);
      teamMemberRole.setWuaId(wuaId);
      teamMemberRole.setRole(role);
      teamMemberRoles.add(teamMemberRole);
    });

    teamMemberRoleRepository.saveAll(teamMemberRoles);
  }

  @Transactional
  public void removeMemberFromTeam(LegacyTeam legacyTeam, TeamMember teamMember) {
    var removedUserId = new WebUserAccountId(teamMember.wuaId().id());
    var instigationUser = new WebUserAccountId(userService.getUser().wuaId());
    teamMemberRoleRepository.deleteAllByTeamAndWuaId(legacyTeam, removedUserId.id());

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

  public List<TeamMemberRole> getTeamMembersWithRoleInTeams(List<LegacyTeam> listOfLegacyTeams, Set<String> roles) {
    return teamMemberRoleRepository.findAllByTeamInAndRoleIn(listOfLegacyTeams, roles);
  }
}
