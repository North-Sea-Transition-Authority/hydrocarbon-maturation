package uk.co.nstauthority.hydrocarbonmaturation.teams.management;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportal.starter.accounts.EnergyPortalServiceAccessService;
import uk.co.fivium.energyportal.starter.serviceproviders.EnergyPortalServiceProviderUserRolesService;
import uk.co.fivium.energyportalapi.client.RequestPurpose;
import uk.co.fivium.energyportalapi.client.user.UserApi;
import uk.co.fivium.energyportalapi.generated.client.UserProjectionRoot;
import uk.co.fivium.energyportalapi.generated.client.UsersProjectionRoot;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRepository;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRoleRepository;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamScopeReference;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.UserCancelledEvent;
import uk.co.nstauthority.hydrocarbonmaturation.teams.management.view.TeamMemberView;

@Service
public class TeamManagementService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TeamManagementService.class);

  private final TeamRepository teamRepository;
  private final TeamRoleRepository teamRoleRepository;
  private final TeamQueryService teamQueryService;
  private final UserApi userApi;
  private final EnergyPortalServiceAccessService energyPortalServiceAccessService;
  private final EnergyPortalServiceProviderUserRolesService energyPortalServiceProviderUserRolesService;
  private final EnergyPortalUserService energyPortalUserService;

  public TeamManagementService(TeamRepository teamRepository,
                               TeamRoleRepository teamRoleRepository,
                               UserApi userApi,
                               TeamQueryService teamQueryService,
                               EnergyPortalServiceAccessService energyPortalServiceAccessService,
                               EnergyPortalServiceProviderUserRolesService energyPortalServiceProviderUserRolesService,
                               EnergyPortalUserService energyPortalUserService
  ) {
    this.teamRepository = teamRepository;
    this.teamRoleRepository = teamRoleRepository;
    this.userApi = userApi;
    this.teamQueryService = teamQueryService;
    this.energyPortalServiceAccessService = energyPortalServiceAccessService;
    this.energyPortalServiceProviderUserRolesService = energyPortalServiceProviderUserRolesService;
    this.energyPortalUserService = energyPortalUserService;
  }

  public Team createScopedTeam(String name, TeamType teamType, TeamScopeReference scopeRef) {
    if (!teamType.isScoped()) {
      throw new TeamManagementException("Team of type %s is not scoped".formatted(teamType));
    }

    if (doesScopedTeamWithReferenceExist(teamType, scopeRef)) {
      throw new TeamManagementException("Team of type %s scope type %s and scope id %s already exists"
          .formatted(teamType, scopeRef.getId(), scopeRef.getType()));
    }

    var team = new Team();
    team.setName(name);
    team.setTeamType(teamType);
    team.setScopeType(scopeRef.getType());
    team.setScopeId(scopeRef.getId());
    return teamRepository.save(team);
  }

  public void updateTeamName(Team team, String teamName) {
    team.setName(teamName);
    teamRepository.save(team);
  }

  Set<TeamType> getTeamTypesUserIsMemberOf(long wuaId) {
    return teamRoleRepository.findAllByWuaId(wuaId)
        .stream()
        .map(teamRole -> teamRole.getTeam().getTeamType())
        .collect(Collectors.toSet());
  }

  public Optional<Team> getStaticTeamOfTypeUserCanManage(TeamType teamType, Long wuaId) {
    if (teamType.isScoped()) {
      throw new TeamManagementException("TeamType %s is scoped, expected static".formatted(teamType));
    }
    return getTeamsOfTypeUserCanManage(teamType, wuaId).stream()
        .findFirst();
  }

  Optional<Team> getStaticTeamOfTypeUserIsMemberOf(TeamType teamType, Long wuaId) {
    if (teamType.isScoped()) {
      throw new TeamManagementException("TeamType %s is scoped, expected static".formatted(teamType));
    }
    return getTeamsOfTypeUserIsMemberOf(teamType, wuaId)
        .stream()
        .findFirst();
  }

  public List<Team> getScopedTeamsOfTypeUserCanManage(TeamType teamType, Long wuaId) {
    if (!teamType.isScoped()) {
      throw new TeamManagementException("TeamType %s is static, expected scoped".formatted(teamType));
    }
    var teams = new ArrayList<>(getTeamsOfTypeUserCanManage(teamType, wuaId));

    if (teamType.equals(TeamType.INDUSTRY) && userCanManageAnyOrganisationTeam(wuaId)) {
      // If we want org teams, and the user is a regulator who can manage any org team, include all the org teams.
      teams.addAll(getAllScopedTeamsOfType(TeamType.INDUSTRY));
    }

    return teams.stream()
        .distinct() // Remove possible dupes from adding all scoped teams the user may already be a team manager of
        .toList();
  }

  Set<Team> getScopedTeamsOfTypeUserIsMemberOf(TeamType teamType, Long wuaId) {

    if (!teamType.isScoped()) {
      throw new TeamManagementException("TeamType %s is static, expected scoped".formatted(teamType));
    }

    var teams = new HashSet<>(getTeamsOfTypeUserIsMemberOf(teamType, wuaId));

    if (teamType.equals(TeamType.INDUSTRY) && userCanManageAnyOrganisationTeam(wuaId)) {
      // If we want org teams, and the user is a regulator who can manage any org team, include all the org teams.
      teams.addAll(getAllScopedTeamsOfType(TeamType.INDUSTRY));
    }

    return new HashSet<>(teams);
  }

  public Optional<Team> getTeam(UUID teamId) {
    return teamRepository.findById(teamId);
  }

  public Optional<EnergyPortalUserDto> getEnergyPortalUser(String emailAddress) {
    var energyPortalUserDtos = energyPortalUserService.findUsersByEmailAddress(emailAddress);

    if (energyPortalUserDtos.size() > 1) {
      throw new TeamManagementException(
          "More than one UK Energy Portal user exists with the email address %s".formatted(emailAddress)
      );
    }

    return energyPortalUserDtos
        .stream()
        .findFirst();
  }

  public TeamMemberView getTeamMemberView(Team team, Long wuaId) {
    var teamRoles = teamRoleRepository.findByWuaIdAndTeam(wuaId, team).stream()
        .map(TeamRole::getRole)
        .toList();

    var userProjection = new UserProjectionRoot()
        .webUserAccountId()
        .forename()
        .surname()
        .primaryEmailAddress()
        .telephoneNumber();

    var user = userApi.findUserById(wuaId, userProjection, new RequestPurpose("Fetch user in team"))
        .orElseThrow(() -> new TeamManagementException("WuaId %s not found via EPA".formatted(wuaId)));

    return TeamMemberView.fromEpaUser(user, team.getId(), teamRoles);
  }

  public List<TeamMemberView> getTeamMemberViewsForTeam(Team team) {
    var teamRoles = teamRoleRepository.findByTeam(team);

    var memberWuaIds = teamRoles.stream()
        .map(TeamRole::getWuaId)
        .distinct()
        .toList();

    var userProjection = new UsersProjectionRoot()
        .webUserAccountId()
        .forename()
        .surname()
        .primaryEmailAddress()
        .telephoneNumber();

    var epaUsers = userApi.searchUsersByIds(memberWuaIds, userProjection, new RequestPurpose("Fetch users in team"));

    return memberWuaIds.stream()
        .map(wuaId -> {
          var epaUser = epaUsers.stream()
              .filter(u -> u.getWebUserAccountId().equals(wuaId))
              .findFirst()
              .orElseThrow(() -> new TeamManagementException("WuaId %s not found in EPA user set".formatted(wuaId)));

          List<Role> userRoles = teamRoles.stream()
              .filter(teamRole -> teamRole.getWuaId().equals(wuaId))
              .map(TeamRole::getRole)
              .toList();

          List<Role> orderedUserRoles = team.getTeamType().getAllowedRoles()
              .stream()
              .filter(userRoles::contains)
              .toList();

          return TeamMemberView.fromEpaUser(epaUser, team.getId(), orderedUserRoles);
        })
        .sorted(Comparator.comparing(TeamMemberView::forename).thenComparing(TeamMemberView::surname))
        .toList();
  }

  @Transactional
  public void setUserTeamRoles(Long wuaId, Team team, List<Role> roles) {
    if (!new HashSet<>(team.getTeamType().getAllowedRoles()).containsAll(roles)) {
      throw new TeamManagementException("Roles %s are not valid for team type %s".formatted(roles, team.getTeamType()));
    }

    // Check the user is valid
    var projection = new UserProjectionRoot()
        .isAccountShared()
        .canLogin();
    var userOptional = userApi.findUserById(wuaId, projection, new RequestPurpose("Validate user account"));
    if (userOptional.isEmpty()) {
      throw new TeamManagementException("User account with wuaId %s does not exist".formatted(wuaId));
    }
    var user = userOptional.get();
    if (user.getIsAccountShared()) {
      throw new TeamManagementException("User account with wuaId %s is a shared account so can't be added to teams"
          .formatted(wuaId)
      );
    }
    if (!user.getCanLogin()) {
      throw new TeamManagementException("User account with wuaId %s is not active so can't be added to teams"
          .formatted(wuaId)
      );
    }

    var isNewUser = teamRoleRepository.findAllByWuaId(wuaId).isEmpty();

    teamRoleRepository.deleteByWuaIdAndTeam(wuaId, team);

    var newTeamRoles = roles.stream()
        .map(role -> {
          var teamRole = new TeamRole();
          teamRole.setTeam(team);
          teamRole.setRole(role);
          teamRole.setWuaId(wuaId);
          return teamRole;
        }).toList();
    teamRoleRepository.saveAll(newTeamRoles);

    if (!doesTeamHaveTeamManager(team)) {
      throw new TeamManagementException("At least 1 team manager must exist in team %s".formatted(team.getId()));
    }

    energyPortalServiceProviderUserRolesService.publishUsersRolesForTeam(
        wuaId,
        team.getId().toString(),
        team.getTeamType().name(),
        roles.stream().map(Role::name).collect(Collectors.toSet())
    );

    if (!isNewUser) {
      return;
    }

    energyPortalServiceAccessService.addUser(wuaId);
  }

  @Transactional
  public void removeUserFromTeam(Long wuaId, Team team) {
    if (!willManageTeamRoleBePresentAfterMemberRemoval(team, wuaId)) {
      throw new TeamManagementException("Can't remove last team manager user %s from team %s".formatted(wuaId, team.getId()));
    }
    handleUserRemovalFromTeam(wuaId, team);
  }

  @Transactional
  @EventListener(UserCancelledEvent.class)
  void onUserCancelledEvent(UserCancelledEvent event) {
    var wuaId = event.wuaId();
    for (var team : getTeamsUserIsMemberOf(wuaId)) {
      handleUserRemovalFromTeam(wuaId, team);
      LOGGER.info("Removed user {} from team {}", wuaId, team.getId());
    }
  }

  private void handleUserRemovalFromTeam(Long wuaId, Team team) {
    teamRoleRepository.deleteByWuaIdAndTeam(wuaId, team);
    energyPortalServiceProviderUserRolesService.publishRemoveUserFromTeam(wuaId, team.getId().toString());

    if (teamRoleRepository.findAllByWuaId(wuaId).isEmpty()) {
      energyPortalServiceAccessService.removeUser(wuaId);
    }
  }

  public boolean willManageTeamRoleBePresentAfterMemberRoleUpdate(Team team, Long wuaId, List<Role> membersNewRoles) {
    if (membersNewRoles.contains(Role.MANAGE_TEAM)) {
      return true;
    }
    return willManageTeamRoleBePresentAfterMemberRemoval(team, wuaId);
  }

  public boolean willManageTeamRoleBePresentAfterMemberRemoval(Team team, Long wuaId) {
    return teamRoleRepository.findByTeam(team).stream()
        .filter(teamRole -> !teamRole.getWuaId().equals(wuaId))
        .anyMatch(teamRole -> teamRole.getRole().equals(Role.MANAGE_TEAM));
  }

  public boolean doesScopedTeamWithReferenceExist(TeamType teamType, TeamScopeReference scopeRef) {
    return teamRepository.findByTeamTypeAndScopeTypeAndScopeId(teamType, scopeRef.getType(), scopeRef.getId())
        .isPresent();
  }

  public boolean canManageTeam(Team team, long wuaId) {
    if (team.getTeamType().isScoped()) {
      return getScopedTeamsOfTypeUserCanManage(team.getTeamType(), wuaId)
          .stream()
          .anyMatch(scopedTeam -> scopedTeam.getId().equals(team.getId()));
    } else {
      return getStaticTeamOfTypeUserCanManage(team.getTeamType(), wuaId).isPresent();
    }
  }

  public boolean isMemberOfTeam(Team team, long wuaId) {
    return teamRoleRepository.existsByTeamAndWuaId(team, wuaId);
  }

  public boolean userCanManageAnyOrganisationTeam(long wuaId) {
    return teamQueryService.userHasStaticRole(wuaId, TeamType.REGULATOR, Role.REGULATOR_MANAGE_ALL_ORGANISATIONS);
  }

  private List<Team> getAllScopedTeamsOfType(TeamType teamType) {
    if (!teamType.isScoped()) {
      throw new TeamManagementException("TeamType %s is static, expected scoped".formatted(teamType));
    }
    return teamRepository.findByTeamType(teamType);
  }

  private boolean doesTeamHaveTeamManager(Team team) {
    return teamRoleRepository.findByTeam(team).stream()
        .anyMatch(teamRole -> teamRole.getRole().equals(Role.MANAGE_TEAM));
  }

  private List<Team> getTeamsUserCanManage(Long wuaId) {
    var userTeamRoles = teamRoleRepository.findByWuaIdAndRole(wuaId, Role.MANAGE_TEAM);
    return userTeamRoles.stream()
        .map(TeamRole::getTeam)
        .toList();
  }

  private Set<Team> getTeamsUserIsMemberOf(Long wuaId) {
    var userTeamRoles = teamRoleRepository.findAllByWuaId(wuaId);
    return userTeamRoles.stream()
        .map(TeamRole::getTeam)
        .collect(Collectors.toSet());
  }

  private List<Team> getTeamsOfTypeUserCanManage(TeamType teamType, Long wuaId) {
    return getTeamsUserCanManage(wuaId).stream()
        .filter(team -> team.getTeamType().equals(teamType))
        .toList();
  }

  private Set<Team> getTeamsOfTypeUserIsMemberOf(TeamType teamType, Long wuaId) {
    return getTeamsUserIsMemberOf(wuaId).stream()
        .filter(team -> team.getTeamType().equals(teamType))
        .collect(Collectors.toSet());
  }

}