package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

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
class SubmissionSearchSelectorServiceFactoryTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private RegulatorSubmissionSearchSelectorService regulatorSubmissionSearchSelectorService;
  @Mock
  private OperatorSubmissionSearchSelectorService operatorSubmissionSearchSelectorService;
  @Mock
  private InternalTeamService internalTeamService;
  @InjectMocks
  private SubmissionSearchSelectorServiceFactory submissionSearchSelectorServiceFactory;

  @Test
  void getSubmissionSearchSelectorStrategy_whenUserIsInRegulatorOrAreaTeam_assertRegulatorServiceProvided() {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(submissionSearchSelectorServiceFactory.getSubmissionSearchSelectorStrategy(USER))
        .isEqualTo(regulatorSubmissionSearchSelectorService);
  }

  @Test
  void getSubmissionSearchSelectorStrategy_whenUserIsNotInRegulatorOrAreaTeam_assertOperatorServiceProvided() {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);

    assertThat(submissionSearchSelectorServiceFactory.getSubmissionSearchSelectorStrategy(USER))
        .isEqualTo(operatorSubmissionSearchSelectorService);
  }
}