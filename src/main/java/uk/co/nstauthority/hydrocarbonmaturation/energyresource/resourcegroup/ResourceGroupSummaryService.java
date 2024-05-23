package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class ResourceGroupSummaryService {

  private final ResourceGroupService resourceGroupService;
  private final SubmissionResourceService submissionResourceService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  public ResourceGroupSummaryService(ResourceGroupService resourceGroupService,
                                     SubmissionResourceService submissionResourceService,
                                     SubmissionDetailService submissionDetailService) {
    this.resourceGroupService = resourceGroupService;
    this.submissionResourceService = submissionResourceService;
    this.submissionDetailService = submissionDetailService;
  }

  public ResourceGroupSummaryView getResourceGroupLinkSummaryFromResource(Resource resource) {
    var resourceGroup = resourceGroupService.getResourceGroupLinkByResource(resource);
    return resourceGroup.map(resourceGroupLink -> createSummaryViewFromResourceGroupLink(resourceGroupLink, resource))
        .orElse(ResourceGroupSummaryView.empty(resource.getType()));
  }

  List<ResourceItemSummaryView> getResourceGroupMembersSummaryWithoutCurrentResource(Resource resource) {
    var resourceItemListOpt = resourceGroupService.getResourceGroupLinkByResource(resource);
    if (resourceItemListOpt.isEmpty()) {
      return List.of();
    }
    var listOfLinks = resourceGroupService.getResourceGroupLinksByResourceGroupId(
            resourceItemListOpt.get().getResourceGroupId())
        .stream()
        .filter(resourceGroupLink -> !resourceGroupLink.getResource().getId().equals(resource.getId()))
        .toList();
    return getListOfResourcesInGroupByResourceGroupLinks(listOfLinks, resource);
  }

  private ResourceGroupSummaryView createSummaryViewFromResourceGroupLink(ResourceGroupLink resourceGroupLink,
                                                                          Resource resource) {
    var listOfLinks = resourceGroupService.getResourceGroupLinksByResourceGroupId(
        resourceGroupLink.getResourceGroupId());

    return new ResourceGroupSummaryView(
        getListOfResourcesInGroupByResourceGroupLinks(listOfLinks, resource),
        resource.getType(),
        BooleanUtil.yesNoFromBoolean(resourceGroupLink.getPreferredView(), "")
    );
  }

  private List<ResourceItemSummaryView> getListOfResourcesInGroupByResourceGroupLinks(
      List<ResourceGroupLink> resourceLinks, Resource resource) {
    return resourceLinks.stream()
        .sorted(Comparator.comparing((ResourceGroupLink resourceLink) ->
                resourceLink.getResource().getCreatedTimestamp())
            .reversed())
        .map(resourceGroupLink -> getResourceItemFromResourceGroupLink(resourceGroupLink, resource))
        .toList();
  }

  private ResourceItemSummaryView getResourceItemFromResourceGroupLink(ResourceGroupLink resourceGroupLink,
                                                                       Resource resource) {
    return new ResourceItemSummaryView(
        ReverseRouter.route(on(ViewResourceRecordController.class)
            .showResourceRecord(resourceGroupLink.getResource().getId())),
        resource.getId().equals(resourceGroupLink.getResource().getId()),
        getResourceName(resourceGroupLink.getResource()),
        BooleanUtils.isTrue(resourceGroupLink.getPreferredView()) ? "(preferred view)" : ""
    );
  }

  private String getResourceName(Resource resource) {
    return submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(
        getLatestSubmissionDetailOrThrow(resource));
  }

  private SubmissionDetail getLatestSubmissionDetailOrThrow(Resource resource) {
    if (resource.getType() == ResourceType.DISCOVERY) {
      return submissionDetailService.getLatestApprovedSubmissionDetailForResourceAndSubmissionTypes(
          resource,
          List.of(
              SubmissionType.CREATE_DISCOVERY,
              SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
              SubmissionType.UPDATE_DISCOVERY,
              SubmissionType.RELINQUISHMENT_DISCOVERY)
          )
          .orElseThrow(() ->
              new RemiEntityNotFoundException("Could not find latest accepted submission for resource %s"
                  .formatted(resource.getId())));
    } else {
      return submissionDetailService.getLatestApprovedSubmissionDetailForResourceAndSubmissionTypes(
          resource,
          List.of(
              SubmissionType.PROSPECT_UPDATE,
              SubmissionType.CREATE_PROSPECT,
              SubmissionType.RELINQUISHMENT_PROSPECT)
          )
          .orElseThrow(() ->
              new RemiEntityNotFoundException("Could not find latest accepted submission for resource %s"
                  .formatted(resource.getId())));
    }
  }
}
