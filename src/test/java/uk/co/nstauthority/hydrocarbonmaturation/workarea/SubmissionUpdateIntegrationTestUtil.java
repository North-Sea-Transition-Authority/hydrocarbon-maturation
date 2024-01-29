package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import java.time.Instant;
import jakarta.persistence.EntityManager;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdate;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

public class SubmissionUpdateIntegrationTestUtil {

  public static SubmissionUpdate createSubmissionUpdate(Submission submission,
                                                        boolean resolved,
                                                        EntityManager entityManager) {
    var submissionUpdate = new SubmissionUpdate();
    submissionUpdate.setSubmission(submission);
    submissionUpdate.setResolved(resolved);
    submissionUpdate.setRequestedTimestamp(Instant.now());
    submissionUpdate.setRequestedByWuaId(1);
    entityManager.persist(submissionUpdate);
    entityManager.flush();
    return submissionUpdate;
  }
}
