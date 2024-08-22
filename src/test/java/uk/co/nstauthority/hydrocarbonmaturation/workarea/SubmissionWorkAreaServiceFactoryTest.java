package uk.co.nstauthority.hydrocarbonmaturation.workarea;

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
class SubmissionWorkAreaServiceFactoryTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private ResponsibleTeamSubmissionWorkAreaQueryService responsibleTeamSubmissionWorkAreaQueryService;
  @Mock
  private OperatorSubmissionWorkAreaQueryService operatorSubmissionWorkAreaQueryService;
  @Mock
  private InternalTeamService internalTeamService;
  @InjectMocks
  private SubmissionWorkAreaServiceFactory submissionWorkAreaServiceFactory;

  @Test
  void getQueryServiceStrategy_whenUserIsRegulator_assertRegulatorServiceProvided() {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .isEqualTo(responsibleTeamSubmissionWorkAreaQueryService);
  }

  @Test
  void getQueryServiceStrategy_whenUserIsNotRegulator_assertOperatorServiceProvided() {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);

    assertThat(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .isEqualTo(operatorSubmissionWorkAreaQueryService);
  }
}