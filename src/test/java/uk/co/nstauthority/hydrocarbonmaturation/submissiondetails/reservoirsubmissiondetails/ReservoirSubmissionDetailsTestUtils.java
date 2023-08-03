package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

public class ReservoirSubmissionDetailsTestUtils {

  private ReservoirSubmissionDetailsTestUtils() {
    throw new AssertionError();
  }

  public static ReservoirSubmissionDetail createReservoirSubmissionDetail() {
    return createReservoirSubmissionDetail(new Reservoir(), new SubmissionDetail());
  }

  public static ReservoirSubmissionDetail createReservoirSubmissionDetail(SubmissionDetail submissionDetail) {
    return createReservoirSubmissionDetail(new Reservoir(), submissionDetail);
  }

  public static ReservoirSubmissionDetail createReservoirSubmissionDetail(Reservoir reservoir, SubmissionDetail submissionDetail) {
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();
    reservoirSubmissionDetail.setId(UUID.randomUUID());
    reservoirSubmissionDetail.setReservoir(reservoir);
    reservoirSubmissionDetail.setSubmissionDetail(submissionDetail);
    return reservoirSubmissionDetail;
  }
}
