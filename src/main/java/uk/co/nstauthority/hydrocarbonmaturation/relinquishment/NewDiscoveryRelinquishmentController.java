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
@RequestMapping("relinquishment/discovery/new")
@HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
@HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
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
                                                                 BindingResult bindingResult,
                                                                 User user) {
    bindingResult = discoveryRelinquishmentService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getNewRelinquishmentPageModelAndView(form),
        () -> {
          var submissionDetail = discoveryRelinquishmentService.createDiscoveryRelinquishment(form, user);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId(), null));
        });
  }

  private ModelAndView getNewRelinquishmentPageModelAndView(DiscoveryRelinquishmentForm form) {
    return new ModelAndView("remi/relinquishment/newDiscoveryRelinquishment")
        .addObject("form", form)
        .addObject("discoveryApiEndpoint", SearchSelectorService.route(on(ResourceRestController.class)
            .searchDiscoveries(null, SubmissionType.RELINQUISHMENT_DISCOVERY, null)))
        .addObject("backLinkUrl",
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .getStartDiscoveryRelinquishmentPage()))
        .addObject("actionUrl",
            ReverseRouter.route(on(NewDiscoveryRelinquishmentController.class)
                .createNewDiscoveryRelinquishmentSubmission(null, null, null)));
  }
}
