package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.EnergyPortalConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Team;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.AddTeamMemberForm;

@Service
public class AreaAddMemberControllerHelperService {

  private final ControllerHelperService controllerHelperService;
  private final EnergyPortalConfiguration energyPortalConfiguration;

  @Autowired
  public AreaAddMemberControllerHelperService(ControllerHelperService controllerHelperService,
                                              EnergyPortalConfiguration energyPortalConfiguration) {
    this.controllerHelperService = controllerHelperService;
    this.energyPortalConfiguration = energyPortalConfiguration;
  }

  public ModelAndView addMemberToTeamSubmission(BindingResult bindingResult,
                                                AddTeamMemberForm form,
                                                Team team,
                                                ModelAndView successfulRedirect,
                                                String submitUrl,
                                                String backLinkUrl) {
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getAddTeamMemberModelAndView(form, submitUrl, backLinkUrl, team),
        () -> successfulRedirect
    );
  }

  public ModelAndView getAddTeamMemberModelAndView(AddTeamMemberForm form, String submitUrl, String backLinkUrl, Team team) {
    return new ModelAndView("remi/permissionmanagement/addTeamMember")
        .addObject("htmlTitle", "Add user to %s".formatted(team.getDisplayName()))
        .addObject("registrationUrl", energyPortalConfiguration.registrationUrl())
        .addObject("form", form)
        .addObject("submitUrl", submitUrl)
        .addObject("backLinkUrl", backLinkUrl);
  }
}
