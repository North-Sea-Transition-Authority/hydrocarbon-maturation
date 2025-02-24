package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRoleTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ExtendWith(MockitoExtension.class)
public class ResourceAccessServiceTest {

  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;
  @Mock
  private TeamQueryService teamQueryService;

  @InjectMocks
  private ResourceAccessService resourceAccessService;

  @Test
  void userHasAccessToResourceForOrganisation() {
    var organisationUnitId = 1;
    var organisationGroupId = 100;
    var organisationGroupIdString = String.valueOf(organisationGroupId);
    var expectedOrgGroups = List.of(organisationGroupId);
    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withScopeId(organisationGroupIdString)
        .buildScopedTeam();
    var wuaId = 999L;
    var user = UserTestUtil.Builder().withWuaId(wuaId).build();
    var teamRole = TeamRoleTestUtil.Builder()
        .withTeam(team)
        .withRole(Role.INDUSTRY_SUBMIT)
        .withWuaId(wuaId)
        .build();
    when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(organisationUnitId))
        .thenReturn(expectedOrgGroups);
    when(teamQueryService.getOrganisationScopedTeams(List.of(organisationGroupIdString)))
        .thenReturn(Set.of(team));
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(teamRole));
    assertTrue(resourceAccessService.userHasAccessToResourceForOrganisation(user, Set.of(Role.INDUSTRY_SUBMIT), organisationUnitId));
  }

  @Test
  void userDoesntHaveAccessToResourceForOrganisation_wrongOrganisation() {
    var organisationUnitId = 1;
    var organisationGroupId = 99;
    var organisationGroupIdString = String.valueOf(organisationGroupId);
    var unexpectedOrgGroups = List.of(organisationGroupId);
    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withScopeId(organisationGroupIdString)
        .buildScopedTeam();
    var otherTeam = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withScopeId("100")
        .buildScopedTeam();
    var wuaId = 999L;
    var user = UserTestUtil.Builder().withWuaId(wuaId).build();
    var otherTeamRole = TeamRoleTestUtil.Builder()
        .withTeam(otherTeam)
        .withRole(Role.INDUSTRY_SUBMIT)
        .withWuaId(wuaId)
        .build();
    when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(organisationUnitId))
        .thenReturn(unexpectedOrgGroups);
    when(teamQueryService.getOrganisationScopedTeams(List.of(organisationGroupIdString)))
        .thenReturn(Set.of(team));
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(otherTeamRole));
    assertFalse(resourceAccessService.userHasAccessToResourceForOrganisation(user, Set.of(Role.INDUSTRY_SUBMIT), organisationUnitId));
  }

  @Test
  void userDoesntHaveAccessToResourceForOrganisation_wrongRole() {
    var organisationUnitId = 1;
    var organisationGroupId = 99;
    var organisationGroupIdString = String.valueOf(organisationGroupId);
    var unexpectedOrgGroups = List.of(organisationGroupId);
    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .withScopeId(organisationGroupIdString)
        .buildScopedTeam();
    var wuaId = 999L;
    var user = UserTestUtil.Builder().withWuaId(wuaId).build();
    var otherTeamRole = TeamRoleTestUtil.Builder()
        .withTeam(team)
        .withRole(Role.INDUSTRY_VIEWER)
        .withWuaId(wuaId)
        .build();
    when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(organisationUnitId))
        .thenReturn(unexpectedOrgGroups);
    when(teamQueryService.getOrganisationScopedTeams(List.of(organisationGroupIdString)))
        .thenReturn(Set.of(team));
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(otherTeamRole));
    assertFalse(resourceAccessService.userHasAccessToResourceForOrganisation(user, Set.of(Role.INDUSTRY_SUBMIT), organisationUnitId));
  }
}
