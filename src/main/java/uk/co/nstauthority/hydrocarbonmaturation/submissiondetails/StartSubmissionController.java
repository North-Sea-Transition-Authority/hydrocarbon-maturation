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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.StartDiscoveryController;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.StartPostDrillDiscoveryController;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery.StartUpdateDiscoverySubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.StartProspectController;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.StartPostDrillDryHoleSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.StartUpdateProspectController;
import uk.co.nstauthority.hydrocarbonmaturation.relinquishment.StartRelinquishmentSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Controller
@RequestMapping("/start-submission")
@HasPermission(permissions = {RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT})
public class StartSubmissionController {

  private final SubmissionTypeSelectService submissionTypeSelectService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionCreateService submissionCreateService;

  @Autowired
  public StartSubmissionController(SubmissionTypeSelectService submissionTypeSelectService,
                                   ControllerHelperService controllerHelperService,
                                   UserDetailService userDetailService,
                                   SubmissionCreateService submissionCreateService) {
    this.submissionTypeSelectService = submissionTypeSelectService;
    this.controllerHelperService = controllerHelperService;
    this.submissionCreateService = submissionCreateService;
  }

  @GetMapping
  public ModelAndView getSubmissionPurpose() {
    return getSubmissionTypeModelAndViewForUser(new SubmissionTypeSelectForm());
  }

  @PostMapping
  public ModelAndView startSubmission(@ModelAttribute("form") SubmissionTypeSelectForm form, BindingResult bindingResult) {
    bindingResult = submissionTypeSelectService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getSubmissionTypeModelAndViewForUser(form),
        () -> switch (form.getSubmissionType()) {
          case CREATE_PROSPECT -> ReverseRouter.redirect(on(StartProspectController.class).getStartPage());
          case PROSPECT_UPDATE -> ReverseRouter.redirect(on(StartUpdateProspectController.class).getStartUpdatePage());
          case POST_DRILL_DRY_HOLE ->
              ReverseRouter.redirect(on(StartPostDrillDryHoleSubmissionController.class).getStartPostDrillDryHolePage());
          case CREATE_DISCOVERY_FROM_PROSPECT ->
              ReverseRouter.redirect(on(StartPostDrillDiscoveryController.class).getStartPostDrillDiscoveryPage());
          case CREATE_DISCOVERY ->
              ReverseRouter.redirect(on(StartDiscoveryController.class).startDiscovery());
          case UPDATE_DISCOVERY ->
              ReverseRouter.redirect(on(StartUpdateDiscoverySubmissionController.class).getStartUpdateDiscoveryPage());
          case RELINQUISHMENT_PROSPECT, RELINQUISHMENT_DISCOVERY ->
              ReverseRouter.redirect(on(StartRelinquishmentSubmissionController.class).getStartRelinquishmentPage());
        });
  }

  ModelAndView getSubmissionTypeModelAndViewForUser(SubmissionTypeSelectForm submissionTypeSelectForm) {
    return new ModelAndView("remi/submissiondetails/startSubmission")
        .addObject("submissionType", submissionCreateService.getSubmissionTypeOptionsForUser())
        .addObject("form", submissionTypeSelectForm)
        .addObject("actionUrl", ReverseRouter.route(on(StartSubmissionController.class).startSubmission(null, null)))
        .addObject("backLinkUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea()));
  }
}
