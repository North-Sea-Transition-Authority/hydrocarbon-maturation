package uk.co.nstauthority.hydrocarbonmaturation.teams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.offshore.OffshoreAreaTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.onshore.OnshoreAreaTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {

  @Mock
  private TeamMemberRoleRepository teamMemberRoleRepository;

  @InjectMocks
  private TeamMemberService teamMemberService;

  @Test
  void getAllTeamMembers_whenMembers_thenNotEmpty() {
    var team = new Team();
    team.setTeamType(TeamType.REGULATOR);

    var roleBuilder = TeamMemberRoleTestUtil.Builder()
        .withTeam(team)
        .withWebUserAccountId(1L);

    var teamMemberRoleAccessManager = roleBuilder.withRole(RegulatorTeamRole.ACCESS_MANAGER.name())
        .build();

    var teamMemberRoleOrgAccessManager = roleBuilder.withRole(RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER.name())
        .build();

    when(teamMemberRoleRepository.findAllByTeam(team)).thenReturn(
        List.of(teamMemberRoleAccessManager, teamMemberRoleOrgAccessManager));

    var result = teamMemberService.getTeamMembers(team);

    var teamView = new TeamView(team.toTeamId(), team.getTeamType(), team.getDisplayName());

    var expectedTeamMember = new TeamMember(new WebUserAccountId(1L), teamView, Set.of(RegulatorTeamRole.ACCESS_MANAGER,
        RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER));

    assertThat(result).containsExactly(expectedTeamMember);
  }

  @Test
  void getAllTeamMembers_whenNoMembers_thenEmpty() {
    var team = new Team();

    when(teamMemberRoleRepository.findAllByTeam(team)).thenReturn(List.of());

    var result = teamMemberService.getTeamMembers(team);

    assertThat(result).isEmpty();
    verify(teamMemberRoleRepository).findAllByTeam(team);
  }

  @Test
  void isMemberOfTeam_whenMember_thenTrue() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var teamId = new TeamId(UUID.randomUUID());

    when(teamMemberRoleRepository.existsByWuaIdAndTeam_Uuid(user.wuaId(), teamId.uuid())).thenReturn(true);

    assertTrue(teamMemberService.isMemberOfTeam(teamId, user));
  }

  @Test
  void isMemberOfTeam_whenNotMember_thenFalse() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var teamId = new TeamId(UUID.randomUUID());

    when(teamMemberRoleRepository.existsByWuaIdAndTeam_Uuid(user.wuaId(), teamId.uuid())).thenReturn(false);

    assertFalse(teamMemberService.isMemberOfTeam(teamId, user));
  }

  @Test
  void isMemberOfTeamWithAnyRoleOf_whenMemberWithRole_thenTrue() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var teamId = new TeamId(UUID.randomUUID());
    var roles = Set.of("ROLE_NAME");

    when(teamMemberRoleRepository.existsByWuaIdAndTeam_UuidAndRoleIn(user.wuaId(), teamId.uuid(), roles))
        .thenReturn(true);

    assertTrue(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, roles));
  }

  @Test
  void isMemberOfTeamWithAnyRoleOf_whenNotMemberWithRole_thenFalse() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var teamId = new TeamId(UUID.randomUUID());
    var roles = Set.of("ROLE_NAME");

    when(teamMemberRoleRepository.existsByWuaIdAndTeam_UuidAndRoleIn(user.wuaId(), teamId.uuid(), roles))
        .thenReturn(false);

    assertFalse(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, roles));
  }

  @Test
  void getTeamMembers_whenTeamMemberHasMultipleRoles_thenRolesMappedCorrectly() {

    var team = TeamTestUtil.Builder().build();

    var webUserAccountId = new WebUserAccountId(100);

    var firstRole = RegulatorTeamRole.ACCESS_MANAGER;
    var secondRole = RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER;

    team.setTeamType(TeamType.REGULATOR);

    var roleBuilder = TeamMemberRoleTestUtil.Builder()
        .withTeam(team)
        .withWebUserAccountId(webUserAccountId.id());

    // GIVEN a user with multiple roles in the same team
    var firstTeamMemberRole = roleBuilder
        .withRole(firstRole.name())
        .build();

    var secondTeamMemberRole = roleBuilder
        .withRole(secondRole.name())
        .build();

    when(teamMemberRoleRepository.findAllByTeam(team)).thenReturn(
        List.of(firstTeamMemberRole, secondTeamMemberRole));

    // WHEN we get the team members for that team
    var resultingTeamMembers = teamMemberService.getTeamMembers(team);

    // THEN one team member is returned with the multiple roles
    assertThat(resultingTeamMembers)
        .extracting(TeamMember::wuaId, TeamMember::roles)
        .containsExactly(
            tuple(
                webUserAccountId,
                Set.of(secondRole, firstRole)
            )
        );
  }

  @Test
  void getTeamMember_whenMemberIsInRegulatorTeam_thenGetTeamMember() {
    var team = new Team(UUID.randomUUID());
    team.setTeamType(TeamType.REGULATOR);

    var wuaId = new WebUserAccountId(123);
    var role = TeamMemberRoleTestUtil.Builder()
        .withRole(RegulatorTeamRole.VIEWER.name())
        .withWebUserAccountId(wuaId.id())
        .build();

    var teamView = new TeamView(team.toTeamId(), team.getTeamType(), team.getDisplayName());

    when(teamMemberRoleRepository.findAllByTeamAndWuaId(team, wuaId.id()))
        .thenReturn(List.of(role));

    var result = teamMemberService.getTeamMember(team, wuaId);

    assertTrue(result.isPresent());
    assertThat(result.get()).extracting(
        TeamMember::teamView,
        TeamMember::wuaId
    ).containsExactly(
        teamView,
        wuaId
    );

    assertThat(result.get().roles()).containsExactly(
        RegulatorTeamRole.VIEWER
    );
  }

  @Test
  void getTeamMember_whenMemberIsInIndustryTeam_thenGetTeamMember() {
    var team = new Team(UUID.randomUUID());
    team.setTeamType(TeamType.INDUSTRY);

    var wuaId = new WebUserAccountId(123);
    var role = TeamMemberRoleTestUtil.Builder()
        .withRole(IndustryTeamRole.VIEWER.name())
        .withWebUserAccountId(wuaId.id())
        .build();

    var teamView = new TeamView(team.toTeamId(), team.getTeamType(), team.getDisplayName());

    when(teamMemberRoleRepository.findAllByTeamAndWuaId(team, wuaId.id()))
        .thenReturn(List.of(role));

    var result = teamMemberService.getTeamMember(team, wuaId);

    assertTrue(result.isPresent());
    assertThat(result.get()).extracting(
        TeamMember::teamView,
        TeamMember::wuaId
    ).containsExactly(
        teamView,
        wuaId
    );

    assertThat(result.get().roles()).containsExactly(
        IndustryTeamRole.VIEWER
    );
  }

  @Test
  void getTeamMember_whenMemberIsInOffshoreAreaTeam_thenGetTeamMember() {
    var team = new Team(UUID.randomUUID());
    team.setTeamType(TeamType.OFFSHORE_AREA);

    var wuaId = new WebUserAccountId(123);
    var role = TeamMemberRoleTestUtil.Builder()
        .withRole(OffshoreAreaTeamRole.OFFSHORE_AREA_TEAM_MEMBER.name())
        .withWebUserAccountId(wuaId.id())
        .build();

    var teamView = new TeamView(team.toTeamId(), team.getTeamType(), team.getDisplayName());

    when(teamMemberRoleRepository.findAllByTeamAndWuaId(team, wuaId.id()))
        .thenReturn(List.of(role));

    var result = teamMemberService.getTeamMember(team, wuaId);

    assertTrue(result.isPresent());
    assertThat(result.get()).extracting(
        TeamMember::teamView,
        TeamMember::wuaId
    ).containsExactly(
        teamView,
        wuaId
    );

    assertThat(result.get().roles()).containsExactly(
        OffshoreAreaTeamRole.OFFSHORE_AREA_TEAM_MEMBER
    );
  }

  @Test
  void getTeamMember_whenMemberIsInOnshoreAreaTeam_thenGetTeamMember() {
    var team = new Team(UUID.randomUUID());
    team.setTeamType(TeamType.ONSHORE_AREA);

    var wuaId = new WebUserAccountId(123);
    var role = TeamMemberRoleTestUtil.Builder()
        .withRole(OnshoreAreaTeamRole.ONSHORE_AREA_TEAM_MEMBER.name())
        .withWebUserAccountId(wuaId.id())
        .build();

    var teamView = new TeamView(team.toTeamId(), team.getTeamType(), team.getDisplayName());

    when(teamMemberRoleRepository.findAllByTeamAndWuaId(team, wuaId.id()))
        .thenReturn(List.of(role));

    var result = teamMemberService.getTeamMember(team, wuaId);

    assertTrue(result.isPresent());
    assertThat(result.get()).extracting(
        TeamMember::teamView,
        TeamMember::wuaId
    ).containsExactly(
        teamView,
        wuaId
    );

    assertThat(result.get().roles()).containsExactly(
        OnshoreAreaTeamRole.ONSHORE_AREA_TEAM_MEMBER
    );
  }

  @Test
  void getTeamMember_whenMemberIsNotInTeam_thenEmpty() {
    var team = new Team(UUID.randomUUID());
    team.setTeamType(TeamType.REGULATOR);

    var wuaId = new WebUserAccountId(123);

    when(teamMemberRoleRepository.findAllByTeamAndWuaId(team, wuaId.id())).thenReturn(List.of());

    var result = teamMemberService.getTeamMember(team, wuaId);

    assertTrue(result.isEmpty());
  }

  @Test
  void getUserAsTeamMembers_whenUserIsNotInTeam_thenNoResults() {

    var user = ServiceUserDetailTestUtil.Builder().build();
    when(teamMemberRoleRepository.findAllByWuaId(user.wuaId())).thenReturn(List.of());

    var result = teamMemberService.getUserAsTeamMembers(user);

    assertThat(result).isEmpty();
  }

  @Test
  void getUserAsTeamMembers_whenUserIsInTeam_thenCorrectlyMapped() {

    var user = ServiceUserDetailTestUtil.Builder().build();
    var team = TeamTestUtil.Builder().build();
    var teamMemberRole = TeamMemberRoleTestUtil.Builder()
        .withTeam(team)
        .withRole(RegulatorTeamRole.EXPLORATION.name())
        .withWebUserAccountId(user.wuaId())
        .build();

    when(teamMemberRoleRepository.findAllByWuaId(user.wuaId())).thenReturn(List.of(teamMemberRole));

    var result = teamMemberService.getUserAsTeamMembers(user);

    assertThat(result).extracting(TeamMember::wuaId, TeamMember::roles, TeamMember::teamView)
        .containsExactly(
            Tuple.tuple(
                new WebUserAccountId(user.wuaId()),
                Set.of(RegulatorTeamRole.EXPLORATION),
                new TeamView(team.toTeamId(), team.getTeamType(), team.getDisplayName())
            )
        );
  }

}
