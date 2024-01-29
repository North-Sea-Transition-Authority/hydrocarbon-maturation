package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;


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
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("relinquishment/discovery/new")
@HasPermission(permissions = {
    RolePermission.EXPLORATION,
    RolePermission.INDUSTRY_SUBMIT,
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER})
public class NewDiscoveryRelinquishmentController {

  private final ControllerHelperService controllerHelperService;
  private final DiscoveryRelinquishmentService discoveryRelinquishmentService;

  @Autowired
  public NewDiscoveryRelinquishmentController(ControllerHelperService controllerHelperService,
                                              DiscoveryRelinquishmentService discoveryRelinquishmentService) {
    this.controllerHelperService = controllerHelperService;
    this.discoveryRelinquishmentService = discoveryRelinquishmentService;
  }

  @GetMapping
  public ModelAndView getNewDiscoveryRelinquishmentPage() {
    return getNewRelinquishmentPageModelAndView(new DiscoveryRelinquishmentForm());
  }

  @PostMapping
  public ModelAndView createNewDiscoveryRelinquishmentSubmission(@Valid @ModelAttribute("form") DiscoveryRelinquishmentForm form,
                                                                 BindingResult bindingResult) {
    bindingResult = discoveryRelinquishmentService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getNewRelinquishmentPageModelAndView(form),
        () -> {
          var submissionDetail = discoveryRelinquishmentService.createDiscoveryRelinquishment(form);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId()));
        });
  }

  private ModelAndView getNewRelinquishmentPageModelAndView(DiscoveryRelinquishmentForm form) {
    return new ModelAndView("remi/relinquishment/newDiscoveryRelinquishment")
        .addObject("form", form)
        .addObject("discoveryApiEndpoint", SearchSelectorService.route(on(ResourceRestController.class)
            .searchDiscoveries(null, SubmissionType.RELINQUISHMENT_DISCOVERY)))
        .addObject("backLinkUrl",
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .getStartDiscoveryRelinquishmentPage()))
        .addObject("actionUrl",
            ReverseRouter.route(on(NewDiscoveryRelinquishmentController.class)
                .createNewDiscoveryRelinquishmentSubmission(null, null)));
  }
}
