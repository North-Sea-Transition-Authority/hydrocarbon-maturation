package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.northernoffshore.NorthernOffshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.onshore.OnshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.southernoffshore.SouthernOffshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class ResponsibleTeamServiceTest {

  @Mock
  private RegulatorTeamService regulatorTeamService;

  @Mock
  private NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService;

  @Mock
  private SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService;

  @Mock
  private OnshoreAreaTeamService onshoreAreaTeamService;

  @InjectMocks
  private ResponsibleTeamService responsibleTeamService;

  @ParameterizedTest
  @EnumSource(ResponsibleTeamType.class)
  void isInResponsibleTeam(ResponsibleTeamType responsibleTeamType) {

    var userDetail = ServiceUserDetailTestUtil.Builder().build();

    switch (responsibleTeamType) {
      case EXPLORATION -> when(regulatorTeamService.isRegulatorTeamRole(userDetail, RegulatorTeamRole.EXPLORATION))
          .thenReturn(true);
      case NORTHERN_NORTH_SEA_WEST_OF_SHETLAND -> when(northernOffshoreAreaTeamService
          .isNorthernOffshoreTeamMember(userDetail))
          .thenReturn(true);
      case CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA -> when(southernOffshoreAreaTeamService
          .isSouthernOffshoreTeamMember(userDetail))
          .thenReturn(true);
      case ONSHORE -> when(onshoreAreaTeamService
          .isOnshoreTeamMember(userDetail))
          .thenReturn(true);
    }

    assertThat(responsibleTeamService.isInResponsibleTeam(userDetail, responsibleTeamType)).isTrue();
  }

  @ParameterizedTest
  @EnumSource(ResourceType.class)
  void canUpdateResponsibleTeam(ResourceType resourceType) {
    var resource = new Resource();
    resource.setType(resourceType);
    assertThat(responsibleTeamService.canUpdateResponsibleTeam(resource))
        .isEqualTo(resourceType == ResourceType.DISCOVERY);
  }
}