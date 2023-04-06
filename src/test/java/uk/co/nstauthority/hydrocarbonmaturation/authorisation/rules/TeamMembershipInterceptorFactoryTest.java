package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class TeamMembershipInterceptorFactoryTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @Mock
  private  RegulatorTeamMembershipInterceptorRule regulatorTeamMembershipInterceptorRule;

  @Mock
  private  OperatorTeamMembershipInterceptorRule operatorTeamMembershipInterceptorRule;

  @Mock
  private  UserDetailService userDetailService;

  @Mock
  private  RegulatorTeamService regulatorTeamService;

  @InjectMocks
  private TeamMembershipInterceptorFactory teamMembershipInterceptorFactory;

  @BeforeEach
  void setUp() {
    when(userDetailService.getUserDetail()).thenReturn(USER);
  }

  @Test
  void getTeamMembershipInterceptorStrategy_whenUserIsRegulator_assertRegulatorServiceProvided() {
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(true);

    assertThat(teamMembershipInterceptorFactory.getTeamMembershipInterceptorStrategy())
        .isEqualTo(regulatorTeamMembershipInterceptorRule);
  }

  @Test
  void getTeamMembershipInterceptorStrategy_whenUserIsNotRegulator_assertOperatorServiceProvided() {
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);

    assertThat(teamMembershipInterceptorFactory.getTeamMembershipInterceptorStrategy())
        .isEqualTo(operatorTeamMembershipInterceptorRule);
  }
}