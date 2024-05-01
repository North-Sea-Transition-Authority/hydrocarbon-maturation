package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith({MockitoExtension.class})
class InternalLegacyTeamServiceTest {

  @Mock
  private UserService userService;

  @Mock
  private RegulatorTeamService regulatorTeamService;

  @Mock
  private AreaTeamService areaTeamService;

  @InjectMocks
  private InternalTeamService internalTeamService;

  @ParameterizedTest
  @CsvSource(value = { "true, true",
      "true, false",
      "false, true"})
  void isRegulator_verifyMethodCall(boolean isRegulatorUser, boolean isAreaUser) {
    var user = UserTestUtil.Builder().build();
    when(userService.getUser()).thenReturn(user);
    when(regulatorTeamService.isRegulatorUser(user)).thenReturn(isRegulatorUser);
    if (!isRegulatorUser) {
      when(areaTeamService.isAreaUser(user)).thenReturn(isAreaUser);
    }
    var result = internalTeamService.isRegulatorOrAreaTeamUser();

    if (isRegulatorUser || isAreaUser) {
      assertThat(result).isTrue();
    } else {
      assertThat(result).isFalse();
    }

    verify(regulatorTeamService).isRegulatorUser(user);
    if (!isRegulatorUser) {
      verify(areaTeamService).isAreaUser(user);
    }
  }
}
