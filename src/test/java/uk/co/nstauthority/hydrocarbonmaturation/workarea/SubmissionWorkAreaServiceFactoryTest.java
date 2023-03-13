package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
class SubmissionWorkAreaServiceFactoryTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @Mock
  private RegulatorSubmissionWorkAreaQueryService regulatorSubmissionWorkAreaQueryService;
  @Mock
  private OperatorSubmissionWorkAreaQueryService operatorSubmissionWorkAreaQueryService;
  @Mock
  private UserDetailService userDetailService;
  @Mock
  private RegulatorTeamService regulatorTeamService;
  @InjectMocks
  private SubmissionWorkAreaServiceFactory submissionWorkAreaServiceFactory;

  @BeforeEach
  void setUp() {
    when(userDetailService.getUserDetail()).thenReturn(USER);
  }

  @Test
  void getQueryServiceStrategy_whenUserIsRegulator_assertRegulatorServiceProvided() {
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(true);

    assertThat(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy())
        .isEqualTo(regulatorSubmissionWorkAreaQueryService);
  }

  @Test
  void getQueryServiceStrategy_whenUserIsNotRegulator_assertOperatorServiceProvided() {
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);

    assertThat(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy())
        .isEqualTo(operatorSubmissionWorkAreaQueryService);
  }
}