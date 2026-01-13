package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import jakarta.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;

@Service
public class ResourceGroupQueryService {
  public static final String GROUP_PREFIX = "(GROUP) ";
  public static final String ACTIVE_RESOURCE_STRING = " (active)";
  private final ResourceGroupLinksRepository resourceGroupLinksRepository;
  private final SubmissionDetailService submissionDetailService;
  private final SubmissionResourceService submissionResourceService;

  @Autowired
  ResourceGroupQueryService(ResourceGroupLinksRepository resourceGroupLinksRepository,
                            SubmissionDetailService submissionDetailService,
                            SubmissionResourceService submissionResourceService) {
    this.resourceGroupLinksRepository = resourceGroupLinksRepository;
    this.submissionDetailService = submissionDetailService;
    this.submissionResourceService = submissionResourceService;
  }

  List<ResourceGroupView> getProspectsAndGroupsByName(String searchTerm, UUID currentResourceId) {
    return submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm)
        .stream()
        .map(submissionDetailService::getLatestApprovedProspectSubmissionForResource)
        .flatMap(Optional::stream)
        .map(submissionDetail -> getResourceGroupViewForSubmissionDetail(submissionDetail, currentResourceId))
        .flatMap(Optional::stream)
        .sorted(Comparator.comparing(ResourceGroupView::getSelectionText))
        .toList();
  }

  List<ResourceGroupView> getDiscoveriesAndGroupsByName(String searchTerm, UUID currentResourceId) {
    return submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm)
        .stream()
        .map(submissionDetailService::getLatestApprovedDiscoverySubmissionDetailForResource)
        .flatMap(Optional::stream)
        .map(submissionDetail -> getResourceGroupViewForSubmissionDetail(submissionDetail, currentResourceId))
        .flatMap(Optional::stream)
        .sorted(Comparator.comparing(ResourceGroupView::getSelectionText))
        .toList();
  }

  public Optional<ResourceGroupView> getResourceGroupViewForSubmissionDetail(SubmissionDetail submissionDetail,
                                                                             @Nullable UUID currentResourceId) {
    var isActiveResourceString = BooleanUtils.isTrue(submissionDetail.getSubmission().getResource().getActive())
        ? ACTIVE_RESOURCE_STRING : "";
    var resourceNameCentreOfInterestAndRoundReference = submissionResourceService
        .getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(submissionDetail) + isActiveResourceString;

    var resource = submissionDetail.getSubmission().getResource();
    var resourceGroupLink = resourceGroupLinksRepository.findByResource(resource);

    var isInGroupWithOtherResources = resourceIsInGroupWithOtherResources(resourceGroupLink);

    if (isInGroupWithOtherResources) {
      var resourceGroupView = new ResourceGroupView(
          submissionDetail.getId(),
          GROUP_PREFIX + resourceNameCentreOfInterestAndRoundReference,
          resourceGroupLink.getResourceGroup().getId()
      );
      return Optional.of(resourceGroupView);
    }

    if (!resource.getId().equals(currentResourceId)) {
      //filters out the current resource so the current resource won't be able to have a group with itself.
      var resourceGroupView = new ResourceGroupView(
          submissionDetail.getId(),
          resourceNameCentreOfInterestAndRoundReference,
          null
      );
      return Optional.of(resourceGroupView);
    }

    return Optional.empty();
  }

  public List<Resource> getResourcesInGroup(ResourceGroup resourceGroup) {
    return resourceGroupLinksRepository.findByResourceGroup(resourceGroup)
            .stream()
            .map(ResourceGroupLink::getResource)
            .toList();
  }

  public Optional<Resource> getActiveResourceForResourceList(List<Resource> resources) {
    return resources.stream()
        .filter(res -> BooleanUtils.isTrue(res.getActive()))
        .findFirst();
  }

  public Optional<Resource> getActiveResourceInGroup(ResourceGroup resourceGroup) {
    return getResourcesInGroup(resourceGroup)
        .stream()
        .filter(res -> BooleanUtils.isTrue(res.getActive()))
        .findFirst();
  }

  public boolean resourceIsInGroupWithOtherResources(ResourceGroupLink resourceGroupLink) {
    var resourcesInGroup = getResourcesInGroup(resourceGroupLink.getResourceGroup());
    return resourcesInGroup.size() > 1;
  }
}
