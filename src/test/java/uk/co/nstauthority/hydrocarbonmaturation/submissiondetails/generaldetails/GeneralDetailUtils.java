package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;


import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

public class GeneralDetailUtils {

  private GeneralDetailUtils() {
    throw new AssertionError();
  }

  public static GeneralDetail buildGeneralDetail(Resource resource) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT, resource);

    var generalDetail = new GeneralDetail();
    generalDetail.setSubmissionDetail(submissionDetail);

    return generalDetail;
  }
}
