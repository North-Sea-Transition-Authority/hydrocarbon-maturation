package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class OilCaseOutputsSummaryService implements SummarySectionService<ReservoirSubmissionDetail> {

  public static final String OIL_VOLUMETRICS = "Oil case volumetrics";
  public static final int OIL_VOLUMETRICS_DISPLAY_ORDER = 60;

  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;
  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public OilCaseOutputsSummaryService(ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
                                      OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService,
                                      GeneralReservoirInformationService generalReservoirInformationService) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.oilCaseOutputsPersistenceService = oilCaseOutputsPersistenceService;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  public OilCaseOutputsSummaryView getOilCaseOutputsSummaryFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var oilCaseOutput = oilCaseOutputsPersistenceService.getOilCaseOutput(reservoirSubmissionDetail);
    var instances = getInstances(reservoirSubmissionDetail);

    oilCaseOutput.ifPresent(caseOutput ->
        filterIndustryStandardInstances(instances, reservoirSubmissionDetail.getSubmissionDetail(), caseOutput));

    var summaryBehaviour = probabilityDistributionSummaryService.getSummaryBehaviour(instances);

    return oilCaseOutput.map(this::createSummaryViewFromOilCaseOutput)
        .orElse(OilCaseOutputsSummaryView.empty(summaryBehaviour));
  }

  private OilCaseOutputsSummaryView createSummaryViewFromOilCaseOutput(OilCaseOutput oilCaseOutput) {
    var reservoirSubmissionDetail = oilCaseOutput.getReservoirSubmissionDetail();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var hasAssociatedGasRecoverableResources = BooleanUtil.yesNoFromBoolean(
        oilCaseOutput.getHasAssociatedGasRecoverableResources(), "");
    var useIndustryStandardConversion = BooleanUtil.yesNoFromBoolean(oilCaseOutput.getUseIndustryStandardConversion(), "");

    var listOfInstances = getInstances(reservoirSubmissionDetail);

    filterIndustryStandardInstances(listOfInstances, submissionDetail, oilCaseOutput);

    var tableView = probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances,
        submissionDetail,
        reservoirSubmissionDetail
    );
    return new OilCaseOutputsSummaryView(hasAssociatedGasRecoverableResources, useIndustryStandardConversion, tableView);
  }

  private void filterIndustryStandardInstances(
      List<ProbabilityDistributionFieldInstance> instances,
      SubmissionDetail submissionDetail,
      OilCaseOutput oilCaseOutput
  ) {
    if (submissionDetail.isProspectSubmission() && BooleanUtils.isTrue(oilCaseOutput.getUseIndustryStandardConversion())) {
      instances.remove(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT);
      instances.add(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_PROSPECT);
    }

    if (submissionDetail.isDiscoverySubmission() && BooleanUtils.isTrue(oilCaseOutput.getUseIndustryStandardConversion())) {
      instances.remove(ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);
      instances.add(
            ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY);
    }
  }

  private static List<ProbabilityDistributionFieldInstance> getInstances(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();

    var resourceType = reservoirSubmissionDetail.getSubmissionDetail().getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT);
      listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT);
      listOfInstances.add(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT);
      listOfInstances.add(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT);
      listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT);

    } else if (resourceType == ResourceType.DISCOVERY) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY);
      listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY);
      listOfInstances.add(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY);
      listOfInstances.add(ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY);
    }

    return listOfInstances;
  }

  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail, User user) {
    // this is only used for oil case only discovery reservoirs
    if (!reservoirSubmissionDetail.getSubmissionDetail().isDiscoverySubmission()
        || !generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)) {
      return Optional.empty();
    }

    var summaryItems = List.of(
        getOilVolumetricsSummaryDiscovery(reservoirSubmissionDetail)
    );

    return Optional.of(new SummarySection(OIL_VOLUMETRICS_DISPLAY_ORDER, summaryItems, ""));
  }

  private SummaryItem getOilVolumetricsSummaryDiscovery(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var instances = getInstances(reservoirSubmissionDetail);

    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var oilCaseOutput = oilCaseOutputsPersistenceService.getOilCaseOutput(reservoirSubmissionDetail);
    oilCaseOutput.ifPresent(caseOutput ->
        filterIndustryStandardInstances(instances, reservoirSubmissionDetail.getSubmissionDetail(), caseOutput));

    var tableView = probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
            instances,
        submissionDetail,
        reservoirSubmissionDetail
    );

    return SummaryItem.withCard(OIL_VOLUMETRICS, SummaryCard.probabilitySummaryCard(tableView));
  }
}
