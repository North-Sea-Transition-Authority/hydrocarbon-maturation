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
@RequestMapping("relinquishment/prospect/new")
@HasPermission(permissions = {
    RolePermission.EXPLORATION,
    RolePermission.INDUSTRY_SUBMIT})
public class NewProspectRelinquishmentController {

  private final ControllerHelperService controllerHelperService;
  private final ProspectRelinquishmentService prospectRelinquishmentService;

  @Autowired
  public NewProspectRelinquishmentController(ControllerHelperService controllerHelperService,
                                             ProspectRelinquishmentService prospectRelinquishmentService) {
    this.controllerHelperService = controllerHelperService;
    this.prospectRelinquishmentService = prospectRelinquishmentService;
  }

  @GetMapping
  public ModelAndView getNewProspectRelinquishmentPage() {
    return getNewRelinquishmentPageModelAndView(new ProspectRelinquishmentForm());
  }

  @PostMapping
  public ModelAndView createNewProspectRelinquishmentSubmission(@Valid @ModelAttribute("form") ProspectRelinquishmentForm form,
                                                                BindingResult bindingResult) {
    bindingResult = prospectRelinquishmentService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getNewRelinquishmentPageModelAndView(form),
        () -> {
          var submissionDetail = prospectRelinquishmentService.createProspectRelinquishment(form);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId()));
        });
  }

  private ModelAndView getNewRelinquishmentPageModelAndView(ProspectRelinquishmentForm form) {
    return new ModelAndView("remi/relinquishment/newProspectRelinquishment")
        .addObject("form", form)
        .addObject("prospectApiEndpoint", SearchSelectorService.route(on(ResourceRestController.class)
            .searchProspects(null, SubmissionType.RELINQUISHMENT_PROSPECT)))
        .addObject("backLinkUrl",
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .getStartProspectRelinquishmentPage()))
        .addObject("actionUrl",
            ReverseRouter.route(on(NewProspectRelinquishmentController.class)
                .createNewProspectRelinquishmentSubmission(null, null)));
  }
}
