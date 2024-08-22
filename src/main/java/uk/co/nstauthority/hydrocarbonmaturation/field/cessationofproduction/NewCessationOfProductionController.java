package uk.co.nstauthority.hydrocarbonmaturation.field.cessationofproduction;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

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
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldSelectForm;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldSelectValidator;
import uk.co.nstauthority.hydrocarbonmaturation.field.NewSubmissionFromFieldService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("field/cessation-of-production/create")
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
@HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
public class NewCessationOfProductionController {

  private final FieldSelectValidator fieldSelectValidator;
  private final NewSubmissionFromFieldService newSubmissionFromFieldService;

  @Autowired
  public NewCessationOfProductionController(FieldSelectValidator fieldSelectValidator,
                                            NewSubmissionFromFieldService newSubmissionFromFieldService) {
    this.fieldSelectValidator = fieldSelectValidator;
    this.newSubmissionFromFieldService = newSubmissionFromFieldService;
  }

  @GetMapping
  public ModelAndView getNewCopPage(@ModelAttribute("form") FieldSelectForm form) {
    return getNewCopPageModelAndView(form);
  }

  @PostMapping
  public ModelAndView createNewCop(@ModelAttribute("form") FieldSelectForm form,
                                   BindingResult bindingResult,
                                   User user) {
    if (!fieldSelectValidator.isValid(bindingResult)) {
      return getNewCopPageModelAndView(form);
    } else {
      var submissionDetail = newSubmissionFromFieldService.createSubmissionFromField(form, SubmissionType.CREATE_COP, user);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId(), null));
    }
  }

  private ModelAndView getNewCopPageModelAndView(FieldSelectForm form) {
    return new ModelAndView("remi/field/cop/newCop")
        .addObject("form", form)
        .addObject("actionUrl",
            ReverseRouter.route(on(NewCessationOfProductionController.class).createNewCop(null, null, null)))
        .addObject("backLinkUrl",
            ReverseRouter.route(on(StartCessationOfProductionController.class).getStartPage()))
        .addObject("fieldApiEndpoint",
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchFields(null, SubmissionType.CREATE_COP, null)));
  }
}
