package uk.co.nstauthority.hydrocarbonmaturation.teams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.digital.energyportalteamaccesslibrary.team.EnergyPortalAccessService;
import uk.co.fivium.digital.energyportalteamaccesslibrary.team.InstigatingWebUserAccountId;
import uk.co.fivium.digital.energyportalteamaccesslibrary.team.ResourceType;
import uk.co.fivium.digital.energyportalteamaccesslibrary.team.TargetWebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.access.EnergyPortalAccessApiConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.access.EnergyPortalAccessApiConfigurationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.AddedToTeamEventPublisher;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RemovedFromTeamEventPublisher;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamRole;

@ExtendWith(MockitoExtension.class)
class TeamMemberRoleServiceTest {

  @Mock
  private TeamMemberRoleRepository teamMemberRoleRepository;

  @Mock
  private AddedToTeamEventPublisher addedToTeamEventPublisher;

  @Mock
  private UserService userService;

  @Captor
  private ArgumentCaptor<List<TeamMemberRole>> teamMemberRoleCaptor;

  @Captor
  private ArgumentCaptor<ResourceType> resourceTypeArgumentCaptor;

  @Captor
  private ArgumentCaptor<TargetWebUserAccountId> targetWebUserAccountIdArgumentCaptor;

  @Captor
  private ArgumentCaptor<InstigatingWebUserAccountId> instigatingWebUserAccountIdArgumentCaptor;

  @Mock
  private RemovedFromTeamEventPublisher removedFromTeamEventPublisher;

  @Mock
  private EnergyPortalAccessService energyPortalAccessService;

  private TeamMemberRoleService teamMemberRoleService;

  private EnergyPortalAccessApiConfiguration energyPortalAccessApiConfiguration;

  @BeforeEach
  void setUp() {
    energyPortalAccessApiConfiguration = EnergyPortalAccessApiConfigurationUtil.getDefaultTestConfig();
    teamMemberRoleService = new TeamMemberRoleService(
        teamMemberRoleRepository,
        addedToTeamEventPublisher,
        userService,
        removedFromTeamEventPublisher,
        energyPortalAccessApiConfiguration,
        energyPortalAccessService
    );
  }

  @Test
  void addUserTeamRoles_whenAddingUser_thenVerifyCalls() {

    var team = TeamTestUtil.Builder().build();

    var userToAdd = EnergyPortalUserDtoTestUtil.Builder()
        .withWebUserAccountId(100)
        .build();

    var role = "ROLE_NAME";

    var instigatingUser = UserTestUtil.Builder()
        .withWuaId(200L)
        .build();

    when(userService.getUser()).thenReturn(instigatingUser);

    teamMemberRoleService.addUserTeamRoles(team, userToAdd, Set.of(role));

    verify(teamMemberRoleRepository, times(1)).deleteAllByTeamAndWuaId(team, userToAdd.webUserAccountId());
    verify(teamMemberRoleRepository, times(1)).saveAll(teamMemberRoleCaptor.capture());

    assertThat(teamMemberRoleCaptor.getValue())
        .extracting(TeamMemberRole::getTeam, TeamMemberRole::getWuaId, TeamMemberRole::getRole)
        .containsExactly(tuple(team, userToAdd.webUserAccountId(), role));

    verify(energyPortalAccessService, times(1)).addUserToAccessTeam(
        resourceTypeArgumentCaptor.capture(),
        targetWebUserAccountIdArgumentCaptor.capture(),
        instigatingWebUserAccountIdArgumentCaptor.capture()
    );

    assertThat(resourceTypeArgumentCaptor.getValue().name())
        .isEqualTo(energyPortalAccessApiConfiguration.resourceType());
    assertThat(targetWebUserAccountIdArgumentCaptor.getValue().getId())
        .isEqualTo(userToAdd.webUserAccountId());
    assertThat(instigatingWebUserAccountIdArgumentCaptor.getValue().getId())
        .isEqualTo(instigatingUser.wuaId());

    verify(addedToTeamEventPublisher, times(1)).publish(
        team.toTeamId(),
        new WebUserAccountId(userToAdd.webUserAccountId()),
        Set.of(role),
        new WebUserAccountId(instigatingUser.wuaId())
    );
  }

  @Test
  void updateUserTeamRoles_whenMemberWithOneRole_thenVerifySingleRowInsert() {
    var team = TeamTestUtil.Builder().build();
    var existingUser = TeamMemberTestUtil.Builder()
        .withWebUserAccountId(100)
        .build();

    var role = "ROLE_NAME";

    teamMemberRoleService.updateUserTeamRoles(team, existingUser.wuaId(), Set.of(role));

    verify(teamMemberRoleRepository, times(1)).deleteAllByTeamAndWuaId(team, existingUser.wuaId().id());
    verify(teamMemberRoleRepository, times(1)).saveAll(teamMemberRoleCaptor.capture());

    assertThat(teamMemberRoleCaptor.getValue())
        .extracting(TeamMemberRole::getTeam, TeamMemberRole::getWuaId, TeamMemberRole::getRole)
        .containsExactly(tuple(team, existingUser.wuaId().id(), role));

    verifyNoInteractions(addedToTeamEventPublisher);
  }

  @Test
  void updateUserTeamRoles_whenMemberWithMultipleRoles_thenVerifyMultipleRowInsert() {

    var team = TeamTestUtil.Builder().build();
    var existingUser = TeamMemberTestUtil.Builder()
        .withWebUserAccountId(100)
        .build();

    var firstRole = "FIRST_ROLE_NAME";
    var secondRole = "SECOND_ROLE_NAME";

    var rolesToGrant = Set.of(firstRole, secondRole);

    teamMemberRoleService.updateUserTeamRoles(team, existingUser.wuaId(), rolesToGrant);

    verify(teamMemberRoleRepository, times(1)).deleteAllByTeamAndWuaId(team, existingUser.wuaId().id());
    verify(teamMemberRoleRepository, times(1)).saveAll(teamMemberRoleCaptor.capture());

    assertThat(teamMemberRoleCaptor.getValue())
        .extracting(TeamMemberRole::getTeam, TeamMemberRole::getWuaId, TeamMemberRole::getRole)
        .containsExactlyInAnyOrder(
            tuple(team, existingUser.wuaId().id(), firstRole),
            tuple(team, existingUser.wuaId().id(), secondRole)
        );

    verifyNoInteractions(addedToTeamEventPublisher);

    verify(teamMemberRoleRepository).deleteAllByTeamAndWuaId(team, existingUser.wuaId().id());
  }

  @Test
  void removeMemberFromTeam_verifyInteractions() {

    var team = TeamTestUtil.Builder().build();

    var removedUser = EnergyPortalUserDtoTestUtil.Builder()
        .withWebUserAccountId(100)
        .build();

    var teamMember = TeamMemberTestUtil.Builder().withWebUserAccountId(removedUser.webUserAccountId()).build();

    var instigatingUser = UserTestUtil.Builder()
        .withWuaId(200L)
        .build();

    when(userService.getUser()).thenReturn(instigatingUser);
    when(teamMemberRoleRepository.existsByWuaId(removedUser.webUserAccountId())).thenReturn(false);

    teamMemberRoleService.removeMemberFromTeam(team, teamMember);

    verify(teamMemberRoleRepository).deleteAllByTeamAndWuaId(team, teamMember.wuaId().id());

    verify(energyPortalAccessService, times(1)).removeUserFromAccessTeam(
        resourceTypeArgumentCaptor.capture(),
        targetWebUserAccountIdArgumentCaptor.capture(),
        instigatingWebUserAccountIdArgumentCaptor.capture()
    );

    assertThat(resourceTypeArgumentCaptor.getValue().name())
        .isEqualTo(energyPortalAccessApiConfiguration.resourceType());
    assertThat(targetWebUserAccountIdArgumentCaptor.getValue().getId())
        .isEqualTo(removedUser.webUserAccountId());
    assertThat(instigatingWebUserAccountIdArgumentCaptor.getValue().getId())
        .isEqualTo(instigatingUser.wuaId());

    verify(removedFromTeamEventPublisher, times(1)).publish(
        teamMember,
        new WebUserAccountId(instigatingUser.wuaId())
    );
  }

  @Test
  void removeMemberFromTeam_verifyNotRemovedFromAccessTeam_whenStillInAnotherTeam() {

    var team = TeamTestUtil.Builder().build();

    var removedUser = EnergyPortalUserDtoTestUtil.Builder()
        .withWebUserAccountId(100)
        .build();

    var teamMember = TeamMemberTestUtil.Builder().withWebUserAccountId(removedUser.webUserAccountId()).build();

    var instigatingUser = UserTestUtil.Builder()
        .withWuaId(200L)
        .build();

    when(userService.getUser()).thenReturn(instigatingUser);
    when(teamMemberRoleRepository.existsByWuaId(teamMember.wuaId().id())).thenReturn(true);

    teamMemberRoleService.removeMemberFromTeam(team, teamMember);

    verify(teamMemberRoleRepository).deleteAllByTeamAndWuaId(team, teamMember.wuaId().id());

    verify(removedFromTeamEventPublisher, times(1)).publish(
        teamMember,
        new WebUserAccountId(instigatingUser.wuaId())
    );

    verifyNoInteractions(energyPortalAccessService);
  }

  @Test
  void getTeamMembersWithRoleInTeams_verifyMethodCall() {
    var listOfTeams = List.of(TeamTestUtil.Builder().build());
    var roles = Set.of(IndustryTeamRole.SUBMITTER.name());
    teamMemberRoleService.getTeamMembersWithRoleInTeams(listOfTeams, roles);

    verify(teamMemberRoleRepository).findAllByTeamInAndRoleIn(listOfTeams, roles);
  }

  @Test
  void getTeamMembersWithRoleInTeams_assertReturn() {
    var expectedReturn = List.of(TeamMemberRoleTestUtil.Builder().build());
    var listOfTeams = List.of(TeamTestUtil.Builder().build());
    var roles = Set.of(IndustryTeamRole.SUBMITTER.name());
    when(teamMemberRoleRepository.findAllByTeamInAndRoleIn(listOfTeams, roles)).thenReturn(expectedReturn);

    var result = teamMemberRoleService.getTeamMembersWithRoleInTeams(listOfTeams, roles);

    assertThat(result).isEqualTo(expectedReturn);
  }
}