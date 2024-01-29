package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals;

import jakarta.transaction.Transactional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.VolumetricTotalsService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;

@Service
public class DiscoveryOilCaseVolumetricTotalsDuplicationService implements SubmissionSectionDuplicationService {

  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final VolumetricTotalsService volumetricTotalsService;

  @Autowired
  public DiscoveryOilCaseVolumetricTotalsDuplicationService(
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      VolumetricTotalsService volumetricTotalsService) {
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.volumetricTotalsService = volumetricTotalsService;
  }


  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail,
                                         SubmissionDetail newSubmissionDetail) {

    var reservoirSubmissionDetails = reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(oldSubmissionDetail);
    if (!newSubmissionDetail.isDiscoverySubmission()
        || !volumetricTotalsService.isOilCaseOnly(reservoirSubmissionDetails)) {
      return;
    }

    var applicableInstances = Set.of(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
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