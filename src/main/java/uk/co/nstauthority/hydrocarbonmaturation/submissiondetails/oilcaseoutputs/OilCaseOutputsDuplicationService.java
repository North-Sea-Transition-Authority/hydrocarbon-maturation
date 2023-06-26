package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
class OilCaseOutputsDuplicationService implements SubmissionSectionDuplicationService {
  private final OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Autowired
  OilCaseOutputsDuplicationService(OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService,
                                   ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
                                   SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService) {
    this.oilCaseOutputsPersistenceService = oilCaseOutputsPersistenceService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    if (!newSubmissionDetail.isDiscoverySubmission() && !newSubmissionDetail.isProspectSubmission()) {
      return;
    }

    oilCaseOutputsPersistenceService.getOilCaseOutput(oldSubmissionDetail)
        .ifPresent(oilCaseOutput ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, oilCaseOutput,
                OilCaseOutput.class)
        );

    if (oldSubmissionDetail.hasSameResourceType(newSubmissionDetail)) {
      var applicableInstances = Set.of(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT,
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT,
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
      );
      var resourceType = oldSubmissionDetail.getSubmission().getResource().getType();
      if (resourceType == ResourceType.DISCOVERY) {
        applicableInstances = Set.of(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
        );
      }

      probabilityDistributionPersistenceService
          .getAllProbabilityDistributionsForInstances(oldSubmissionDetail, applicableInstances)
          .forEach(probabilityDistribution ->
              submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution,
                  ProbabilityDistribution.class));
    }
  }
}
