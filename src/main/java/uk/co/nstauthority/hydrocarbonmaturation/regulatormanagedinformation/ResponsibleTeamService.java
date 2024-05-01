package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.northernoffshore.NorthernOffshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.onshore.OnshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.southernoffshore.SouthernOffshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class ResponsibleTeamService {

  private final RegulatorTeamService regulatorTeamService;
  private final NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService;
  private final SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService;
  private final OnshoreAreaTeamService onshoreAreaTeamService;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  public ResponsibleTeamService(RegulatorTeamService regulatorTeamService,
                                NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService,
                                SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService,
                                OnshoreAreaTeamService onshoreAreaTeamService,
                                RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService) {
    this.regulatorTeamService = regulatorTeamService;
    this.northernOffshoreAreaTeamService = northernOffshoreAreaTeamService;
    this.southernOffshoreAreaTeamService = southernOffshoreAreaTeamService;
    this.onshoreAreaTeamService = onshoreAreaTeamService;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
  }

  public boolean isInResponsibleTeam(User user, SubmissionDetail submissionDetail) {
    var regulatorManagedInformation = regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResourceOrThrow(submissionDetail.getSubmission().getResource());

    return isInResponsibleTeam(user, regulatorManagedInformation.getResponsibleTeamType());
  }

  public boolean isInResponsibleTeam(User user, ResponsibleTeamType responsibleTeamType) {
    return switch (responsibleTeamType) {
      case EXPLORATION -> regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.EXPLORATION);
      case NORTHERN_NORTH_SEA_WEST_OF_SHETLAND -> northernOffshoreAreaTeamService
          .isNorthernOffshoreTeamMember(user);
      case CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA -> southernOffshoreAreaTeamService
          .isSouthernOffshoreTeamMember(user);
      case ONSHORE -> onshoreAreaTeamService
          .isOnshoreTeamMember(user);
    };
  }

  public List<EnergyPortalUserDto> getResponsibleTeamUsers(ResponsibleTeamType responsibleTeamType) {
    return switch (responsibleTeamType) {
      case EXPLORATION -> regulatorTeamService.getExplorationTeamEnergyPortalUsers();
      case NORTHERN_NORTH_SEA_WEST_OF_SHETLAND -> northernOffshoreAreaTeamService
          .getNorthernOffShoreAreaTeamEnergyPortalUsers();
      case CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA -> southernOffshoreAreaTeamService
          .getSouthernOffShoreAreaTeamEnergyPortalUsers();
      case ONSHORE -> onshoreAreaTeamService.getOnShoreAreaTeamEnergyPortalUsers();
    };
  }

  public List<ResponsibleTeamType> getResponsibleTeamTypesForUser(User user) {
    var responsibleTeamTypeList = new ArrayList<ResponsibleTeamType>();

    if (regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.EXPLORATION)) {
      responsibleTeamTypeList.add(ResponsibleTeamType.EXPLORATION);
    }

    if (northernOffshoreAreaTeamService.isNorthernOffshoreTeamMember(user)) {
      responsibleTeamTypeList.add(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);
    }

    if (southernOffshoreAreaTeamService.isSouthernOffshoreTeamMember(user)) {
      responsibleTeamTypeList.add(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);
    }

    if (onshoreAreaTeamService.isOnshoreTeamMember(user)) {
      responsibleTeamTypeList.add(ResponsibleTeamType.ONSHORE);
    }

    return responsibleTeamTypeList;
  }
}
