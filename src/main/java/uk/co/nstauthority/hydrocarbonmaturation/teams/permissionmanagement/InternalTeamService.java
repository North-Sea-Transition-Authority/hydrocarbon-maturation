package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
public class InternalTeamService {

  private final UserDetailService userDetailService;
  private final RegulatorTeamService regulatorTeamService;
  private final AreaTeamService areaTeamService;

  @Autowired
  public InternalTeamService(UserDetailService userDetailService, RegulatorTeamService regulatorTeamService,
                             AreaTeamService areaTeamService) {
    this.userDetailService = userDetailService;
    this.regulatorTeamService = regulatorTeamService;
    this.areaTeamService = areaTeamService;
  }

  public boolean isRegulatorOrAreaTeamUser() {
    var userDetail = userDetailService.getUserDetail();
    return regulatorTeamService.isRegulatorUser(userDetail) || areaTeamService.isAreaUser(userDetail);
  }
}
