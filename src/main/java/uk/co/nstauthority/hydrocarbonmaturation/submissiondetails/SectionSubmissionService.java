package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;

/**
 * Interface to determine the behaviour of a section during submission.
 */
public interface SectionSubmissionService {

  boolean isSectionSubmittable(SubmissionDetail submissionDetail, User user);

}