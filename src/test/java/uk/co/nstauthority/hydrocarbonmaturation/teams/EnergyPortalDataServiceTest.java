package uk.co.nstauthority.hydrocarbonmaturation.teams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportal.serviceproviders.epmq.ScopeType;
import uk.co.fivium.energyportal.serviceproviders.epmq.messages.ServiceProviderTeamDto;
import uk.co.fivium.energyportal.serviceproviders.epmq.messages.ServiceProviderTeamTypeRoleDto;
import uk.co.fivium.energyportal.serviceproviders.epmq.messages.ServiceProviderUserTeamRolesDto;


@ExtendWith(MockitoExtension.class)
class EnergyPortalDataServiceTest {

  @Mock
  private TeamRepository teamRepository;

  @Mock
  private TeamRoleRepository teamRoleRepository;

  @InjectMocks
  private EnergyPortalDataService energyPortalDataService;

  @ParameterizedTest
  @EnumSource(value = TeamType.class, names = "INDUSTRY", mode = EnumSource.Mode.EXCLUDE)
  void getServiceProviderTeamDtos(TeamType nonIndustryTeamType) {
    var team1 = TeamTestUtil.Builder().withTeamType(TeamType.INDUSTRY).buildScopedTeam();
    var team2 = TeamTestUtil.Builder().withTeamType(nonIndustryTeamType).buildStaticTeam();

    var expectedDto1 = new ServiceProviderTeamDto(
        team1.getId().toString(),
        team1.getScopeId(),
        ScopeType.ORGANISATION_GROUP,
        null,
        team1.getTeamType().name()
    );
    var expectedDto2 = new ServiceProviderTeamDto(
        team2.getId().toString(),
        team2.getScopeId(),
        ScopeType.ORGANISATION_GROUP,
        nonIndustryTeamType.getDisplayName(),
        team2.getTeamType().name()
    );

    when(teamRepository.findAll()).thenReturn(List.of(team1, team2));

    assertThat(energyPortalDataService.getServiceProviderTeamDtos())
        .containsExactlyInAnyOrder(expectedDto1, expectedDto2);
  }

  @Test
  void getTeamTypeToServiceProviderTeamTypeRoleDtos() {
    var regulatorServiceRoleDtos = Set.of(
        createServiceRoleDto(Role.MANAGE_TEAM, true),
        createServiceRoleDto(Role.REGULATOR_MANAGE_ALL_ORGANISATIONS, false),
        createServiceRoleDto(Role.EXPLORATION, false),
        createServiceRoleDto(Role.VIEW_ANY_RESOURCE, false),
        createServiceRoleDto(Role.RESERVES_AND_RESOURCES_ADVISOR, false),
        createServiceRoleDto(Role.PROJECT_ANALYST, false)
    );
    var northernOffshoreAreaServiceRoleDtos = Set.of(
        createServiceRoleDto(Role.MANAGE_TEAM, true),
        createServiceRoleDto(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, false)
    );
    var southernOffshoreAreaServiceRoleDtos = Set.of(
        createServiceRoleDto(Role.MANAGE_TEAM, true),
        createServiceRoleDto(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, false)
    );
    var onshoreOffshoreAreaServiceRoleDtos = Set.of(
        createServiceRoleDto(Role.MANAGE_TEAM, true),
        createServiceRoleDto(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, false)
    );
    var industryServiceRoleDtos = Set.of(
        createServiceRoleDto(Role.MANAGE_TEAM, true),
        createServiceRoleDto(Role.INDUSTRY_SUBMIT, false),
        createServiceRoleDto(Role.INDUSTRY_VIEWER, false)
    );

    assertThat(energyPortalDataService.getTeamTypeToServiceProviderTeamTypeRoleDtos())
        .isEqualTo(
            Map.of(
                TeamType.REGULATOR.name(), regulatorServiceRoleDtos,
                TeamType.NORTHERN_OFFSHORE_AREA.name(), northernOffshoreAreaServiceRoleDtos,
                TeamType.SOUTHERN_OFFSHORE_AREA.name(), southernOffshoreAreaServiceRoleDtos,
                TeamType.ONSHORE_AREA.name(), onshoreOffshoreAreaServiceRoleDtos,
                TeamType.INDUSTRY.name(), industryServiceRoleDtos
            )
        );
  }

  @Test
  void getTeamTypes() {
    assertThat(energyPortalDataService.getTeamTypes()).isEqualTo(Set.of(
        TeamType.REGULATOR.name(),
        TeamType.NORTHERN_OFFSHORE_AREA.name(),
        TeamType.SOUTHERN_OFFSHORE_AREA.name(),
        TeamType.ONSHORE_AREA.name(),
        TeamType.INDUSTRY.name()
    ));
  }


  @Test
  void getServiceProviderUserTeamRolesDtos() {
    var team1 = TeamTestUtil.Builder().buildScopedTeam();
    var team2 = TeamTestUtil.Builder().buildStaticTeam();

    var wuaId1Team1TeamRole = TeamRoleTestUtil.Builder()
        .withWuaId(1L)
        .withTeam(team1)
        .withRole(Role.MANAGE_TEAM)
        .build();

    var wuaId1Team2TeamRole = TeamRoleTestUtil.Builder()
        .withWuaId(1L)
        .withTeam(team2)
        .withRole(Role.INDUSTRY_VIEWER)
        .build();

    var wuaId2Team2TeamRole1 = TeamRoleTestUtil.Builder()
        .withWuaId(2L)
        .withTeam(team2)
        .withRole(Role.MANAGE_TEAM)
        .build();

    var wuaId2Team2TeamRole2 = TeamRoleTestUtil.Builder()
        .withWuaId(2L)
        .withTeam(team2)
        .withRole(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)
        .build();

    when(teamRoleRepository.findAll()).thenReturn(List.of(
        wuaId1Team1TeamRole,
        wuaId1Team2TeamRole,
        wuaId2Team2TeamRole1,
        wuaId2Team2TeamRole2
    ));

    assertThat(energyPortalDataService.getServiceProviderUserTeamRolesDtos())
        .containsExactlyInAnyOrder(
            new ServiceProviderUserTeamRolesDto(
                1L,
                team1.getId().toString(),
                team1.getTeamType().name(),
                Set.of(Role.MANAGE_TEAM.name())
            ),
            new ServiceProviderUserTeamRolesDto(
                1L,
                team2.getId().toString(),
                team2.getTeamType().name(),
                Set.of(Role.INDUSTRY_VIEWER.name())
            ),
            new ServiceProviderUserTeamRolesDto(
                2L,
                team2.getId().toString(),
                team2.getTeamType().name(),
                Set.of(Role.MANAGE_TEAM.name(), Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM.name())
            )
        );
  }

  private ServiceProviderTeamTypeRoleDto createServiceRoleDto(Role role, boolean isAssessManager) {
    return new ServiceProviderTeamTypeRoleDto(
        role.name(),
        role.getName(),
        role.getDescription(),
        isAssessManager,
        role.ordinal()
    );
  }
}