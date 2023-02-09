package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;


import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/resource-details")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@IsFromResponsibleTeam
public class ResourceDetailsController {

  public static final String PAGE_NAME = "Resource details";

  private final ControllerHelperService controllerHelperService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final ResourceDetailsFormService resourceDetailsFormService;
  private final UserDetailService userDetailService;
  private final RegulatorTeamService regulatorTeamService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  public ResourceDetailsController(ControllerHelperService controllerHelperService,
                                   ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                   ResourceDetailsFormService resourceDetailsFormService,
                                   UserDetailService userDetailService, RegulatorTeamService regulatorTeamService,
                                   SubmissionDetailService submissionDetailService) {
    this.controllerHelperService = controllerHelperService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.resourceDetailsFormService = resourceDetailsFormService;
    this.userDetailService = userDetailService;
    this.regulatorTeamService = regulatorTeamService;
    this.submissionDetailService = submissionDetailService;
  }

  @GetMapping
  public ModelAndView getResourceDetails(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var form = resourceDetailsFormService.getResourceDetailsForm(submissionDetail);
    return getResourceDetailsPageModelAndView(form, submissionDetail);
  }

  @PostMapping
  public ModelAndView saveResourceDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                          @ModelAttribute("form") ResourceDetailsForm form,
                                          BindingResult bindingResult, ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = resourceDetailsFormService.validate(form, bindingResult, validationType, submissionDetail);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getResourceDetailsPageModelAndView(form, submissionDetail),
        () -> {
          resourceDetailsPersistenceService.saveResourceDetails(form, submissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        });
  }

  private ModelAndView getResourceDetailsPageModelAndView(ResourceDetailsForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/resourceDetails")
        .addObject("organisationUnitEndpoint",
            SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null)))
        .addObject("resourceType",
            submissionDetail.getResourceId().getType().getDisplayName().toLowerCase())
        .addObject("responsibleOrganisation", resourceDetailsFormService
            .getPreSelectedOrganisationUnit(form.getResponsibleOrganisationId()))
        .addObject("form", form)
        .addObject("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore())
        .addObject("isRegulator",
            regulatorTeamService.isRegulatorUser(userDetailService.getUserDetail()))
        .addObject("relinquishmentSubmission",
            submissionDetail.isDiscoveryRelinquishment()
                || submissionDetail.isProspectRelinquishment());

    return TaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
  }
}
