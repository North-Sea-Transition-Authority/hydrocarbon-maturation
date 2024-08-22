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

    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), responsibleTeamType.getTeamType(), responsibleTeamType.getRole()))
          .thenReturn(true);

    assertThat(responsibleTeamService.isInResponsibleTeam(user, submissionDetail)).isTrue();
  }

  @ParameterizedTest
  @EnumSource(ResponsibleTeamType.class)
  void isInResponsibleTeam(ResponsibleTeamType responsibleTeamType) {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), responsibleTeamType.getTeamType(), responsibleTeamType.getRole()))
          .thenReturn(true);

    assertThat(responsibleTeamService.isInResponsibleTeam(user, responsibleTeamType)).isTrue();
  }

  @ParameterizedTest
  @EnumSource(ResponsibleTeamType.class)
  void isNotInResponsibleTeam(ResponsibleTeamType responsibleTeamType) {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), responsibleTeamType.getTeamType(), responsibleTeamType.getRole()))
        .thenReturn(false);

    assertThat(responsibleTeamService.isInResponsibleTeam(user, responsibleTeamType)).isFalse();
  }

  @ParameterizedTest
  @EnumSource(ResponsibleTeamType.class)
  void getResponsibleTeamUsers(ResponsibleTeamType responsibleTeamType) {

    var team = new Team();
    team.setTeamType(responsibleTeamType.getTeamType());
    when(teamQueryService.getStaticTeam(responsibleTeamType.getTeamType())).thenReturn(team);

    when(teamQueryService.getUsersInTeamsWithRoles(
          Set.of(team),
          Set.of(responsibleTeamType.getRole())))
          .thenReturn(List.of(energyPortalUserDto));

    assertThat(responsibleTeamService.getResponsibleTeamUsers(responsibleTeamType))
        .isEqualTo(List.of(energyPortalUserDto));
  }

  @Test
  void getResponsibleTeamTypesForExploration() {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(true);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
        .thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
        .thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
        .thenReturn(false);

    assertThat(responsibleTeamService.getResponsibleTeamTypesForUser(user)).containsExactly(
        ResponsibleTeamType.EXPLORATION);
  }

  @Test
  void getResponsibleTeamTypesForNorthernAreaTeam() {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
        .thenReturn(true);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
        .thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
        .thenReturn(false);

    assertThat(responsibleTeamService.getResponsibleTeamTypesForUser(user)).containsExactly(
        ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);
  }

  @Test
  void getResponsibleTeamTypesForSouthernAreaTeam() {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
        .thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
        .thenReturn(true);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
        .thenReturn(false);

    assertThat(responsibleTeamService.getResponsibleTeamTypesForUser(user)).containsExactly(
        ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);
  }

  @Test
  void getResponsibleTeamTypesForOnshoreAreaTeam() {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
        .thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
        .thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
        .thenReturn(true);

    assertThat(responsibleTeamService.getResponsibleTeamTypesForUser(user)).containsExactly(
        ResponsibleTeamType.ONSHORE);
  }

  @Test
  void getResponsibleTeamTypesForMultipleRoles() {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION))
        .thenReturn(true);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
        .thenReturn(true);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
        .thenReturn(true);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM))
        .thenReturn(true);

    assertThat(responsibleTeamService.getResponsibleTeamTypesForUser(user)).containsExactly(
        ResponsibleTeamType.EXPLORATION,
        ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND,
        ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA,
        ResponsibleTeamType.ONSHORE);
  }
}