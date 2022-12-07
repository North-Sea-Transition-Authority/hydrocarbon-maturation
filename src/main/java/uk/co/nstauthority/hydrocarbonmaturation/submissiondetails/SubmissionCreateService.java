package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.StartDiscoveryForm;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery.UpdateDiscoveryForm;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.PostDrillDryHoleForm;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;

@Service
public class SubmissionCreateService {

  private final ResourceService resourceService;
  private final SubmissionDetailService submissionDetailService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final ResourceLinkService resourceLinkService;
  private final SubmissionDetailDuplicationService submissionDetailDuplicationService;

  @Autowired
  public SubmissionCreateService(ResourceService resourceService,
                                 SubmissionDetailService submissionDetailService,
                                 GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                 ResourceLinkService resourceLinkService,
                                 SubmissionDetailDuplicationService submissionDetailDuplicationService) {
    this.resourceService = resourceService;
    this.submissionDetailService = submissionDetailService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.resourceLinkService = resourceLinkService;
    this.submissionDetailDuplicationService = submissionDetailDuplicationService;
  }

  @Transactional
  public SubmissionDetail createInitialProspect() {
    var resource = resourceService.createResource(ResourceType.PROSPECT);
    return submissionDetailService.createInitialSubmissionDetail(resource, SubmissionType.CREATE_PROSPECT);
  }

  @Transactional
  public SubmissionDetail createUpdateProspect(UpdateProspectForm form) {
    var oldSubmissionDetail = getSubmissionDetailByGeneralDetail(form.getProspectSelectedOption());
    var resource = oldSubmissionDetail.getResourceId();

    var newSubmissionDetail =
        submissionDetailService.createInitialSubmissionDetail(resource, SubmissionType.PROSPECT_UPDATE);

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);

    var newGeneralDetail = generalDetailsPersistenceService.getGeneralDetailOrThrow(newSubmissionDetail);
    generalDetailsPersistenceService.updateGeneralDetailProspect(form, newGeneralDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createPostDrillDryHoleSubmission(PostDrillDryHoleForm form) {
    var oldSubmissionDetail = getSubmissionDetailByGeneralDetail(form.getProspectSelectedOption());
    var resource = oldSubmissionDetail.getResourceId();

    var newSubmissionDetail =
        submissionDetailService.createInitialSubmissionDetail(resource, SubmissionType.POST_DRILL_DRY_HOLE);

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createNewDiscovery(StartDiscoveryForm form) {
    var discoveryResource = resourceService.createResource(ResourceType.DISCOVERY);
    var oldSubmissionDetail = getSubmissionDetailByGeneralDetail(form.getProspectSelectedOption());
    var prospectResource = oldSubmissionDetail.getResourceId();

    resourceLinkService.createNewResourceLink(prospectResource, discoveryResource);

    var newSubmissionDetail =
        submissionDetailService.createInitialSubmissionDetail(discoveryResource, SubmissionType.CREATE_DISCOVERY);

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createUpdateDiscovery(UpdateDiscoveryForm form) {
    var oldSubmissionDetail = getSubmissionDetailByGeneralDetail(form.getDiscoverySelectedOption());
    var resource = oldSubmissionDetail.getResourceId();

    var newSubmissionDetail =
        submissionDetailService.createInitialSubmissionDetail(resource, SubmissionType.UPDATE_DISCOVERY);

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);

    return newSubmissionDetail;
  }

  private SubmissionDetail getSubmissionDetailByGeneralDetail(String generalDetailId) {
    var previousGeneralDetail = generalDetailsPersistenceService
        .getGeneralDetailByIdOrThrow(UUID.fromString(generalDetailId));
    return previousGeneralDetail.getSubmissionDetail();
  }
}
