package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryoilcasevolumetrictotals;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class DiscoveryOilCaseVolumetricTotalsSummaryService {

  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Autowired
  public DiscoveryOilCaseVolumetricTotalsSummaryService(
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
  }

  public DiscoveryOilCaseVolumetricTotalsSummaryView createSummaryView(SubmissionDetail submissionDetail) {
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();

    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(
            ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
    listOfInstances.add(
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);

    var listOfSummaryViews = new ArrayList<ProbabilityDistributionSummaryView>();
    listOfInstances.forEach(instance -> listOfSummaryViews.add(
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            instance, submissionDetail)));
    return new DiscoveryOilCaseVolumetricTotalsSummaryView(listOfSummaryViews);
  }
}
