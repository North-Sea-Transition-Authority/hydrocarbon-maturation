package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.industry;

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
import org.springframework.boot.test.mock.mockito.SpyBean;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroup;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup.OrganisationGroupQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.LegacyTeam;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberRoleTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamType;

@ExtendWith(MockitoExtension.class)
class IndustryLegacyTeamServiceTest {

  private final static Integer ORGANISATION_GROUP_ID = 1;
  private final static Integer ORGANISATION_UNIT_ID = 5;
  private final static User USER = UserTestUtil.Builder().build();

  @Mock
  private TeamService teamService;

  @Mock
  private TeamMemberService teamMemberService;

  @Mock
  private TeamMemberRoleService teamMemberRoleService;

  @Mock
  private OrganisationGroupQueryService organisationGroupQueryService;

  @InjectMocks
  @SpyBean
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

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, USER, Set.of(IndustryTeamRole.ACCESS_MANAGER.name())))
        .thenReturn(true);

    assertTrue(industryTeamService.isAccessManager(teamId, USER));
  }

  @Test
  void isSubmitter_whenSubmitter_thenTrue() {

    var teamId = new TeamId(UUID.randomUUID());

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, USER, Set.of(IndustryTeamRole.SUBMITTER.name())))
        .thenReturn(true);

    assertTrue(industryTeamService.isSubmitter(teamId, USER));
  }

  @Test
  void isSubmitter_whenNotSubmitter_thenFalse() {

    var teamId = new TeamId(UUID.randomUUID());

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, USER, Set.of(IndustryTeamRole.SUBMITTER.name())))
        .thenReturn(false);

    assertFalse(industryTeamService.isSubmitter(teamId, USER));
  }

  @Test
  void isAccessManager_whenAccessManager_thenFalse() {

    var teamId = new TeamId(UUID.randomUUID());

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, USER, Set.of(IndustryTeamRole.ACCESS_MANAGER.name())))
        .thenReturn(false);

    assertFalse(industryTeamService.isAccessManager(teamId, USER));
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
    var team = TeamTestUtil.Builder().build();
    when(teamService.getTeamByOrganisationGroupId(ORGANISATION_GROUP_ID))
        .thenReturn(Optional.of(team));
    industryTeamService.isSubmitterForOrganisationGroups(USER, List.of(ORGANISATION_GROUP_ID,2,3));
    verify(teamMemberService).isMemberOfTeamWithAnyRoleOf(team.toTeamId(),
        USER,
        Set.of(IndustryTeamRole.SUBMITTER.name()));
    verify(teamService).getTeamByOrganisationGroupId(ORGANISATION_GROUP_ID);
    verify(teamService).getTeamByOrganisationGroupId(2);
    verify(teamService).getTeamByOrganisationGroupId(3);

  }

  @Test
  void isSubmitterForOrganisationUnit_whenSubmitter_assertTrue() {
    var team = mock(LegacyTeam.class);
    var teamId = new TeamId(UUID.randomUUID());
    when(team.toTeamId()).thenReturn(teamId);
    when(teamService.getTeamByOrganisationGroupId(ORGANISATION_GROUP_ID))
        .thenReturn(Optional.of(team));

    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, USER, Set.of(IndustryTeamRole.SUBMITTER.name())))
        .thenReturn(true);
    assertTrue(industryTeamService.isSubmitterForOrganisationGroups(USER, List.of(ORGANISATION_GROUP_ID, 2)));
  }

  @Test
  void isSubmitterForOrganisationUnit_whenNotSubmitter_assertFalse() {
    var teamId = new TeamId(UUID.randomUUID());
    var team = mock(LegacyTeam.class);
    when(team.toTeamId()).thenReturn(teamId);

    when(teamService.getTeamByOrganisationGroupId(ORGANISATION_GROUP_ID))
        .thenReturn(Optional.of(team));
    when(teamService.getTeamByOrganisationGroupId(2))
        .thenReturn(Optional.of(team));
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(teamId, USER, Set.of(IndustryTeamRole.SUBMITTER.name())))
        .thenReturn(false);
    assertFalse(industryTeamService.isSubmitterForOrganisationGroups(USER, List.of(ORGANISATION_GROUP_ID, 2)));
  }

  @Test
  void isSubmitterForOrganisationUnit_whenNotMemberOfTeam_assertFalse() {

    when(teamService.getTeamByOrganisationGroupId(ORGANISATION_GROUP_ID))
        .thenReturn(Optional.empty());
    when(teamService.getTeamByOrganisationGroupId(2))
        .thenReturn(Optional.empty());

    assertFalse(industryTeamService.isSubmitterForOrganisationGroups(USER, List.of(ORGANISATION_GROUP_ID, 2)));
    verifyNoInteractions(teamMemberService);
  }

  @Test
  void getOrganisationGroupIdsForUser_verifyMethodCall() {

    industryTeamService.getOrganisationGroupIdsForUser(USER);

    verify(teamService).getOrganisationGroupsUserIsTeamMemberOf(USER);
  }

  @Test
  void getOrganisationGroupIdsForUser_assertReturn() {

    var expectedResult = List.of(ORGANISATION_GROUP_ID, 2);
    when(teamService.getOrganisationGroupsUserIsTeamMemberOf(USER))
        .thenReturn(expectedResult);
    var result = industryTeamService.getOrganisationGroupIdsForUser(USER);
    assertThat(result).isEqualTo(expectedResult);
  }

  @Test
  void getOrganisationUnitIdsForUserWithAnyIndustryTeamRoles_submitter_assertReturn() {

    var orgUnit = OrganisationUnitTestUtil.Builder()
        .withOrganisationId(ORGANISATION_UNIT_ID)
        .build();
    when(industryTeamService.getOrganisationGroupIdsForUser(USER))
        .thenReturn(List.of(ORGANISATION_GROUP_ID));
    var team = TeamTestUtil.Builder().build();
    when(industryTeamService.getTeamByOrganisationGroupId(ORGANISATION_GROUP_ID))
        .thenReturn(Optional.of(team));
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(team.toTeamId(), USER, Set.of("SUBMITTER")))
        .thenReturn(true);
    var organisationGroup = new OrganisationGroup();
    organisationGroup.setOrganisationUnits(List.of(orgUnit));
    organisationGroup.setOrganisationGroupId(ORGANISATION_GROUP_ID);
    when(organisationGroupQueryService.getOrganisationGroupsByIds(List.of(ORGANISATION_GROUP_ID)))
        .thenReturn(List.of(organisationGroup));
    var result = industryTeamService
        .getOrganisationUnitIdsForUserWithIndustryTeamRoles(USER, Set.of(IndustryTeamRole.SUBMITTER));

    assertThat(result).isEqualTo(List.of(ORGANISATION_UNIT_ID));
  }

  @Test
  void getOrganisationUnitIdsForUserWithAnyIndustryTeamRoles_whenUserNotInAnyTeam_assertReturn() {

    when(industryTeamService.getOrganisationGroupIdsForUser(USER))
        .thenReturn(List.of());
    var result = industryTeamService
        .getOrganisationUnitIdsForUserWithIndustryTeamRoles(USER, Set.of(IndustryTeamRole.SUBMITTER));

    assertThat(result).isEmpty();
  }

  @Test
  void getWuaIdsOfUsersWithRoleForOrganisationGroupIds_assertReturn() {
    var expectedWuaId = 1L;
    var organisationGroupIds = List.of(ORGANISATION_GROUP_ID);
    var teamForOrgGroup = TeamTestUtil.Builder().build();
    var roleSet = Set.of(IndustryTeamRole.SUBMITTER.name());
    var teamMemberRole = TeamMemberRoleTestUtil.Builder()
        .withWebUserAccountId(expectedWuaId).build();
    when(teamService.getTeamByOrganisationGroupId(ORGANISATION_GROUP_ID))
        .thenReturn(Optional.of(teamForOrgGroup));
    when(teamMemberRoleService.getTeamMembersWithRoleInTeams(List.of(teamForOrgGroup), roleSet))
        .thenReturn(List.of(teamMemberRole));
    var result = industryTeamService.getWuaIdsOfUsersWithRoleForOrganisationGroupIds(
        organisationGroupIds, Set.of(IndustryTeamRole.SUBMITTER));
    assertThat(result).containsExactly(expectedWuaId);
  }

  @Test
  void getOrganisationUnitIdsForUserWithAnyIndustryTeamRoles_whenUserNotInAnyOrgUnitGroup_assertReturn() {

    when(industryTeamService.getOrganisationGroupIdsForUser(USER))
        .thenReturn(List.of(ORGANISATION_GROUP_ID));
    var team = TeamTestUtil.Builder().build();
    when(industryTeamService.getTeamByOrganisationGroupId(ORGANISATION_GROUP_ID))
        .thenReturn(Optional.of(team));
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(team.toTeamId(), USER, Set.of("VIEWER")))
        .thenReturn(true);
    var organisationGroup = new OrganisationGroup();
    organisationGroup.setOrganisationUnits(List.of());
    organisationGroup.setOrganisationGroupId(ORGANISATION_GROUP_ID);
    when(organisationGroupQueryService.getOrganisationGroupsByIds(List.of(ORGANISATION_GROUP_ID)))
        .thenReturn(List.of(organisationGroup));
    var result = industryTeamService
        .getOrganisationUnitIdsForUserWithIndustryTeamRoles(USER, Set.of(IndustryTeamRole.VIEWER));

    assertThat(result).isEmpty();
  }

  @Test
  void getOrganisationUnitIdsForUser_whenUserNotInAnyOrgUnitGroup_assertEmpty() {

    when(industryTeamService.getOrganisationGroupIdsForUser(USER))
        .thenReturn(List.of(ORGANISATION_GROUP_ID));
    var organisationGroup = new OrganisationGroup();
    organisationGroup.setOrganisationUnits(List.of());
    organisationGroup.setOrganisationGroupId(ORGANISATION_GROUP_ID);
    when(organisationGroupQueryService.getOrganisationGroupsByIds(List.of(ORGANISATION_GROUP_ID)))
        .thenReturn(List.of(organisationGroup));
    var result = industryTeamService
        .getOrganisationUnitIdsForUser(USER);

    assertThat(result).isEmpty();
  }

  @Test
  void getOrganisationUnitIdsForUser_assertReturn() {

    var orgUnit = OrganisationUnitTestUtil.Builder()
        .withOrganisationId(ORGANISATION_UNIT_ID)
        .build();
    when(industryTeamService.getOrganisationGroupIdsForUser(USER))
        .thenReturn(List.of(ORGANISATION_GROUP_ID));
    var organisationGroup = new OrganisationGroup();
    organisationGroup.setOrganisationUnits(List.of(orgUnit));
    organisationGroup.setOrganisationGroupId(ORGANISATION_GROUP_ID);
    when(organisationGroupQueryService.getOrganisationGroupsByIds(List.of(ORGANISATION_GROUP_ID)))
        .thenReturn(List.of(organisationGroup));
    var result = industryTeamService
        .getOrganisationUnitIdsForUser(USER);

    assertThat(result).isEqualTo(List.of(ORGANISATION_UNIT_ID));
  }

  @Test
  void isSubmitter_inTeam_andHasRole() {

    var team = new LegacyTeam(UUID.randomUUID());
    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.INDUSTRY)).thenReturn(List.of(team));
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(team.toTeamId(), USER,
        Set.of(IndustryTeamRole.SUBMITTER.name())))
        .thenReturn(true);

    assertThat(industryTeamService.isSubmitter(USER)).isTrue();
  }

  @Test
  void isSubmitter_inTeam_andNotInRole() {

    var team = new LegacyTeam(UUID.randomUUID());
    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.INDUSTRY)).thenReturn(List.of(team));
    when(teamMemberService.isMemberOfTeamWithAnyRoleOf(team.toTeamId(), USER,
        Set.of(IndustryTeamRole.SUBMITTER.name())))
        .thenReturn(false);

    assertThat(industryTeamService.isSubmitter(USER)).isFalse();
  }

  @Test
  void isSubmitter_notInTeam() {

    when(teamService.getTeamsOfTypeThatUserBelongsTo(USER, TeamType.INDUSTRY)).thenReturn(List.of());
    assertThat(industryTeamService.isSubmitter(USER)).isFalse();
  }
}
