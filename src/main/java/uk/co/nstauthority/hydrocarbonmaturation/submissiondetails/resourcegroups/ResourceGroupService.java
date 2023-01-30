package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class ResourceGroupService {
  private final RelatedProspectsPersistenceService relatedProspectsPersistenceService;
  private final ResourceGroupLinksRepository resourceGroupLinksRepository;
  private final RelatedProspectsSubmissionService relatedProspectsSubmissionService;

  public ResourceGroupService(RelatedProspectsPersistenceService relatedProspectsPersistenceService,
                              ResourceGroupLinksRepository resourceGroupLinksRepository,
                              RelatedProspectsSubmissionService relatedProspectsSubmissionService) {
    this.relatedProspectsPersistenceService = relatedProspectsPersistenceService;
    this.resourceGroupLinksRepository = resourceGroupLinksRepository;
    this.relatedProspectsSubmissionService = relatedProspectsSubmissionService;
  }

  @Transactional
  public void createResourceGroupLink(SubmissionDetail submissionDetail) {
    if (!relatedProspectsSubmissionService.isSectionSubmittable(submissionDetail)) {
      return;
    }

    relatedProspectsPersistenceService.getRelatedProspectIfRelatedToAnotherProspect(submissionDetail)
        .ifPresent(relatedProspect -> {

          var relatedProspectResource = relatedProspect.getRelatedSubmissionDetail().getResourceId();
          var currentProspectResource = submissionDetail.getResourceId();

          var resourceGroupLinkOptional = resourceGroupLinksRepository.findByResource(relatedProspectResource);

          var relatedProspectIsInGroup = resourceGroupLinkOptional.isPresent();
          if (relatedProspectIsInGroup) {
            addToGroup(relatedProspect, currentProspectResource, resourceGroupLinkOptional.get());
          } else {
            createNewGroup(relatedProspect, relatedProspectResource, currentProspectResource);
          }
        });
  }


  private void addToGroup(RelatedProspect relatedProspect,
                          Resource currentProspectResource,
                          ResourceGroupLink resourceGroupLinkForRelatedProspect) {
    var resourceGroupId = resourceGroupLinkForRelatedProspect.getResourceGroupId();

    // create the resource group link
    var resourceGroupLinkForCurrentProspect = buildResourceGroupLink(
        resourceGroupId,
        currentProspectResource
    );
    resourceGroupLinksRepository.save(resourceGroupLinkForCurrentProspect);

    if (BooleanUtils.isTrue(relatedProspect.getPreferredView())) {
      setCurrentlyPreferredViewAsNotPreferred(resourceGroupLinkForRelatedProspect, resourceGroupId);

      resourceGroupLinkForCurrentProspect.setPreferredView(true);
    }
  }

  private void setCurrentlyPreferredViewAsNotPreferred(ResourceGroupLink resourceGroupLinkForRelatedProspect,
                                                       UUID resourceGroupId) {
    if (resourceGroupLinkForRelatedProspect.getPreferredView()) {
      resourceGroupLinkForRelatedProspect.setPreferredView(false);
    } else {
      var resourceGroupLink = getPreferredViewResourceGroupLinkByGroupIdOrThrow(resourceGroupId);
      resourceGroupLink.setPreferredView(false);
    }
  }

  private ResourceGroupLink getPreferredViewResourceGroupLinkByGroupIdOrThrow(UUID resourceGroupId) {
    return resourceGroupLinksRepository.findByResourceGroupIdAndPreferredViewIsTrue(resourceGroupId)
        .orElseThrow(() -> new IllegalStateException(
            "There should be at least one ResourceGroupLink with a preferred view for resource group id %s"
                .formatted(resourceGroupId)));
  }

  private void createNewGroup(RelatedProspect relatedProspect,
                              Resource relatedProspectResource,
                              Resource currentProspectResource) {
    var resourceGroupId = UUID.randomUUID();

    var resourceGroupLinkForCurrentProspect = buildResourceGroupLink(resourceGroupId, currentProspectResource);

    var resourceGroupLinkForRelatedProspect = buildResourceGroupLink(resourceGroupId, relatedProspectResource);

    if (BooleanUtils.isTrue(relatedProspect.getPreferredView())) {
      resourceGroupLinkForCurrentProspect.setPreferredView(true);
    } else {
      resourceGroupLinkForRelatedProspect.setPreferredView(true);
    }

    resourceGroupLinksRepository.saveAll(List.of(resourceGroupLinkForCurrentProspect, resourceGroupLinkForRelatedProspect));
  }

  static ResourceGroupLink buildResourceGroupLink(UUID resourceGroupId, Resource resource) {
    var resourceGroupLinkForRelated = new ResourceGroupLink();
    resourceGroupLinkForRelated.setResourceGroupId(resourceGroupId);
    resourceGroupLinkForRelated.setResource(resource);
    resourceGroupLinkForRelated.setPreferredView(false);
    return resourceGroupLinkForRelated;
  }
}
