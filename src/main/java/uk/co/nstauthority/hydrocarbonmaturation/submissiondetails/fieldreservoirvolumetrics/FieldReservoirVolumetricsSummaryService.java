package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import java.util.ArrayList;
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
public class FieldReservoirVolumetricsSummaryService implements SummarySectionService<ReservoirSubmissionDetail> {

  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final GeneralReservoirInformationService generalReservoirInformationService;

  public static final String OIL_VOLUMETRICS = "Oil volumetrics";
  public static final String GAS_VOLUMETRICS = "Gas volumetrics";
  public static final String OIL_AND_GAS_VOLUMETRICS = "Oil and gas volumetrics";

  public static final int FIELD_RESERVOIR_VOLUMETRICS_DISPLAY_ORDER = 60;

  @Autowired
  public FieldReservoirVolumetricsSummaryService(ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
                                                 GeneralReservoirInformationService generalReservoirInformationService) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }


  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    if (!reservoirSubmissionDetail.getSubmissionDetail().isFieldSubmission()) {
      return Optional.empty();
    }

    var summaryItems = new ArrayList<SummaryItem>();
    var reservoirHydrocarbons = generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)
        .orElse(new ReservoirHydrocarbons());
    if (generalReservoirInformationService.isOilOnly(reservoirHydrocarbons)) {
      summaryItems.add(getOilCaseSummary(reservoirSubmissionDetail));
    } else if (generalReservoirInformationService.isGasOnly(reservoirHydrocarbons)) {
      summaryItems.add(getGasCaseSummary(reservoirSubmissionDetail));
    } else if (generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)) {
      summaryItems.add(getCombinedSummary(reservoirSubmissionDetail));
    }
    return Optional.of(new SummarySection(FIELD_RESERVOIR_VOLUMETRICS_DISPLAY_ORDER, summaryItems, ""));
  }

  private SummaryItem getOilCaseSummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var listOfInstances = List.of(ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD);

    var listOfSummaryViews =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            listOfInstances, submissionDetail, reservoirSubmissionDetail
        );

    return SummaryItem.withCard(OIL_VOLUMETRICS, SummaryCard.probabilitySummaryCard(listOfSummaryViews));
  }

  private SummaryItem getGasCaseSummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    List<ProbabilityDistributionFieldInstance> listOfInstances =
        List.of(ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD);

    var listOfSummaryViews =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            listOfInstances, submissionDetail, reservoirSubmissionDetail
        );

    return SummaryItem.withCard(GAS_VOLUMETRICS, SummaryCard.probabilitySummaryCard(listOfSummaryViews));
  }

  private SummaryItem getCombinedSummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    List<ProbabilityDistributionFieldInstance> listOfInstances =
        List.of(
            ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD,
            ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
        );

    var listOfSummaryViews =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            listOfInstances, submissionDetail, reservoirSubmissionDetail
        );

    return SummaryItem.withCard(OIL_AND_GAS_VOLUMETRICS, SummaryCard.probabilitySummaryCard(listOfSummaryViews));
  }
}
