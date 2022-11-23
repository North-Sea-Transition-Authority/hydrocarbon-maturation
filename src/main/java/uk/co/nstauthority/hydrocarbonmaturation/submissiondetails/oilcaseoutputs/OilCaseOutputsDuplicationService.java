package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import java.util.Objects;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
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
    if (!Objects.equals(newSubmissionDetail.getSubmissionType(), SubmissionType.PROSPECT_UPDATE)) {
      return;
    }
    
    oilCaseOutputsPersistenceService.getOilCaseOutput(oldSubmissionDetail)
        .ifPresent(oilCaseOutput ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, oilCaseOutput,
                OilCaseOutput.class)
        );

    probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldSubmissionDetail, Set.of(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE,
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES,
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES,
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES
        ))
        .forEach(probabilityDistribution ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution,
                ProbabilityDistribution.class));
  }
}
