package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamMemberRolesForm;

@Service
public class AreaAddRolesControllerHelperService {
  private final EnergyPortalUserService energyPortalUserService;

  @Autowired
  public AreaAddRolesControllerHelperService(EnergyPortalUserService energyPortalUserService) {
    this.energyPortalUserService = energyPortalUserService;
  }

  public ModelAndView renderAddTeamMemberRoles(WebUserAccountId wuaId,
                                               Map<String, String> roles, String backLinkUrl) {
    var energyPortalUser = getEnergyPortalUser(wuaId);
    return getAddTeamMemberRolesModelAndView(energyPortalUser, new TeamMemberRolesForm(), backLinkUrl, roles)
        .addObject("roles", roles);
  }

  public ModelAndView getAddTeamMemberRolesModelAndView(EnergyPortalUserDto energyPortalUser,
                                                        TeamMemberRolesForm form,
                                                        String backLinkUrl,
                                                        Map<String, String> roles) {
    return new ModelAndView("remi/permissionmanagement/addTeamMemberRoles")
        .addObject("pageTitle", "What actions does %s perform?".formatted(energyPortalUser.displayName()))
        .addObject("form", form)
        .addObject("roles", roles)
        .addObject("backLinkUrl", backLinkUrl);
  }

  public EnergyPortalUserDto getEnergyPortalUser(WebUserAccountId webUserAccountId) {
    var energyPortalUser = energyPortalUserService.findByWuaId(webUserAccountId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "No Energy Portal user with WUA_ID %s could be found".formatted(webUserAccountId)
        ));

    if (energyPortalUser.isSharedAccount()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Energy Portal user with WUA_ID %s is a shared account and is not allowed to be added to this service"
              .formatted(webUserAccountId)
      );
    }

    if (!energyPortalUser.canLogin()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          """
                  Energy Portal user with WUA_ID %s does not have login access to the Energy Portal and is
                  not allowed to be added to this service
              """
              .formatted(webUserAccountId)
      );
    }

    return energyPortalUser;
  }
}
