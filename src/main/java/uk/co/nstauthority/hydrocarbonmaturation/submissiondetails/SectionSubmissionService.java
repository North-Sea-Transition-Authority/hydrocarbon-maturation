package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

/**
 * Interface to determine the behaviour of a section during submission.
 */
public interface SectionSubmissionService {

  boolean isSectionSubmittable(SubmissionDetail submissionDetail);

}