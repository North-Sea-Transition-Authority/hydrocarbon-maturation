package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

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
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.DiscoveryPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.ProspectPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Controller
@RequestMapping("/start-submission")
@HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
@HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
public class StartSubmissionController {

  private final ResourceTypeSelectService resourceTypeSelectService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionCreateService submissionCreateService;

  @Autowired
  public StartSubmissionController(ResourceTypeSelectService resourceTypeSelectService,
                                   ControllerHelperService controllerHelperService,
                                   SubmissionCreateService submissionCreateService) {
    this.resourceTypeSelectService = resourceTypeSelectService;
    this.controllerHelperService = controllerHelperService;
    this.submissionCreateService = submissionCreateService;
  }

  @GetMapping
  public ModelAndView getSubmissionType(User user) {
    return getSubmissionTypeModelAndViewForUser(new ResourceTypeSelectForm(), user);
  }

  @PostMapping
  public ModelAndView startSubmission(@ModelAttribute("form") ResourceTypeSelectForm form,
                                      BindingResult bindingResult,
                                      User user) {
    bindingResult = resourceTypeSelectService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getSubmissionTypeModelAndViewForUser(form, user),
        () -> switch (form.getResourceType()) {
          case PROSPECT -> ReverseRouter.redirect(on(ProspectPurposeController.class).getProspectPurposeForm(null));
          case DISCOVERY -> ReverseRouter.redirect(on(DiscoveryPurposeController.class).getDiscoveryPurposeForm(null));
          case FIELD -> ReverseRouter.redirect(on(FieldPurposeController.class).getFieldPurposeForm(null));
        });
  }

  ModelAndView getSubmissionTypeModelAndViewForUser(ResourceTypeSelectForm resourceTypeSelectForm, User user) {
    return new ModelAndView("remi/submissiondetails/startSubmission")
        .addObject("resourceTypes", submissionCreateService.getResourceTypeOptionsForUser(user))
        .addObject("form", resourceTypeSelectForm)
        .addObject("actionUrl", ReverseRouter.route(on(StartSubmissionController.class).startSubmission(null, null, null)))
        .addObject("backLinkUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)));
  }
}