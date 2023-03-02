package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;
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
    when(regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER)).thenReturn(true);
    teamManagementService.getTeamsThatUserHasAccessTo(user);
    verify(teamService).getAllTeams();
  }
  @Test
  void getTeamsThatUserHasAccessTo_NotIndustryAccessManagerInRegulatorTeam_ReturnOnlyTeamsMemberOf() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    teamManagementService.getTeamsThatUserHasAccessTo(user);
    verify(teamService).getTeamsThatUserBelongsTo(user);
  }

  @Test
  void isIndustryAccessManagerInRegulatorTeam_InTeamAndIsIndustryAccessManager() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    when(regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER)).thenReturn(true);
    var industryAccessManagerInRegulatorTeam = teamManagementService.isIndustryAccessManagerInRegulatorTeam(user);

    assertThat(industryAccessManagerInRegulatorTeam).isTrue();
  }

  @Test
  void isIndustryAccessManagerInRegulatorTeam_InTeamButNotIndustryAccessManager() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    when(regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER)).thenReturn(false);
    var industryAccessManagerInRegulatorTeam = teamManagementService.isIndustryAccessManagerInRegulatorTeam(user);

    assertThat(industryAccessManagerInRegulatorTeam).isFalse();
  }

  @Test
  void isIndustryAccessManagerInRegulatorTeam_NotRegulator() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var industryAccessManagerInRegulatorTeam = teamManagementService.isIndustryAccessManagerInRegulatorTeam(user);

    assertThat(industryAccessManagerInRegulatorTeam).isFalse();
  }
}