package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimate;

import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

public record SubmissionEstimateView(
    String estimatedOn,
    String earliest,
    String mostLikely,
    String latest
) {
  static SubmissionEstimateView from(SubmissionEstimate estimates) {
    return new SubmissionEstimateView(
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
