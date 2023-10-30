package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals;

import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
public class DiscoveryCombinedCaseVolTotalsDupService implements SubmissionSectionDuplicationService {
  private final DiscoveryCombinedCaseVolTotalsService discoveryCombinedCaseVolTotalsService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  DiscoveryCombinedCaseVolTotalsDupService(
      DiscoveryCombinedCaseVolTotalsService discoveryCombinedCaseVolTotalsService,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.discoveryCombinedCaseVolTotalsService = discoveryCombinedCaseVolTotalsService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail,
                                         SubmissionDetail newSubmissionDetail) {

    if (!newSubmissionDetail.isDiscoverySubmission()) {
      return;
    }

    discoveryCombinedCaseVolTotalsService.getCombinedCaseVolumetricTotal(oldSubmissionDetail)
        .ifPresent(combinedCaseVolumetricTotal ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
                newSubmissionDetail,
                combinedCaseVolumetricTotal,
                CombinedCaseVolumetricTotal.class
            )
        );

    var applicableInstances = Set.of(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    );

    probabilityDistributionPersistenceService
        .getAllProbabilityDistributionsForInstances(oldSubmissionDetail, applicableInstances)
        .forEach(probabilityDistribution ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
                newSubmissionDetail, probabilityDistribution, ProbabilityDistribution.class
            )
        );
  }
}
