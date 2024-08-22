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
@RequestMapping("relinquishment/prospect/new")
@HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
@HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
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
                                                                BindingResult bindingResult,
                                                                User user) {
    bindingResult = prospectRelinquishmentService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getNewRelinquishmentPageModelAndView(form),
        () -> {
          var submissionDetail = prospectRelinquishmentService.createProspectRelinquishment(form, user);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId(), null));
        });
  }

  private ModelAndView getNewRelinquishmentPageModelAndView(ProspectRelinquishmentForm form) {
    return new ModelAndView("remi/relinquishment/newProspectRelinquishment")
        .addObject("form", form)
        .addObject("prospectApiEndpoint", SearchSelectorService.route(on(ResourceRestController.class)
            .searchProspects(null, SubmissionType.RELINQUISHMENT_PROSPECT, null)))
        .addObject("backLinkUrl",
            ReverseRouter.route(on(StartRelinquishmentSubmissionController.class)
                .getStartProspectRelinquishmentPage()))
        .addObject("actionUrl",
            ReverseRouter.route(on(NewProspectRelinquishmentController.class)
                .createNewProspectRelinquishmentSubmission(null, null, null)));
  }
}
