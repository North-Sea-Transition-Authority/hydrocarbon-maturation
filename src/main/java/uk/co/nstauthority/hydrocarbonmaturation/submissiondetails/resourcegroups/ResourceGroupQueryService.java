package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;

@Service
class ResourceGroupQueryService {
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

  public List<ResourceGroupView> getProspectsAndGroupsByName(String searchTerm) {
    return submissionResourceService.getResourcesBySubmissionTypeAndResourceName(ResourceType.PROSPECT, searchTerm).stream()
        .map(submissionDetailService::getLatestApprovedProspectSubmissionForResource)
        .flatMap(Optional::stream)
        .map(this::getResourceGroupViewForSumbissionDetail)
        .flatMap(Optional::stream)
        .toList();
  }

  public Optional<ResourceGroupView> getResourceGroupViewForSumbissionDetail(SubmissionDetail submissionDetail) {
    var resourceName = submissionResourceService.getResourceNameOrDefault(submissionDetail);

    var resourceGroupLink = resourceGroupLinksRepository.findByResource(submissionDetail.getResourceId());

    var isInGroup = resourceGroupLink.isPresent();

    if (isInGroup && resourceGroupLink.get().getPreferredView()) {
      var resourceGroupView = new ResourceGroupView(
          submissionDetail.getId(),
          GROUP_PREFIX + resourceName,
          resourceGroupLink.get().getResourceGroupId()
      );
      return Optional.of(resourceGroupView);
    }

    if (!isInGroup) {
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
