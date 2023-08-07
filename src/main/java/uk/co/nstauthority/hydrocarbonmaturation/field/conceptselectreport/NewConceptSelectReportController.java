package uk.co.nstauthority.hydrocarbonmaturation.field.conceptselectreport;

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
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("field/concept-select-report/create")
@HasPermission(permissions = {
    RolePermission.INDUSTRY_SUBMIT,
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER})
public class NewConceptSelectReportController {

  private final FieldSelectValidator fieldSelectValidator;
  private final NewSubmissionFromFieldService newSubmissionFromFieldService;

  @Autowired
  public NewConceptSelectReportController(FieldSelectValidator fieldSelectValidator,
                                          NewSubmissionFromFieldService newSubmissionFromFieldService) {
    this.fieldSelectValidator = fieldSelectValidator;
    this.newSubmissionFromFieldService = newSubmissionFromFieldService;
  }

  @GetMapping
  public ModelAndView getNewCsrPage(@ModelAttribute("form") FieldSelectForm form) {
    return getNewCsrPageModelAndView(form);
  }

  @PostMapping
  public ModelAndView createNewCsr(@ModelAttribute("form") FieldSelectForm form,
                                   BindingResult bindingResult) {
    if (!fieldSelectValidator.isValid(bindingResult)) {
      return getNewCsrPageModelAndView(form);
    } else {
      var submissionDetail = newSubmissionFromFieldService.createSubmissionFromField(form, SubmissionType.CREATE_CSR);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId()));
    }
  }

  private ModelAndView getNewCsrPageModelAndView(FieldSelectForm form) {
    return new ModelAndView("remi/field/csr/newCsr")
        .addObject("form", form)
        .addObject("actionUrl",
            ReverseRouter.route(on(NewConceptSelectReportController.class).createNewCsr(null, null)))
        .addObject("backLinkUrl",
            ReverseRouter.route(on(StartConceptSelectReportController.class).getStartPage()))
        .addObject("fieldApiEndpoint",
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchFields(null, SubmissionType.CREATE_CSR, null)));
  }
}
