package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.PostFieldDiscoveryLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRepository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatusService;
import uk.co.nstauthority.hydrocarbonmaturation.field.epmq.FieldStatusSnsService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Service
public class ResourceArchiveService {

  static final String DISCOVERY_LINKED_TO_FIELD_WARNING = "Discoveries cannot be archived if they are linked to a field.";
  static final String OPEN_CORRECTION_WARNING = "%s cannot be archived with an open correction.";
  static final String ACTIVE_SUBMISSIONS_WARNING =
      "%s cannot be archived with open draft submissions or submissions awaiting QA.";

  private final ResourceLinkService resourceLinkService;
  private final SubmissionDetailService submissionDetailService;
  private final ResourceRepository resourceRepository;
  private final PostFieldDiscoveryLinkService postFieldDiscoveryLinkService;
  private final FieldStatusSnsService fieldStatusSnsService;
  private final FieldPersistenceService fieldPersistenceService;
  private final FieldStatusService fieldStatusService;

  @Autowired
  public ResourceArchiveService(ResourceLinkService resourceLinkService,
                                SubmissionDetailService submissionDetailService,
                                ResourceRepository resourceRepository,
                                PostFieldDiscoveryLinkService postFieldDiscoveryLinkService,
                                FieldStatusSnsService fieldStatusSnsService,
                                FieldPersistenceService fieldPersistenceService,
                                FieldStatusService fieldStatusService
  ) {
    this.resourceLinkService = resourceLinkService;
    this.submissionDetailService = submissionDetailService;
    this.resourceRepository = resourceRepository;
    this.postFieldDiscoveryLinkService = postFieldDiscoveryLinkService;
    this.fieldStatusSnsService = fieldStatusSnsService;
    this.fieldPersistenceService = fieldPersistenceService;
    this.fieldStatusService = fieldStatusService;
  }

  public Optional<String> resourceCannotBeArchivedWarning(Resource resource) {
    if (resourceIsDiscoveryAndLinkedToField(resource)) {
      return Optional.of(DISCOVERY_LINKED_TO_FIELD_WARNING);
    }

    var resourceTypeDisplayText = resource.getType().getDisplayName();

    var openCorrectionExists = submissionDetailService.openCorrectionExists(
        Set.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.DRAFT, SubmissionDetailStatus.SUBMITTED),
        resource
    );

    if (openCorrectionExists) {
      return Optional.of(OPEN_CORRECTION_WARNING.formatted(resourceTypeDisplayText));
    }

    if (submissionDetailService.hasDraftOrSubmittedSubmission(resource)) {
      return Optional.of(ACTIVE_SUBMISSIONS_WARNING.formatted(resourceTypeDisplayText));
    }

    return Optional.empty();
  }

  public void archiveResource(Resource resource) {
    resource.setArchived(true);
    resourceRepository.save(resource);

    if (resource.getType().equals(ResourceType.FIELD)) {
      var field = fieldPersistenceService.getFieldOrThrow(resource);
      fieldStatusSnsService.publishFieldStatusUpdateSnsMessage(field.getFieldIdentifier(), 9999);
    }
  }

  public void unarchiveResource(Resource resource) {
    resource.setArchived(false);
    resourceRepository.save(resource);

    if (resource.getType().equals(ResourceType.FIELD)) {
      var field = fieldPersistenceService.getFieldOrThrow(resource);
      var status = fieldStatusService.getMostRecentFieldStatus(field)
          .orElseThrow(() -> new IllegalStateException("field status not found for field id: %s"
              .formatted(field.getFieldIdentifier())));

      fieldStatusSnsService.publishFieldStatusUpdateSnsMessage(field.getFieldIdentifier(), status.getStatusValue());
    }
  }

  private boolean resourceIsDiscoveryAndLinkedToField(Resource resource) {
    return ResourceType.DISCOVERY.equals(resource.getType())
        && (resourceLinkService.discoveryIsLinkedToField(resource)
        || postFieldDiscoveryLinkService.postFieldDiscoveryLinkExists(resource));
  }
}
