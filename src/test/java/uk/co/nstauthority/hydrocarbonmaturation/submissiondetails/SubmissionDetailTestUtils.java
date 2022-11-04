package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;


import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

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

  private static SubmissionDetail createSubmissionDetail(SubmissionType submissionType) {
    var resourceId = UUID.randomUUID();
    var resource = new Resource();
    resource.setId(resourceId);

    var submissionDetailId = UUID.randomUUID();
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setResourceId(resource);

    return submissionDetail;
  }
}
