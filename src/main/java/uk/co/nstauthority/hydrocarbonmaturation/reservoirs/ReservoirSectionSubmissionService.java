package uk.co.nstauthority.hydrocarbonmaturation.reservoirs;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

/**
 * Interface to determine the behaviour of a reservoir section during submission.
 */
public interface ReservoirSectionSubmissionService {

  boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail);

}