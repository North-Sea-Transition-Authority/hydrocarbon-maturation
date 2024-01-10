package uk.co.nstauthority.hydrocarbonmaturation.teams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

  @Mock
  private TeamRepository teamRepository;
  @Mock
  private TeamMemberRoleRepository teamMemberRoleRepository;

  @InjectMocks
  private TeamService teamService;

  @Test
  void getTeam_whenMatch_thenReturnTeam() {

    var team = TeamTestUtil.Builder().build();

    when(teamRepository.findByUuidAndTeamType(team.getUuid(), team.getTeamType())).thenReturn(Optional.of(team));
    var result = teamService.getTeam(team.toTeamId(), team.getTeamType());

    assertThat(result).contains(team);
    verify(teamRepository).findByUuidAndTeamType(team.getUuid(), team.getTeamType());
  }

  @Test
  void getTeam_whenNoMatch_thenEmptyOptionalReturned() {

    var team = TeamTestUtil.Builder().build();

    when(teamRepository.findByUuidAndTeamType(team.getUuid(), team.getTeamType())).thenReturn(Optional.empty());
    var result = teamService.getTeam(team.toTeamId(), team.getTeamType());

    assertThat(result).isEmpty();
    verify(teamRepository).findByUuidAndTeamType(team.getUuid(), team.getTeamType());
  }

  @ParameterizedTest
  @EnumSource(value = TeamType.class)
  void getTeamsOfTypeThatUserBelongsTo_whenUserIsNotMember_thenNoTeamsReturned(TeamType teamType) {
    var user = UserTestUtil.Builder().build();
    when(teamRepository.findAllTeamsOfTypeThatUserIsMemberOf(user.wuaId(), teamType)).thenReturn(List.of());

    var result = teamService.getTeamsOfTypeThatUserBelongsTo(user, teamType);

    assertThat(result).isEmpty();
    verify(teamRepository, times(1)).findAllTeamsOfTypeThatUserIsMemberOf(user.wuaId(), teamType);
  }

  @ParameterizedTest
  @EnumSource(value = TeamType.class)
  void getTeamsOfTypeThatUserBelongsTo_whenUserIsMember_thenTeamsReturned(TeamType teamType) {
    var user = UserTestUtil.Builder().build();
    var team = new Team();

    when(teamRepository.findAllTeamsOfTypeThatUserIsMemberOf(user.wuaId(), teamType)).thenReturn(List.of(team));

    var result = teamService.getTeamsOfTypeThatUserBelongsTo(user, teamType);

    assertThat(result).containsExactly(team);
    verify(teamRepository, times(1)).findAllTeamsOfTypeThatUserIsMemberOf(user.wuaId(), teamType);
  }

  @Test
  void getTeamsThatUserBelongsTo() {
    var user = UserTestUtil.Builder().build();

    var team = new Team(UUID.randomUUID());
    var teamMemberRole1 = new TeamMemberRole(UUID.randomUUID());
    teamMemberRole1.setTeam(team);
    var teamMemberRole2 = new TeamMemberRole(UUID.randomUUID());
    teamMemberRole2.setTeam(team);

    when(teamMemberRoleRepository.findAllByWuaId(user.wuaId())).thenReturn(List.of(teamMemberRole1, teamMemberRole2));

    var result = teamService.getTeamsThatUserBelongsTo(user);

    assertThat(result).isEqualTo(List.of(team));
  }

  @Test
  void isUserInMultipleTeams_UserIsInOneTeam() {
    var user = UserTestUtil.Builder().build();

    var team = new Team(UUID.randomUUID());
    var teamMemberRole1 = new TeamMemberRole(UUID.randomUUID());
    teamMemberRole1.setTeam(team);
    var teamMemberRole2 = new TeamMemberRole(UUID.randomUUID());
    teamMemberRole2.setTeam(team);

    when(teamMemberRoleRepository.findAllByWuaId(user.wuaId())).thenReturn(List.of(teamMemberRole1, teamMemberRole2));

    var result = teamService.isUserInMultipleTeams(user);

    assertThat(result).isFalse();
  }

  @Test
  void isUserInMultipleTeams_UserIsInTwoTeam() {
    var user = UserTestUtil.Builder().build();

    var team1 = new Team(UUID.randomUUID());
    var teamMemberRole1 = new TeamMemberRole(UUID.randomUUID());
    teamMemberRole1.setTeam(team1);
    var team2 = new Team(UUID.randomUUID());
    var teamMemberRole2 = new TeamMemberRole(UUID.randomUUID());
    teamMemberRole2.setTeam(team2);

    when(teamMemberRoleRepository.findAllByWuaId(user.wuaId())).thenReturn(List.of(teamMemberRole1, teamMemberRole2));

    var result = teamService.isUserInMultipleTeams(user);

    assertThat(result).isTrue();
  }

  @Test
  void getOrganisationGroupsUserIsTeamMemberOf_assertReturn() {
    var user = UserTestUtil.Builder().build();
    var teamMemberRole = new TeamMemberRole();
    var team = new Team();
    team.setOrganisationGroupId(1);
    teamMemberRole.setTeam(team);
    var teamMemberRole2 = new TeamMemberRole();
    var team2 = new Team();
    team2.setOrganisationGroupId(2);
    teamMemberRole2.setTeam(team2);
    var listOfOrgGroupIds = List.of(teamMemberRole,teamMemberRole2);
    when(teamMemberRoleRepository.findAllByWuaId(user.wuaId()))
        .thenReturn(listOfOrgGroupIds);
    assertThat(teamService.getOrganisationGroupsUserIsTeamMemberOf(user))
        .isEqualTo(List.of(1,2));
  }

  @Test
  void getRegulatorTeams_verifyMethodCall() {
    teamService.getRegulatorTeams();

    verify(teamRepository).findAllByTeamType(TeamType.REGULATOR);
  }

  @Test
  void getRegulatorTeams_assertReturn() {
    List<Team> listOfTeams = mock(List.class);
    when(teamRepository.findAllByTeamType(TeamType.REGULATOR)).thenReturn(listOfTeams);
    var result = teamService.getRegulatorTeams();

    assertThat(result).isEqualTo(listOfTeams);
  }

  @Test
  void getAllTeamsOfType_assertReturn() {
    var regulatorTeam = new Team();
    regulatorTeam.setTeamType(TeamType.REGULATOR);
    var industryTeam = new Team();
    industryTeam.setTeamType(TeamType.INDUSTRY);
    var areaTeam = new Team();
    areaTeam.setTeamType(TeamType.NORTHERN_OFFSHORE_AREA);

    List<Team> listOfTeams = List.of(
        regulatorTeam,
        industryTeam,
        areaTeam
    );
    when(teamRepository.findAll()).thenReturn(listOfTeams);
    var result = teamService.getAllTeamOfTypes(Set.of(TeamType.REGULATOR, TeamType.INDUSTRY));

    assertThat(result)
        .extracting(Team::getTeamType)
        .containsExactlyInAnyOrder(TeamType.REGULATOR, TeamType.INDUSTRY);
  }
}
