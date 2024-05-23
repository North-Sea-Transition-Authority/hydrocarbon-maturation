package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;

@Service
public class SubmissionResourceService {
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;
  private final FieldPersistenceService fieldPersistenceService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Autowired
  public SubmissionResourceService(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                   TrapPropertiesPersistenceService trapPropertiesPersistenceService,
                                   FieldPersistenceService fieldPersistenceService,
                                   GeneralDetailsPersistenceService generalDetailsPersistenceService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
    this.fieldPersistenceService = fieldPersistenceService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
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
    if (submissionDetail.isFieldSubmission()) {
      return Optional.of(fieldPersistenceService.getFieldOrThrow(submissionDetail.getSubmission().getResource())
          .getCorrectedLegalFieldName());
    } else {
      return resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail)
          .map(ResourceDetail::getResourceName);
    }
  }

  public String getResourceNameOrDefault(SubmissionDetail submissionDetail) {
    return getResourceName(submissionDetail)
        .orElseGet(() -> SubmissionDetailUtil.getDefaultForNamelessResource(submissionDetail.getCreatedTimestamp()));
  }

  public String getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(SubmissionDetail submissionDetail) {
    return getResourceName(submissionDetail)
        .map(resourceName -> appendCentreOfInterestAndLicenceApplicationReference(resourceName, submissionDetail))
        .orElseGet(() -> SubmissionDetailUtil.getDefaultForNamelessResource(submissionDetail.getCreatedTimestamp()));
  }

  public String getResourceDisplayName(ResourceDetail resourceDetail) {
    var centreOfInterestBlockSuffix = trapPropertiesPersistenceService
        .getCentreOfInterestBlockFromSubmissionDetail(resourceDetail.getSubmissionDetail())
        .map(item -> " (" + item + ")")
        .orElse("");
    return resourceDetail.getResourceName() + centreOfInterestBlockSuffix;
  }

  private Stream<Resource> getResourcesByNameStream(String name) {
    return resourceDetailsPersistenceService.getResourceDetailsByName(name).stream()
        .map(detail -> detail.getSubmissionDetail().getSubmission().getResource())
        .distinct();
  }

  private String appendCentreOfInterestAndLicenceApplicationReference(String resourceName,
                                                                      SubmissionDetail submissionDetail) {
    var centreOfInterest = trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(submissionDetail)
        .orElse(null);

    var applicationReference = generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)
        .map(GeneralDetail::getLicenceApplicationReference)
        .orElse(null);

    return SubmissionDetailDisplayUtil.getDisplayName(
        resourceName,
        centreOfInterest,
        applicationReference
    );
  }
}