package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;


import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;

public class SubmissionDetailTestUtils {

  private SubmissionDetailTestUtils() {
    throw new AssertionError();
  }

  public static SubmissionDetail createNewProspectUpdateSubmissionDetail() {
    return createSubmissionDetail(SubmissionType.PROSPECT_UPDATE);
  }

  public static SubmissionDetail createNewProspectSubmissionDetail() {
    return createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
  }

  public static SubmissionDetail createNewDiscoverySubmissionDetail() {
    return createSubmissionDetail(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
  }

  public static SubmissionDetail createNewDiscoveryUpdateSubmissionDetail() {
    return createSubmissionDetail(SubmissionType.UPDATE_DISCOVERY);
  }

  public static SubmissionDetail createNewPostDrillDryWellSubmissionDetail() {
    return createSubmissionDetail(SubmissionType.POST_DRILL_DRY_HOLE);
  }

  public static SubmissionDetail createSubmissionDetail(SubmissionType submissionType, UUID submissionDetailId) {
    var resource = new Resource();
    var resourceId = UUID.randomUUID();
    var resourceType = ResourceType.PROSPECT;
    resource.setId(resourceId);
    resource.setType(resourceType);
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setResourceId(resource);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);

    return submissionDetail;

  }

  public static SubmissionDetail createSubmissionDetail(SubmissionType submissionType) {
    var submissionDetailId = UUID.randomUUID();
    return createSubmissionDetail(submissionType, submissionDetailId);
  }
}
