package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class LegacyLegacyTeamManagementServiceTest {
  @Mock
  private RegulatorTeamService regulatorTeamService;
  @Mock
  private TeamService teamService;

  @InjectMocks
  private LegacyTeamManagementService legacyTeamManagementService;

  @Test
  void getTeamsThatUserHasAccessTo_IndustryAccessManagerInRegulatorTeam_ReturnAllTeam() {
    var user = UserTestUtil.Builder().build();
    when(regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER)).thenReturn(true);
    legacyTeamManagementService.getTeamsThatUserHasAccessTo(user);
    verify(teamService).getAllTeamOfTypes(Set.of(TeamType.INDUSTRY, TeamType.REGULATOR));
  }

  @Test
  void getTeamsThatUserHasAccessTo_NotIndustryAccessManagerInRegulatorTeam_ReturnOnlyTeamsMemberOf() {
    var user = UserTestUtil.Builder().build();
    legacyTeamManagementService.getTeamsThatUserHasAccessTo(user);
    verify(teamService).getTeamsThatUserBelongsTo(user);
  }

  @Test
  void isIndustryAccessManagerInRegulatorTeam_InTeamAndIsIndustryAccessManager() {
    var user = UserTestUtil.Builder().build();
    when(regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER)).thenReturn(true);
    var industryAccessManagerInRegulatorTeam = legacyTeamManagementService.isIndustryAccessManagerInRegulatorTeam(user);

    assertThat(industryAccessManagerInRegulatorTeam).isTrue();
  }

  @Test
  void isIndustryAccessManagerInRegulatorTeam_InTeamButNotIndustryAccessManager() {
    var user = UserTestUtil.Builder().build();
    when(regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.INDUSTRY_ACCESS_MANAGER)).thenReturn(false);
    var industryAccessManagerInRegulatorTeam = legacyTeamManagementService.isIndustryAccessManagerInRegulatorTeam(user);

    assertThat(industryAccessManagerInRegulatorTeam).isFalse();
  }

  @Test
  void isIndustryAccessManagerInRegulatorTeam_NotRegulator() {
    var user = UserTestUtil.Builder().build();
    var industryAccessManagerInRegulatorTeam = legacyTeamManagementService.isIndustryAccessManagerInRegulatorTeam(user);

    assertThat(industryAccessManagerInRegulatorTeam).isFalse();
  }
}