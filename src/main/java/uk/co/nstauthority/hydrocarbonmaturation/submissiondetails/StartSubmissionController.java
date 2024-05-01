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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.DiscoveryPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.ProspectPurposeController;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Controller
@RequestMapping("/start-submission")
@HasPermission(permissions = {
    RolePermission.EXPLORATION,
    RolePermission.INDUSTRY_SUBMIT,
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER
})
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
  public ModelAndView getSubmissionType() {
    return getSubmissionTypeModelAndViewForUser(new ResourceTypeSelectForm());
  }

  @PostMapping
  public ModelAndView startSubmission(@ModelAttribute("form") ResourceTypeSelectForm form, BindingResult bindingResult) {
    bindingResult = resourceTypeSelectService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getSubmissionTypeModelAndViewForUser(form),
        () -> switch (form.getResourceType()) {
          case PROSPECT -> ReverseRouter.redirect(on(ProspectPurposeController.class).getProspectPurposeForm());
          case DISCOVERY -> ReverseRouter.redirect(on(DiscoveryPurposeController.class).getDiscoveryPurposeForm());
          case FIELD -> ReverseRouter.redirect(on(FieldPurposeController.class).getFieldPurposeForm());
        });
  }

  ModelAndView getSubmissionTypeModelAndViewForUser(ResourceTypeSelectForm resourceTypeSelectForm) {
    return new ModelAndView("remi/submissiondetails/startSubmission")
        .addObject("resourceTypes", submissionCreateService.getResourceTypeOptionsForUser())
        .addObject("form", resourceTypeSelectForm)
        .addObject("actionUrl", ReverseRouter.route(on(StartSubmissionController.class).startSubmission(null, null)))
        .addObject("backLinkUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)));
  }
}