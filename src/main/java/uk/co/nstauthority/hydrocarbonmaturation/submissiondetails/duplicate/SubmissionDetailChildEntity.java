package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

public interface SubmissionDetailChildEntity {
  void setId(UUID id);

  void setSubmissionDetail(SubmissionDetail submissionDetail);
}
