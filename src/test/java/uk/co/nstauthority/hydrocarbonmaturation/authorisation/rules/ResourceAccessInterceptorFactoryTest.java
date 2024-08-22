package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;

@ExtendWith(MockitoExtension.class)
class ResourceAccessInterceptorFactoryTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private RegulatorResourceAccessInterceptorRule regulatorTeamMembershipInterceptorRule;

  @Mock
  private OperatorResourceAccessInterceptorRule operatorTeamMembershipInterceptorRule;

  @Mock
  private InternalTeamService internalTeamService;

  @InjectMocks
  private ResourceAccessInterceptorFactory resourceAccessInterceptorFactory;

  @Test
  void getTeamMembershipInterceptorStrategy_whenUserIsRegulatorOrAreaTeam_assertRegulatorServiceProvided() {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy(USER))
        .isEqualTo(regulatorTeamMembershipInterceptorRule);
  }

  @Test
  void getTeamMembershipInterceptorStrategy_whenUserIsNotRegulator_assertOperatorServiceProvided() {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);

    assertThat(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy(USER))
        .isEqualTo(operatorTeamMembershipInterceptorRule);
  }
}