package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.notify.SubmissionEmailService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverygascasevolumetrictotals.DiscoveryGasCaseVolumetricTotalsService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class SubmissionApprovalServiceTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @Mock
  private SubmissionDetailPersistenceService submissionDetailPersistenceService;
  @Mock
  private RegulatorTeamService regulatorTeamService;
  @Mock
  private UserDetailService userDetailService;
  @Mock
  private SubmissionEmailService submissionEmailService;
  @Mock
  private SubmissionUpdateService submissionUpdateService;
  @Mock
  private ResponsibleTeamService responsibleTeamService;
  @Mock
  private AreaTeamService areaTeamService;
  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;
  @Mock
  private DiscoveryGasCaseVolumetricTotalsService discoveryGasCaseVolumetricTotalsService;
  @Mock
  private GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;
  @InjectMocks
  private SubmissionApprovalService submissionApprovalService;

  @Captor
  private ArgumentCaptor<GasCaseVolumetricTotal> gasCaseVolumetricTotalArgumentCaptor;

  @Nested
  class WhenSubmittingSubmission {

    private SubmissionDetail submissionDetail;

    @BeforeEach
    void setup() {
      submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    }

    @Test
    void whenVersionNumberGreaterThanOne_thenVerifyUpdateResolved() {
      submissionDetail.setVersionNumber(2);
      when(userDetailService.getUserDetail()).thenReturn(USER);
      when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);
      when(areaTeamService.isAreaUser(USER)).thenReturn(false);

      submissionApprovalService.submitSubmission(submissionDetail);

      verify(submissionDetailPersistenceService).submitSubmission(submissionDetail);
      verify(submissionUpdateService, times(1))
          .resolveUpdateOnSubmission(submissionDetail);
      verify(submissionEmailService, times(1))
          .sendSubmittedSubmissionEmails(submissionDetail);
    }

    @Test
    void whenCorrection_thenVerifyOnlyStatusUpdated() {
      submissionDetail.setCorrection(true);

      submissionApprovalService.submitSubmission(submissionDetail);

      var submissionDetailArgumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);
      verify(submissionDetailPersistenceService).saveSubmissionDetail(submissionDetailArgumentCaptor.capture());
      assertEquals(SubmissionDetailStatus.APPROVED, submissionDetailArgumentCaptor.getValue().getStatus());
      verifyNoInteractions(submissionUpdateService);
      verifyNoInteractions(submissionEmailService);
    }

    @Test
    void andCanNotAutoApprove_ThenVerifyCallToSubmit() {
      submissionDetail.setVersionNumber(1);
      when(userDetailService.getUserDetail()).thenReturn(USER);
      when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);
      when(areaTeamService.isAreaUser(USER)).thenReturn(false);

      submissionApprovalService.submitSubmission(submissionDetail);

      verify(submissionDetailPersistenceService).submitSubmission(submissionDetail);
      verifyNoInteractions(submissionUpdateService);
      verify(submissionEmailService, times(1))
          .sendSubmittedSubmissionEmails(submissionDetail);
    }

    @ParameterizedTest
    @CsvSource(value = { "true, true",
                         "true, false",
                         "false, true"})
    void andCanAutoApprove_ThenVerifyCallsToSubmitAndApprove(boolean isRegulatorUser, boolean isAreaUser) {
      when(userDetailService.getUserDetail()).thenReturn(USER);
      when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(isRegulatorUser);
      if (!isRegulatorUser) {
        when(areaTeamService.isAreaUser(USER)).thenReturn(isAreaUser);
      }

      submissionApprovalService.submitSubmission(submissionDetail);

      verify(submissionDetailPersistenceService).submitSubmission(submissionDetail);
      verify(submissionDetailPersistenceService).approveSubmission(submissionDetail);
      verifyNoInteractions(submissionEmailService);
      verifyNoInteractions(submissionUpdateService);
    }
  }

  @Nested
  class WhenApprovingSubmission {

    private SubmissionDetail submissionDetail;


    @BeforeEach
    void setup() {
      submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    }

    @Test
    void approveSubmission_verifyMethodCalls_whenCanApprove() {
      when(userDetailService.getUserDetail()).thenReturn(USER);
      when(responsibleTeamService.isInResponsibleTeam(userDetailService.getUserDetail(), submissionDetail))
          .thenReturn(true);
      submissionApprovalService.approveSubmissionOrThrow(submissionDetail);
      verify(submissionDetailPersistenceService).approveSubmission(submissionDetail);
      verify(submissionEmailService).sendSubmissionApprovalEmail(submissionDetail);
    }

    @Test
    void approveSubmission_verifyNoMethodCalls_whenCannotApprove() {
      when(userDetailService.getUserWuaId()).thenReturn(1);
      assertThatThrownBy(() -> submissionApprovalService.approveSubmissionOrThrow(submissionDetail))
          .isExactlyInstanceOf(ActionNotAllowedException.class)
          .hasMessage("Submission cannot be approved by user with ID %s"
              .formatted(USER.wuaId()));
      verifyNoInteractions(submissionDetailPersistenceService);
      verifyNoInteractions(submissionEmailService);
    }

    @Test
    void canApproveSubmission_whenRegulator_inResponsibleTeam() {
      when(userDetailService.getUserDetail()).thenReturn(USER);
      when(responsibleTeamService.isInResponsibleTeam(userDetailService.getUserDetail(), submissionDetail))
          .thenReturn(true);

      assertThat(submissionApprovalService.canApproveSubmission(submissionDetail)).isTrue();
    }

    @Test
    void canApproveSubmission_whenRegulator_notInResponsibleTeam() {
      when(userDetailService.getUserDetail()).thenReturn(USER);
      when(responsibleTeamService.isInResponsibleTeam(userDetailService.getUserDetail(), submissionDetail))
          .thenReturn(false);

      assertThat(submissionApprovalService.canApproveSubmission(submissionDetail)).isFalse();
    }

    @Test
    void canApproveSubmission_whenRegulator_inResponsibleTeam_hasUnresolvedUpdate_assertFalse() {
      when(userDetailService.getUserDetail()).thenReturn(USER);
      when(responsibleTeamService.isInResponsibleTeam(userDetailService.getUserDetail(), submissionDetail))
          .thenReturn(true);
      when(submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submissionDetail.getSubmission()))
          .thenReturn(true);
      assertThat(submissionApprovalService.canApproveSubmission(submissionDetail)).isFalse();
    }
  }

  @Nested
  class WhenCopySingleReservoirIntoTotals {

    @Test
    void copySingleReservoirIntoTotals_whenNotADiscovery() {
      var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      submissionApprovalService.submitSubmission(submissionDetail);
      verifyNoInteractions(reservoirSubmissionDetailPersistenceService);
      verifyNoInteractions(probabilityDistributionPersistenceService);
      verifyNoInteractions(generalReservoirInformationService);
    }

    @Test
    void copySingleReservoirIntoTotals_whenMoreThanOneReservoir() {
      var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
      when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
              .thenReturn(List.of(new ReservoirSubmissionDetail(), new ReservoirSubmissionDetail()));
      submissionApprovalService.submitSubmission(submissionDetail);
      verifyNoInteractions(probabilityDistributionPersistenceService);
      verifyNoInteractions(generalReservoirInformationService);
    }

    @Test
    void copySingleReservoirIntoTotals_wheOnlyOneReservoir_oilOnlyCase() {
      var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
      var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
      var totals = List.of(
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
      );
      when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
          .thenReturn(List.of(reservoirSubmissionDetail));
      when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)).thenReturn(true);
      when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)).thenReturn(false);
      when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)).thenReturn(false);
      submissionApprovalService.submitSubmission(submissionDetail);
      verify(probabilityDistributionPersistenceService, times(1)).deleteProbabilityDistributions(totals, submissionDetail);
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
    }

    @Test
    void copySingleReservoirIntoTotals_wheOnlyOneReservoir_gasOnlyCase_hasAssociatedCondensate() {
      var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
      var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
      var totals = List.of(
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
      );
      var gasCaseOutput = new GasCaseOutput();
      gasCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
      gasCaseOutput.setHasAssociatedCondensateRecoverableResources(true);
      when(gasCaseOutputsPersistenceService.getGasCaseOutput(reservoirSubmissionDetail)).thenReturn(Optional.of(gasCaseOutput));
      when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
          .thenReturn(List.of(reservoirSubmissionDetail));
      when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)).thenReturn(false);
      when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)).thenReturn(true);
      when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)).thenReturn(false);
      submissionApprovalService.submitSubmission(submissionDetail);
      verify(probabilityDistributionPersistenceService, times(1)).deleteProbabilityDistributions(totals, submissionDetail);
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
      verify(discoveryGasCaseVolumetricTotalsService, times(1)).saveDiscoveryGasCaseVolumetricTotals(gasCaseVolumetricTotalArgumentCaptor.capture());
      assertThat(gasCaseVolumetricTotalArgumentCaptor.getValue().getHasAssociatedCondensateRecoverableResources()).isTrue();
      assertThat(gasCaseVolumetricTotalArgumentCaptor.getValue().getSubmissionDetail()).isEqualTo(submissionDetail);
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
    }

    @Test
    void copySingleReservoirIntoTotals_wheOnlyOneReservoir_gasOnlyCase_doesntHaveAssociatedCondensate() {
      var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
      var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
      var totals = List.of(
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
      );
      var gasCaseOutput = new GasCaseOutput();
      gasCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
      gasCaseOutput.setHasAssociatedCondensateRecoverableResources(false);
      when(gasCaseOutputsPersistenceService.getGasCaseOutput(reservoirSubmissionDetail)).thenReturn(Optional.of(gasCaseOutput));
      when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
          .thenReturn(List.of(reservoirSubmissionDetail));
      when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)).thenReturn(false);
      when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)).thenReturn(true);
      when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)).thenReturn(false);
      submissionApprovalService.submitSubmission(submissionDetail);
      verify(probabilityDistributionPersistenceService, times(1)).deleteProbabilityDistributions(totals, submissionDetail);
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
    }

    @Test
    void copySingleReservoirIntoTotals_wheOnlyOneReservoir_combinedCase_hasAssociatedCondensate() {
      var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
      var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
      var totals = List.of(
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
      );
      var gasCaseOutput = new GasCaseOutput();
      gasCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
      gasCaseOutput.setHasAssociatedCondensateRecoverableResources(true);
      when(gasCaseOutputsPersistenceService.getGasCaseOutput(reservoirSubmissionDetail)).thenReturn(Optional.of(gasCaseOutput));
      when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
          .thenReturn(List.of(reservoirSubmissionDetail));
      when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)).thenReturn(false);
      when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)).thenReturn(false);
      when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)).thenReturn(true);
      submissionApprovalService.submitSubmission(submissionDetail);
      verify(probabilityDistributionPersistenceService, times(1)).deleteProbabilityDistributions(totals, submissionDetail);
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
      verify(discoveryGasCaseVolumetricTotalsService, times(1)).saveDiscoveryGasCaseVolumetricTotals(gasCaseVolumetricTotalArgumentCaptor.capture());
      assertThat(gasCaseVolumetricTotalArgumentCaptor.getValue().getHasAssociatedCondensateRecoverableResources()).isTrue();
      assertThat(gasCaseVolumetricTotalArgumentCaptor.getValue().getSubmissionDetail()).isEqualTo(submissionDetail);
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
    }

    @Test
    void copySingleReservoirIntoTotals_wheOnlyOneReservoir_combinedCase_doesntHaveAssociatedCondensate() {
      var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
      var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
      var totals = List.of(
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
      );
      var gasCaseOutput = new GasCaseOutput();
      gasCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
      gasCaseOutput.setHasAssociatedCondensateRecoverableResources(false);
      when(gasCaseOutputsPersistenceService.getGasCaseOutput(reservoirSubmissionDetail)).thenReturn(Optional.of(gasCaseOutput));
      when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
          .thenReturn(List.of(reservoirSubmissionDetail));
      when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)).thenReturn(false);
      when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)).thenReturn(false);
      when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)).thenReturn(true);
      submissionApprovalService.submitSubmission(submissionDetail);
      verify(probabilityDistributionPersistenceService, times(1)).deleteProbabilityDistributions(totals, submissionDetail);
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
      verify(probabilityDistributionPersistenceService).copyProbabilityDistribution(
          ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
          submissionDetail
      );
    }
  }

}