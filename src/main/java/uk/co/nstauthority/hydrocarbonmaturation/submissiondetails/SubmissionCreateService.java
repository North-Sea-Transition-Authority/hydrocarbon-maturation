package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;

@Service
public class SubmissionCreateService {

  private final ResourceService resourceService;
  private final SubmissionDetailService submissionDetailService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Autowired
  public SubmissionCreateService(ResourceService resourceService,
                                 SubmissionDetailService submissionDetailService,
                                 GeneralDetailsPersistenceService generalDetailsPersistenceService) {
    this.resourceService = resourceService;
    this.submissionDetailService = submissionDetailService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
  }

  @Transactional
  public SubmissionDetail createInitialProspect() {
    var resource = resourceService.createResource(ResourceType.PROSPECT);
    return submissionDetailService.createInitialSubmissionDetail(resource, SubmissionType.CREATE_PROSPECT);
  }

  @Transactional
  public SubmissionDetail createUpdateProspect(UpdateProspectForm form) {
    var previousGeneralDetail = generalDetailsPersistenceService
        .getGeneralDetailById(UUID.fromString(form.getProspectSelectedOption()))
        .orElseThrow(() -> new RemiEntityNotFoundException("The prospect with ID %s you want to update does not exist!"
            .formatted(form.getProspectSelectedOption())));

    var resourceId = previousGeneralDetail.getSubmissionDetail().getResourceId().getId();

    var resource = resourceService.getResourceForId(resourceId)
        .orElseThrow(() -> new RemiEntityNotFoundException("No resource with the specified ID %s found."
            .formatted(resourceId)));

    var submissionDetail =
        submissionDetailService.createInitialSubmissionDetail(resource, SubmissionType.PROSPECT_UPDATE);

    generalDetailsPersistenceService.updateGeneralDetailProspect(form, submissionDetail, previousGeneralDetail);
    return submissionDetail;
  }
}
