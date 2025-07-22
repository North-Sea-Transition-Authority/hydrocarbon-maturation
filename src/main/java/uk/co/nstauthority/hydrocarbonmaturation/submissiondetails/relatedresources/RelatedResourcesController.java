package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsRegulatorInResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/related-prospects")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.CREATE_RESOURCE_SUBMISSIONS)
@IsRegulatorInResponsibleTeam
public class RelatedResourcesController {
  private final SubmissionDetailService submissionDetailService;
  private final RelatedResourcesFormService relatedResourcesFormService;
  private final RelatedResourcesPersistenceService relatedResourcesPersistenceService;
  private final RelatedResourcesFormValidator relatedResourcesFormValidator;

  @Autowired
  public RelatedResourcesController(SubmissionDetailService submissionDetailService,
                                    RelatedResourcesFormService relatedResourcesFormService,
                                    RelatedResourcesPersistenceService relatedResourcesPersistenceService,
                                    RelatedResourcesFormValidator relatedResourcesFormValidator) {
    this.submissionDetailService = submissionDetailService;
    this.relatedResourcesFormService = relatedResourcesFormService;
    this.relatedResourcesPersistenceService = relatedResourcesPersistenceService;
    this.relatedResourcesFormValidator = relatedResourcesFormValidator;
  }

  @GetMapping
  public ModelAndView getRelatedResources(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                          @ModelAttribute("form") RelatedResourcesForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var relatedProspectsForm = relatedResourcesFormService.getRelatedResourcesForm(submissionDetail);
    return getRelatedResourcesModelAndView(relatedProspectsForm, submissionDetail);
  }

  @PostMapping
  public ModelAndView saveRelatedResources(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                           @ModelAttribute("form") RelatedResourcesForm form,
                                           BindingResult bindingResult,
                                           ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (!relatedResourcesFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      return getRelatedResourcesModelAndView(form, submissionDetail);
    }
    relatedResourcesPersistenceService.saveRelatedResources(form, submissionDetail);
    return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId, null));
  }

  private ModelAndView getRelatedResourcesModelAndView(RelatedResourcesForm form, SubmissionDetail submissionDetail) {
    var pageName = getPageName(submissionDetail);
    var modelAndView = new ModelAndView("remi/submissiondetails/relatedResources")
        .addObject("form", form)
        .addObject("relatedSubmissionDetail", relatedResourcesFormService.getPreSelectedSubmissionDetail(
            form))
        .addObject("resourceType",
            submissionDetail.getSubmission().getResource().getType().getDisplayName().toLowerCase())
        .addObject("pageTitle", pageName);

    if (submissionDetail.isProspectSubmission()) {
      modelAndView.addObject("resourceGroupsEndpoint",
          SearchSelectorService.route(on(ResourceGroupRestController.class)
              .searchProspects(null, submissionDetail.getSubmission().getResource().getId())));
    } else if (submissionDetail.isDiscoverySubmission()) {
      modelAndView.addObject("resourceGroupsEndpoint",
          SearchSelectorService.route(on(ResourceGroupRestController.class)
              .searchDiscoveries(null, submissionDetail.getSubmission().getResource().getId())));
    }

    return TaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(modelAndView, pageName, submissionDetail);
  }

  private String getPageName(SubmissionDetail submissionDetail) {
    if (submissionDetail.isDiscoverySubmission()) {
      return "Related discoveries";
    } else {
      return "Related prospects";
    }
  }
}
