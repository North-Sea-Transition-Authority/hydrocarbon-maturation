package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.UUID;
import jakarta.persistence.EntityManager;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

public class SubmissionTestUtils {

  private SubmissionTestUtils() {
    throw new AssertionError();
  }

  public static Submission createSubmission(Resource resource) {
    var submission = new Submission();
    submission.setId(UUID.randomUUID());
    submission.setResource(resource);
    return submission;
  }

  public static Submission createSubmissionForIntegrationTest(Resource resource, EntityManager entityManager) {
    var submission = new Submission();
    submission.setId(null);
    submission.setResource(resource);
    entityManager.persist(submission);
    entityManager.flush();
    return submission;
  }

  public static Submission createSubmissionForIntegrationTest(Resource resource, Boolean withdrawn, EntityManager entityManager) {
    var submission = new Submission();
    submission.setId(null);
    submission.setWithdrawn(withdrawn);
    submission.setResource(resource);
    entityManager.persist(submission);
    entityManager.flush();
    return submission;
  }
}
