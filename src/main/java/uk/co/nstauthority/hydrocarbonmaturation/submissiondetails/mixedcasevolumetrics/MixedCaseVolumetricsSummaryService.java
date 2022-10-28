package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class MixedCaseVolumetricsSummaryService {

  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Autowired
  public MixedCaseVolumetricsSummaryService(ProbabilityDistributionSummaryService probabilityDistributionSummaryService) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
  }

  public ProbabilityDistributionsFormSummaryView getMixedCaseVolumetricsSummaryView(SubmissionDetail submissionDetail) {
    var listOfSummaryViews = new ArrayList<ProbabilityDistributionSummaryView>();

    var formationVolumeFactor =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY,
            submissionDetail);
    listOfSummaryViews.add(formationVolumeFactor);

    var oilHydrocarbonSaturation =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION,
            submissionDetail);
    listOfSummaryViews.add(oilHydrocarbonSaturation);

    var gasHydrocarbonSaturation =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION,
            submissionDetail);
    listOfSummaryViews.add(gasHydrocarbonSaturation);

    var associatedGasOilRatioLifeOfField =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
            submissionDetail);
    listOfSummaryViews.add(associatedGasOilRatioLifeOfField);

    var gasExpansionFactor =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_GAS_EXPANSION_FACTOR,
            submissionDetail);
    listOfSummaryViews.add(gasExpansionFactor);

    var condensateGasRatio =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
            submissionDetail);
    listOfSummaryViews.add(condensateGasRatio);

    var oilRecoveryFactor =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR,
            submissionDetail);
    listOfSummaryViews.add(oilRecoveryFactor);

    var gasRecoveryFactor =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR,
            submissionDetail);
    listOfSummaryViews.add(gasRecoveryFactor);

    return new ProbabilityDistributionsFormSummaryView(listOfSummaryViews);
  }
}