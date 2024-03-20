package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimates;

import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

public record SubmissionEstimatesView(
    String estimatedOn,
    String earliest,
    String mostLikely,
    String latest
) {
  static SubmissionEstimatesView from(SubmissionEstimates estimates) {
    return new SubmissionEstimatesView(
        DateUtil.formatLongDate(estimates.getDateWhenEstimated()),
        estimates.getEarliestDate() != null
            ? DateUtil.formatLongDate(estimates.getEarliestDate())
            : "",
        DateUtil.formatLongDate(estimates.getMostLikelyDate()),
        estimates.getLatestDate() != null
            ? DateUtil.formatLongDate(estimates.getLatestDate())
            : ""
    );
  }
}
