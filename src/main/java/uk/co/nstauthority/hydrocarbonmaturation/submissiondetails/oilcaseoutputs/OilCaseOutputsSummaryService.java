package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

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
public class OilCaseOutputsSummaryService {

  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;

  @Autowired
  public OilCaseOutputsSummaryService(ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
                                      OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.oilCaseOutputsPersistenceService = oilCaseOutputsPersistenceService;
  }

  public OilCaseOutputsSummaryView getOilCaseOutputsSummaryFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var oilCaseOutput = oilCaseOutputsPersistenceService.getOilCaseOutput(reservoirSubmissionDetail);
    return oilCaseOutput.map(this::createSummaryViewFromOilCaseOutput)
        .orElse(OilCaseOutputsSummaryView.empty());
  }

  private OilCaseOutputsSummaryView createSummaryViewFromOilCaseOutput(OilCaseOutput oilCaseOutput) {
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    var reservoirSubmissionDetail = oilCaseOutput.getReservoirSubmissionDetail();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var hasAssociatedGasRecoverableResources = BooleanUtil.yesNoFromBoolean(
        oilCaseOutput.getHasAssociatedGasRecoverableResources(), "");
    var riskedOilEquivalentRecoverableMean = oilCaseOutput.getRiskedOilEquivalentRecoverableMean() != null
        ? oilCaseOutput.getRiskedOilEquivalentRecoverableMean().toString()
        : "";

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT);

      listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT);

      if (oilCaseOutput.getHasAssociatedGasRecoverableResources() == null
          || oilCaseOutput.getHasAssociatedGasRecoverableResources()) {
        listOfInstances.add(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT);
      }

      listOfInstances.add(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT);
    } else if (resourceType == ResourceType.DISCOVERY) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY);

      listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY);

      if (oilCaseOutput.getHasAssociatedGasRecoverableResources() == null
          || oilCaseOutput.getHasAssociatedGasRecoverableResources()) {
        listOfInstances.add(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
      }

      listOfInstances.add(
          ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);
    }

    var listOfSummaryViews = new ArrayList<ProbabilityDistributionSummaryView>();
    listOfInstances.forEach(instance -> listOfSummaryViews.add(
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            instance, submissionDetail, reservoirSubmissionDetail)));
    return new OilCaseOutputsSummaryView(hasAssociatedGasRecoverableResources, riskedOilEquivalentRecoverableMean,
        listOfSummaryViews);
  }
}
