package uk.co.nstauthority.hydrocarbonmaturation.teams;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeamQueryServiceTest {

  @Mock
  private TeamRepository teamRepository;

  @Mock
  private TeamRoleRepository teamRoleRepository;

  @InjectMocks
  private TeamQueryService teamQueryService;

  @Test
  void userHasStaticRole_hasRole() {
    setupStaticTeamAndRoles(1L, TeamType.REGULATOR, List.of(
        Role.REGULATOR_MANAGE_ALL_ORGANISATIONS,
        Role.MANAGE_TEAM
    ));

    assertThat(teamQueryService.userHasStaticRole(1L, TeamType.REGULATOR, Role.REGULATOR_MANAGE_ALL_ORGANISATIONS))
        .isTrue();
  }

  @Test
  void userHasStaticRole_doesNotHaveRole() {
    setupStaticTeamAndRoles(1L, TeamType.REGULATOR, List.of(
        Role.REGULATOR_MANAGE_ALL_ORGANISATIONS,
        Role.MANAGE_TEAM
    ));

    assertThat(teamQueryService.userHasStaticRole(1L, TeamType.REGULATOR, Role.VIEW_ANY_RESOURCE))
        .isFalse();
  }

  @Test
  void userHasStaticRole_invalidRole() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> teamQueryService.userHasStaticRole(1L, TeamType.REGULATOR, Role.INDUSTRY_SUBMIT));
  }

  @Test
  void userHasStaticRole_noTeamInstance() {
    when(teamRepository.findByTeamType(TeamType.REGULATOR)).thenReturn(List.of());
    assertThat(teamQueryService.userHasStaticRole(1L, TeamType.REGULATOR, Role.VIEW_ANY_RESOURCE))
        .isFalse();
  }

  @Test
  void userHasAtLeastOneStaticRole_hasRole() {
    setupStaticTeamAndRoles(1L, TeamType.REGULATOR, List.of(
        Role.REGULATOR_MANAGE_ALL_ORGANISATIONS,
        Role.MANAGE_TEAM
    ));

    assertThat(teamQueryService.userHasAtLeastOneStaticRole(1L, TeamType.REGULATOR, Set.of(Role.REGULATOR_MANAGE_ALL_ORGANISATIONS, Role.VIEW_ANY_RESOURCE)))
        .isTrue();
  }

  @Test
  void userHasAtLeastOneStaticRole_doesNotHaveRole() {
    setupStaticTeamAndRoles(1L, TeamType.REGULATOR, List.of(
        Role.REGULATOR_MANAGE_ALL_ORGANISATIONS,
        Role.MANAGE_TEAM
    ));

    assertThat(teamQueryService.userHasAtLeastOneStaticRole(1L, TeamType.REGULATOR, Set.of(Role.VIEW_ANY_RESOURCE)))
        .isFalse();
  }

  @Test
  void userHasAtLeastOneStaticRole_invalidRole() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> teamQueryService.userHasAtLeastOneStaticRole(1L, TeamType.REGULATOR, Set.of(Role.INDUSTRY_SUBMIT)));
  }

  @Test
  void userHasAtLeastOneStaticRole_noTeamInstance() {
    when(teamRepository.findByTeamType(TeamType.REGULATOR)).thenReturn(List.of());
    assertThat(teamQueryService.userHasAtLeastOneStaticRole(1L, TeamType.REGULATOR, Set.of(Role.REGULATOR_MANAGE_ALL_ORGANISATIONS)))
        .isFalse();
  }

  @Test
  void userHasScopedRole_hasRole() {
    var scope = TeamScopeReference.from("123", "ORGGRP");
    setupScopedTeamAndRoles(1L, TeamType.INDUSTRY, scope, List.of(
        Role.MANAGE_TEAM,
        Role.INDUSTRY_VIEWER
    ));

    assertThat(teamQueryService.userHasScopedRole(1L, TeamType.INDUSTRY, scope, Role.INDUSTRY_VIEWER))
        .isTrue();
  }

  @Test
  void userHasScopedRole_doesNotHaveRole() {
    var scope = TeamScopeReference.from("123", "ORGGRP");
    setupScopedTeamAndRoles(1L, TeamType.INDUSTRY, scope, List.of(
        Role.MANAGE_TEAM,
        Role.INDUSTRY_VIEWER
    ));

    assertThat(teamQueryService.userHasScopedRole(1L, TeamType.INDUSTRY, scope, Role.INDUSTRY_SUBMIT))
        .isFalse();
  }

  @Test
  void userHasScopedRole_invalidRole() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> teamQueryService.userHasScopedRole(1L, TeamType.INDUSTRY, TeamScopeReference.from("1", "ORGGRP"), Role.REGULATOR_MANAGE_ALL_ORGANISATIONS));
  }

  @Test
  void userHasScopedRole_noTeamInstance() {
    when(teamRepository.findByTeamTypeAndScopeTypeAndScopeId(TeamType.INDUSTRY, "ORGGRP", "1")).thenReturn(Optional.empty());
    assertThat(teamQueryService.userHasScopedRole(1L, TeamType.INDUSTRY, TeamScopeReference.from("1", "ORGGRP"), Role.INDUSTRY_VIEWER))
        .isFalse();
  }

  @Test
  void userHasAtLeastOneScopedRole_hasRole() {
    var scope = TeamScopeReference.from("123", "ORGGRP");
    setupScopedTeamAndRoles(1L, TeamType.INDUSTRY, scope, List.of(
        Role.MANAGE_TEAM,
        Role.INDUSTRY_VIEWER
    ));

    assertThat(teamQueryService.userHasAtLeastOneScopedRole(1L, TeamType.INDUSTRY, scope, Set.of(Role.INDUSTRY_SUBMIT, Role.INDUSTRY_VIEWER)))
        .isTrue();
  }

  @Test
  void userHasAtLeastOneScopedRole_doesNotHaveRole() {
    var scope = TeamScopeReference.from("123", "ORGGRP");
    setupScopedTeamAndRoles(1L, TeamType.INDUSTRY, scope, List.of(
        Role.MANAGE_TEAM,
        Role.INDUSTRY_VIEWER
    ));

    assertThat(teamQueryService.userHasAtLeastOneScopedRole(1L, TeamType.INDUSTRY, scope, Set.of(Role.INDUSTRY_SUBMIT)))
        .isFalse();
  }

  @Test
  void userHasAtLeastOneScopedRole_invalidRole() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> teamQueryService.userHasScopedRole(1L, TeamType.INDUSTRY, TeamScopeReference.from("1", "ORGGRP"), Role.VIEW_ANY_RESOURCE));
  }

  @Test
  void userHasAtLeastOneScopedRole_noTeamInstance() {
    when(teamRepository.findByTeamTypeAndScopeTypeAndScopeId(TeamType.INDUSTRY, "ORGGRP", "1"))
        .thenReturn(Optional.empty());

    assertThat(teamQueryService.userHasAtLeastOneScopedRole(1L, TeamType.INDUSTRY, TeamScopeReference.from("1", "ORGGRP"), Set.of(Role.INDUSTRY_VIEWER)))
        .isFalse();
  }


  private void setupStaticTeamAndRoles(Long wuaId, TeamType teamType, List<Role> roles) {
    var team = new Team(UUID.randomUUID());
    team.setTeamType(teamType);
    var teamRoles = roles.stream()
        .map(role -> createTeamRole(wuaId, team, role))
        .toList();

    when(teamRepository.findByTeamType(teamType))
        .thenReturn(List.of(team));
    when(teamRoleRepository.findByWuaIdAndTeam(wuaId, team))
        .thenReturn(teamRoles);
  }

  private void setupScopedTeamAndRoles(Long wuaId, TeamType teamType, TeamScopeReference scopeRef, List<Role> roles) {
    var team = new Team(UUID.randomUUID());
    team.setScopeType(scopeRef.getType());
    team.setScopeId(scopeRef.getId());
    team.setTeamType(teamType);
    var teamRoles = roles.stream()
        .map(role -> createTeamRole(wuaId, team, role))
        .toList();

    when(teamRepository.findByTeamTypeAndScopeTypeAndScopeId(teamType, scopeRef.getType(), scopeRef.getId()))
        .thenReturn(Optional.of(team));
    when(teamRoleRepository.findByWuaIdAndTeam(wuaId, team))
        .thenReturn(teamRoles);
  }


  private TeamRole createTeamRole(Long wuaId, Team team, Role role) {
    var teamRole = new TeamRole(UUID.randomUUID());
    teamRole.setWuaId(wuaId);
    teamRole.setTeam(team);
    teamRole.setRole(role);
    return teamRole;
  }

}
