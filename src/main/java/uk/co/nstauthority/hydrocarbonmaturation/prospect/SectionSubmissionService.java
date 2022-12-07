package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

/**
 * Interface to determine the behaviour of a section during submission.
 */
public interface SectionSubmissionService {

  boolean isSectionSubmittable(SubmissionDetail submissionDetail);

}