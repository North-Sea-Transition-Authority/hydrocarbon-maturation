package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;

@Service
public class SubmissionResourceService {
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  @Autowired
  public SubmissionResourceService(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                   TrapPropertiesPersistenceService trapPropertiesPersistenceService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
  }

  public List<Resource> getResourcesBySubmissionTypeAndResourceName(ResourceType resourceType, String name) {
    return getResourcesByNameStream(name)
        .filter(resource -> resource.getType() == resourceType)
        .toList();
  }

  public List<Resource> getResourcesByName(String name) {
    return getResourcesByNameStream(name).toList();
  }

  public Optional<String> getResourceName(SubmissionDetail submissionDetail) {
    return resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail)
        .map(ResourceDetail::getResourceName);
  }

  public String getResourceNameOrDefault(SubmissionDetail submissionDetail) {
    return getResourceName(submissionDetail)
        .orElseGet(() -> SubmissionDetailUtil.getDefaultForNamelessResource(submissionDetail.getCreatedTimestamp()));
  }

  public String getResourceNameWithCentreOfInterestOrDefault(SubmissionDetail submissionDetail) {
    return getResourceName(submissionDetail)
        .map(resourceName ->
          trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(submissionDetail)
              .map(centreOfInterestBlock ->
                  resourceName + " (" + centreOfInterestBlock + ")")
              .orElse(resourceName))
        .orElseGet(() -> SubmissionDetailUtil.getDefaultForNamelessResource(submissionDetail.getCreatedTimestamp()));
  }

  public String getResourceDisplayName(ResourceDetail resourceDetail) {
    var centreOfInterestBlockSuffix = trapPropertiesPersistenceService
        .getCentreOfInterestBlockFromSubmissionDetail(resourceDetail.getSubmissionDetail())
        .map(item -> " (" + item + ")")
        .orElse("");
    return resourceDetail.getResourceName() + centreOfInterestBlockSuffix;
  }

  public String getResourceDisplayName(UUID resourceDetailId) {
    var resourceDetail = resourceDetailsPersistenceService.getResourceDetailByIdOrThrow(resourceDetailId);
    return getResourceDisplayName(resourceDetail);
  }

  private Stream<Resource> getResourcesByNameStream(String name) {
    return resourceDetailsPersistenceService.getResourceDetailsByName(name).stream()
        .map(detail -> detail.getSubmissionDetail().getSubmission().getResource())
        .distinct();
  }
}