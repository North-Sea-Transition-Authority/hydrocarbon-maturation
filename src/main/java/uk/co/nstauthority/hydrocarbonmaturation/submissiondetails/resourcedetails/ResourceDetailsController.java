package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;


import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/resource-details")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.FIELD_SUBMISSIONS, mode = HasSubmissionType.Mode.EXCLUDE)
@IsFromResponsibleTeam
public class ResourceDetailsController {

  public static final String PAGE_NAME = "Resource details";

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final ResourceDetailsFormService resourceDetailsFormService;
  private final SubmissionDetailService submissionDetailService;
  private final ResourceDetailsFormValidator resourceDetailsFormValidator;
  private final InternalTeamService internalTeamService;

  @Autowired
  public ResourceDetailsController(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                   ResourceDetailsFormService resourceDetailsFormService,
                                   SubmissionDetailService submissionDetailService,
                                   ResourceDetailsFormValidator resourceDetailsFormValidator,
                                   InternalTeamService internalTeamService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.resourceDetailsFormService = resourceDetailsFormService;
    this.submissionDetailService = submissionDetailService;
    this.resourceDetailsFormValidator = resourceDetailsFormValidator;
    this.internalTeamService = internalTeamService;
  }

  @GetMapping
  public ModelAndView getResourceDetails(@PathVariable("submissionDetailId") UUID submissionDetailId, User user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var form = resourceDetailsFormService.getResourceDetailsForm(submissionDetail);
    return getResourceDetailsPageModelAndView(form, submissionDetail, user);
  }

  @PostMapping
  public ModelAndView saveResourceDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                          @ModelAttribute("form") ResourceDetailsForm form,
                                          BindingResult bindingResult,
                                          ValidationType validationType,
                                          User user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    if (resourceDetailsFormValidator.isValid(bindingResult, submissionDetail, validationType, user)) {
      resourceDetailsPersistenceService.saveResourceDetails(form, submissionDetail);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId, null));
    } else {
      return getResourceDetailsPageModelAndView(form, submissionDetail, user);
    }
  }

  private ModelAndView getResourceDetailsPageModelAndView(ResourceDetailsForm form,
                                                          SubmissionDetail submissionDetail,
                                                          User user) {
    var modelAndView = new ModelAndView("remi/submissiondetails/resourceDetails")
        .addObject("organisationUnitEndpoint",
            SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null, null)))
        .addObject("resourceType",
            submissionDetail.getSubmission().getResource().getType().getDisplayName().toLowerCase())
        .addObject("responsibleOrganisation", resourceDetailsFormService
            .getPreSelectedOrganisationUnit(form.getResponsibleOrganisationId()))
        .addObject("form", form)
        .addObject("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore())
        .addObject("isRegulatorOrAreaTeamUser", internalTeamService.isRegulatorOrAreaTeamUser(user))
        .addObject("relinquishmentSubmission",
            submissionDetail.isDiscoveryRelinquishment()
                || submissionDetail.isProspectRelinquishment());

    var showHistoricNameInfoModelObject = "showHistoricNameInfo";
    var noOrganisationAddedModelObject = "noOrganisationAdded";
    var lockedOrganisationNameModelObject = "lockedOrganisationName";

    modelAndView
        .addObject(showHistoricNameInfoModelObject, false)
        .addObject(noOrganisationAddedModelObject, false)
        .addObject(lockedOrganisationNameModelObject, "");

    if (BooleanUtils.isTrue(submissionDetail.isCorrection())) {
      modelAndView.addObject(showHistoricNameInfoModelObject, true);
      var resourceDetailOpt = resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail);
      if (resourceDetailOpt.isPresent()) {
        var resourceDetail = resourceDetailOpt.get();
        var currentOrganisationName = resourceDetailsPersistenceService.getOrganisationNameFromPortal(resourceDetail);
        if (currentOrganisationName.isBlank()) {
          modelAndView.addObject(noOrganisationAddedModelObject, true);
        } else {
          var historicOrganisationName =
              resourceDetailsPersistenceService.getOrganisationDisplayFromResourceDetails(resourceDetail);
          if (!currentOrganisationName.equals(historicOrganisationName)) {
            modelAndView.addObject(lockedOrganisationNameModelObject, historicOrganisationName);
          } else {
            modelAndView.addObject(lockedOrganisationNameModelObject, currentOrganisationName);
          }
        }

      }
    }

    return TaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
  }
}
