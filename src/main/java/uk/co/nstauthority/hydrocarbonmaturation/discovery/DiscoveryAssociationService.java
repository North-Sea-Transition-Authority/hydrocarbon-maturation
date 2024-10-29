package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatusService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@Service
public class DiscoveryAssociationService {

  private final ResourceLinkService resourceLinkService;
  private final FieldPersistenceService fieldPersistenceService;
  private final FieldStatusService fieldStatusService;
  private final SubmissionUpdateService submissionUpdateService;

  @Autowired
  public DiscoveryAssociationService(ResourceLinkService resourceLinkService,
                                     FieldPersistenceService fieldPersistenceService,
                                     FieldStatusService fieldStatusService,
                                     SubmissionUpdateService submissionUpdateService) {
    this.resourceLinkService = resourceLinkService;
    this.fieldPersistenceService = fieldPersistenceService;
    this.fieldStatusService = fieldStatusService;
    this.submissionUpdateService = submissionUpdateService;
  }

  public boolean associatedWithStatus300PlusField(Submission submission) {
    var resource = submission.getResource();

    if (!resource.getType().equals(ResourceType.DISCOVERY)) {
      return false;
    }

    var associatedFields = resourceLinkService.getAssociatedFieldResources(resource);

    return associatedFields.stream()
        .map(fieldPersistenceService::getFieldOrThrow)
        .map(fieldStatusService::getMostRecentFieldStatus)
        .flatMap(Optional::stream)
        .anyMatch(fieldStatus -> fieldStatus.getStatusValue() >= 300);
  }

  public boolean blockedByAssociatedFieldStatus(Submission submission) {
    var submissionDetail = submissionUpdateService.getDraftUpdateSubmissionDetailBySubmission(submission);
    var isCorrection = submissionDetail.map(SubmissionDetail::isCorrection).orElse(false);

    return !isCorrection && associatedWithStatus300PlusField(submission);
  }

}
