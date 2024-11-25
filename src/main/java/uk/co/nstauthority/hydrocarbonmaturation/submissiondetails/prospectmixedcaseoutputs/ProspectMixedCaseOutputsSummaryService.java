package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.prospectmixedcaseoutputs;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class ProspectMixedCaseOutputsSummaryService {

  static final List<ProbabilityDistributionFieldInstance> ALL_INSTANCES = List.of(
      ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT
  );

  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final ProspectMixedCaseOutputsPersistenceService prospectMixedCaseOutputsPersistenceService;

  @Autowired
  public ProspectMixedCaseOutputsSummaryService(
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
      ProspectMixedCaseOutputsPersistenceService prospectMixedCaseOutputsPersistenceService) {
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.prospectMixedCaseOutputsPersistenceService = prospectMixedCaseOutputsPersistenceService;
  }

  public MixedCaseOutputsSummaryView getMixedCaseOutputsSummaryFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var summaryBehaviour = probabilityDistributionSummaryService.getSummaryBehaviour(ALL_INSTANCES);
    var mixedCaseOutput = prospectMixedCaseOutputsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail);
    return mixedCaseOutput.map(this::createSummaryViewFromMixedCaseOutput)
        .orElse(MixedCaseOutputsSummaryView.empty(summaryBehaviour));
  }

  private MixedCaseOutputsSummaryView createSummaryViewFromMixedCaseOutput(MixedCaseOutput mixedCaseOutput) {

    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT);
    listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT);
    listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT);

    if (BooleanUtils.isTrue(mixedCaseOutput.getHasAssociatedGasRecoverableResources())) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT);
    }

    listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT);

    if (BooleanUtils.isTrue(mixedCaseOutput.getHasAssociatedCondensateRecoverableResources())) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT);
    }

    if (BooleanUtils.isTrue(mixedCaseOutput.getUseIndustryStandardConversion())) {
      listOfInstances.add(
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_PROSPECT);
    } else {
      listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT);
    }

    listOfInstances.add(ProbabilityDistributionFieldInstance.MIXED_CASE_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT);

    var reservoirSubmissionDetail = mixedCaseOutput.getReservoirSubmissionDetail();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var tableView = probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
            listOfInstances,
            submissionDetail,
            reservoirSubmissionDetail
        );

    var hasAssociatedGasRecoverableResources = BooleanUtil.yesNoFromBoolean(
        mixedCaseOutput.getHasAssociatedGasRecoverableResources(), "");

    var hasAssociatedCondensateRecoverableResources = BooleanUtil.yesNoFromBoolean(
        mixedCaseOutput.getHasAssociatedCondensateRecoverableResources(), "");

    var useIndustryStandardConversion = BooleanUtil.yesNoFromBoolean(
        mixedCaseOutput.getUseIndustryStandardConversion(), "");

    return new MixedCaseOutputsSummaryView(
        hasAssociatedGasRecoverableResources,
        hasAssociatedCondensateRecoverableResources,
        useIndustryStandardConversion,
        tableView
    );
  }
}