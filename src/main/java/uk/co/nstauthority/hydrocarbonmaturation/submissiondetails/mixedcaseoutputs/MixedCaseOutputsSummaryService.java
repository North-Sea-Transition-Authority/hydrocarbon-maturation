package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class MixedCaseOutputsSummaryService {

  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService;

  @Autowired
  public MixedCaseOutputsSummaryService(ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
                                      MixedCaseOutputsPersistenceService mixedCaseOutputsPersistenceService) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.mixedCaseOutputsPersistenceService = mixedCaseOutputsPersistenceService;
  }

  public MixedCaseOutputsSummaryView getMixedCaseOutputsSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var mixedCaseOutput = mixedCaseOutputsPersistenceService.getMixedCaseOutput(submissionDetail);
    return mixedCaseOutput.map(this::createSummaryViewFromMixedCaseOutput)
        .orElse(MixedCaseOutputsSummaryView.empty());
  }

  private MixedCaseOutputsSummaryView createSummaryViewFromMixedCaseOutput(MixedCaseOutput mixedCaseOutput) {
    var summaryViews = new ArrayList<ProbabilityDistributionSummaryView>();
    var submissionDetail = mixedCaseOutput.getSubmissionDetail();
    var hasAssociatedGasRecoverableResources = BooleanUtil.yesNoFromBoolean(
        mixedCaseOutput.getHasAssociatedGasRecoverableResources(), "");
    var riskedOilEquivalentRecoverableMean = mixedCaseOutput.getRiskedOilEquivalentRecoverableMean() != null
        ? mixedCaseOutput.getRiskedOilEquivalentRecoverableMean().toString()
        : "";

    var oilInPlace =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE,
            submissionDetail);
    summaryViews.add(oilInPlace);

    var oilRecoverableResources =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES,
            submissionDetail);
    summaryViews.add(oilRecoverableResources);

    if (mixedCaseOutput.getHasAssociatedGasRecoverableResources() == null
        || mixedCaseOutput.getHasAssociatedGasRecoverableResources()) {
      var associatedCondensateRecoverableResources =
          probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES,
              submissionDetail);
      summaryViews.add(associatedCondensateRecoverableResources);
    }

    var gasInPlace =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE,
            submissionDetail);
    summaryViews.add(gasInPlace);


    var gasRecoverableResources =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES,
            submissionDetail);
    summaryViews.add(gasRecoverableResources);

    var associatedCondensateRecoverableResources =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES,
            submissionDetail);
    summaryViews.add(associatedCondensateRecoverableResources);

    var oilEquivalentRecoverableResources =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES,
            submissionDetail);
    summaryViews.add(oilEquivalentRecoverableResources);

    return new MixedCaseOutputsSummaryView(
        hasAssociatedGasRecoverableResources,
        riskedOilEquivalentRecoverableMean,
        summaryViews
    );
  }
}