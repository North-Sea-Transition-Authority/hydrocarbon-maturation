package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@Service
public class OnshoreOffshoreService {

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final FieldPersistenceService fieldPersistenceService;

  @Autowired
  public OnshoreOffshoreService(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                FieldPersistenceService fieldPersistenceService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.fieldPersistenceService = fieldPersistenceService;
  }

  public OnshoreOffshore getOnshoreOffshore(SubmissionDetail submissionDetail) {
    var resource = submissionDetail.getSubmission().getResource();
    var resourceType = resource.getType();
    if (resourceType == ResourceType.PROSPECT || resourceType == ResourceType.DISCOVERY) {
      return resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail).getOnshoreOffshore();
    } else {
      return fieldPersistenceService.getFieldOrThrow(resource).getOnshoreOffshore();
    }
  }

}