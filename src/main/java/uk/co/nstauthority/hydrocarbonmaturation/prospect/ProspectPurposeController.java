package uk.co.nstauthority.hydrocarbonmaturation.prospect;

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
import uk.co.nstauthority.hydrocarbonmaturation.discovery.StartPostDrillDiscoveryController;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.StartPostDrillDryHoleSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.StartUpdateProspectController;
import uk.co.nstauthority.hydrocarbonmaturation.relinquishment.StartRelinquishmentSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/prospect/purpose")
@HasPermission(permissions = {RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT})
public class ProspectPurposeController {

  private final ProspectPurposeTypeSelectService prospectPurposeTypeSelectService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public ProspectPurposeController(ProspectPurposeTypeSelectService prospectPurposeTypeSelectService,
                                   ControllerHelperService controllerHelperService) {
    this.prospectPurposeTypeSelectService = prospectPurposeTypeSelectService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView getProspectPurposeForm() {
    return getProspectPurposeTypeModelAndView(new ProspectPurposeTypeSelectForm());
  }

  @PostMapping
  public ModelAndView startProspectSubmission(@ModelAttribute("form") ProspectPurposeTypeSelectForm form,
                                              BindingResult bindingResult) {
    bindingResult = prospectPurposeTypeSelectService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getProspectPurposeTypeModelAndView(form),
        () -> switch (form.getPurposeType()) {
          case CREATE_PROSPECT -> ReverseRouter.redirect(on(StartProspectController.class).getStartPage());
          case PROSPECT_UPDATE -> ReverseRouter.redirect(on(StartUpdateProspectController.class).getStartUpdatePage());
          case POST_DRILL_DRY_HOLE ->
              ReverseRouter.redirect(on(StartPostDrillDryHoleSubmissionController.class).getStartPostDrillDryHolePage());
          case CREATE_DISCOVERY_FROM_PROSPECT ->
              ReverseRouter.redirect(on(StartPostDrillDiscoveryController.class).getStartPostDrillDiscoveryPage());
          case RELINQUISHMENT_PROSPECT ->
              ReverseRouter.redirect(on(StartRelinquishmentSubmissionController.class).getStartProspectRelinquishmentPage());
        });
  }

  ModelAndView getProspectPurposeTypeModelAndView(ProspectPurposeTypeSelectForm prospectPurposeTypeSelectForm) {
    return new ModelAndView("remi/submissiondetails/submissionPurpose")
        .addObject("purposeTypes", DisplayableEnumOptionUtil.getDisplayableOptions(ProspectPurposeType.class))
        .addObject("form", prospectPurposeTypeSelectForm)
        .addObject("actionUrl", ReverseRouter.route(on(ProspectPurposeController.class)
            .startProspectSubmission(null, null)))
        .addObject("backLinkUrl", ReverseRouter.route(on(StartSubmissionController.class)
            .getSubmissionType()));
  }
}
