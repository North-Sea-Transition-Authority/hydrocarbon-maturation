package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class ResourceRecordQueryServiceFactoryTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @Mock
  private RegulatorResourceRecordQueryService regulatorResourceRecordQueryService;
  @Mock
  private OperatorResourceRecordQueryService operatorResourceRecordQueryService;
  @Mock
  private UserDetailService userDetailService;
  @Mock
  private RegulatorTeamService regulatorTeamService;
  @Mock
  private AreaTeamService areaTeamService;
  @InjectMocks
  private ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory;

  @BeforeEach
  void setUp() {
    when(userDetailService.getUserDetail()).thenReturn(USER);
  }

  @ParameterizedTest
  @CsvSource(value = { "true, true",
      "true, false",
      "false, true"})
  void getQueryServiceStrategy_whenUserIsInRegulatorOrAreaTeam_assertRegulatorServiceProvided(
      boolean isRegulatorUser, boolean isAreaUser) {
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(isRegulatorUser);
    if (!isRegulatorUser) {
      when(areaTeamService.isAreaUser(USER)).thenReturn(isAreaUser);
    }

    assertThat(resourceRecordQueryServiceFactory.getQueryServiceStrategy()).isEqualTo(regulatorResourceRecordQueryService);
  }

  @Test
  void getQueryServiceStrategy_whenUserIsNotInRegulatorOrAreaTeam_assertOperatorServiceProvided() {
    when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);
    when(areaTeamService.isAreaUser(USER)).thenReturn(false);

    assertThat(resourceRecordQueryServiceFactory.getQueryServiceStrategy()).isEqualTo(operatorResourceRecordQueryService);
  }
}