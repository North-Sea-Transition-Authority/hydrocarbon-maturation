package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.northernoffshore.NorthernOffshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.onshore.OnshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.southernoffshore.SouthernOffshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
public class ResponsibleTeamService {

  private final RegulatorTeamService regulatorTeamService;
  private final NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService;
  private final SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService;
  private final OnshoreAreaTeamService onshoreAreaTeamService;

  public ResponsibleTeamService(RegulatorTeamService regulatorTeamService,
                                NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService,
                                SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService,
                                OnshoreAreaTeamService onshoreAreaTeamService) {
    this.regulatorTeamService = regulatorTeamService;
    this.northernOffshoreAreaTeamService = northernOffshoreAreaTeamService;
    this.southernOffshoreAreaTeamService = southernOffshoreAreaTeamService;
    this.onshoreAreaTeamService = onshoreAreaTeamService;
  }

  public boolean isInResponsibleTeam(ServiceUserDetail userDetail, ResponsibleTeamType responsibleTeamType) {
    return switch (responsibleTeamType) {
      case EXPLORATION -> regulatorTeamService.isRegulatorTeamRole(userDetail, RegulatorTeamRole.EXPLORATION);
      case NORTHERN_NORTH_SEA_WEST_OF_SHETLAND -> northernOffshoreAreaTeamService
          .isNorthernOffshoreTeamMember(userDetail);
      case CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA -> southernOffshoreAreaTeamService
          .isSouthernOffshoreTeamMember(userDetail);
      case ONSHORE -> onshoreAreaTeamService
          .isOnshoreTeamMember(userDetail);
    };
  }

  public boolean canUpdateResponsibleTeam(Resource resource) {
    return resource.getType() == ResourceType.DISCOVERY;
  }
}
