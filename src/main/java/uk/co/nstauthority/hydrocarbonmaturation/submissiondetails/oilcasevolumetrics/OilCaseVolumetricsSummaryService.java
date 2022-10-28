package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class OilCaseVolumetricsSummaryService {

  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Autowired
  public OilCaseVolumetricsSummaryService(ProbabilityDistributionSummaryService probabilityDistributionSummaryService) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
  }

  public ProbabilityDistributionsFormSummaryView getOilCaseVolumetricsSummaryView(SubmissionDetail submissionDetail) {
    var listOfSummaryViews = new ArrayList<ProbabilityDistributionSummaryView>();

    var formationVolumeFactor =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY,
            submissionDetail);
    listOfSummaryViews.add(formationVolumeFactor);

    var oilHydrocarbonSaturation =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION,
            submissionDetail);
    listOfSummaryViews.add(oilHydrocarbonSaturation);

    var associatedGasOilRatioLifeOfField =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
            submissionDetail);
    listOfSummaryViews.add(associatedGasOilRatioLifeOfField);

    var oilRecoveryFactor =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR,
            submissionDetail);
    listOfSummaryViews.add(oilRecoveryFactor);

    return new ProbabilityDistributionsFormSummaryView(listOfSummaryViews);
  }
}