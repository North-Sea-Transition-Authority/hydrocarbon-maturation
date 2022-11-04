package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.PostDrillDryHoleForm;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
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
        .getGeneralDetailByIdOrThrow(UUID.fromString(form.getProspectSelectedOption()));
    var resource = getResourceFromGeneralDetail(previousGeneralDetail);

    var submissionDetail =
        submissionDetailService.createInitialSubmissionDetail(resource, SubmissionType.PROSPECT_UPDATE);

    generalDetailsPersistenceService.updateGeneralDetailProspect(form, submissionDetail, previousGeneralDetail);
    return submissionDetail;
  }

  @Transactional
  public SubmissionDetail createPostDrillDryHoleSubmission(PostDrillDryHoleForm form) {
    var previousGeneralDetail = generalDetailsPersistenceService
        .getGeneralDetailByIdOrThrow(UUID.fromString(form.getProspectSelectedOption()));
    var resource = getResourceFromGeneralDetail(previousGeneralDetail);

    var submissionDetail = submissionDetailService.createInitialSubmissionDetail(resource,
        SubmissionType.POST_DRILL_DRY_HOLE);

    generalDetailsPersistenceService.createGeneralDetailWithProspectName(submissionDetail, previousGeneralDetail.getProspectName()
    );
    return submissionDetail;
  }

  private Resource getResourceFromGeneralDetail(GeneralDetail generalDetail) {
    return generalDetail.getSubmissionDetail().getResourceId();
  }
}
