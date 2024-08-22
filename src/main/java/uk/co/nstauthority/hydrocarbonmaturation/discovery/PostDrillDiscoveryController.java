package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("/discovery/create-from-prospect")
@HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
@HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
public class PostDrillDiscoveryController {

  private final StartDiscoveryService startDiscoveryService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public PostDrillDiscoveryController(StartDiscoveryService startDiscoveryService,
                                      ControllerHelperService controllerHelperService) {
    this.startDiscoveryService = startDiscoveryService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView getStartDiscoveryPage(@ModelAttribute("form") StartDiscoveryForm form) {
    return getStartDiscoveryModelAndView(form);
  }

  @PostMapping
  public ModelAndView startDiscovery(@Valid @ModelAttribute("form") StartDiscoveryForm form,
                                     BindingResult bindingResult,
                                     User user) {
    bindingResult = startDiscoveryService.validateDiscoveryFromProspect(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getStartDiscoveryModelAndView(form),
        () -> {
          var submissionDetail = startDiscoveryService.createNewDiscoveryFromProspect(form, user);
          return ReverseRouter.redirect(on(TaskListController.class)
              .getTaskList(submissionDetail.getId(), null));
        }
    );
  }

  private ModelAndView getStartDiscoveryModelAndView(StartDiscoveryForm form) {
    return new ModelAndView("remi/discovery/newDiscoveryFromProspect")
        .addObject("backLinkUrl",
            ReverseRouter.route(on(StartPostDrillDiscoveryController.class).getStartPostDrillDiscoveryPage()))
        .addObject("actionUrl", ReverseRouter.route(on(PostDrillDiscoveryController.class)
            .startDiscovery(null, null, null)))
        .addObject("form", form)
        .addObject("prospectApiEndpoint",
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchProspects(null, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT, null)));
  }
}
