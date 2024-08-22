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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.cessationofproduction.StartCessationOfProductionController;
import uk.co.nstauthority.hydrocarbonmaturation.field.conceptselectreport.StartConceptSelectReportController;
import uk.co.nstauthority.hydrocarbonmaturation.field.fielddevelopmentplan.StartFieldDevelopmentPlanController;
import uk.co.nstauthority.hydrocarbonmaturation.field.fielddevelopmentplanaddendum.StartFieldDevelopmentPlanAddendumController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTypeSelectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTypeSelectService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("/field/purpose")
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
@HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
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
  public ModelAndView getFieldPurposeForm(User user) {
    return getFieldPurposeTypeModelAndView(new SubmissionTypeSelectForm(), user);
  }

  @PostMapping
  public ModelAndView startFieldSubmission(@ModelAttribute("form") SubmissionTypeSelectForm form,
                                           BindingResult bindingResult,
                                           User user) {
    bindingResult = submissionTypeSelectService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getFieldPurposeTypeModelAndView(form, user),
        () -> switch (form.getSubmissionType()) {
          case CREATE_FIELD -> ReverseRouter.redirect(on(StartFieldController.class).getStartPage());
          case CREATE_CSR -> ReverseRouter.redirect(on(StartConceptSelectReportController.class).getStartPage());
          case CREATE_FDP -> ReverseRouter.redirect(on(StartFieldDevelopmentPlanController.class).getStartPage());
          case CREATE_FDPA ->
              ReverseRouter.redirect(on(StartFieldDevelopmentPlanAddendumController.class).getStartPage());
          case CREATE_COP -> ReverseRouter.redirect(on(StartCessationOfProductionController.class).getStartPage());
          default -> throw new ResponseStatusException(HttpStatus.FORBIDDEN,
              "Invalid submission type selected for field only form");
        });
  }

  ModelAndView getFieldPurposeTypeModelAndView(SubmissionTypeSelectForm submissionTypeSelectForm, User user) {
    return new ModelAndView("remi/submissiondetails/submissionPurpose")
        .addObject("submissionTypes", submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.FIELD, user))
        .addObject("form", submissionTypeSelectForm)
        .addObject("actionUrl", ReverseRouter.route(on(FieldPurposeController.class)
            .startFieldSubmission(null, null, null)))
        .addObject("backLinkUrl", ReverseRouter.route(on(StartSubmissionController.class)
            .getSubmissionType(null)));
  }
}
