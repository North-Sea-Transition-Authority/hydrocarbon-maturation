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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@Controller
@RequestMapping("/discovery/create-from-prospect")
@HasPermission(permissions = {
    RolePermission.EXPLORATION,
    RolePermission.INDUSTRY_SUBMIT,
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER})
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
                                     BindingResult bindingResult) {
    bindingResult = startDiscoveryService.validateDiscoveryFromProspect(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getStartDiscoveryModelAndView(form),
        () -> {
          var submissionDetail = startDiscoveryService.createNewDiscoveryFromProspect(form);
          return ReverseRouter.redirect(on(TaskListController.class)
              .getTaskList(submissionDetail.getId()));
        }
    );
  }

  private ModelAndView getStartDiscoveryModelAndView(StartDiscoveryForm form) {
    return new ModelAndView("remi/discovery/newDiscoveryFromProspect")
        .addObject("backLinkUrl",
            ReverseRouter.route(on(StartPostDrillDiscoveryController.class).getStartPostDrillDiscoveryPage()))
        .addObject("actionUrl", ReverseRouter.route(on(PostDrillDiscoveryController.class)
            .startDiscovery(null, null)))
        .addObject("form", form)
        .addObject("prospectApiEndpoint",
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchProspects(null, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT)));
  }
}
