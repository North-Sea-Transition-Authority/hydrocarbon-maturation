package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.DiscoveryPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.ProspectPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("/relinquishment")
public class StartRelinquishmentSubmissionController {

  @HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
  @HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
  @GetMapping("/prospect/start")
  public ModelAndView getStartProspectRelinquishmentPage() {
    return new ModelAndView("remi/relinquishment/startRelinquishment")
        .addObject("backLinkUrl",
            ReverseRouter.route(on(ProspectPurposeController.class).getProspectPurposeForm(null)))
        .addObject("actionUrl",
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class).startProspectRelinquishment()))
        .addObject("resourceType", ResourceType.PROSPECT);
  }

  @HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
  @HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
  @PostMapping("/prospect/start")
  public ModelAndView startProspectRelinquishment() {
    return ReverseRouter.redirect(on(NewProspectRelinquishmentController.class).getNewProspectRelinquishmentPage());
  }

  @HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
  @HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
  @HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
  @HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
  @HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
  @GetMapping("/discovery/start")
  public ModelAndView getStartDiscoveryRelinquishmentPage() {
    return new ModelAndView("remi/relinquishment/startRelinquishment")
        .addObject("backLinkUrl",
            ReverseRouter.route(on(DiscoveryPurposeController.class).getDiscoveryPurposeForm(null)))
        .addObject("actionUrl",
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class).startDiscoveryRelinquishment()))
        .addObject("resourceType", ResourceType.DISCOVERY);
  }


  @HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
  @HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
  @HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
  @HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
  @HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
  @PostMapping("/discovery/start")
  public ModelAndView startDiscoveryRelinquishment() {
    return ReverseRouter.redirect(on(NewDiscoveryRelinquishmentController.class).getNewDiscoveryRelinquishmentPage());
  }
}