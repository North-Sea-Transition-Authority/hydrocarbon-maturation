package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

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
class SubmissionSearchSelectorServiceFactoryTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @Mock
  private RegulatorSubmissionSearchSelectorService regulatorSubmissionSearchSelectorService;
  @Mock
  private OperatorSubmissionSearchSelectorService operatorSubmissionSearchSelectorService;
  @Mock
  private UserDetailService userDetailService;
  @Mock
  private RegulatorTeamService regulatorTeamService;
  @InjectMocks
  private SubmissionSearchSelectorServiceFactory submissionSearchSelectorServiceFactory;

  @BeforeEach
  void setUp() {
    when(userDetailService.getUserDetail()).thenReturn(USER);
  }

  @Test
  void getQueryServiceStrategy_whenUserIsRegulator_assertRegulatorServiceProvided() {
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(true);

    assertThat(submissionSearchSelectorServiceFactory.getSubmissionSearchSelectorStrategy()).isEqualTo(
        regulatorSubmissionSearchSelectorService);
  }

  @Test
  void getQueryServiceStrategy_whenUserIsNotRegulator_assertOperatorServiceProvided() {
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);

    assertThat(submissionSearchSelectorServiceFactory.getSubmissionSearchSelectorStrategy()).isEqualTo(
        operatorSubmissionSearchSelectorService);
  }
}