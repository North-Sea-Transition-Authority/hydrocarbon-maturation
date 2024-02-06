package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

public record FieldOilCaseVolumetricTotalsSummaryView(
    List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews) {

  public static FieldOilCaseVolumetricTotalsSummaryView emptyForSubmissionDetail(SubmissionDetail submissionDetail) {
    var summaryViews = new java.util.ArrayList<>(List.of(
        ProbabilityDistributionSummaryView.empty("Oil recoverable resources"),
        ProbabilityDistributionSummaryView.empty("Associated-gas recoverable resources"),
        ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources"),
        ProbabilityDistributionSummaryView.empty("Natural gas liquid resources"),
        ProbabilityDistributionSummaryView.empty("Total sales gas resources")
    ));
    if (!submissionDetail.isConceptSelectReportSubmission()) {
      summaryViews.addAll(List.of(
          ProbabilityDistributionSummaryView.empty("Oil reserves"),
          ProbabilityDistributionSummaryView.empty("Associated gas reserves"),
          ProbabilityDistributionSummaryView.empty("Oil equivalent reserves"),
          ProbabilityDistributionSummaryView.empty("Natural gas liquid reserves"),
          ProbabilityDistributionSummaryView.empty("Total sales gas reserves")
      ));
    }

    return new FieldOilCaseVolumetricTotalsSummaryView(summaryViews);
  }
}
