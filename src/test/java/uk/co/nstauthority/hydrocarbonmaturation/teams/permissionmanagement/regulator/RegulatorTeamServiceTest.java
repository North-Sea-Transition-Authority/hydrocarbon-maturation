package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ExtendWith(MockitoExtension.class)
class RegulatorTeamServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private TeamService teamService;

  @Mock
  private TeamMemberService teamMemberService;

  @Mock
  private TeamMemberRoleService teamMemberRoleService;

  @Mock
  private EnergyPortalUserService energyPortalUserService;

  @InjectMocks
  private RegulatorTeamService regulatorTeamService;

  @Test
  void getRegulatorTeamForUser_whenUserBelongsToRegulatorTeam_thenReturnRegulatorTeam() {

    var team = new Team();
    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.REGULATOR))
        .thenReturn(List.of(team));

    var result = regulatorTeamService.getRegulatorTeamForUser(USER);

    assertThat(result).contains(team);
  }

  @Test
  void getRegulatorTeamForUser_whenUserDoesNotBelongToRegulatorTeam_thenReturnEmpty() {

    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.REGULATOR)).thenReturn(List.of());

    var result = regulatorTeamService.getRegulatorTeamForUser(USER);

    assertThat(result).isEmpty();
  }

  @Test
  void getTeam_whenMatch_thenReturnTeam() {

    var team = TeamTestUtil.Builder().build();

    var teamId = team.toTeamId();

    when(teamService.getTeam(teamId, TeamType.REGULATOR)).thenReturn(Optional.of(team));

    assertThat(regulatorTeamService.getTeam(teamId)).contains(team);
    verify(teamService, times(1)).getTeam(teamId, team.getTeamType());
  }

  @Test
  void getTeam_whenNoMatch_thenEmptyOptional() {

    var team = TeamTestUtil.Builder().build();

    var teamId = team.toTeamId();

    when(teamService.getTeam(teamId, TeamType.REGULATOR)).thenReturn(Optional.empty());

    assertThat(regulatorTeamService.getTeam(teamId)).isEmpty();
    verify(teamService, times(1)).getTeam(teamId, team.getTeamType());
  }

  @Test
  void isAccessManager_whenAccessManager_thenTrue() {

    var teamId = new TeamId(UUID.randomUUID());

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, USER, Set.of(RegulatorTeamRole.ACCESS_MANAGER.name())))
        .thenReturn(true);
    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.REGULATOR))
        .thenReturn(List.of(TeamTestUtil.Builder().withId(teamId.uuid()).build()));
    assertTrue(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.ACCESS_MANAGER));
  }

  @Test
  void isAccessManager_whenAccessManager_thenFalse() {

    var teamId = new TeamId(UUID.randomUUID());

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, USER, Set.of(RegulatorTeamRole.ACCESS_MANAGER.name())))
        .thenReturn(false);
    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.REGULATOR))
        .thenReturn(List.of(TeamTestUtil.Builder().withId(teamId.uuid()).build()));
    assertFalse(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.ACCESS_MANAGER));
  }

  @Test
  void isIndustryAccessManager_whenIndustryAccessManager_thenTrue() {

    var teamId = new TeamId(UUID.randomUUID());

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, USER, Set.of(RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER.name())))
        .thenReturn(true);
    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.REGULATOR))
        .thenReturn(List.of(TeamTestUtil.Builder().withId(teamId.uuid()).build()));
    assertTrue(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER));
  }

  @Test
  void isIndustryAccessManager_whenIndustryAccessManager_thenFalse() {
    var teamId = new TeamId(UUID.randomUUID());

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, USER, Set.of(RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER.name())))
        .thenReturn(false);
    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.REGULATOR))
        .thenReturn(List.of(TeamTestUtil.Builder().withId(teamId.uuid()).build()));
    assertFalse(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER));
  }

  @Test
  void isExplorationUser_whenExploration_thenTrue() {

    var teamId = new TeamId(UUID.randomUUID());

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, USER, Set.of(RegulatorTeamRole.EXPLORATION.name())))
        .thenReturn(true);
    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.REGULATOR))
        .thenReturn(List.of(TeamTestUtil.Builder().withId(teamId.uuid()).build()));
    assertTrue(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.EXPLORATION));
  }

  @Test
  void isIndustryAccessManager_whenNotExploration_thenFalse() {
    var teamId = new TeamId(UUID.randomUUID());

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, USER, Set.of(RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER.name())))
        .thenReturn(false);
    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.REGULATOR))
        .thenReturn(List.of(TeamTestUtil.Builder().withId(teamId.uuid()).build()));
    assertFalse(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER));
  }

  @Test
  void isProjectAnalyst_whenProjectAnalyst_assertTrue() {
    var teamId = new TeamId(UUID.randomUUID());

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, USER, Set.of(RegulatorTeamRole.PROJECT_ANALYST.name())))
        .thenReturn(true);
    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.REGULATOR))
        .thenReturn(List.of(TeamTestUtil.Builder().withId(teamId.uuid()).build()));
    assertTrue(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.PROJECT_ANALYST));

    assertTrue(regulatorTeamService.isProjectAnalyst(USER));
  }

  @Test
  void isProjectAnalyst_whenNotProjectAnalyst_assertFalse() {
    var teamId = new TeamId(UUID.randomUUID());

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, USER, Set.of(RegulatorTeamRole.PROJECT_ANALYST.name())))
        .thenReturn(false);
    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.REGULATOR))
        .thenReturn(List.of(TeamTestUtil.Builder().withId(teamId.uuid()).build()));
    assertFalse(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.PROJECT_ANALYST));
    assertFalse(regulatorTeamService.isProjectAnalyst(USER));
  }

  @Test
  void isReservesAndResourcesAdvisor_whenReservesAndResourcesAdvisor_assertTrue() {
    var teamId = new TeamId(UUID.randomUUID());

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, USER,
        Set.of(RegulatorTeamRole.RESERVES_AND_RESOURCES_ADVISOR.name())))
        .thenReturn(true);
    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.REGULATOR))
        .thenReturn(List.of(TeamTestUtil.Builder().withId(teamId.uuid()).build()));

    assertTrue(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.RESERVES_AND_RESOURCES_ADVISOR));
    assertTrue(regulatorTeamService.isReservesAndResourcesAdvisor(USER));
  }

  @Test
  void isReservesAndResourcesAdvisor_whenNotReservesAndResourcesAdvisor_assertFalse() {
    var teamId = new TeamId(UUID.randomUUID());

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, USER,
        Set.of(RegulatorTeamRole.RESERVES_AND_RESOURCES_ADVISOR.name())))
        .thenReturn(false);
    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.REGULATOR))
        .thenReturn(List.of(TeamTestUtil.Builder().withId(teamId.uuid()).build()));

    assertFalse(regulatorTeamService.isRegulatorTeamRole(USER, RegulatorTeamRole.RESERVES_AND_RESOURCES_ADVISOR));
    assertFalse(regulatorTeamService.isReservesAndResourcesAdvisor(USER));
  }

  @Test
  void addUserTeamRoles_verifyRepositoryInteractions() {

    var team = TeamTestUtil.Builder().build();
    var userToAdd = EnergyPortalUserDtoTestUtil.Builder().build();
    var regulatorRoles = Set.of(
        RegulatorTeamRole.ACCESS_MANAGER,
        RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER
    );

    regulatorTeamService.addUserTeamRoles(team, userToAdd, regulatorRoles);

    var rolesAsStrings = regulatorRoles
        .stream()
        .map(RegulatorTeamRole::name)
        .collect(Collectors.toSet());

    verify(teamMemberRoleService, times(1)).addUserTeamRoles(team, userToAdd, rolesAsStrings);
  }

  @Test
  void getRegulatorTeamMembersWithRole_whenExploration_assertReturn() {
    var team = TeamTestUtil.Builder().build();
    var teamMemberExploration = TeamMemberTestUtil.Builder().withRole(RegulatorTeamRole.EXPLORATION).build();
    var teamMemberAccessManager = TeamMemberTestUtil.Builder().withRole(RegulatorTeamRole.ACCESS_MANAGER).build();

    when(teamService.getRegulatorTeams()).thenReturn(List.of(team));
    when(teamMemberService.getTeamMembers(team))
        .thenReturn(List.of(teamMemberExploration, teamMemberAccessManager));

    var result = regulatorTeamService.getRegulatorTeamMembersWithRole(RegulatorTeamRole.EXPLORATION);
    assertThat(result)
        .containsExactly(teamMemberExploration);
  }

  @Test
  void getRegulatorTeamMembersWithRole_whenIndustryAccessManager_whenNoTeamMembers_assertEmpty() {
    var team = TeamTestUtil.Builder().build();
    var teamMemberExploration = TeamMemberTestUtil.Builder().withRole(RegulatorTeamRole.EXPLORATION).build();
    var teamMemberAccessManager = TeamMemberTestUtil.Builder().withRole(RegulatorTeamRole.ACCESS_MANAGER).build();

    when(teamService.getRegulatorTeams()).thenReturn(List.of(team));
    when(teamMemberService.getTeamMembers(team))
        .thenReturn(List.of(teamMemberExploration, teamMemberAccessManager));

    var result = regulatorTeamService.getRegulatorTeamMembersWithRole(RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER);
    assertThat(result).isEmpty();
  }

  @Test
  void getExplorationTeamEnergyPortalUsers_assertReturnExplorationUsers() {
    var explorationUserWuaId1 = 1L;
    var explorationUserWuaId2 = 2L;
    var explorationUserWuaId3 = 3L;
    var nonExplorationUserWuaId = 4L;

    var explorationUser1 = EnergyPortalUserDtoTestUtil.Builder()
        .withWebUserAccountId(explorationUserWuaId1)
        .build();
    var explorationUser2 = EnergyPortalUserDtoTestUtil.Builder()
        .withWebUserAccountId(explorationUserWuaId2)
        .build();
    var explorationUser3 = EnergyPortalUserDtoTestUtil.Builder()
        .withWebUserAccountId(explorationUserWuaId3)
        .build();
    var regulatorTeamMemberList = List.of(
        TeamMemberTestUtil.Builder().withWebUserAccountId(explorationUserWuaId1)
            .withRole(RegulatorTeamRole.EXPLORATION)
            .build(),
        TeamMemberTestUtil.Builder().withWebUserAccountId(explorationUserWuaId2)
            .withRole(RegulatorTeamRole.EXPLORATION)
            .build(),
        TeamMemberTestUtil.Builder().withWebUserAccountId(explorationUserWuaId3)
            .withRole(RegulatorTeamRole.EXPLORATION)
            .build(),
        TeamMemberTestUtil.Builder().withWebUserAccountId(nonExplorationUserWuaId)
            .withRole(RegulatorTeamRole.ACCESS_MANAGER)
            .build()
    );

    var regulatorTeam = TeamTestUtil.Builder().build();

    when(teamService.getRegulatorTeams())
        .thenReturn(List.of(regulatorTeam));
    when(teamMemberService.getTeamMembers(regulatorTeam))
        .thenReturn(regulatorTeamMemberList);
    when(energyPortalUserService.findByWuaIds(
        List.of(
            WebUserAccountId.valueOf(String.valueOf(explorationUserWuaId1)),
            WebUserAccountId.valueOf(String.valueOf(explorationUserWuaId2)),
            WebUserAccountId.valueOf(String.valueOf(explorationUserWuaId3)))))
        .thenReturn(List.of(explorationUser1, explorationUser2, explorationUser3));

    assertThat(regulatorTeamService.getExplorationTeamEnergyPortalUsers())
        .containsExactly(
            explorationUser1,
            explorationUser2,
            explorationUser3
        );
  }

  @Test
  void isExploration_inTeam_andHasRole() {

    var team = new Team(UUID.randomUUID());
    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.REGULATOR)).thenReturn(List.of(team));
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(team.toTeamId(), USER,
        Set.of(RegulatorTeamRole.EXPLORATION.name())))
        .thenReturn(true);

    assertThat(regulatorTeamService.isExploration(USER)).isTrue();
  }

  @Test
  void isExploration_inTeam_andNotInRole() {

    var team = new Team(UUID.randomUUID());
    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.REGULATOR)).thenReturn(List.of(team));
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(team.toTeamId(), USER,
        Set.of(RegulatorTeamRole.EXPLORATION.name())))
        .thenReturn(false);

    assertThat(regulatorTeamService.isExploration(USER)).isFalse();
  }

  @Test
  void isExploration_notInTeam() {

    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.REGULATOR)).thenReturn(List.of());
    assertThat(regulatorTeamService.isExploration(USER)).isFalse();
  }
}
