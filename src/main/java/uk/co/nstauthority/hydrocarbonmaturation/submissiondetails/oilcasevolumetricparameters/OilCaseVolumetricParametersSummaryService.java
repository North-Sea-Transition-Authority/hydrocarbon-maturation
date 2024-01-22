package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class OilCaseVolumetricParametersSummaryService implements SummarySectionService<ReservoirSubmissionDetail> {
  public static final String OIL_CASE_VOLUMETRIC_PARAMETERS = "Oil case volumetric parameters";
  public static final int OIL_CASE_VOLUMETRIC_PARAMETERS_DISPLAY_ORDER = 40;

  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public OilCaseVolumetricParametersSummaryService(ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
                                                   GeneralReservoirInformationService generalReservoirInformationService) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  public ProbabilityDistributionsFormSummaryView getOilCaseVolumetricsSummaryView(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var resourceType = submissionDetail.getSubmission().getResource().getType();

    if (resourceType == ResourceType.PROSPECT) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_PROSPECT);

      listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_PROSPECT);

      listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_PROSPECT);
    } else if (resourceType == ResourceType.DISCOVERY) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_DISCOVERY);

      listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_DISCOVERY);

      listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_DISCOVERY);
    }

    listOfInstances.add(ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD);

    var listOfSummaryViews = new ArrayList<ProbabilityDistributionSummaryView>();
    listOfInstances.forEach(instance -> listOfSummaryViews.add(
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            instance, submissionDetail, reservoirSubmissionDetail)));
    return new ProbabilityDistributionsFormSummaryView(listOfSummaryViews);
  }

  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    if (!reservoirSubmissionDetail.getSubmissionDetail().isDiscoverySubmission()) {
      return Optional.empty();
    }

    if (generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)) {
      var summaryItems = List.of(
          getOilVolumetricParametersOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail)
      );
      return Optional.of(new SummarySection(OIL_CASE_VOLUMETRIC_PARAMETERS_DISPLAY_ORDER, summaryItems, ""));
    }
    return Optional.empty();
  }

  private SummaryItem getOilVolumetricParametersOutputsSummaryFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail
  ) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_DISCOVERY);

    var listOfSummaryViews =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            listOfInstances, submissionDetail, reservoirSubmissionDetail
        );

    var summaryCard = SummaryCard.probabilitySummaryCard(
        listOfSummaryViews
    );
    return SummaryItem.withCard(OIL_CASE_VOLUMETRIC_PARAMETERS, summaryCard);
  }
}