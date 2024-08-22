package uk.co.nstauthority.hydrocarbonmaturation.prospect;

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
import uk.co.nstauthority.hydrocarbonmaturation.discovery.StartPostDrillDiscoveryController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.StartPostDrillDryHoleSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.StartUpdateProspectController;
import uk.co.nstauthority.hydrocarbonmaturation.relinquishment.StartRelinquishmentSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTypeSelectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionTypeSelectService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("/prospect/purpose")
@HasAnyRole(role = Role.INDUSTRY_SUBMIT, teamType = TeamType.INDUSTRY)
@HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
public class ProspectPurposeController {

  private final SubmissionTypeSelectService submissionTypeSelectService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public ProspectPurposeController(SubmissionTypeSelectService submissionTypeSelectService,
                                   ControllerHelperService controllerHelperService) {
    this.submissionTypeSelectService = submissionTypeSelectService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView getProspectPurposeForm(User user) {
    return getProspectPurposeTypeModelAndView(new SubmissionTypeSelectForm(), user);
  }

  @PostMapping
  public ModelAndView startProspectSubmission(@ModelAttribute("form") SubmissionTypeSelectForm form,
                                              BindingResult bindingResult,
                                              User user) {
    bindingResult = submissionTypeSelectService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getProspectPurposeTypeModelAndView(form, user),
        () -> switch (form.getSubmissionType()) {
          case CREATE_PROSPECT -> ReverseRouter.redirect(on(StartProspectController.class).getStartPage());
          case PROSPECT_UPDATE -> ReverseRouter.redirect(on(StartUpdateProspectController.class).getStartUpdatePage());
          case POST_DRILL_DRY_HOLE ->
              ReverseRouter.redirect(on(StartPostDrillDryHoleSubmissionController.class).getStartPostDrillDryHolePage());
          case CREATE_DISCOVERY_FROM_PROSPECT ->
              ReverseRouter.redirect(on(StartPostDrillDiscoveryController.class).getStartPostDrillDiscoveryPage());
          case RELINQUISHMENT_PROSPECT ->
              ReverseRouter.redirect(on(StartRelinquishmentSubmissionController.class).getStartProspectRelinquishmentPage());
          default -> throw new ResponseStatusException(HttpStatus.FORBIDDEN,
              "Invalid submission type selected for prospect only form");
        });
  }

  ModelAndView getProspectPurposeTypeModelAndView(SubmissionTypeSelectForm submissionTypeSelectForm, User user) {
    return new ModelAndView("remi/submissiondetails/submissionPurpose")
        .addObject("submissionTypes", submissionTypeSelectService.getSubmissionTypeOptions(ResourceType.PROSPECT, user))
        .addObject("form", submissionTypeSelectForm)
        .addObject("actionUrl", ReverseRouter.route(on(ProspectPurposeController.class)
            .startProspectSubmission(null, null, null)))
        .addObject("backLinkUrl", ReverseRouter.route(on(StartSubmissionController.class)
            .getSubmissionType(null)));
  }
}
