package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Service
public class ResponsibleTeamService {

  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final TeamQueryService teamQueryService;

  @Autowired
  public ResponsibleTeamService(RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
                                TeamQueryService teamQueryService) {
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.teamQueryService = teamQueryService;
  }

  public boolean isInResponsibleTeam(User user, SubmissionDetail submissionDetail) {
    var regulatorManagedInformation = regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResourceOrThrow(submissionDetail.getSubmission().getResource());

    return isInResponsibleTeam(user, regulatorManagedInformation.getResponsibleTeamType());
  }

  public boolean isInResponsibleTeam(User user, ResponsibleTeamType responsibleTeamType) {
    return switch (responsibleTeamType) {
      case EXPLORATION -> teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION);
      case NORTHERN_NORTH_SEA_WEST_OF_SHETLAND ->
          teamQueryService.userHasRoleInTeamType(
              user.wuaId(),
              TeamType.NORTHERN_OFFSHORE_AREA,
              Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM
          );
      case CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA ->
          teamQueryService.userHasRoleInTeamType(
              user.wuaId(),
              TeamType.SOUTHERN_OFFSHORE_AREA,
              Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM
          );
      case ONSHORE ->
          teamQueryService.userHasRoleInTeamType(
              user.wuaId(),
              TeamType.ONSHORE_AREA,
              Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM
          );
    };
  }

  public List<EnergyPortalUserDto> getResponsibleTeamUsers(ResponsibleTeamType responsibleTeamType) {
    return switch (responsibleTeamType) {
      case EXPLORATION -> teamQueryService.getUsersInTeamsWithRoles(
          Set.of(teamQueryService.getStaticTeam(TeamType.REGULATOR)),
          Set.of(Role.EXPLORATION));
      case NORTHERN_NORTH_SEA_WEST_OF_SHETLAND -> teamQueryService.getUsersInTeamsWithRoles(
          Set.of(teamQueryService.getStaticTeam(TeamType.NORTHERN_OFFSHORE_AREA)),
          Set.of(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM));
      case CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA -> teamQueryService.getUsersInTeamsWithRoles(
          Set.of(teamQueryService.getStaticTeam(TeamType.SOUTHERN_OFFSHORE_AREA)),
          Set.of(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM));
      case ONSHORE -> teamQueryService.getUsersInTeamsWithRoles(
          Set.of(teamQueryService.getStaticTeam(TeamType.ONSHORE_AREA)),
          Set.of(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM));
    };
  }

  public List<ResponsibleTeamType> getResponsibleTeamTypesForUser(User user) {
    var teamRoles = teamQueryService.getTeamRolesForUser(user.wuaId());
    Map<TeamType, Role> teamTypeRoleMap = teamRoles.stream()
        .collect(Collectors.toMap(teamRole -> teamRole.getTeam().getTeamType(), TeamRole::getRole));

    return ResponsibleTeamType.getResponsibleTeamTypesForTeamsAndRoles(teamTypeRoleMap);
  }
}
