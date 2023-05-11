package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesFormService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchResourceRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/{resourceId}/resource-group")
@HasPermission(permissions = RolePermission.EXPLORATION)
public class ResourceGroupController {
  private final ControllerHelperService controllerHelperService;
  private final ResourceService resourceService;
  private final SubmissionDetailService submissionDetailService;
  private final SubmissionResourceService submissionResourceService;
  private final ResourceGroupSummaryService resourceGroupSummaryService;
  private final ManageResourceGroupsService manageResourceGroupsService;
  private final RelatedResourcesFormService relatedResourcesFormService;

  @Autowired
  ResourceGroupController(ControllerHelperService controllerHelperService,
                          ResourceService resourceService,
                          SubmissionDetailService submissionDetailService,
                          SubmissionResourceService submissionResourceService,
                          ResourceGroupSummaryService resourceGroupSummaryService,
                          ManageResourceGroupsService manageResourceGroupsService,
                          RelatedResourcesFormService relatedResourcesFormService) {
    this.controllerHelperService = controllerHelperService;
    this.resourceService = resourceService;
    this.submissionDetailService = submissionDetailService;
    this.submissionResourceService = submissionResourceService;
    this.resourceGroupSummaryService = resourceGroupSummaryService;
    this.manageResourceGroupsService = manageResourceGroupsService;
    this.relatedResourcesFormService = relatedResourcesFormService;
  }

  @GetMapping
  public ModelAndView getResourceGroupPage(@PathVariable("resourceId") UUID resourceId,
                                           @ModelAttribute("form") RelatedResourcesForm form) {
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() -> new RemiEntityNotFoundException("Could not find resource %s"
            .formatted(resourceId)));
    var relatedProspectsForm = manageResourceGroupsService.getRelatedResourcesFormFromResource(resource);
    return getResourceGroupsModelAndView(relatedProspectsForm, resource);
  }

  @PostMapping
  public ModelAndView saveResourceGroup(@PathVariable("resourceId") UUID resourceId,
                                        @ModelAttribute("form") RelatedResourcesForm form,
                                        BindingResult bindingResult) {
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() -> new RemiEntityNotFoundException("Could not find resource %s"
            .formatted(resourceId)));

    bindingResult = manageResourceGroupsService.validateForResource(form, bindingResult, resource);
    return controllerHelperService.checkErrorsAndRedirect(bindingResult,
        getResourceGroupsModelAndView(form, resource),
        () -> {
          manageResourceGroupsService.updateResourceGroupLinkFromResource(resource, form);
          return ReverseRouter.redirect(on(ViewResourceRecordController.class).showResourceRecord(resourceId));
        });
  }

  private ModelAndView getResourceGroupsModelAndView(RelatedResourcesForm form, Resource resource) {
    var pageName = getPageName(resource);
    var resourceName = submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(
        submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource)
            .orElseThrow(() -> new RemiEntityNotFoundException("Could not find the latest approved submission for resource %s"
                .formatted(resource.getId()))));
    var modelAndView = new ModelAndView("remi/review/manageResourceGroups")
        .addObject("form", form)
        .addObject("relatedSubmissionDetail", relatedResourcesFormService.getPreSelectedSubmissionDetail(
            form))
        .addObject("resourceType", resource.getType().getDisplayName().toLowerCase())
        .addObject("resourceTypePlural", resource.getType().getPluralName().toLowerCase())
        .addObject("resourcesInGroup",
            resourceGroupSummaryService.getResourceGroupMembersSummaryWithoutCurrentResource(resource))
        .addObject("cancelUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
            .showResourceRecord(resource.getId())))
        .addObject("pageTitle", pageName);

    if (resource.getType() == ResourceType.PROSPECT) {
      modelAndView.addObject("resourceGroupsEndpoint",
          SearchSelectorService.route(on(ResourceGroupRestController.class)
              .searchProspects(null, resource.getId())));
    } else if (resource.getType() == ResourceType.DISCOVERY) {
      modelAndView.addObject("resourceGroupsEndpoint",
          SearchSelectorService.route(on(ResourceGroupRestController.class)
              .searchDiscoveries(null, resource.getId())));
    }

    var cancelUrl = ReverseRouter.route(on(ViewResourceRecordController.class)
        .showResourceRecord(resource.getId()));
    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(pageName)
        .addBreadcrumb(
            SearchResourceRecordsController.PAGE_TITLE,
            ReverseRouter.route(on(SearchResourceRecordsController.class).searchResourceRecords(null, null)))
        .addBreadcrumb(resourceName, cancelUrl)
        .build();
    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
    return modelAndView;
  }

  private String getPageName(Resource resource) {
    return "Related %s".formatted(resource.getType().getPluralName().toLowerCase());
  }
}
