package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;


import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
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

  public static SubmissionDetail createNewPostDrillDryWellSubmissionDetail() {
    return createSubmissionDetail(SubmissionType.POST_DRILL_DRY_HOLE);
  }

  public static SubmissionDetail createDiscoveryFromProspectSubmissionDetail() {
    return createDiscoverySubmissionDetail(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
  }

  public static SubmissionDetail createNewDiscoverySubmissionDetail() {
    return createDiscoverySubmissionDetail(SubmissionType.CREATE_DISCOVERY);
  }

  public static SubmissionDetail createNewDiscoveryUpdateSubmissionDetail() {
    return createDiscoverySubmissionDetail(SubmissionType.UPDATE_DISCOVERY);
  }

  public static SubmissionDetail createSubmissionDetail(SubmissionType submissionType,
                                                        UUID submissionDetailId,
                                                        Resource resource) {

    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
    submissionDetail.setVersionNumber(1);
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    submissionDetail.setSubmitterType(SubmitterType.REGULATOR);
    submissionDetail.setCreatedByWuaId(1);
    var submission = SubmissionTestUtils.createSubmission(resource);
    submission.setResource(resource);
    submissionDetail.setSubmission(submission);
    return submissionDetail;

  }

  public static SubmissionDetail createSubmissionDetail(SubmissionType submissionType, UUID submissionDetailId) {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);

    return createSubmissionDetail(submissionType, submissionDetailId, resource);
  }

  public static SubmissionDetail createDiscoverySubmissionDetail(SubmissionType submissionType, UUID submissionDetailId) {
    var resource = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);

    return createSubmissionDetail(submissionType, submissionDetailId, resource);
  }

  public static SubmissionDetail createDiscoverySubmissionDetail(SubmissionType submissionType) {
    var resource = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    var submissionDetailId = UUID.randomUUID();

    return createSubmissionDetail(submissionType, submissionDetailId, resource);
  }

  public static SubmissionDetail createSubmissionDetail(SubmissionType submissionType, Resource resource) {
    var submissionDetailId = UUID.randomUUID();
    return createSubmissionDetail(submissionType, submissionDetailId, resource);
  }

  public static SubmissionDetail createSubmissionDetail(SubmissionType submissionType) {
    var submissionDetailId = UUID.randomUUID();
    return createSubmissionDetail(submissionType, submissionDetailId);
  }

  public static SubmissionDetail createSubmissionDetailWithResourceAndSubmissionType(SubmissionType submissionType) {
    var resourceType = submissionType.getResourceTypes().stream().findFirst().orElse(null);

    var resource = ResourceTestUtils.buildResource(resourceType);

    return createSubmissionDetail(submissionType, resource);
  }
}
