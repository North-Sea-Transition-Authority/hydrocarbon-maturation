package uk.co.nstauthority.hydrocarbonmaturation.discovery;

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
import uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery.StartUpdateDiscoverySubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.relinquishment.StartRelinquishmentSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/discovery/purpose")
@HasPermission(permissions = {
    RolePermission.EXPLORATION,
    RolePermission.INDUSTRY_SUBMIT,
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER})
public class DiscoveryPurposeController {

  private final DiscoveryPurposeTypeSelectService discoveryPurposeTypeSelectService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public DiscoveryPurposeController(DiscoveryPurposeTypeSelectService discoveryPurposeTypeSelectService,
                                    ControllerHelperService controllerHelperService) {
    this.discoveryPurposeTypeSelectService = discoveryPurposeTypeSelectService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView getDiscoveryPurposeForm() {
    return getDiscoveryPurposeTypeModelAndView(new DiscoveryPurposeTypeSelectForm());
  }

  @PostMapping
  public ModelAndView startDiscoverySubmission(@ModelAttribute("form") DiscoveryPurposeTypeSelectForm form,
                                               BindingResult bindingResult) {
    bindingResult = discoveryPurposeTypeSelectService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getDiscoveryPurposeTypeModelAndView(form),
        () -> switch (form.getPurposeType()) {
          case CREATE_DISCOVERY_FROM_PROSPECT ->
              ReverseRouter.redirect(on(StartPostDrillDiscoveryController.class).getStartPostDrillDiscoveryPage());
          case CREATE_DISCOVERY -> ReverseRouter.redirect(on(StartDiscoveryController.class).startDiscovery());
          case UPDATE_DISCOVERY ->
              ReverseRouter.redirect(on(StartUpdateDiscoverySubmissionController.class).getStartUpdateDiscoveryPage());
          case RELINQUISHMENT_DISCOVERY ->
              ReverseRouter.redirect(on(StartRelinquishmentSubmissionController.class).getStartDiscoveryRelinquishmentPage());
        });
  }

  ModelAndView getDiscoveryPurposeTypeModelAndView(DiscoveryPurposeTypeSelectForm prospectPurposeTypeSelectForm) {
    return new ModelAndView("remi/submissiondetails/submissionPurpose")
        .addObject("purposeTypes", DisplayableEnumOptionUtil.getDisplayableOptions(DiscoveryPurposeType.class))
        .addObject("form", prospectPurposeTypeSelectForm)
        .addObject("actionUrl", ReverseRouter.route(on(DiscoveryPurposeController.class)
            .startDiscoverySubmission(null, null)))
        .addObject("backLinkUrl", ReverseRouter.route(on(StartSubmissionController.class)
            .getSubmissionType()));
  }
}
