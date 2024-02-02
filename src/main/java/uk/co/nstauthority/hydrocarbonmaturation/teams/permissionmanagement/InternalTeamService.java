package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
public class InternalTeamService {

  private final UserService userService;
  private final RegulatorTeamService regulatorTeamService;
  private final AreaTeamService areaTeamService;

  @Autowired
  public InternalTeamService(UserService userService, RegulatorTeamService regulatorTeamService,
                             AreaTeamService areaTeamService) {
    this.userService = userService;
    this.regulatorTeamService = regulatorTeamService;
    this.areaTeamService = areaTeamService;
  }

  public boolean isRegulatorOrAreaTeamUser() {
    var user = userService.getUser();
    return isRegulatorOrAreaTeamUser(user);
  }

  public boolean isRegulatorOrAreaTeamUser(User user) {
    return regulatorTeamService.isRegulatorUser(user) || areaTeamService.isAreaUser(user);
  }
}
