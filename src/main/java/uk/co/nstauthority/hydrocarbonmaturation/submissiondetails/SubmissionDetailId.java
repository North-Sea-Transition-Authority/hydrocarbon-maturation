package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.UUID;

public record SubmissionDetailId(UUID submissionDetailId) {

  public static SubmissionDetailId fromSubmissionDetailId(UUID submissionDetailId) {
    return new SubmissionDetailId(submissionDetailId);
  }
}
