package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistoryQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistorySelectorUtil;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/correction")
@HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
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
      return correctionService.startCorrectionForTypeAndRedirect(form, user, resource);
    }
  }

  private ModelAndView getCorrectionModelAndView(CorrectionForm form, Resource resource) {
    var modelAndView = new ModelAndView("remi/systemofrecord/corrections/corrections");
    var approvedSubmissions = submissionHistoryQueryService.getAcceptedSubmissionsOnResource(resource.getId());
    var submissionsOnResource = SubmissionHistorySelectorUtil.getSubmissionSelectorItems(
        approvedSubmissions
    );

    Map<String, String> removableSubmissionMap;

    if (approvedSubmissions.size() > 1) {
      var filteredSubmissions = approvedSubmissions.stream()
              .filter(s -> SubmissionDetailUtil.isNotFirstSubmissionOrIsFieldSubmission(s.submissionType()))
              .toList();

      var filteredSubmissionsOnResource = SubmissionHistorySelectorUtil.getSubmissionSelectorItems(
              filteredSubmissions
      );

      removableSubmissionMap = filteredSubmissionsOnResource.entrySet().stream()
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    } else {
      removableSubmissionMap = submissionsOnResource.entrySet().stream().findFirst().stream()
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    var correctionTypes = correctionService.getCorrectionTypes(resource);

    if (resource.getType().equals(ResourceType.FIELD)) {
      var submissionTypes = new ArrayList<>(SubmissionType.getSubmissionTypesForResourceType(ResourceType.FIELD));
      submissionTypes.remove(SubmissionType.CREATE_FIELD);

      modelAndView.addObject("submissionTypes", submissionTypes);
    }

    modelAndView
        .addObject("correctionTypes", correctionTypes)
        .addObject("submissionsOnResource", submissionsOnResource)
        .addObject("removableSubmissions", removableSubmissionMap)
        .addObject("form", form)
        .addObject("cancelUrl",
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resource.getId(), null)));

    return modelAndView;
  }
}