package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistoryQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistorySelectorUtil;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/correction")
@HasPermission(permissions = {
    RolePermission.EXPLORATION,
    RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
    RolePermission.ONSHORE_AREA_TEAM_MEMBER})
public class CorrectionSelectionController {

  private final ResourceService resourceService;
  private final CorrectionService correctionService;
  private final UserDetailService userDetailService;
  private final CorrectionFormValidator correctionFormValidator;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionHistoryQueryService submissionHistoryQueryService;

  @Autowired
  public CorrectionSelectionController(ResourceService resourceService,
                                       CorrectionService correctionService,
                                       UserDetailService userDetailService,
                                       CorrectionFormValidator correctionFormValidator,
                                       ControllerHelperService controllerHelperService,
                                       SubmissionHistoryQueryService submissionHistoryQueryService) {
    this.resourceService = resourceService;
    this.correctionService = correctionService;
    this.userDetailService = userDetailService;
    this.correctionFormValidator = correctionFormValidator;
    this.controllerHelperService = controllerHelperService;
    this.submissionHistoryQueryService = submissionHistoryQueryService;
  }

  @GetMapping
  public ModelAndView startCorrection(@PathVariable("resourceId") UUID resourceId) {
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() -> new RemiEntityNotFoundException("Could not find resource for id %s"
            .formatted(resourceId)));

    if (!correctionService.canStartCorrection(resource)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "User with id %s cannot start a correction for resource %s"
              .formatted(userDetailService.getUserDetail().wuaId(), resourceId)
      );
    }
    return getCorrectionModelAndView(new CorrectionForm(), resourceId);
  }

  @PostMapping
  public ModelAndView startCorrection(@PathVariable("resourceId") UUID resourceId,
                                      @ModelAttribute("form") CorrectionForm form,
                                      BindingResult bindingResult) {
    bindingResult = correctionFormValidator.validateForm(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getCorrectionModelAndView(form, resourceId),
        () -> ReverseRouter.redirect(on(ViewResourceRecordController.class).showResourceRecord(resourceId))
    );
  }

  private ModelAndView getCorrectionModelAndView(CorrectionForm form, UUID resourceId) {
    var modelAndView = new ModelAndView("remi/systemofrecord/corrections/corrections");
    var submissionsOnResource = SubmissionHistorySelectorUtil.getSubmissionSelectorItems(
        submissionHistoryQueryService.getApprovedSubmissionsOnResource(resourceId)
    );

    modelAndView
        .addObject("correctionTypes", CorrectionType.getCorrectionTypes())
        .addObject("submissionsOnResource", submissionsOnResource)
        .addObject("form", form)
        .addObject("cancelUrl",
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId))
        );

    return modelAndView;
  }
}
