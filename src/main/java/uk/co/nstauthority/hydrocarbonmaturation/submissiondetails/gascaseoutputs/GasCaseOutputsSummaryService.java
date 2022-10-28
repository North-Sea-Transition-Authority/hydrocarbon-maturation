package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class GasCaseOutputsSummaryService {

  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;

  @Autowired
  public GasCaseOutputsSummaryService(ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
                                      GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.gasCaseOutputsPersistenceService = gasCaseOutputsPersistenceService;
  }

  public GasCaseOutputsSummaryView getGasCaseOutputsSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var gasCaseOutput = gasCaseOutputsPersistenceService.getGasCaseOutput(submissionDetail);
    return gasCaseOutput.map(this::createSummaryViewFromGasCaseOutput)
        .orElse(GasCaseOutputsSummaryView.empty());
  }

  private GasCaseOutputsSummaryView createSummaryViewFromGasCaseOutput(GasCaseOutput gasCaseOutput) {
    var summaryViews = new ArrayList<ProbabilityDistributionSummaryView>();
    var submissionDetail = gasCaseOutput.getSubmissionDetail();
    var hasAssociatedGasRecoverableResources = BooleanUtil.yesNoFromBoolean(
        gasCaseOutput.getHasAssociatedCondensateRecoverableResources(), "");

    var gasInPlace =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE,
            submissionDetail);
    summaryViews.add(gasInPlace);

    var gasRecoverableResources =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES,
            submissionDetail);
    summaryViews.add(gasRecoverableResources);

    if (gasCaseOutput.getHasAssociatedCondensateRecoverableResources() == null
        || gasCaseOutput.getHasAssociatedCondensateRecoverableResources()) {
      var associatedCondensateRecoverableResources =
          probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES,
              submissionDetail);
      summaryViews.add(associatedCondensateRecoverableResources);
    }

    var oilEquivalentRecoverableResources =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES,
            submissionDetail);
    summaryViews.add(oilEquivalentRecoverableResources);

    return new GasCaseOutputsSummaryView(hasAssociatedGasRecoverableResources, summaryViews);
  }
}