package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.List;
import javax.transaction.Transactional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.notify.SubmissionEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals.DiscoveryGasCaseVolumetricTotalsService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
public class SubmissionApprovalService {

  private final SubmissionDetailPersistenceService submissionDetailPersistenceService;
  private final RegulatorTeamService regulatorTeamService;
  private final UserDetailService userDetailService;
  private final SubmissionEmailService submissionEmailService;
  private final SubmissionUpdateService submissionUpdateService;
  private final ResponsibleTeamService responsibleTeamService;
  private final AreaTeamService areaTeamService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final GeneralReservoirInformationService generalReservoirInformationService;
  private final DiscoveryGasCaseVolumetricTotalsService discoveryGasCaseVolumetricTotalsService;
  private final GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;

  @Autowired
  public SubmissionApprovalService(SubmissionDetailPersistenceService submissionDetailPersistenceService,
                                   RegulatorTeamService regulatorTeamService,
                                   UserDetailService userDetailService,
                                   SubmissionEmailService submissionEmailService,
                                   SubmissionUpdateService submissionUpdateService,
                                   ResponsibleTeamService responsibleTeamService,
                                   AreaTeamService areaTeamService,
                                   ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
                                   ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
                                   GeneralReservoirInformationService generalReservoirInformationService,
                                   DiscoveryGasCaseVolumetricTotalsService discoveryGasCaseVolumetricTotalsService,
                                   GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService) {
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
    this.regulatorTeamService = regulatorTeamService;
    this.userDetailService = userDetailService;
    this.submissionEmailService = submissionEmailService;
    this.submissionUpdateService = submissionUpdateService;
    this.responsibleTeamService = responsibleTeamService;
    this.areaTeamService = areaTeamService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.generalReservoirInformationService = generalReservoirInformationService;
    this.discoveryGasCaseVolumetricTotalsService = discoveryGasCaseVolumetricTotalsService;
    this.gasCaseOutputsPersistenceService = gasCaseOutputsPersistenceService;
  }

  @Transactional
  public void submitSubmission(SubmissionDetail submissionDetail) {
    copySingleReservoirIntoTotals(submissionDetail);

    // corrections will already have submission and approval details stored, just need to set the status
    if (BooleanUtils.isTrue(submissionDetail.isCorrection())) {
      submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
      submissionDetailPersistenceService.saveSubmissionDetail(submissionDetail);
    } else {
      submissionDetailPersistenceService.submitSubmission(submissionDetail);

      if (submissionDetail.isUpdateSubmission()) {
        submissionUpdateService.resolveUpdateOnSubmission(submissionDetail);
      }

      if (canAutoApproveSubmission()) {
        submissionDetailPersistenceService.approveSubmission(submissionDetail);
      } else {
        submissionEmailService.sendSubmittedSubmissionEmails(submissionDetail);
      }
    }

  }

  @Transactional
  public void approveSubmissionOrThrow(SubmissionDetail submissionDetail) {
    if (canApproveSubmission(submissionDetail)) {
      submissionDetailPersistenceService.approveSubmission(submissionDetail);

      submissionEmailService.sendSubmissionApprovalEmail(submissionDetail);
    } else {
      throw new ActionNotAllowedException("Submission cannot be approved by user with ID %s"
          .formatted(userDetailService.getUserWuaId()));
    }
  }

  public boolean canApproveSubmission(SubmissionDetail submissionDetail) {
    return responsibleTeamService
        .isInResponsibleTeam(userDetailService.getUserDetail(), submissionDetail)
        && !submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submissionDetail.getSubmission());
  }

  private boolean canAutoApproveSubmission() {
    var userDetail = userDetailService.getUserDetail();
    return regulatorTeamService.isRegulatorUser(userDetail) || areaTeamService.isAreaUser(userDetail);
  }

  /** Clean any previously entered totals (can happen if more than one reservoir existed originally).
   * If there is only a single reservoir at the point of submission
   * then copy the probability information from that into the totals for the submission.
   * @param submissionDetail the submission detail being submitted
   */
  private void copySingleReservoirIntoTotals(SubmissionDetail submissionDetail) {
    if (!submissionDetail.isDiscoverySubmission()) {
      return;
    }

    var reservoirSubmissionDetails =
        reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail);
    var totals = List.of(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
    );

    if (reservoirSubmissionDetails.size() == 1) {
      probabilityDistributionPersistenceService.deleteProbabilityDistributions(totals, submissionDetail);
      discoveryGasCaseVolumetricTotalsService.deleteGasCaseVolumetricTotalsForSubmissionDetail(submissionDetail);

      var reservoirSubmissionDetail = reservoirSubmissionDetails.get(0);
      if (generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)
          || generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)) {
        probabilityDistributionPersistenceService.copyProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
            reservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
            submissionDetail
        );
        probabilityDistributionPersistenceService.copyProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
            reservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
            submissionDetail
        );
        probabilityDistributionPersistenceService.copyProbabilityDistribution(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            reservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            submissionDetail
        );
      }
      if (generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)
          || generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)) {
        probabilityDistributionPersistenceService.copyProbabilityDistribution(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
            reservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
            submissionDetail
        );
        probabilityDistributionPersistenceService.copyProbabilityDistribution(
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            reservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            submissionDetail
        );

        var gasCaseOutput = gasCaseOutputsPersistenceService.getGasCaseOutput(reservoirSubmissionDetail);
        var hasAssociatedCondensate = gasCaseOutput
            .map(GasCaseOutput::getHasAssociatedCondensateRecoverableResources)
            .orElse(false);

        if (hasAssociatedCondensate) {
          var newGasCaseVolumetricTotal = new GasCaseVolumetricTotal();
          newGasCaseVolumetricTotal.setSubmissionDetail(submissionDetail);
          newGasCaseVolumetricTotal.setHasAssociatedCondensateRecoverableResources(true);
          discoveryGasCaseVolumetricTotalsService.saveDiscoveryGasCaseVolumetricTotals(newGasCaseVolumetricTotal);

          probabilityDistributionPersistenceService.copyProbabilityDistribution(
              ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
              reservoirSubmissionDetail,
              ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
              submissionDetail
          );
        }
      }
      probabilityDistributionPersistenceService.copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
    }
  }
}
