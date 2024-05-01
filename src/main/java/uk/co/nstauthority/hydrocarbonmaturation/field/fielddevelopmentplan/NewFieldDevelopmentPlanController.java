package uk.co.nstauthority.hydrocarbonmaturation.field.fielddevelopmentplan;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldSelectForm;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldSelectValidator;
import uk.co.nstauthority.hydrocarbonmaturation.field.NewSubmissionFromFieldService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@Controller
@RequestMapping("field/field-development-plan/create")
@HasPermission(permissions = {
    RolePermission.INDUSTRY_SUBMIT,
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER})
public class NewFieldDevelopmentPlanController {

  private final FieldSelectValidator fieldSelectValidator;
  private final NewSubmissionFromFieldService newSubmissionFromFieldService;

  @Autowired
  public NewFieldDevelopmentPlanController(FieldSelectValidator fieldSelectValidator,
                                           NewSubmissionFromFieldService newSubmissionFromFieldService) {
    this.fieldSelectValidator = fieldSelectValidator;
    this.newSubmissionFromFieldService = newSubmissionFromFieldService;
  }

  @GetMapping
  public ModelAndView getNewFdpPage(@ModelAttribute("form") FieldSelectForm form) {
    return getNewFdpPageModelAndView(form);
  }

  @PostMapping
  public ModelAndView createNewFdp(@ModelAttribute("form") FieldSelectForm form,
                                   BindingResult bindingResult) {
    if (!fieldSelectValidator.isValid(bindingResult)) {
      return getNewFdpPageModelAndView(form);
    } else {
      var submissionDetail = newSubmissionFromFieldService.createSubmissionFromField(form, SubmissionType.CREATE_FDP);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId()));
    }
  }

  private ModelAndView getNewFdpPageModelAndView(FieldSelectForm form) {
    return new ModelAndView("remi/field/fdp/newFdp")
        .addObject("form", form)
        .addObject("actionUrl",
            ReverseRouter.route(on(NewFieldDevelopmentPlanController.class).createNewFdp(null, null)))
        .addObject("backLinkUrl",
            ReverseRouter.route(on(StartFieldDevelopmentPlanController.class).getStartPage()))
        .addObject("fieldApiEndpoint",
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchFields(null, SubmissionType.CREATE_FDP, null)));
  }
}
