package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;

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
    return teamQueryService.userHasRoleInTeamType(
          user.wuaId(),
          responsibleTeamType.getTeamType(),
          responsibleTeamType.getRole());
  }

  public List<EnergyPortalUserDto> getResponsibleTeamUsers(ResponsibleTeamType responsibleTeamType) {
    return teamQueryService.getUsersInTeamsWithRoles(
          Set.of(teamQueryService.getStaticTeam(responsibleTeamType.getTeamType())),
          Set.of(responsibleTeamType.getRole()));
  }

  public List<ResponsibleTeamType> getResponsibleTeamTypesForUser(User user) {
    return Arrays.stream(ResponsibleTeamType.values())
        .filter(responsibleTeamType -> isInResponsibleTeam(user, responsibleTeamType))
        .toList();
  }
}
