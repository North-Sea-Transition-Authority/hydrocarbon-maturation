package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import java.util.UUID;
import javax.persistence.EntityManager;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

public class ReservoirSubmissionDetailsIntegrationTestUtils {

  private ReservoirSubmissionDetailsIntegrationTestUtils() {
    throw new AssertionError();
  }

  public static ReservoirSubmissionDetail createReservoirSubmissionDetail(SubmissionDetail submissionDetail, EntityManager entityManager) {
    var reservoir = new Reservoir();
    reservoir.setName("reservoir name: %s".formatted(UUID.randomUUID()));
    entityManager.persist(reservoir);
    entityManager.flush();

    return createReservoirSubmissionDetail(reservoir, submissionDetail, entityManager);
  }

  public static ReservoirSubmissionDetail createReservoirSubmissionDetail(Reservoir reservoir, SubmissionDetail submissionDetail, EntityManager entityManager) {
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();
    reservoirSubmissionDetail.setReservoir(reservoir);
    reservoirSubmissionDetail.setSubmissionDetail(submissionDetail);
    entityManager.persist(reservoirSubmissionDetail);
    entityManager.flush();

    return reservoirSubmissionDetail;
  }
}
