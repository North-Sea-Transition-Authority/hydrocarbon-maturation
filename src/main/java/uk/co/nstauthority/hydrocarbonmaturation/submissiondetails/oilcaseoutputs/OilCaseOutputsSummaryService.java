package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class OilCaseOutputsSummaryService {

  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;

  @Autowired
  public OilCaseOutputsSummaryService(ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
                                      OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.oilCaseOutputsPersistenceService = oilCaseOutputsPersistenceService;
  }

  public OilCaseOutputsSummaryView getOilCaseOutputsSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var oilCaseOutput = oilCaseOutputsPersistenceService.getOilCaseOutput(submissionDetail);
    return oilCaseOutput.map(this::createSummaryViewFromOilCaseOutput)
        .orElse(OilCaseOutputsSummaryView.empty());
  }

  private OilCaseOutputsSummaryView createSummaryViewFromOilCaseOutput(OilCaseOutput oilCaseOutput) {
    var summaryViews = new ArrayList<ProbabilityDistributionSummaryView>();
    var submissionDetail = oilCaseOutput.getSubmissionDetail();
    var hasAssociatedGasRecoverableResources = BooleanUtil.yesNoFromBoolean(
        oilCaseOutput.getHasAssociatedGasRecoverableResources(), "");
    var riskedOilEquivalentRecoverableMean = oilCaseOutput.getRiskedOilEquivalentRecoverableMean() != null
        ? oilCaseOutput.getRiskedOilEquivalentRecoverableMean().toString()
        : "";

    var oilInPlace =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE,
            submissionDetail);
    summaryViews.add(oilInPlace);

    var oilRecoverableResources =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES,
            submissionDetail);
    summaryViews.add(oilRecoverableResources);

    if (oilCaseOutput.getHasAssociatedGasRecoverableResources() == null
        || oilCaseOutput.getHasAssociatedGasRecoverableResources()) {
      var associatedGasRecoverableResources =
          probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES,
              submissionDetail);
      summaryViews.add(associatedGasRecoverableResources);
    }

    var oilEquivalentRecoverableResources =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES,
            submissionDetail);
    summaryViews.add(oilEquivalentRecoverableResources);

    return new OilCaseOutputsSummaryView(hasAssociatedGasRecoverableResources, riskedOilEquivalentRecoverableMean, summaryViews);
  }
}
