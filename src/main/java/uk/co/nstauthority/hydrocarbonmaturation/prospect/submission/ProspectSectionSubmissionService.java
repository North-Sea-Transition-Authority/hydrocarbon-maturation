package uk.co.nstauthority.hydrocarbonmaturation.prospect.submission;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

/**
 * Interface to determine the behaviour of a prospect section during submission.
 */
public interface ProspectSectionSubmissionService {

  boolean isSectionSubmittable(SubmissionDetail submissionDetail);

}