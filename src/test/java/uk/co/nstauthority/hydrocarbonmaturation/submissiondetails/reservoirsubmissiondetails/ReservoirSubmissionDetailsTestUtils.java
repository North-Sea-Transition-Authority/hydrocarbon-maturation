package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

public class ReservoirSubmissionDetailsTestUtils {

  private ReservoirSubmissionDetailsTestUtils() {
    throw new AssertionError();
  }

  public static ReservoirSubmissionDetail createReservoirSubmissionDetail() {
    return createReservoirSubmissionDetail(SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY));
  }

  public static ReservoirSubmissionDetail createReservoirSubmissionDetail(Reservoir reservoir) {
    return createReservoirSubmissionDetail(reservoir, SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY));
  }

  public static ReservoirSubmissionDetail createReservoirSubmissionDetail(SubmissionType submissionType) {
    return createReservoirSubmissionDetail(SubmissionDetailTestUtils.createSubmissionDetail(submissionType));
  }

  public static ReservoirSubmissionDetail createReservoirSubmissionDetail(SubmissionType submissionType, Reservoir reservoir) {
    return createReservoirSubmissionDetail(reservoir, SubmissionDetailTestUtils.createSubmissionDetail(submissionType));
  }

  public static ReservoirSubmissionDetail createReservoirSubmissionDetail(SubmissionDetail submissionDetail) {
    var reservoir = new Reservoir();
    reservoir.setId(UUID.randomUUID());
    reservoir.setName("reservoir name: %s".formatted(reservoir.getId()));
    return createReservoirSubmissionDetail(reservoir, submissionDetail);
  }

  public static ReservoirSubmissionDetail createReservoirSubmissionDetail(Reservoir reservoir, SubmissionDetail submissionDetail) {
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();
    reservoirSubmissionDetail.setId(UUID.randomUUID());
    reservoirSubmissionDetail.setReservoir(reservoir);
    reservoirSubmissionDetail.setSubmissionDetail(submissionDetail);
    return reservoirSubmissionDetail;
  }
}
