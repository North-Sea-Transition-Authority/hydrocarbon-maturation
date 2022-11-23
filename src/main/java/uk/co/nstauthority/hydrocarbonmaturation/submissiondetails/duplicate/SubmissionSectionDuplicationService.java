package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate;

import javax.transaction.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

public interface SubmissionSectionDuplicationService {

  @Transactional
  void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail);
}