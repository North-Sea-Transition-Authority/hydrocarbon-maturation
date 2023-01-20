package uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@Controller
@RequestMapping("/prospect/update-discovery")
@AccessibleByServiceUsers
public class UpdateDiscoveryController {

  private final UpdateDiscoveryService updateDiscoveryService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public UpdateDiscoveryController(UpdateDiscoveryService updateDiscoveryService,
                            ControllerHelperService controllerHelperService) {
    this.updateDiscoveryService = updateDiscoveryService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView getUpdateDiscoveryPage(@ModelAttribute("form") UpdateDiscoveryForm form) {
    return getDiscoveryPageModelAndView(form);
  }

  @PostMapping
  public ModelAndView createUpdateDiscovery(@ModelAttribute("form") UpdateDiscoveryForm form,
                                            BindingResult bindingResult) {
    bindingResult = updateDiscoveryService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getDiscoveryPageModelAndView(form),
        () -> {
          var submissionDetail = updateDiscoveryService.createUpdateDiscovery(form);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId()));
        });
  }

  private ModelAndView getDiscoveryPageModelAndView(UpdateDiscoveryForm form) {
    return new ModelAndView("remi/prospect/updateDiscovery")
        .addObject("form", form)
        .addObject("actionUrl",
            ReverseRouter.route(on(UpdateDiscoveryController.class).createUpdateDiscovery(form, null)))
        .addObject("backLinkUrl",
            ReverseRouter.route(on(StartUpdateDiscoverySubmissionController.class).getStartUpdateDiscoveryPage()))
        .addObject("discoveryApiEndpoint",
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchDiscoveries(null, SubmissionType.UPDATE_DISCOVERY)));
  }
}
