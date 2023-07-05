package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.util.Objects;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class ProbabilityDistributionSummaryService {

  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  public ProbabilityDistributionSummaryService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
  }

  public ProbabilityDistributionSummaryView getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
      ProbabilityDistributionFieldInstance probabilityDistributionFieldInstance,
      SubmissionDetail submissionDetail) {
    var probabilityDistribution =
        probabilityDistributionPersistenceService.getProbabilityDistribution(
            submissionDetail,
            probabilityDistributionFieldInstance)
            .orElse(null);

    if (probabilityDistribution == null) {
      return ProbabilityDistributionSummaryView.empty(probabilityDistributionFieldInstance.getDisplayName());
    }

    var p99 = Objects.requireNonNullElse(probabilityDistribution.getP99(), "").toString();
    var p90 = Objects.requireNonNullElse(probabilityDistribution.getP90(), "").toString();
    var mode = Objects.requireNonNullElse(probabilityDistribution.getModeValue(), "").toString();
    var p50 = Objects.requireNonNullElse(probabilityDistribution.getP50(), "").toString();
    var meanOrConstant = "";
    if (probabilityDistribution.getMeanValue() != null) {
      meanOrConstant = probabilityDistribution.getMeanValue().toString();
    } else if (probabilityDistribution.getConstantValue() != null) {
      meanOrConstant = probabilityDistribution.getConstantValue().toString();
    }

    var p10 = Objects.requireNonNullElse(probabilityDistribution.getP10(), "").toString();
    var p1 = Objects.requireNonNullElse(probabilityDistribution.getP1(), "").toString();
    var min = Objects.requireNonNullElse(probabilityDistribution.getMinValue(), "").toString();
    var max = Objects.requireNonNullElse(probabilityDistribution.getMaxValue(), "").toString();
    var mostLikely = Objects.requireNonNullElse(probabilityDistribution.getMostLikelyValue(), "").toString();

    var probabilityDistributionType = "";

    if (probabilityDistribution.getDistributionType() != null) {
      probabilityDistributionType = probabilityDistribution.getDistributionType().getDisplayName();
    } else if (probabilityDistribution.getOtherDistributionType() != null) {
      probabilityDistributionType = probabilityDistribution.getOtherDistributionType();
    }

    var probabilityDistributionName = Objects.requireNonNullElse(probabilityDistributionFieldInstance.getDisplayName(), "");
    var units = Objects.requireNonNullElse(probabilityDistribution.getInstance().getUnits(), "");
    var comments = Objects.requireNonNullElse(probabilityDistribution.getComments(), "");

    return new ProbabilityDistributionSummaryView(
        probabilityDistributionName,
        probabilityDistributionType,
        min,
        p99,
        p90,
        mode,
        p50,
        meanOrConstant,
        p10,
        p1,
        mostLikely,
        max,
        units,
        comments);
  }
}
