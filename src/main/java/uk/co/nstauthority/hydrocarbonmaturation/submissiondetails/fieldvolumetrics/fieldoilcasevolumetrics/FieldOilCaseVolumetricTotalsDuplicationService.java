package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics;

import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;

@Service
public class FieldOilCaseVolumetricTotalsDuplicationService implements SubmissionSectionDuplicationService {

  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  private final VolumetricTotalsService volumetricTotalsService;

  @Autowired
  public FieldOilCaseVolumetricTotalsDuplicationService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService,
      VolumetricTotalsService volumetricTotalsService) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
    this.volumetricTotalsService = volumetricTotalsService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail,
                                         SubmissionDetail newSubmissionDetail) {

    if (!newSubmissionDetail.isFieldSubmission()
        || !volumetricTotalsService.isOilCaseOnly(oldSubmissionDetail)) {
      return;
    }

    var applicableInstances = new java.util.HashSet<>(Set.of(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD
    ));
    if (!oldSubmissionDetail.isConceptSelectReportSubmission()) {
      applicableInstances.addAll(List.of(
              ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD,
              ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESERVES_FIELD,
              ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD,
              ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD,
              ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
          )
      );
    }

    probabilityDistributionPersistenceService
        .getAllProbabilityDistributionsForInstances(oldSubmissionDetail, applicableInstances)
        .forEach(probabilityDistribution ->
            submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
                newSubmissionDetail, probabilityDistribution, ProbabilityDistribution.class
            )
        );
  }
}
