package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;


import java.util.UUID;

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
    var submissionDetailId = UUID.randomUUID();
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
    submissionDetail.setSubmissionType(submissionType);
    return submissionDetail;
  }
}
