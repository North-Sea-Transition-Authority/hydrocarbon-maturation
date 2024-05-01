package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.onshore;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.boot.test.mock.mockito.SpyBean;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamType;

@ExtendWith(MockitoExtension.class)
class OnshoreAreaLegacyTeamServiceTest {

  @Mock
  private TeamService teamService;

  @Mock
  private TeamMemberRoleService teamMemberRoleService;

  @Mock
  private TeamMemberService teamMemberService;

  @Mock
  private EnergyPortalUserService energyPortalUserService;

  @InjectMocks
  @SpyBean
  private OnshoreAreaTeamService onshoreAreaTeamService;

  private final User user = UserTestUtil.Builder().build();

  @Test
  void getTeam_whenMatch_thenReturnTeam() {

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.ONSHORE_AREA)
        .build();

    var teamId = team.toTeamId();

    when(teamService.getTeam(teamId, TeamType.ONSHORE_AREA)).thenReturn(Optional.of(team));

    assertThat(onshoreAreaTeamService.getTeam(teamId)).contains(team);
    verify(teamService, times(1)).getTeam(teamId, team.getTeamType());
  }

  @Test
  void getTeam_whenNoMatch_thenEmptyOptional() {

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.ONSHORE_AREA)
        .build();

    var teamId = team.toTeamId();

    when(teamService.getTeam(teamId, TeamType.ONSHORE_AREA)).thenReturn(Optional.empty());

    assertThat(onshoreAreaTeamService.getTeam(teamId)).isEmpty();
    verify(teamService, times(1)).getTeam(teamId, team.getTeamType());
  }


  @Test
  void addUserTeamRoles_verifyRepositoryInteractions() {

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.ONSHORE_AREA)
        .build();
    var userToAdd = EnergyPortalUserDtoTestUtil.Builder().build();
    var areaTeamRoles = Set.of(
        OnshoreAreaTeamRole.ONSHORE_AREA_MANAGE_TEAM
    );

    onshoreAreaTeamService.addUserTeamRoles(team, userToAdd, areaTeamRoles);

    var rolesAsStrings = areaTeamRoles
        .stream()
        .map(OnshoreAreaTeamRole::name)
        .collect(Collectors.toSet());

    verify(teamMemberRoleService, times(1)).addUserTeamRoles(team, userToAdd, rolesAsStrings);
  }

  @Test
  void isOnshoreAreaUser_inAnOnshoreAreaTeam() {

    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.ONSHORE_AREA)).thenReturn(List.of(new LegacyTeam()));

    assertThat(onshoreAreaTeamService.isOnshoreAreaUser(user)).isTrue();
  }

  @Test
  void isOnshoreAreaUser_notInAnOnshoreAreaTeam() {

    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.ONSHORE_AREA)).thenReturn(List.of());

    assertThat(onshoreAreaTeamService.isOnshoreAreaUser(user)).isFalse();
  }

  @Test
  void isOnshoreAreaTeamMember_inTeam_andHasRole() {

    var team = new LegacyTeam(UUID.randomUUID());
    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.ONSHORE_AREA)).thenReturn(List.of(team));
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(team.toTeamId(), user,
        Set.of(OnshoreAreaTeamRole.ONSHORE_AREA_TEAM_MEMBER.name())))
        .thenReturn(true);

    assertThat(onshoreAreaTeamService.isOnshoreTeamMember(user)).isTrue();
  }

  @Test
  void isOnshoreAreaTeamMember_inTeam_andNotInRole() {

    var team = new LegacyTeam(UUID.randomUUID());
    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.ONSHORE_AREA)).thenReturn(List.of(team));
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(team.toTeamId(), user,
        Set.of(OnshoreAreaTeamRole.ONSHORE_AREA_TEAM_MEMBER.name())))
        .thenReturn(false);

    assertThat(onshoreAreaTeamService.isOnshoreTeamMember(user)).isFalse();
  }

  @Test
  void isOnshoreAreaTeamMember_notInTeam() {

    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.ONSHORE_AREA)).thenReturn(List.of());
    assertThat(onshoreAreaTeamService.isOnshoreTeamMember(user)).isFalse();
  }

  @Test
  void getOnShoreAreaTeamEnergyPortalUsers_assertReturnTeamMemberUsers() {
    var onshoreTeamMemberUserWuaId1 = 1L;
    var onshoreTeamMemberUserWuaId2 = 2L;
    var onshoreTeamMemberUserWuaId3 = 3L;
    var nonOnshoreTeamMemberUserWuaId = 4L;

    var onshoreTeamMemberUser1 = EnergyPortalUserDtoTestUtil.Builder()
        .withWebUserAccountId(onshoreTeamMemberUserWuaId1)
        .build();
    var onshoreTeamMemberUser2 = EnergyPortalUserDtoTestUtil.Builder()
        .withWebUserAccountId(onshoreTeamMemberUserWuaId2)
        .build();
    var onshoreTeamMemberUser3 = EnergyPortalUserDtoTestUtil.Builder()
        .withWebUserAccountId(onshoreTeamMemberUserWuaId3)
        .build();
    var onshoreTeamMemberList = List.of(
        TeamMemberTestUtil.Builder().withWebUserAccountId(onshoreTeamMemberUserWuaId1)
            .withRole(OnshoreAreaTeamRole.ONSHORE_AREA_TEAM_MEMBER)
            .build(),
        TeamMemberTestUtil.Builder().withWebUserAccountId(onshoreTeamMemberUserWuaId2)
            .withRole(OnshoreAreaTeamRole.ONSHORE_AREA_TEAM_MEMBER)
            .build(),
        TeamMemberTestUtil.Builder().withWebUserAccountId(onshoreTeamMemberUserWuaId3)
            .withRole(OnshoreAreaTeamRole.ONSHORE_AREA_TEAM_MEMBER)
            .build(),
        TeamMemberTestUtil.Builder().withWebUserAccountId(nonOnshoreTeamMemberUserWuaId)
            .withRole(OnshoreAreaTeamRole.ONSHORE_AREA_MANAGE_TEAM)
            .build()
    );

    var onshoreAreaTeam = TeamTestUtil.Builder().build();
    onshoreAreaTeam.setTeamType(TeamType.ONSHORE_AREA);

    when(teamService.getAllTeamOfTypes(Set.of(TeamType.ONSHORE_AREA))).thenReturn(List.of(onshoreAreaTeam));
    when(teamMemberService.getTeamMembers(onshoreAreaTeam)).thenReturn(onshoreTeamMemberList);
    when(energyPortalUserService.findByWuaIds(
        List.of(
            WebUserAccountId.valueOf(String.valueOf(onshoreTeamMemberUserWuaId1)),
            WebUserAccountId.valueOf(String.valueOf(onshoreTeamMemberUserWuaId2)),
            WebUserAccountId.valueOf(String.valueOf(onshoreTeamMemberUserWuaId3)))))
        .thenReturn(List.of(onshoreTeamMemberUser1, onshoreTeamMemberUser2, onshoreTeamMemberUser3));

    assertThat(onshoreAreaTeamService.getOnShoreAreaTeamEnergyPortalUsers())
        .containsExactly(
            onshoreTeamMemberUser1,
            onshoreTeamMemberUser2,
            onshoreTeamMemberUser3
        );
  }
}