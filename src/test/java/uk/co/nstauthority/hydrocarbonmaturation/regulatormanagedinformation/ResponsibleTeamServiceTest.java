package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRoleTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ExtendWith(MockitoExtension.class)
class ResponsibleTeamServiceTest {

  @Mock
  private TeamQueryService teamQueryService;

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @InjectMocks
  private ResponsibleTeamService responsibleTeamService;

  private final User user = UserTestUtil.Builder().build();
  private final EnergyPortalUserDto energyPortalUserDto = EnergyPortalUserDtoTestUtil.Builder().build();

  @ParameterizedTest
  @EnumSource(ResponsibleTeamType.class)
  void isInResponsibleTeam_takingSubmissionDetails(ResponsibleTeamType responsibleTeamType) {

    var resource = mock(Resource.class);
    var submission = new Submission();
    submission.setResource(resource);
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setSubmission(submission);
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(responsibleTeamType);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(resource))
        .thenReturn(regulatorManagedInformation);

    switch (responsibleTeamType) {
      case EXPLORATION -> when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
          .thenReturn(true);
      case NORTHERN_NORTH_SEA_WEST_OF_SHETLAND -> when(teamQueryService.userHasRoleInTeamType(
          user.wuaId(),
          TeamType.NORTHERN_OFFSHORE_AREA,
          Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
          .thenReturn(true);
      case CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA -> when(teamQueryService.userHasRoleInTeamType(
          user.wuaId(),
          TeamType.SOUTHERN_OFFSHORE_AREA,
          Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
          .thenReturn(true);
      case ONSHORE -> when(teamQueryService.userHasRoleInTeamType(
          user.wuaId(),
          TeamType.ONSHORE_AREA,
          Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
          .thenReturn(true);
    }

    assertThat(responsibleTeamService.isInResponsibleTeam(user, submissionDetail)).isTrue();
  }

  @ParameterizedTest
  @EnumSource(ResponsibleTeamType.class)
  void isInResponsibleTeam(ResponsibleTeamType responsibleTeamType) {

    switch (responsibleTeamType) {
      case EXPLORATION -> when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
          .thenReturn(true);
      case NORTHERN_NORTH_SEA_WEST_OF_SHETLAND -> when(teamQueryService.userHasRoleInTeamType(
          user.wuaId(),
          TeamType.NORTHERN_OFFSHORE_AREA,
          Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
          .thenReturn(true);
      case CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA -> when(teamQueryService.userHasRoleInTeamType(
          user.wuaId(),
          TeamType.SOUTHERN_OFFSHORE_AREA,
          Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
          .thenReturn(true);
      case ONSHORE -> when(teamQueryService.userHasRoleInTeamType(
          user.wuaId(),
          TeamType.ONSHORE_AREA,
          Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
          .thenReturn(true);
    }

    assertThat(responsibleTeamService.isInResponsibleTeam(user, responsibleTeamType)).isTrue();
  }

  @ParameterizedTest
  @EnumSource(ResponsibleTeamType.class)
  void getResponsibleTeamUsers(ResponsibleTeamType responsibleTeamType) {

    var team = new Team();
    team.setTeamType(responsibleTeamType.getTeamType());
    when(teamQueryService.getStaticTeam(responsibleTeamType.getTeamType())).thenReturn(team);

    switch (responsibleTeamType) {
      case EXPLORATION -> when(teamQueryService.getUsersInTeamsWithRoles(
          Set.of(team),
          Set.of(Role.EXPLORATION)))
          .thenReturn(List.of(energyPortalUserDto));
      case NORTHERN_NORTH_SEA_WEST_OF_SHETLAND, ONSHORE, CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA ->
          when(teamQueryService.getUsersInTeamsWithRoles(
              Set.of(team),
              Set.of(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)))
              .thenReturn(List.of(energyPortalUserDto));
    }

    assertThat(responsibleTeamService.getResponsibleTeamUsers(responsibleTeamType))
        .isEqualTo(List.of(energyPortalUserDto));
  }

  @Test
  void getResponsibleTeamTypesForExploration() {
    var team = TeamTestUtil.Builder().withTeamType(TeamType.REGULATOR).buildStaticTeam();
    var teamRole = TeamRoleTestUtil.Builder()
        .withRole(Role.EXPLORATION)
        .withTeam(team)
        .build();
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(teamRole));

    assertThat(responsibleTeamService.getResponsibleTeamTypesForUser(user)).containsExactly(
        ResponsibleTeamType.EXPLORATION);
  }

  @Test
  void getResponsibleTeamTypesForNorthernAreaTeam() {
    var team = TeamTestUtil.Builder().withTeamType(TeamType.NORTHERN_OFFSHORE_AREA).buildStaticTeam();
    var teamRole = TeamRoleTestUtil.Builder()
        .withRole(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)
        .withTeam(team)
        .build();
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(teamRole));

    assertThat(responsibleTeamService.getResponsibleTeamTypesForUser(user)).containsExactly(
        ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);
  }

  @Test
  void getResponsibleTeamTypesForSouthernAreaTeam() {
    var team = TeamTestUtil.Builder().withTeamType(TeamType.SOUTHERN_OFFSHORE_AREA).buildStaticTeam();
    var teamRole = TeamRoleTestUtil.Builder()
        .withRole(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)
        .withTeam(team)
        .build();
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(teamRole));

    assertThat(responsibleTeamService.getResponsibleTeamTypesForUser(user)).containsExactly(
        ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);
  }

  @Test
  void getResponsibleTeamTypesForOnshoreAreaTeam() {
    var team = TeamTestUtil.Builder().withTeamType(TeamType.ONSHORE_AREA).buildStaticTeam();
    var teamRole = TeamRoleTestUtil.Builder()
        .withRole(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)
        .withTeam(team)
        .build();
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(teamRole));

    assertThat(responsibleTeamService.getResponsibleTeamTypesForUser(user)).containsExactly(
        ResponsibleTeamType.ONSHORE);
  }

  @Test
  void getResponsibleTeamTypesForMultipleRoles() {
    var team1 = TeamTestUtil.Builder().withTeamType(TeamType.ONSHORE_AREA).buildStaticTeam();
    var teamRole1 = TeamRoleTestUtil.Builder()
        .withRole(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)
        .withTeam(team1)
        .build();
    var team2 = TeamTestUtil.Builder().withTeamType(TeamType.NORTHERN_OFFSHORE_AREA).buildStaticTeam();
    var teamRole2 = TeamRoleTestUtil.Builder()
        .withRole(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)
        .withTeam(team2)
        .build();
    var team3 = TeamTestUtil.Builder().withTeamType(TeamType.SOUTHERN_OFFSHORE_AREA).buildStaticTeam();
    var teamRole3 = TeamRoleTestUtil.Builder()
        .withRole(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)
        .withTeam(team3)
        .build();
    var team4 = TeamTestUtil.Builder().withTeamType(TeamType.REGULATOR).buildStaticTeam();
    var teamRole4 = TeamRoleTestUtil.Builder()
        .withRole(Role.EXPLORATION)
        .withTeam(team4)
        .build();
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(teamRole1, teamRole2, teamRole3, teamRole4));

    assertThat(responsibleTeamService.getResponsibleTeamTypesForUser(user)).containsExactly(
        ResponsibleTeamType.EXPLORATION,
        ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND,
        ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA,
        ResponsibleTeamType.ONSHORE);
  }

  @ParameterizedTest
  @EnumSource(value = Role.class, mode = EnumSource.Mode.EXCLUDE, names = {"EXPLORATION", "MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM"})
  void getResponsibleTeamTypesForUser_withOtherRoles(Role role) {
    var team = TeamTestUtil.Builder().withTeamType(TeamType.REGULATOR).buildStaticTeam();
    var teamRole = TeamRoleTestUtil.Builder()
        .withRole(role)
        .withTeam(team)
        .build();
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(teamRole));

    assertThat(responsibleTeamService.getResponsibleTeamTypesForUser(user)).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(
      value = TeamType.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"REGULATOR", "NORTHERN_OFFSHORE_AREA", "SOUTHERN_OFFSHORE_AREA", "ONSHORE_AREA"})
  void getResponsibleTeamTypesForUser_withOtherTeams(TeamType teamType) {
    var team = TeamTestUtil.Builder().withTeamType(teamType).buildStaticTeam();
    var teamRole = TeamRoleTestUtil.Builder()
        .withRole(Role.MANAGE_TEAM)
        .withTeam(team)
        .build();
    when(teamQueryService.getTeamRolesForUser(user.wuaId())).thenReturn(Set.of(teamRole));

    assertThat(responsibleTeamService.getResponsibleTeamTypesForUser(user)).isEmpty();
  }
}