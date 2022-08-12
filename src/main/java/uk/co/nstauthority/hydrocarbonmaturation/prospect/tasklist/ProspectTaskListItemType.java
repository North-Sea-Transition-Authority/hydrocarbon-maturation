package uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

/**
 * Class to be used as the generic type in the prospect form task list implementation. The class wraps
 * properties that methods in the task list implementation require.
 * @param submissionDetail The submission detail that is being accessed
 */
record ProspectTaskListItemType(SubmissionDetail submissionDetail) {
}
