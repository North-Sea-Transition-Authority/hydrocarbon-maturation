package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

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
class ResourceRecordQueryServiceFactoryTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private RegulatorResourceRecordQueryService regulatorResourceRecordQueryService;
  @Mock
  private OperatorResourceRecordQueryService operatorResourceRecordQueryService;
  @Mock
  private InternalTeamService internalTeamService;
  @InjectMocks
  private ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory;

  @Test
  void getQueryServiceStrategy_whenUserIsInRegulatorOrAreaTeam_assertRegulatorServiceProvided() {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    assertThat(resourceRecordQueryServiceFactory.getQueryServiceStrategy(USER)).isEqualTo(regulatorResourceRecordQueryService);
  }

  @Test
  void getQueryServiceStrategy_whenUserIsNotInRegulatorOrAreaTeam_assertOperatorServiceProvided() {
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);

    assertThat(resourceRecordQueryServiceFactory.getQueryServiceStrategy(USER)).isEqualTo(operatorResourceRecordQueryService);
  }
}