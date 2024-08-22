package uk.co.nstauthority.hydrocarbonmaturation.teams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;

@ExtendWith({MockitoExtension.class})
class InternalTeamServiceTest {

  @Mock
  private TeamQueryService teamQueryService;

  @InjectMocks
  private InternalTeamService internalTeamService;

  @ParameterizedTest
  @EnumSource(value = TeamType.class, mode = EnumSource.Mode.EXCLUDE, names = "INDUSTRY")
  void isRegulatorOrAreaTeamUser(TeamType teamType) {
    var user = UserTestUtil.Builder().build();
    var teamRole = new TeamRole();
    var team = new Team();
    team.setTeamType(teamType);
    teamRole.setTeam(team);
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(teamRole));
    assertThat(internalTeamService.isRegulatorOrAreaTeamUser(user)).isTrue();
  }

  @Test
  void isNotRegulatorOrAreaTeamUser() {
    var user = UserTestUtil.Builder().build();
    var teamRole = new TeamRole();
    var team = new Team();
    team.setTeamType(TeamType.INDUSTRY);
    teamRole.setTeam(team);
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(teamRole));
    assertThat(internalTeamService.isRegulatorOrAreaTeamUser(user)).isFalse();
  }
}
