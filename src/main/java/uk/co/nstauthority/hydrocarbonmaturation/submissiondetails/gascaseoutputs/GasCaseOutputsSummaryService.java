package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
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
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    var submissionDetail = gasCaseOutput.getSubmissionDetail();
    var hasAssociatedGasRecoverableResources = BooleanUtil.yesNoFromBoolean(
        gasCaseOutput.getHasAssociatedCondensateRecoverableResources(), "");

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT);

      listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT);

      if (gasCaseOutput.getHasAssociatedCondensateRecoverableResources() == null
          || gasCaseOutput.getHasAssociatedCondensateRecoverableResources()) {
        listOfInstances.add(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT);
      }

      listOfInstances.add(
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT);
    } else if (resourceType == ResourceType.DISCOVERY) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY);

      listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY);

      if (gasCaseOutput.getHasAssociatedCondensateRecoverableResources() == null
          || gasCaseOutput.getHasAssociatedCondensateRecoverableResources()) {
        listOfInstances.add(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY);
      }

      listOfInstances.add(
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);
    }

    var listOfSummaryViews = new ArrayList<ProbabilityDistributionSummaryView>();
    listOfInstances.forEach(instance -> listOfSummaryViews.add(
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            instance, submissionDetail)));
    return new GasCaseOutputsSummaryView(hasAssociatedGasRecoverableResources, listOfSummaryViews);
  }
}