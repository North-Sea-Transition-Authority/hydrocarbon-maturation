package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class GasCaseVolumetricParametersSummaryService implements SummarySectionService<ReservoirSubmissionDetail> {
  public static final String GAS_CASE_VOLUMETRIC_PARAMETERS = "Gas case volumetric parameters";
  public static final int GAS_CASE_VOLUMETRIC_PARAMETERS_DISPLAY_ORDER = 50;

  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public GasCaseVolumetricParametersSummaryService(
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
      GeneralReservoirInformationService generalReservoirInformationService) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  public ProbabilityDistributionTableView getGasCaseVolumetricsTableView(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var resourceType = submissionDetail.getSubmission().getResource().getType();

    if (resourceType == ResourceType.PROSPECT) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_PROSPECT);

      listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_PROSPECT);
    } else if (resourceType == ResourceType.DISCOVERY) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY);

      listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY);
    }

    listOfInstances.add(ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD);

    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR);

    return probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances,
        submissionDetail,
        reservoirSubmissionDetail
    );
  }

  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail, User user) {
    if (!reservoirSubmissionDetail.getSubmissionDetail().isDiscoverySubmission()) {
      return Optional.empty();
    }

    if (generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)) {
      var summaryItems = List.of(
          getGasCaseVolumetricParametersSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail));
      return Optional.of(new SummarySection(GAS_CASE_VOLUMETRIC_PARAMETERS_DISPLAY_ORDER, summaryItems, ""));
    }
    return Optional.empty();
  }

  private SummaryItem getGasCaseVolumetricParametersSummaryFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail
  ) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR);

    var tableView = probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    );

    return SummaryItem.withCard(GAS_CASE_VOLUMETRIC_PARAMETERS, SummaryCard.probabilitySummaryCard(tableView));
  }
}
