package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;

@Service
public class SubmissionResourceService {
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  @Autowired
  public SubmissionResourceService(GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                   TrapPropertiesPersistenceService trapPropertiesPersistenceService) {
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
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
    return generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)
        .map(GeneralDetail::getResourceName);
  }

  public String getResourceNameOrDefault(SubmissionDetail submissionDetail) {
    return getResourceName(submissionDetail)
        .orElseGet(() -> SubmissionDetailUtil.getDefaultForNamelessResource(submissionDetail.getCreatedTimestamp()));
  }

  public String getResourceDisplayName(GeneralDetail generalDetail) {
    var centreOfInterestBlockSuffix = trapPropertiesPersistenceService
        .getCentreOfInterestBlockFromSubmissionDetail(generalDetail.getSubmissionDetail())
        .map(item -> " (" + item + ")")
        .orElse("");
    return generalDetail.getResourceName() + centreOfInterestBlockSuffix;
  }

  public String getResourceDisplayName(UUID generalDetailId) {
    var generalDetail = generalDetailsPersistenceService.getGeneralDetailByIdOrThrow(generalDetailId);
    return getResourceDisplayName(generalDetail);
  }

  private Stream<Resource> getResourcesByNameStream(String name) {
    return generalDetailsPersistenceService.getGeneralDetailsByName(name).stream()
        .map(detail -> detail.getSubmissionDetail().getResourceId())
        .distinct();
  }
}