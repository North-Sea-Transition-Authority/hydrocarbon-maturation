package uk.co.nstauthority.hydrocarbonmaturation.prospect.submission;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

public interface DryWellSectionSubmissionService {
  boolean isSectionSubmittable(SubmissionDetail submissionDetail);
}
