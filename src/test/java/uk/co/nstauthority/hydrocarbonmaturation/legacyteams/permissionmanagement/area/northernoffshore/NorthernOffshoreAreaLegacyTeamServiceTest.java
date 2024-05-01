package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.northernoffshore;

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
class NorthernOffshoreAreaLegacyTeamServiceTest {

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
  private NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService;

  private final User user = UserTestUtil.Builder().build();

  @Test
  void getTeam_whenMatch_thenReturnTeam() {

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.NORTHERN_OFFSHORE_AREA)
        .build();

    var teamId = team.toTeamId();

    when(teamService.getTeam(teamId, TeamType.NORTHERN_OFFSHORE_AREA)).thenReturn(Optional.of(team));

    assertThat(northernOffshoreAreaTeamService.getTeam(teamId)).contains(team);
    verify(teamService, times(1)).getTeam(teamId, team.getTeamType());
  }

  @Test
  void getTeam_whenNoMatch_thenEmptyOptional() {

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.NORTHERN_OFFSHORE_AREA)
        .build();

    var teamId = team.toTeamId();

    when(teamService.getTeam(teamId, TeamType.NORTHERN_OFFSHORE_AREA)).thenReturn(Optional.empty());

    assertThat(northernOffshoreAreaTeamService.getTeam(teamId)).isEmpty();
    verify(teamService, times(1)).getTeam(teamId, team.getTeamType());
  }


  @Test
  void addUserTeamRoles_verifyRepositoryInteractions() {

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.NORTHERN_OFFSHORE_AREA)
        .build();
    var userToAdd = EnergyPortalUserDtoTestUtil.Builder().build();
    var areaTeamRoles = Set.of(
        NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_MANAGE_TEAM
    );

    northernOffshoreAreaTeamService.addUserTeamRoles(team, userToAdd, areaTeamRoles);

    var rolesAsStrings = areaTeamRoles
        .stream()
        .map(NorthernOffshoreAreaTeamRole::name)
        .collect(Collectors.toSet());

    verify(teamMemberRoleService, times(1)).addUserTeamRoles(team, userToAdd, rolesAsStrings);
  }

  @Test
  void isNorthernOffshoreAreaUser_inAnOffshoreAreaTeam() {

    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.NORTHERN_OFFSHORE_AREA)).thenReturn(
        List.of(new LegacyTeam()));

    assertThat(northernOffshoreAreaTeamService.isNorthernOffshoreAreaUser(user)).isTrue();
  }

  @Test
  void isNorthernOffshoreAreaUser_notInAnOffshoreAreaTeam() {

    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.NORTHERN_OFFSHORE_AREA)).thenReturn(List.of());

    assertThat(northernOffshoreAreaTeamService.isNorthernOffshoreAreaUser(user)).isFalse();
  }

  @Test
  void isNorthernOffshoreareaTeamMember_inTeam_andHasRole() {

    var team = new LegacyTeam(UUID.randomUUID());
    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.NORTHERN_OFFSHORE_AREA)).thenReturn(List.of(team));
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(team.toTeamId(), user,
        Set.of(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER.name())))
        .thenReturn(true);

    assertThat(northernOffshoreAreaTeamService.isNorthernOffshoreTeamMember(user)).isTrue();
  }

  @Test
  void isNorthernOffshoreareaTeamMember_inTeam_andNotInRole() {

    var team = new LegacyTeam(UUID.randomUUID());
    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.NORTHERN_OFFSHORE_AREA)).thenReturn(List.of(team));
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(team.toTeamId(), user,
        Set.of(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER.name())))
        .thenReturn(false);

    assertThat(northernOffshoreAreaTeamService.isNorthernOffshoreTeamMember(user)).isFalse();
  }

  @Test
  void isNorthernOffshoreareaTeamMember_notInTeam() {

    when(teamService.getTeamsOfTypeThatUserBelongsTo(user, TeamType.NORTHERN_OFFSHORE_AREA)).thenReturn(List.of());
    assertThat(northernOffshoreAreaTeamService.isNorthernOffshoreTeamMember(user)).isFalse();
  }

  @Test
  void getNorthernOffshoreAreaTeamEnergyPortalUsers_assertReturnTeamMemberUsers() {
    var northernOffshoreTeamMemberUserWuaId1 = 1L;
    var northernOffshoreTeamMemberUserWuaId2 = 2L;
    var northernOffshoreTeamMemberUserWuaId3 = 3L;
    var nonNorthernOffshoreTeamMemberUserWuaId = 4L;

    var northernOffshoreTeamMemberUser1 = EnergyPortalUserDtoTestUtil.Builder()
        .withWebUserAccountId(northernOffshoreTeamMemberUserWuaId1)
        .build();
    var northernOffshoreTeamMemberUser2 = EnergyPortalUserDtoTestUtil.Builder()
        .withWebUserAccountId(northernOffshoreTeamMemberUserWuaId2)
        .build();
    var northernOffshoreTeamMemberUser3 = EnergyPortalUserDtoTestUtil.Builder()
        .withWebUserAccountId(northernOffshoreTeamMemberUserWuaId3)
        .build();
    var northernOffshoreTeamMemberList = List.of(
        TeamMemberTestUtil.Builder().withWebUserAccountId(northernOffshoreTeamMemberUserWuaId1)
            .withRole(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER)
            .build(),
        TeamMemberTestUtil.Builder().withWebUserAccountId(northernOffshoreTeamMemberUserWuaId2)
            .withRole(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER)
            .build(),
        TeamMemberTestUtil.Builder().withWebUserAccountId(northernOffshoreTeamMemberUserWuaId3)
            .withRole(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER)
            .build(),
        TeamMemberTestUtil.Builder().withWebUserAccountId(nonNorthernOffshoreTeamMemberUserWuaId)
            .withRole(NorthernOffshoreAreaTeamRole.NORTHERN_OFFSHORE_AREA_MANAGE_TEAM)
            .build()
    );

    var northernOffshoreAreaTeam = TeamTestUtil.Builder().build();
    northernOffshoreAreaTeam.setTeamType(TeamType.NORTHERN_OFFSHORE_AREA);

    when(teamService.getAllTeamOfTypes(Set.of(TeamType.NORTHERN_OFFSHORE_AREA)))
        .thenReturn(List.of(northernOffshoreAreaTeam));
    when(teamMemberService.getTeamMembers(northernOffshoreAreaTeam)).thenReturn(northernOffshoreTeamMemberList);
    when(energyPortalUserService.findByWuaIds(
        List.of(
            WebUserAccountId.valueOf(String.valueOf(northernOffshoreTeamMemberUserWuaId1)),
            WebUserAccountId.valueOf(String.valueOf(northernOffshoreTeamMemberUserWuaId2)),
            WebUserAccountId.valueOf(String.valueOf(northernOffshoreTeamMemberUserWuaId3)))))
        .thenReturn(List.of(northernOffshoreTeamMemberUser1, northernOffshoreTeamMemberUser2,
            northernOffshoreTeamMemberUser3));

    assertThat(northernOffshoreAreaTeamService.getNorthernOffShoreAreaTeamEnergyPortalUsers())
        .containsExactly(
            northernOffshoreTeamMemberUser1,
            northernOffshoreTeamMemberUser2,
            northernOffshoreTeamMemberUser3
        );
  }
}