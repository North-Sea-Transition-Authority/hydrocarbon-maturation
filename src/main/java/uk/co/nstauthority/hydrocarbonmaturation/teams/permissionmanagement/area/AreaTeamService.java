package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.northernoffshore.NorthernOffshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.onshore.OnshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.southernoffshore.SouthernOffshoreAreaTeamService;

@Service
public class AreaTeamService {

  private final NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService;
  private final SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService;
  private final OnshoreAreaTeamService onshoreAreaTeamService;

  @Autowired
  AreaTeamService(NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService,
                  SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService,
                  OnshoreAreaTeamService onshoreAreaTeamService) {
    this.northernOffshoreAreaTeamService = northernOffshoreAreaTeamService;
    this.southernOffshoreAreaTeamService = southernOffshoreAreaTeamService;
    this.onshoreAreaTeamService = onshoreAreaTeamService;
  }

  public boolean isAreaUser(ServiceUserDetail userDetail) {
    return northernOffshoreAreaTeamService.isNorthernOffshoreAreaUser(userDetail)
        || southernOffshoreAreaTeamService.isSouthernOffshoreAreaUser(userDetail)
        || onshoreAreaTeamService.isOnshoreAreaUser(userDetail);
  }
}
