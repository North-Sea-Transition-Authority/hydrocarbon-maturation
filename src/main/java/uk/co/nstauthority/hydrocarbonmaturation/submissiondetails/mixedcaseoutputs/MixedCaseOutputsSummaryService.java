package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
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

  public MixedCaseOutputsSummaryView getMixedCaseOutputsSummaryFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var mixedCaseOutput = mixedCaseOutputsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail);
    return mixedCaseOutput.map(this::createSummaryViewFromMixedCaseOutput)
        .orElse(MixedCaseOutputsSummaryView.empty());
  }

  private MixedCaseOutputsSummaryView createSummaryViewFromMixedCaseOutput(MixedCaseOutput mixedCaseOutput) {
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    var reservoirSubmissionDetail = mixedCaseOutput.getReservoirSubmissionDetail();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var hasAssociatedGasRecoverableResources = BooleanUtil.yesNoFromBoolean(
        mixedCaseOutput.getHasAssociatedGasRecoverableResources(), "");
    var riskedOilEquivalentRecoverableMean = mixedCaseOutput.getRiskedOilEquivalentRecoverableMean() != null
        ? mixedCaseOutput.getRiskedOilEquivalentRecoverableMean().toString()
        : "";

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT);

      listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT);

      if (mixedCaseOutput.getHasAssociatedGasRecoverableResources() == null
          || mixedCaseOutput.getHasAssociatedGasRecoverableResources()) {
        listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT);
      }

      listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT);


      listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT);

      listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT);

      listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT);
    } else if (resourceType == ResourceType.DISCOVERY) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_DISCOVERY);

      listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_DISCOVERY);

      if (mixedCaseOutput.getHasAssociatedGasRecoverableResources() == null
          || mixedCaseOutput.getHasAssociatedGasRecoverableResources()) {
        listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
      }

      listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_DISCOVERY);


      listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_DISCOVERY);

      listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY);

      listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);
    }

    var listOfSummaryViews = new ArrayList<ProbabilityDistributionSummaryView>();
    listOfInstances.forEach(instance -> listOfSummaryViews.add(
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            instance, submissionDetail, reservoirSubmissionDetail)));
    return new MixedCaseOutputsSummaryView(
        hasAssociatedGasRecoverableResources,
        riskedOilEquivalentRecoverableMean,
        listOfSummaryViews
    );
  }
}