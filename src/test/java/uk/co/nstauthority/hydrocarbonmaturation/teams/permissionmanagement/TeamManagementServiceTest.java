package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class TeamManagementServiceTest {
  @Mock
  private RegulatorTeamService regulatorTeamService;
  @Mock
  private TeamService teamService;

  @InjectMocks
  private TeamManagementService teamManagementService;

  @Test
  void getTeamsThatUserHasAccessTo_IndustryAccessManagerInRegulatorTeam_ReturnAllTeam() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var team1 = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .build();
    when(regulatorTeamService.getRegulatorTeamForUser(user)).thenReturn(Optional.of(team1));
    when(regulatorTeamService.isIndustryAccessManager(team1.toTeamId(), user)).thenReturn(true);
    teamManagementService.getTeamsThatUserHasAccessTo(user);
    verify(teamService).getAllTeams();
  }
  @Test
  void getTeamsThatUserHasAccessTo_NotIndustryAccessManagerInRegulatorTeam_ReturnOnlyTeamsMemberOf() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    when(regulatorTeamService.getRegulatorTeamForUser(user)).thenReturn(Optional.empty());
    teamManagementService.getTeamsThatUserHasAccessTo(user);
    verify(teamService).getTeamsThatUserBelongsTo(user);
  }

  @Test
  void isIndustryAccessManagerInRegulatorTeam_InTeamAndIsIndustryAccessManager() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var team1 = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .build();
    when(regulatorTeamService.getRegulatorTeamForUser(user)).thenReturn(Optional.of(team1));
    when(regulatorTeamService.isIndustryAccessManager(team1.toTeamId(), user)).thenReturn(true);
    var industryAccessManagerInRegulatorTeam = teamManagementService.isIndustryAccessManagerInRegulatorTeam(user);

    assertThat(industryAccessManagerInRegulatorTeam).isTrue();
  }

  @Test
  void isIndustryAccessManagerInRegulatorTeam_InTeamButNotIndustryAccessManager() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var team1 = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .build();
    when(regulatorTeamService.getRegulatorTeamForUser(user)).thenReturn(Optional.of(team1));
    when(regulatorTeamService.isIndustryAccessManager(team1.toTeamId(), user)).thenReturn(false);
    var industryAccessManagerInRegulatorTeam = teamManagementService.isIndustryAccessManagerInRegulatorTeam(user);

    assertThat(industryAccessManagerInRegulatorTeam).isFalse();
  }

  @Test
  void isIndustryAccessManagerInRegulatorTeam_NotRegulator() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    when(regulatorTeamService.getRegulatorTeamForUser(user)).thenReturn(Optional.empty());
    var industryAccessManagerInRegulatorTeam = teamManagementService.isIndustryAccessManagerInRegulatorTeam(user);

    assertThat(industryAccessManagerInRegulatorTeam).isFalse();
  }
}