package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class TeamTypeServiceTest {
  @Mock
  private RegulatorTeamService regulatorTeamService;

  @InjectMocks
  private TeamTypeService teamTypeService;

  @Test
  void isIndustryAccessManagerInRegulatorTeam_InTeamAndIsIndustryAccessManager() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var team1 = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .withId(UUID.randomUUID())
        .build();
    when(regulatorTeamService.getRegulatorTeamForUser(user)).thenReturn(Optional.of(team1));
    when(regulatorTeamService.isIndustryAccessManager(TeamId.valueOf(team1.getUuid()), user)).thenReturn(true);
    var industryAccessManagerInRegulatorTeam = teamTypeService.isIndustryAccessManagerInRegulatorTeam(user);

    assertThat(industryAccessManagerInRegulatorTeam).isTrue();
  }

  @Test
  void isIndustryAccessManagerInRegulatorTeam_InTeamButNotIndustryAccessManager() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var team1 = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .withId(UUID.randomUUID())
        .build();
    when(regulatorTeamService.getRegulatorTeamForUser(user)).thenReturn(Optional.of(team1));
    when(regulatorTeamService.isIndustryAccessManager(TeamId.valueOf(team1.getUuid()), user)).thenReturn(false);
    var industryAccessManagerInRegulatorTeam = teamTypeService.isIndustryAccessManagerInRegulatorTeam(user);

    assertThat(industryAccessManagerInRegulatorTeam).isFalse();
  }

  @Test
  void isIndustryAccessManagerInRegulatorTeam_NotRegulator() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    when(regulatorTeamService.getRegulatorTeamForUser(user)).thenReturn(Optional.empty());
    var industryAccessManagerInRegulatorTeam = teamTypeService.isIndustryAccessManagerInRegulatorTeam(user);

    assertThat(industryAccessManagerInRegulatorTeam).isFalse();
  }
}