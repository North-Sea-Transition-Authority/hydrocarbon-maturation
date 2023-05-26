package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.southernoffshore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.SpyBean;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ExtendWith(MockitoExtension.class)
class SouthernOffshoreAreaTeamServiceTest {

  @Mock
  private TeamService teamService;

  @Mock
  private TeamMemberRoleService teamMemberRoleService;

  @InjectMocks
  @SpyBean
  private SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService;

  @Test
  void getTeam_whenMatch_thenReturnTeam() {

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.SOUTHERN_OFFSHORE_AREA)
        .build();

    var teamId = team.toTeamId();

    when(teamService.getTeam(teamId, TeamType.SOUTHERN_OFFSHORE_AREA)).thenReturn(Optional.of(team));

    assertThat(southernOffshoreAreaTeamService.getTeam(teamId)).contains(team);
    verify(teamService, times(1)).getTeam(teamId, team.getTeamType());
  }

  @Test
  void getTeam_whenNoMatch_thenEmptyOptional() {

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.SOUTHERN_OFFSHORE_AREA)
        .build();

    var teamId = team.toTeamId();

    when(teamService.getTeam(teamId, TeamType.SOUTHERN_OFFSHORE_AREA)).thenReturn(Optional.empty());

    assertThat(southernOffshoreAreaTeamService.getTeam(teamId)).isEmpty();
    verify(teamService, times(1)).getTeam(teamId, team.getTeamType());
  }


  @Test
  void addUserTeamRoles_verifyRepositoryInteractions() {

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.SOUTHERN_OFFSHORE_AREA)
        .build();
    var userToAdd = EnergyPortalUserDtoTestUtil.Builder().build();
    var areaTeamRoles = Set.of(
        SouthernOffshoreAreaTeamRole.SOUTHERN_OFFSHORE_AREA_MANAGE_TEAM
    );

    southernOffshoreAreaTeamService.addUserTeamRoles(team, userToAdd, areaTeamRoles);

    var rolesAsStrings = areaTeamRoles
        .stream()
        .map(SouthernOffshoreAreaTeamRole::name)
        .collect(Collectors.toSet());

    verify(teamMemberRoleService, times(1)).addUserTeamRoles(team, userToAdd, rolesAsStrings);
  }
}