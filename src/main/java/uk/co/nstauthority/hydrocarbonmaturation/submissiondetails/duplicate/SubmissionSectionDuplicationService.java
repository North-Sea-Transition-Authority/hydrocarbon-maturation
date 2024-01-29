package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate;

import jakarta.transaction.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

public interface SubmissionSectionDuplicationService {

  @Transactional
  void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail);
}