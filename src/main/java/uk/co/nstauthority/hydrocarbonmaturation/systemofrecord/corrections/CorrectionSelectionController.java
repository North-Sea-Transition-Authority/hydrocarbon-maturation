package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistoryQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistorySelectorUtil;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

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
  private final CorrectionFormValidator correctionFormValidator;
  private final SubmissionHistoryQueryService submissionHistoryQueryService;

  @Autowired
  public CorrectionSelectionController(ResourceService resourceService,
                                       CorrectionService correctionService,
                                       CorrectionFormValidator correctionFormValidator,
                                       SubmissionHistoryQueryService submissionHistoryQueryService) {
    this.resourceService = resourceService;
    this.correctionService = correctionService;
    this.correctionFormValidator = correctionFormValidator;
    this.submissionHistoryQueryService = submissionHistoryQueryService;
  }

  @GetMapping
  public ModelAndView startCorrection(@PathVariable("resourceId") UUID resourceId, User user) {
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() -> new RemiEntityNotFoundException("Could not find resource for id %s"
            .formatted(resourceId)));

    if (!correctionService.canStartCorrection(resource, user)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "User with id %s cannot start a correction for resource %s"
              .formatted(user.wuaId(), resourceId)
      );
    }
    return getCorrectionModelAndView(new CorrectionForm(), resource);
  }

  @PostMapping
  public ModelAndView startCorrection(@PathVariable("resourceId") UUID resourceId,
                                      @ModelAttribute("form") CorrectionForm form,
                                      BindingResult bindingResult,
                                      User user) {
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() -> new RemiEntityNotFoundException("Could not find resource for id %s"
            .formatted(resourceId)));

    if (!correctionFormValidator.isValid(form, resource, bindingResult)) {
      return getCorrectionModelAndView(form, resource);
    } else {
      return correctionService.startCorrectionForTypeAndRedirect(form, user);
    }
  }

  private ModelAndView getCorrectionModelAndView(CorrectionForm form, Resource resource) {
    var modelAndView = new ModelAndView("remi/systemofrecord/corrections/corrections");
    var approvedSubmissions = submissionHistoryQueryService.getAcceptedSubmissionsOnResource(resource.getId());
    var submissionsOnResource = SubmissionHistorySelectorUtil.getSubmissionSelectorItems(
        approvedSubmissions
    );
    // Only the latest submission on a resource can be removed. Create a map to select from on the correction screen.
    var removableSubmissionMap = submissionsOnResource.entrySet().stream().findFirst().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    var correctionTypes = correctionService.getCorrectionTypes(resource);

    modelAndView
        .addObject("correctionTypes", correctionTypes)
        .addObject("submissionsOnResource", submissionsOnResource)
        .addObject("removableSubmissions", removableSubmissionMap)
        .addObject("form", form)
        .addObject("cancelUrl",
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId()))
        );

    return modelAndView;
  }
}