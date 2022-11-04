package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

/**
 * Class to be used as the generic type in the post-drill dry hole form task list implementation. The class wraps
 * properties that methods in the task list implementation require.
 * @param submissionDetail The submission detail that is being accessed
 */
public record PostDrillDryHoleTaskListItemType(SubmissionDetail submissionDetail) {
}
