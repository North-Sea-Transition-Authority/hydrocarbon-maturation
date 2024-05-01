package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.northernoffshore.NorthernOffshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.onshore.OnshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.southernoffshore.SouthernOffshoreAreaTeamService;

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

  public boolean isAreaUser(User user) {
    return northernOffshoreAreaTeamService.isNorthernOffshoreAreaUser(user)
        || southernOffshoreAreaTeamService.isSouthernOffshoreAreaUser(user)
        || onshoreAreaTeamService.isOnshoreAreaUser(user);
  }

  public boolean isAreaTeamMember(User user) {
    return northernOffshoreAreaTeamService.isNorthernOffshoreTeamMember(user)
        || southernOffshoreAreaTeamService.isSouthernOffshoreTeamMember(user)
        || onshoreAreaTeamService.isOnshoreTeamMember(user);
  }
}
