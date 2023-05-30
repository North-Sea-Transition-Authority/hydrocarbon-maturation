package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.SpyBean;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.northernoffshore.NorthernOffshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.onshore.OnshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.southernoffshore.SouthernOffshoreAreaTeamService;

@ExtendWith(MockitoExtension.class)
class AreaTeamServiceTest {
  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @Mock
  private NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService;

  @Mock
  private SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService;

  @Mock
  private OnshoreAreaTeamService onshoreAreaTeamService;

  @InjectMocks
  @SpyBean
  private AreaTeamService areaTeamService;

  @ParameterizedTest
  @CsvSource(value = { "true, true, true",
                       "true, true, false",
                       "true, false, true",
                       "true, false, false",
                       "false, true, true",
                       "false, true, false",
                       "false, false, true",
                       "false, false, false"})
  void getQueryServiceStrategy_whenUserIsInRegulatorOrAreaTeam_assertRegulatorServiceProvided(
      boolean isNorthernOffshoreAreaUser, boolean isSouthernOffshoreAreaUser, boolean isOnshoreAreaUser) {
    when(northernOffshoreAreaTeamService.isNorthernOffshoreAreaUser(USER)).thenReturn(isNorthernOffshoreAreaUser);
    if (!isNorthernOffshoreAreaUser) {
      when(southernOffshoreAreaTeamService.isSouthernOffshoreAreaUser(USER)).thenReturn(isSouthernOffshoreAreaUser);
    }
    if (!isNorthernOffshoreAreaUser && !isSouthernOffshoreAreaUser) {
      when(onshoreAreaTeamService.isOnshoreAreaUser(USER)).thenReturn(isOnshoreAreaUser);
    }

    assertThat(areaTeamService.isAreaUser(USER))
        .isEqualTo(isNorthernOffshoreAreaUser || isSouthernOffshoreAreaUser || isOnshoreAreaUser);
  }
}