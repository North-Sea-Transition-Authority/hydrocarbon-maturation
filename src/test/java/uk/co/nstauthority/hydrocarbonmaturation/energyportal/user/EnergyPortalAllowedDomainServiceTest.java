package uk.co.nstauthority.hydrocarbonmaturation.energyportal.user;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroupEmailDomain;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ExtendWith(MockitoExtension.class)
class EnergyPortalAllowedDomainServiceTest {

  private static final String USER_EMAIL = "user@example.com";

  @Mock
  private OrganisationGroupQueryService organisationGroupQueryService;

  @InjectMocks
  private EnergyPortalAllowedDomainService energyPortalAllowedDomainService;

  @ParameterizedTest
  @MethodSource("provideDomainIsAllowedCombinations")
  void isAllowedDomain_industry(String domain, boolean isAllowed) {
    var industryTeam = new Team(UUID.randomUUID());
    industryTeam.setTeamType(TeamType.INDUSTRY);
    industryTeam.setName("industry team");
    industryTeam.setScopeType("ORGANISATION_GROUP_ID");
    industryTeam.setScopeId("1");

    var orgGroup = new OrganisationGroupDto();
    orgGroup.setOrganisationGroupId(1);
    orgGroup.setEmailDomains(List.of(new OrganisationGroupEmailDomain(domain)));

    when(organisationGroupQueryService.getOrganisationGroupById(Integer.parseInt(industryTeam.getScopeId()))).thenReturn(
        Optional.of(orgGroup)
    );

    assertThat(energyPortalAllowedDomainService.isAllowedDomain(USER_EMAIL, industryTeam)).isEqualTo(isAllowed);
  }

  @ParameterizedTest
  @MethodSource("provideDomainIsAllowedCombinations")
  void isAllowedDomain_regulator(String domain, boolean isAllowed) {
    var regTeam = new Team(UUID.randomUUID());
    regTeam.setTeamType(TeamType.REGULATOR);
    regTeam.setName("regulator team");

    var orgGroup = new OrganisationGroupDto();
    orgGroup.setEmailDomains(List.of(new OrganisationGroupEmailDomain(domain)));

    when(organisationGroupQueryService.getRegulatorOrganisationGroup()).thenReturn(
        Optional.of(orgGroup)
    );

    assertThat(energyPortalAllowedDomainService.isAllowedDomain(USER_EMAIL, regTeam)).isEqualTo(isAllowed);
  }

  @ParameterizedTest
  @MethodSource("provideDomainIsAllowedCombinations")
  void isAllowedDomain_northernOffshore(String domain, boolean isAllowed) {
    var consulteeTeam = new Team(UUID.randomUUID());
    consulteeTeam.setTeamType(TeamType.NORTHERN_OFFSHORE_AREA);
    consulteeTeam.setName("northern offshore team");

    var orgGroup = new OrganisationGroupDto();
    orgGroup.setEmailDomains(List.of(new OrganisationGroupEmailDomain(domain)));

    when(organisationGroupQueryService.getRegulatorOrganisationGroup()).thenReturn(
        Optional.of(orgGroup)
    );

    assertThat(energyPortalAllowedDomainService.isAllowedDomain(USER_EMAIL, consulteeTeam)).isEqualTo(isAllowed);
  }

  @ParameterizedTest
  @MethodSource("provideDomainIsAllowedCombinations")
  void isAllowedDomain_onShore(String domain, boolean isAllowed) {
    var consulteeTeam = new Team(UUID.randomUUID());
    consulteeTeam.setTeamType(TeamType.ONSHORE_AREA);
    consulteeTeam.setName("onshore team");

    var orgGroup = new OrganisationGroupDto();
    orgGroup.setEmailDomains(List.of(new OrganisationGroupEmailDomain(domain)));

    when(organisationGroupQueryService.getRegulatorOrganisationGroup()).thenReturn(
        Optional.of(orgGroup)
    );

    assertThat(energyPortalAllowedDomainService.isAllowedDomain(USER_EMAIL, consulteeTeam)).isEqualTo(isAllowed);
  }

  @ParameterizedTest
  @MethodSource("provideDomainIsAllowedCombinations")
  void isAllowedDomain_southernOffShore(String domain, boolean isAllowed) {
    var consulteeTeam = new Team(UUID.randomUUID());
    consulteeTeam.setTeamType(TeamType.SOUTHERN_OFFSHORE_AREA);
    consulteeTeam.setName("southern offshore team");

    var orgGroup = new OrganisationGroupDto();
    orgGroup.setEmailDomains(List.of(new OrganisationGroupEmailDomain(domain)));

    when(organisationGroupQueryService.getRegulatorOrganisationGroup()).thenReturn(
        Optional.of(orgGroup)
    );

    assertThat(energyPortalAllowedDomainService.isAllowedDomain(USER_EMAIL, consulteeTeam)).isEqualTo(isAllowed);
  }

  @ParameterizedTest
  @EnumSource(TeamType.class)
  void isAllowedDomain_ShouldSupportAllTeamTypes(TeamType teamType) {
    Team team = new Team(UUID.randomUUID());
    team.setTeamType(teamType);
    team.setScopeId("1");

    lenient().when(organisationGroupQueryService.getOrganisationGroupById(anyInt()))
        .thenReturn(Optional.empty());
    lenient().when(organisationGroupQueryService.getRegulatorOrganisationGroup())
        .thenReturn(Optional.empty());

    assertDoesNotThrow(() ->
        energyPortalAllowedDomainService.isAllowedDomain(USER_EMAIL, team)
    );
  }

  private static Stream<Arguments> provideDomainIsAllowedCombinations() {
    return Stream.of(
        Arguments.of("example.com", true),
        Arguments.of("domain.com", false)
    );
  }
}