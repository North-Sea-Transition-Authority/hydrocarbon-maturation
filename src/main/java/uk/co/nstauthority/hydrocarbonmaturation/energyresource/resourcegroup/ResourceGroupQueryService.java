package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;

@Service
public class ResourceGroupQueryService {
  public static final String GROUP_PREFIX = "(GROUP) ";
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
        .toList();
  }

  List<ResourceGroupView> getDiscoveriesAndGroupsByName(String searchTerm, UUID currentResourceId) {
    return submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.DISCOVERY, searchTerm)
        .stream()
        .map(submissionDetailService::getLatestApprovedDiscoverySubmissionDetailForResource)
        .flatMap(Optional::stream)
        .map(submissionDetail -> getResourceGroupViewForSubmissionDetail(submissionDetail, currentResourceId))
        .flatMap(Optional::stream)
        .toList();
  }

  public Optional<ResourceGroupView> getResourceGroupViewForSubmissionDetail(SubmissionDetail submissionDetail,
                                                                             @Nullable UUID currentResourceId) {
    var resourceName = submissionResourceService.getResourceNameOrDefault(submissionDetail);

    var resourceGroupLink = resourceGroupLinksRepository.findByResource(
        submissionDetail.getSubmission().getResource());

    var isInGroup = resourceGroupLink.isPresent();

    if (isInGroup && resourceGroupLink.get().getPreferredView()) {
      var resourceGroupView = new ResourceGroupView(
          submissionDetail.getId(),
          GROUP_PREFIX + resourceName,
          resourceGroupLink.get().getResourceGroupId()
      );
      return Optional.of(resourceGroupView);
    }

    if (!isInGroup && !submissionDetail.getSubmission().getResource().getId().equals(currentResourceId)) {
      //filters out the current resource so the current resource won't be able to have a group with itself.
      var resourceGroupView = new ResourceGroupView(
          submissionDetail.getId(),
          resourceName,
          null
      );
      return Optional.of(resourceGroupView);
    }

    return Optional.empty();
  }
}
