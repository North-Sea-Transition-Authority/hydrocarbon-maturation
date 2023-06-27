package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTypeSelectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTypeSelectService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/field/purpose")
@HasPermission(permissions = {
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER
})
public class FieldPurposeController {
  private final ControllerHelperService controllerHelperService;
  private final SubmissionTypeSelectService submissionTypeSelectService;

  @Autowired
  public FieldPurposeController(ControllerHelperService controllerHelperService,
                                SubmissionTypeSelectService submissionTypeSelectService) {
    this.controllerHelperService = controllerHelperService;
    this.submissionTypeSelectService = submissionTypeSelectService;
  }

  @GetMapping
  public ModelAndView getFieldPurposeForm() {
    return getFieldPurposeTypeModelAndView(new SubmissionTypeSelectForm());
  }

  @PostMapping
  public ModelAndView startFieldSubmission(@ModelAttribute("form") SubmissionTypeSelectForm form,
                                               BindingResult bindingResult) {
    bindingResult = submissionTypeSelectService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getFieldPurposeTypeModelAndView(form),
        () -> switch (form.getSubmissionType()) {
          case CREATE_FIELD -> ReverseRouter.redirect(on(StartFieldController.class).getStartPage());
          default -> throw new ResponseStatusException(HttpStatus.FORBIDDEN,
              "Invalid submission type selected for field only form");
        });
  }

  ModelAndView getFieldPurposeTypeModelAndView(SubmissionTypeSelectForm submissionTypeSelectForm) {
    return new ModelAndView("remi/submissiondetails/submissionPurpose")
        .addObject("submissionTypes", submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.FIELD))
        .addObject("form", submissionTypeSelectForm)
        .addObject("actionUrl", ReverseRouter.route(on(FieldPurposeController.class)
            .startFieldSubmission(null, null)))
        .addObject("backLinkUrl", ReverseRouter.route(on(StartSubmissionController.class)
            .getSubmissionType()));
  }
}
