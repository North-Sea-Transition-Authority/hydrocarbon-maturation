package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ExtendWith(MockitoExtension.class)
class IndustryTeamServiceTest {

  @Mock
  private TeamService teamService;

  @Mock
  private TeamMemberService teamMemberService;

  @Mock
  private TeamMemberRoleService teamMemberRoleService;

  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;

  @InjectMocks
  private IndustryTeamService industryTeamService;

  @Test
  void getTeam_whenMatch_thenReturnTeam() {

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamId = team.toTeamId();

    when(teamService.getTeam(teamId, TeamType.INDUSTRY)).thenReturn(Optional.of(team));

    assertThat(industryTeamService.getTeam(teamId)).contains(team);
    verify(teamService, times(1)).getTeam(teamId, team.getTeamType());
  }

  @Test
  void getTeam_whenNoMatch_thenEmptyOptional() {

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();

    var teamId = team.toTeamId();

    when(teamService.getTeam(teamId, TeamType.INDUSTRY)).thenReturn(Optional.empty());

    assertThat(industryTeamService.getTeam(teamId)).isEmpty();
    verify(teamService, times(1)).getTeam(teamId, team.getTeamType());
  }

  @Test
  void isAccessManager_whenAccessManager_thenTrue() {

    var teamId = new TeamId(UUID.randomUUID());
    var user = ServiceUserDetailTestUtil.Builder().build();

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, Set.of(IndustryTeamRole.ACCESS_MANAGER.name())))
        .thenReturn(true);

    assertTrue(industryTeamService.isAccessManager(teamId, user));
  }

  @Test
  void isSubmitter_whenSubmitter_thenTrue() {

    var teamId = new TeamId(UUID.randomUUID());
    var user = ServiceUserDetailTestUtil.Builder().build();

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, Set.of(IndustryTeamRole.SUBMITTER.name())))
        .thenReturn(true);

    assertTrue(industryTeamService.isSubmitter(teamId, user));
  }

  @Test
  void isSubmitter_whenNotSubmitter_thenFalse() {

    var teamId = new TeamId(UUID.randomUUID());
    var user = ServiceUserDetailTestUtil.Builder().build();

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, Set.of(IndustryTeamRole.SUBMITTER.name())))
        .thenReturn(false);

    assertFalse(industryTeamService.isSubmitter(teamId, user));
  }

  @Test
  void isAccessManager_whenAccessManager_thenFalse() {

    var teamId = new TeamId(UUID.randomUUID());
    var user = ServiceUserDetailTestUtil.Builder().build();

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, Set.of(IndustryTeamRole.ACCESS_MANAGER.name())))
        .thenReturn(false);

    assertFalse(industryTeamService.isAccessManager(teamId, user));
  }

  @Test
  void addUserTeamRoles_verifyRepositoryInteractions() {

    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.INDUSTRY)
        .build();
    var userToAdd = EnergyPortalUserDtoTestUtil.Builder().build();
    var industryRoles = Set.of(
        IndustryTeamRole.ACCESS_MANAGER
    );

    industryTeamService.addUserTeamRoles(team, userToAdd, industryRoles);

    var rolesAsStrings = industryRoles
        .stream()
        .map(IndustryTeamRole::name)
        .collect(Collectors.toSet());

    verify(teamMemberRoleService, times(1)).addUserTeamRoles(team, userToAdd, rolesAsStrings);
  }

  @Test
  void isSubmitterForOrganisationUnit_verifyMethodCalls() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var team = TeamTestUtil.Builder().build();
    when(teamService.getTeamByOrganisationGroupId(1))
        .thenReturn(Optional.of(team));
    industryTeamService.isSubmitterForOrganisationGroups(user, List.of(1,2,3));
    verify(teamMemberService).isMemberOfTeamWithAnyRoleOf(team.toTeamId(),
        user,
        Set.of(IndustryTeamRole.SUBMITTER.name()));
    verify(teamService).getTeamByOrganisationGroupId(1);
    verify(teamService).getTeamByOrganisationGroupId(2);
    verify(teamService).getTeamByOrganisationGroupId(3);

  }

  @Test
  void isSubmitterForOrganisationUnit_whenSubmitter_assertTrue() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var team = mock(Team.class);
    var teamId = new TeamId(UUID.randomUUID());
    when(team.toTeamId()).thenReturn(teamId);
    when(teamService.getTeamByOrganisationGroupId(1))
        .thenReturn(Optional.of(team));

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, Set.of(IndustryTeamRole.SUBMITTER.name())))
        .thenReturn(true);
    assertTrue(industryTeamService.isSubmitterForOrganisationGroups(user, List.of(1, 2)));
  }

  @Test
  void isSubmitterForOrganisationUnit_whenNotSubmitter_assertFalse() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var teamId = new TeamId(UUID.randomUUID());
    var team = mock(Team.class);
    when(team.toTeamId()).thenReturn(teamId);

    when(teamService.getTeamByOrganisationGroupId(1))
        .thenReturn(Optional.of(team));
    when(teamService.getTeamByOrganisationGroupId(2))
        .thenReturn(Optional.of(team));
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, user, Set.of(IndustryTeamRole.SUBMITTER.name())))
        .thenReturn(false);
    assertFalse(industryTeamService.isSubmitterForOrganisationGroups(user, List.of(1, 2)));
  }

  @Test
  void isSubmitterForOrganisationUnit_whenNotMemberOfTeam_assertFalse() {
    var user = ServiceUserDetailTestUtil.Builder().build();

    when(teamService.getTeamByOrganisationGroupId(1))
        .thenReturn(Optional.empty());
    when(teamService.getTeamByOrganisationGroupId(2))
        .thenReturn(Optional.empty());

    assertFalse(industryTeamService.isSubmitterForOrganisationGroups(user, List.of(1, 2)));
    verifyNoInteractions(teamMemberService);
  }

  @Test
  void getOrganisationGroupIdsForUser_verifyMethodCall() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    industryTeamService.getOrganisationGroupIdsForUser(user);

    verify(teamService).getOrganisationGroupsUserIsTeamMemberOf(user);
  }

  @Test
  void getOrganisationGroupIdsForUser_assertReturn() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var expectedResult = List.of(1,2);
    when(teamService.getOrganisationGroupsUserIsTeamMemberOf(user))
        .thenReturn(expectedResult);
    var result = industryTeamService.getOrganisationGroupIdsForUser(user);
    assertThat(result).isEqualTo(expectedResult);
  }
}
