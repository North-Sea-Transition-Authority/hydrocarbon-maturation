package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
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

  private final ServiceUserDetail userDetail = ServiceUserDetailTestUtil.Builder().build();
  private final EnergyPortalUserDto energyPortalUserDto = EnergyPortalUserDtoTestUtil.Builder().build();

  @ParameterizedTest
  @EnumSource(ResponsibleTeamType.class)
  void isInResponsibleTeam(ResponsibleTeamType responsibleTeamType) {

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
  @EnumSource(ResponsibleTeamType.class)
  void getResponsibleTeamUsers(ResponsibleTeamType responsibleTeamType) {

    switch (responsibleTeamType) {
      case EXPLORATION -> when(regulatorTeamService.getExplorationTeamEnergyPortalUsers())
          .thenReturn(List.of(energyPortalUserDto));
      case NORTHERN_NORTH_SEA_WEST_OF_SHETLAND -> when(northernOffshoreAreaTeamService
          .getNorthernOffShoreAreaTeamEnergyPortalUsers())
          .thenReturn(List.of(energyPortalUserDto));
      case CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA -> when(southernOffshoreAreaTeamService
          .getSouthernOffShoreAreaTeamEnergyPortalUsers())
          .thenReturn(List.of(energyPortalUserDto));
      case ONSHORE -> when(onshoreAreaTeamService.getOnShoreAreaTeamEnergyPortalUsers())
          .thenReturn(List.of(energyPortalUserDto));
    }

    assertThat(responsibleTeamService.getResponsibleTeamUsers(responsibleTeamType))
        .isEqualTo(List.of(energyPortalUserDto));
  }

  @ParameterizedTest
  @EnumSource(ResourceType.class)
  void canUpdateResponsibleTeam(ResourceType resourceType) {
    var resource = new Resource();
    resource.setType(resourceType);
    assertThat(responsibleTeamService.canUpdateResponsibleTeam(resource))
        .isEqualTo(resourceType == ResourceType.DISCOVERY);
  }

  @Test
  void getResponsibleTeamTypesForUser_all() {
    when(regulatorTeamService.isRegulatorTeamRole(userDetail, RegulatorTeamRole.EXPLORATION)).thenReturn(true);
    when(northernOffshoreAreaTeamService.isNorthernOffshoreTeamMember(userDetail)).thenReturn(true);
    when(southernOffshoreAreaTeamService.isSouthernOffshoreTeamMember(userDetail)).thenReturn(true);
    when(onshoreAreaTeamService.isOnshoreTeamMember(userDetail)).thenReturn(true);

    assertThat(responsibleTeamService.getResponsibleTeamTypesForUser(userDetail)).containsExactly(
        ResponsibleTeamType.EXPLORATION, ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND,
        ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA, ResponsibleTeamType.ONSHORE);
  }

  @Test
  void getResponsibleTeamTypesForUser_none() {
    when(regulatorTeamService.isRegulatorTeamRole(userDetail, RegulatorTeamRole.EXPLORATION)).thenReturn(false);
    when(northernOffshoreAreaTeamService.isNorthernOffshoreTeamMember(userDetail)).thenReturn(false);
    when(southernOffshoreAreaTeamService.isSouthernOffshoreTeamMember(userDetail)).thenReturn(false);
    when(onshoreAreaTeamService.isOnshoreTeamMember(userDetail)).thenReturn(false);

    assertThat(responsibleTeamService.getResponsibleTeamTypesForUser(userDetail)).isEmpty();
  }
}