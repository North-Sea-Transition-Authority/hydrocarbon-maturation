package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldgasvolumetricparameters;

import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class FieldGasVolumetricParametersSummaryService implements SummarySectionService<ReservoirSubmissionDetail> {

  public static final int FIELD_GAS_VOLUMETRIC_PARAMETERS_DISPLAY_ORDER = 55;
  public static final String FIELD_GAS_VOLUMETRIC_PARAMETERS = "Gas volumetric parameters";

  private static final List<ProbabilityDistributionFieldInstance> FIELD_GAS_VOLUMETRIC_PARAMETERS_INSTANCES = List.of(
      ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_FIELD,
      ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR_FIELD,
      ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD_CONSTANT
  );
  private final GeneralReservoirInformationService generalReservoirInformationService;
  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Autowired
  public FieldGasVolumetricParametersSummaryService(
      GeneralReservoirInformationService generalReservoirInformationService,
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService) {
    this.generalReservoirInformationService = generalReservoirInformationService;
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    if (!reservoirSubmissionDetail.getSubmissionDetail().isFieldSubmission()) {
      return Optional.empty();
    }

    // if no hydrocarbon case a blank new ReservoirHydrocarbon will fail the if condition
    var reservoirHydrocarbons = generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)
        .orElse(new ReservoirHydrocarbons());

    if (generalReservoirInformationService.isGasOnly(reservoirHydrocarbons)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)) {
      var summaryItems = List.of(
          getFieldGasVolumetricParametersProbabilitiesSummaryItem(reservoirSubmissionDetail),
          getFieldGasVolumetricParametersMinMaxMostLikelySummaryItem(reservoirSubmissionDetail)
      );
      var summarySection = new SummarySection(FIELD_GAS_VOLUMETRIC_PARAMETERS_DISPLAY_ORDER, summaryItems, "");
      return Optional.of(summarySection);
    }

    return Optional.empty();
  }

  @NotNull
  private SummaryItem getFieldGasVolumetricParametersProbabilitiesSummaryItem(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var tableView = probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        FIELD_GAS_VOLUMETRIC_PARAMETERS_INSTANCES,
        reservoirSubmissionDetail.getSubmissionDetail(),
        reservoirSubmissionDetail
    );
    var summaryCard = SummaryCard.probabilitySummaryCard(tableView);
    return SummaryItem.withCard(FIELD_GAS_VOLUMETRIC_PARAMETERS, summaryCard);
  }

  @NotNull
  private SummaryItem getFieldGasVolumetricParametersMinMaxMostLikelySummaryItem(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var tableView = probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            List.of(ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_FIELD),
            reservoirSubmissionDetail.getSubmissionDetail(),
            reservoirSubmissionDetail
        );

    var summaryCard = SummaryCard.probabilitySummaryCard(tableView);
    return SummaryItem.withCard(FIELD_GAS_VOLUMETRIC_PARAMETERS, summaryCard);
  }
}
