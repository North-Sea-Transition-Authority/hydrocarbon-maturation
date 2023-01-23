package uk.co.nstauthority.hydrocarbonmaturation.relinquishment;


import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import javax.validation.Valid;
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
@RequestMapping("relinquishment/new")
@HasPermission(permissions = {RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT})
public class NewRelinquishmentController {

  private final ControllerHelperService controllerHelperService;
  private final RelinquishmentService relinquishmentService;

  @Autowired
  public NewRelinquishmentController(ControllerHelperService controllerHelperService,
                                     RelinquishmentService relinquishmentService) {
    this.controllerHelperService = controllerHelperService;
    this.relinquishmentService = relinquishmentService;
  }

  @GetMapping
  public ModelAndView getNewRelinquishmentPage() {
    return getNewRelinquishmentPageModelAndView(new RelinquishmentForm());
  }

  @PostMapping
  public ModelAndView createNewRelinquishmentSubmission(@Valid @ModelAttribute("form") RelinquishmentForm form,
                                                        BindingResult bindingResult) {
    bindingResult = relinquishmentService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getNewRelinquishmentPageModelAndView(form),
        () -> {
          var submissionDetail = relinquishmentService.createRelinquishment(form);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId()));
        });
  }

  private ModelAndView getNewRelinquishmentPageModelAndView(RelinquishmentForm form) {
    return new ModelAndView("remi/relinquishment/newRelinquishment")
        .addObject("form", form)
        .addObject("prospectApiEndpoint",
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchProspects(null, SubmissionType.RELINQUISHMENT_PROSPECT)))
        .addObject("discoveryApiEndpoint",
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchDiscoveries(null, SubmissionType.RELINQUISHMENT_DISCOVERY)))
        .addObject("backLinkUrl",
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .getStartRelinquishmentPage()))
        .addObject("actionUrl",
            ReverseRouter.route(on(NewRelinquishmentController.class)
                .createNewRelinquishmentSubmission(null, null)));
  }
}
