package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.DiscoveryPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.ProspectPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/relinquishment")
public class StartRelinquishmentSubmissionController {

  @HasPermission(permissions = {
      RolePermission.EXPLORATION,
      RolePermission.INDUSTRY_SUBMIT})
  @GetMapping("/prospect/start")
  public ModelAndView getStartProspectRelinquishmentPage() {
    return new ModelAndView("remi/relinquishment/startRelinquishment")
        .addObject("backLinkUrl",
            ReverseRouter.route(on(ProspectPurposeController.class).getProspectPurposeForm()))
        .addObject("actionUrl",
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class).startProspectRelinquishment()))
        .addObject("resourceType", ResourceType.PROSPECT);
  }

  @HasPermission(permissions = {
      RolePermission.EXPLORATION,
      RolePermission.INDUSTRY_SUBMIT})
  @PostMapping("/prospect/start")
  public ModelAndView startProspectRelinquishment() {
    return ReverseRouter.redirect(on(NewProspectRelinquishmentController.class).getNewProspectRelinquishmentPage());
  }

  @HasPermission(permissions = {
      RolePermission.EXPLORATION,
      RolePermission.INDUSTRY_SUBMIT,
      RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
      RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
      RolePermission.ONSHORE_AREA_TEAM_MEMBER})
  @GetMapping("/discovery/start")
  public ModelAndView getStartDiscoveryRelinquishmentPage() {
    return new ModelAndView("remi/relinquishment/startRelinquishment")
        .addObject("backLinkUrl",
            ReverseRouter.route(on(DiscoveryPurposeController.class).getDiscoveryPurposeForm()))
        .addObject("actionUrl",
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class).startDiscoveryRelinquishment()))
        .addObject("resourceType", ResourceType.DISCOVERY);
  }

  @HasPermission(permissions = {
      RolePermission.EXPLORATION,
      RolePermission.INDUSTRY_SUBMIT,
      RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
      RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
      RolePermission.ONSHORE_AREA_TEAM_MEMBER})
  @PostMapping("/discovery/start")
  public ModelAndView startDiscoveryRelinquishment() {
    return ReverseRouter.redirect(on(NewDiscoveryRelinquishmentController.class).getNewDiscoveryRelinquishmentPage());
  }
}