package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class ResourceAccessInterceptorFactoryTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private RegulatorResourceAccessInterceptorRule regulatorTeamMembershipInterceptorRule;

  @Mock
  private OperatorResourceAccessInterceptorRule operatorTeamMembershipInterceptorRule;

  @Mock
  private UserService userService;

  @Mock
  private RegulatorTeamService regulatorTeamService;

  @Mock
  private AreaTeamService areaTeamService;

  @InjectMocks
  private ResourceAccessInterceptorFactory resourceAccessInterceptorFactory;

  @BeforeEach
  void setUp() {
    when(userService.getUser()).thenReturn(USER);
  }

  @ParameterizedTest
  @CsvSource(value = { "true, true",
      "true, false",
      "false, true"})
  void getTeamMembershipInterceptorStrategy_whenUserIsRegulatorOrAreaTeam_assertRegulatorServiceProvided(
      boolean isRegulatorUser, boolean isAreaUser) {
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(isRegulatorUser);
    if (!isRegulatorUser) {
      when(areaTeamService.isAreaUser(USER)).thenReturn(isAreaUser);
    }

    assertThat(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy())
        .isEqualTo(regulatorTeamMembershipInterceptorRule);
  }

  @Test
  void getTeamMembershipInterceptorStrategy_whenUserIsNotRegulator_assertOperatorServiceProvided() {
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);
    when(areaTeamService.isAreaUser(USER)).thenReturn(false);

    assertThat(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy())
        .isEqualTo(operatorTeamMembershipInterceptorRule);
  }
}