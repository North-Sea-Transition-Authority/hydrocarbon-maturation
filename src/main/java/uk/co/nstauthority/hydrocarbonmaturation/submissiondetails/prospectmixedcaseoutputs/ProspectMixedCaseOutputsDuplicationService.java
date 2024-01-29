package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.prospectmixedcaseoutputs;

import jakarta.transaction.Transactional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class ProspectMixedCaseOutputsDuplicationService implements ReservoirSectionDuplicationService {
  private final ProspectMixedCaseOutputsPersistenceService prospectMixedCaseOutputsPersistenceService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @Autowired
  ProspectMixedCaseOutputsDuplicationService(
      ProspectMixedCaseOutputsPersistenceService prospectMixedCaseOutputsPersistenceService,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService) {
    this.prospectMixedCaseOutputsPersistenceService = prospectMixedCaseOutputsPersistenceService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.reservoirDuplicationPersistenceService = reservoirDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateReservoirSection(ReservoirSubmissionDetail oldReservoirSubmissionDetail,
                                        ReservoirSubmissionDetail newReservoirSubmissionDetail) {
    var oldSubmissionDetail = oldReservoirSubmissionDetail.getSubmissionDetail();
    var newSubmissionDetail = newReservoirSubmissionDetail.getSubmissionDetail();

    if (!newSubmissionDetail.isProspectSubmission()) {
      return;
    }

    prospectMixedCaseOutputsPersistenceService.getMixedCaseOutput(oldReservoirSubmissionDetail)
        .ifPresent(mixedCaseOutput ->
            reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(newReservoirSubmissionDetail, mixedCaseOutput,
                MixedCaseOutput.class)
        );

    if (oldSubmissionDetail.hasSameResourceType(newSubmissionDetail)) {
      var applicableInstances = Set.of(
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT,
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT,
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT,
          ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT,
          ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
          ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
          ProbabilityDistributionFieldInstance.MIXED_CASE_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT
      );

      probabilityDistributionPersistenceService
          .getAllProbabilityDistributionsForInstances(oldReservoirSubmissionDetail, applicableInstances)
          .forEach(probabilityDistribution ->
              reservoirDuplicationPersistenceService
                  .duplicateChildAndReplaceParent(newReservoirSubmissionDetail, probabilityDistribution,
                      ProbabilityDistribution.class));

      probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(
          newReservoirSubmissionDetail, applicableInstances).forEach(
            probabilityDistribution -> probabilityDistribution.setSubmissionDetail(newSubmissionDetail)
      );
    }
  }
}
