package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldoilvolumetricparameters;

import java.util.List;
import java.util.Optional;
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
public class FieldOilVolumetricParametersSummaryService implements SummarySectionService<ReservoirSubmissionDetail> {

  public static final String FIELD_OIL_VOLUMETRIC_PARAMETERS = "Oil volumetric parameters";
  public static final int FIELD_OIL_VOLUMETRIC_PARAMETERS_DISPLAY_ORDER = 50;

  private static final List<ProbabilityDistributionFieldInstance> FIELD_OIL_VOLUMETRIC_PARAMETERS_INSTANCES = List.of(
      ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_FIELD,
      ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD_CONSTANT,
      ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_FIELD
      );

  private final GeneralReservoirInformationService generalReservoirInformationService;
  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Autowired
  public FieldOilVolumetricParametersSummaryService(
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

    if (generalReservoirInformationService.isOilOnly(reservoirHydrocarbons)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)) {
      var summaryItems = List.of(
          getFieldOilVolumetricsProbabilitySummaryItem(reservoirSubmissionDetail),
          getFieldOilVolumetricsMinMaxMostLikelySummaryItem(reservoirSubmissionDetail)
      );
      var summarySection = new SummarySection(FIELD_OIL_VOLUMETRIC_PARAMETERS_DISPLAY_ORDER, summaryItems, "");
      return Optional.of(summarySection);
    }

    return Optional.empty();
  }

  private SummaryItem getFieldOilVolumetricsProbabilitySummaryItem(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var tableView = probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        FIELD_OIL_VOLUMETRIC_PARAMETERS_INSTANCES,
        reservoirSubmissionDetail.getSubmissionDetail(),
        reservoirSubmissionDetail
    );
    var summaryCard = SummaryCard.probabilitySummaryCard(tableView);

    return SummaryItem.withCard(FIELD_OIL_VOLUMETRIC_PARAMETERS, summaryCard);
  }

  private SummaryItem getFieldOilVolumetricsMinMaxMostLikelySummaryItem(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var tableView = probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        List.of(ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_FIELD),
        reservoirSubmissionDetail.getSubmissionDetail(),
        reservoirSubmissionDetail
    );

    return SummaryItem.withCard(FIELD_OIL_VOLUMETRIC_PARAMETERS, SummaryCard.probabilitySummaryCard(tableView));
  }
}
