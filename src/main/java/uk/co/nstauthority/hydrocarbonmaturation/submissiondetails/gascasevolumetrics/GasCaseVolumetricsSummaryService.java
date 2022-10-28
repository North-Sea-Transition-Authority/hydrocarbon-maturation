package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class GasCaseVolumetricsSummaryService {

  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Autowired
  public GasCaseVolumetricsSummaryService(ProbabilityDistributionSummaryService probabilityDistributionSummaryService) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
  }

  public ProbabilityDistributionsFormSummaryView getGasCaseVolumetricsSummaryView(SubmissionDetail submissionDetail) {
    var listOfSummaryViews = new ArrayList<ProbabilityDistributionSummaryView>();

    var gasHydrocarbonSaturation =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION,
            submissionDetail);
    listOfSummaryViews.add(gasHydrocarbonSaturation);

    var gasExpansionFactor =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR,
            submissionDetail);
    listOfSummaryViews.add(gasExpansionFactor);

    var condensateGasRatioLifeOfField =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
            submissionDetail);
    listOfSummaryViews.add(condensateGasRatioLifeOfField);

    var gasRecoveryFactor =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR,
            submissionDetail);
    listOfSummaryViews.add(gasRecoveryFactor);

    return new ProbabilityDistributionsFormSummaryView(listOfSummaryViews);
  }
}
