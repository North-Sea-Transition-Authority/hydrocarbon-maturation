package uk.co.nstauthority.hydrocarbonmaturation.teams;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;

@Service
public class TeamQueryService {
  private final TeamRepository teamRepository;
  private final TeamRoleRepository teamRoleRepository;
  private final EnergyPortalUserService energyPortalUserService;

  @Autowired
  public TeamQueryService(TeamRepository teamRepository,
                          TeamRoleRepository teamRoleRepository,
                          EnergyPortalUserService energyPortalUserService) {
    this.teamRepository = teamRepository;
    this.teamRoleRepository = teamRoleRepository;
    this.energyPortalUserService = energyPortalUserService;
  }

  public boolean userHasStaticRole(Long wuaId, TeamType teamType, Role role) {
    return userHasAtLeastOneStaticRole(wuaId, teamType, Set.of(role));
  }

  public boolean userHasAtLeastOneStaticRole(Long wuaId, TeamType teamType, Set<Role> roles) {
    assertRolesValidForTeamType(roles, teamType);
    if (teamType.isScoped()) {
      throw new IllegalArgumentException("TeamType %s is not static".formatted(teamType));
    }

    return teamRepository.findByTeamType(teamType).stream()
            .findFirst()
            .filter(team -> userHasAtLeastOneRole(wuaId, team, roles))
            .isPresent();
  }

  public boolean userHasRoleInTeamType(Long wuaId, TeamType teamType, Role role) {
    assertRolesValidForTeamType(Set.of(role), teamType);

    return teamRepository.findByTeamType(teamType).stream()
        .findFirst()
        .filter(team -> userHasAtLeastOneRole(wuaId, team, Set.of(role)))
        .isPresent();
  }

  public Set<EnergyPortalUserDto> getUserWithStaticRole(TeamType teamType, Role role) {

    assertRolesValidForTeamType(Set.of(role), teamType);

    if (teamType.isScoped()) {
      throw new IllegalArgumentException("TeamType %s is not static".formatted(teamType));
    }

    Optional<Team> optionalTeam = teamRepository.findByTeamType(teamType)
            .stream()
            .findFirst();

    if (optionalTeam.isEmpty()) {
      return Set.of();
    }

    List<WebUserAccountId> usersWithRoles = teamRoleRepository.findAllByTeamAndRole(optionalTeam.get(), role)
            .stream()
            .map(teamRole -> new WebUserAccountId(teamRole.getWuaId()))
            .toList();

    if (CollectionUtils.isEmpty(usersWithRoles)) {
      return Set.of();
    }

    return new HashSet<>(energyPortalUserService
            .findByWuaIds(usersWithRoles)
    );
  }

  public boolean userHasScopedRole(Long wuaId, TeamType teamType, TeamScopeReference scopeRef, Role role) {
    return userHasAtLeastOneScopedRole(wuaId, teamType, scopeRef, Set.of(role));
  }

  public boolean userHasAtLeastOneScopedRole(Long wuaId, TeamType teamType, TeamScopeReference scopeRef, Set<Role> roles) {
    assertRolesValidForTeamType(roles, teamType);
    if (!teamType.isScoped()) {
      throw new IllegalArgumentException("TeamType %s is not scoped".formatted(teamType));
    }
    return teamRepository.findByTeamTypeAndScopeTypeAndScopeId(teamType, scopeRef.getType(), scopeRef.getId())
            .filter(team -> userHasAtLeastOneRole(wuaId, team, roles))
            .isPresent();
  }

  public Team getStaticTeam(TeamType teamType) {
    if (teamType.isScoped()) {
      throw new IllegalArgumentException("TeamType %s is not static".formatted(teamType));
    }
    var teams = teamRepository.findByTeamType(teamType);
    if (teams.isEmpty()) {
      throw new IllegalArgumentException("TeamType %s is doesn't have static team associated with it".formatted(teamType));
    }
    if (teams.size() > 1) {
      throw new IllegalArgumentException("TeamType %s has more than one static team associated with it".formatted(teamType));
    }
    return teams.get(0);
  }

  public Optional<Team> getScopedTeam(TeamType teamType, TeamScopeReference scopeRef) {
    return teamRepository.findByTeamTypeAndScopeTypeAndScopeId(teamType, scopeRef.getType(), scopeRef.getId());
  }

  public Set<Team> getScopedTeams(TeamType teamType, String scopeType, Collection<String> scopeIds) {
    return teamRepository.findByTeamTypeAndScopeTypeAndScopeIdIn(teamType, scopeType, new HashSet<>(scopeIds));
  }

  public Set<Team> getOrganisationScopedTeams(Collection<String> scopeIds) {
    return getScopedTeams(TeamType.INDUSTRY, "ORGGRP", new HashSet<>(scopeIds));
  }

  public Set<TeamRole> getTeamRolesForUser(long wuaId) {
    return new HashSet<>(teamRoleRepository.findAllByWuaId(wuaId));
  }

  public boolean areRolesValidForTeamType(Collection<Role> roles, TeamType teamType) {
    return new HashSet<>(teamType.getAllowedRoles()).containsAll(roles);
  }

  public Set<Team> getTeamsOfTypeUserIsMemberOf(long wuaId, TeamType teamType) {
    return teamRoleRepository.findAllByWuaId(wuaId)
            .stream()
            .map(TeamRole::getTeam)
            .filter(team -> team.getTeamType().equals(teamType))
            .collect(Collectors.toSet());
  }

  public Set<Team> getTeamsOfTypeUserIsMemberOfWithRoles(long wuaId, TeamType teamType, Set<Role> roles) {
    var teamRoles = teamRoleRepository.findByWuaIdAndRoleIn(wuaId, roles);
    if (teamRoles.isEmpty()) {
      return Set.of();
    }
    return teamRoles.stream()
        .map(TeamRole::getTeam)
        .filter(team -> team.getTeamType().equals(teamType))
        .collect(Collectors.toSet());
  }

  public List<EnergyPortalUserDto> getUsersInTeamsWithRoles(Set<Team> teams, Set<Role> roles) {
    List<Long> wuaIds = new HashSet<>(teamRoleRepository.findByTeamIn(teams))
        .stream()
        .filter(teamRole -> roles.contains(teamRole.getRole()))
        .map(TeamRole::getWuaId)
        .distinct()
        .toList();

    if (CollectionUtils.isEmpty(wuaIds)) {
      return List.of();
    }

    List<WebUserAccountId> userIds = wuaIds
        .stream()
        .map(WebUserAccountId::new)
        .toList();

    return energyPortalUserService.findByWuaIds(userIds);
  }

  public Map<Role, Set<EnergyPortalUserDto>> getUsersInScopedTeam(TeamType teamType, TeamScopeReference teamScope) {

    if (!teamType.isScoped()) {
      throw new IllegalArgumentException("TeamType %s is not scoped".formatted(teamType));
    }

    var team = teamRepository.findByTeamTypeAndScopeTypeAndScopeId(teamType, teamScope.getType(), teamScope.getId());

    if (team.isEmpty()) {
      return Map.of();
    }

    Set<TeamRole> teamRoles = new HashSet<>(teamRoleRepository.findByTeam(team.get()));

    if (CollectionUtils.isEmpty(teamRoles)) {
      return Map.of();
    }

    Map<Role, Set<Long>> roleToUserIds = teamRoles
            .stream()
            .collect(Collectors.groupingBy(TeamRole::getRole, Collectors.mapping(TeamRole::getWuaId, Collectors.toSet())));

    List<WebUserAccountId> userIds = teamRoles
            .stream()
            .map(teamRole -> new WebUserAccountId(teamRole.getWuaId()))
            .toList();

    List<EnergyPortalUserDto> users = energyPortalUserService
            .findByWuaIds(userIds);

    Map<Role, Set<EnergyPortalUserDto>> rolesForUsers = new EnumMap<>(Role.class);

    roleToUserIds.forEach((role, wuaIds) -> {
      Set<EnergyPortalUserDto> usersWithRole = users
              .stream()
              .filter(user -> wuaIds.contains(user.webUserAccountId()))
              .collect(Collectors.toSet());

      rolesForUsers.put(role, usersWithRole);
    });

    return rolesForUsers;
  }

  private boolean userHasAtLeastOneRole(Long wuaId, Team team, Set<Role> roles) {
    return teamRoleRepository.findByWuaIdAndTeam(wuaId, team).stream()
            .anyMatch(teamRole -> roles.contains(teamRole.getRole()));
  }

  private void assertRolesValidForTeamType(Set<Role> roles, TeamType teamType) {
    roles.forEach(role -> {
      if (!teamType.getAllowedRoles().contains(role)) {
        throw new IllegalArgumentException("Role %s is not valid for TeamType %s".formatted(role, teamType));
      }
    });
  }

}
