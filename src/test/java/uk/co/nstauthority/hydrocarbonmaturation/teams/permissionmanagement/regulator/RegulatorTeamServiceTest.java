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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ExtendWith(MockitoExtension.class)
class RegulatorTeamServiceTest {

  @Mock
  private TeamService teamService;

  @Mock
  private TeamMemberService teamMemberService;

  @Mock
  private TeamMemberRoleService teamMemberRoleService;

  @InjectMocks
  private RegulatorTeamService regulatorTeamService;

  @Test
  void getRegulatorTeamForUser_whenUserBelongsToRegulatorTeam_thenReturnRegulatorTeam() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var team = new Team();
    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.REGULATOR))
        .thenReturn(List.of(team));

    var result = regulatorTeamService.getRegulatorTeamForUser(user);

    assertThat(result).contains(team);
  }

  @Test
  void getRegulatorTeamForUser_whenUserDoesNotBelongToRegulatorTeam_thenReturnEmpty() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.REGULATOR)).thenReturn(List.of());

    var result = regulatorTeamService.getRegulatorTeamForUser(user);

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
    var user = ServiceUserDetailTestUtil.Builder().build();

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, Set.of(RegulatorTeamRole.ACCESS_MANAGER.name())))
        .thenReturn(true);
    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.REGULATOR))
        .thenReturn(List.of(TeamTestUtil.Builder().withId(teamId.uuid()).build()));
    assertTrue(regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.ACCESS_MANAGER));
  }

  @Test
  void isAccessManager_whenAccessManager_thenFalse() {

    var teamId = new TeamId(UUID.randomUUID());
    var user = ServiceUserDetailTestUtil.Builder().build();

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, Set.of(RegulatorTeamRole.ACCESS_MANAGER.name())))
        .thenReturn(false);
    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.REGULATOR))
        .thenReturn(List.of(TeamTestUtil.Builder().withId(teamId.uuid()).build()));
    assertFalse(regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.ACCESS_MANAGER));
  }

  @Test
  void isIndustryAccessManager_whenIndustryAccessManager_thenTrue() {

    var teamId = new TeamId(UUID.randomUUID());
    var user = ServiceUserDetailTestUtil.Builder().build();

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, Set.of(RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER.name())))
        .thenReturn(true);
    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.REGULATOR))
        .thenReturn(List.of(TeamTestUtil.Builder().withId(teamId.uuid()).build()));
    assertTrue(regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER));
  }

  @Test
  void isIndustryAccessManager_whenIndustryAccessManager_thenFalse() {
    var teamId = new TeamId(UUID.randomUUID());
    var user = ServiceUserDetailTestUtil.Builder().build();

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, Set.of(RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER.name())))
        .thenReturn(false);
    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.REGULATOR))
        .thenReturn(List.of(TeamTestUtil.Builder().withId(teamId.uuid()).build()));
    assertFalse(regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER));
  }

  @Test
  void isExplorationUser_whenExploration_thenTrue() {

    var teamId = new TeamId(UUID.randomUUID());
    var user = ServiceUserDetailTestUtil.Builder().build();

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, Set.of(RegulatorTeamRole.EXPLORATION.name())))
        .thenReturn(true);
    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.REGULATOR))
        .thenReturn(List.of(TeamTestUtil.Builder().withId(teamId.uuid()).build()));
    assertTrue(regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.EXPLORATION));
  }

  @Test
  void isIndustryAccessManager_whenNotExploration_thenFalse() {
    var teamId = new TeamId(UUID.randomUUID());
    var user = ServiceUserDetailTestUtil.Builder().build();

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, Set.of(RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER.name())))
        .thenReturn(false);
    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.REGULATOR))
        .thenReturn(List.of(TeamTestUtil.Builder().withId(teamId.uuid()).build()));
    assertFalse(regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER));
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
}
