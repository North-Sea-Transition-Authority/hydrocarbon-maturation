package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals.DiscoveryCombinedCaseVolTotalsService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals.DiscoveryGasCaseVolumetricTotalsService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverymixedcasevolumetrics.DiscoveryMixedCaseVolumetricsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TotalVolumesTaskListSectionService;

@ExtendWith(MockitoExtension.class)
class ReservoirTotalsCleanAndCopyServiceTest {

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
  @Mock
  private DiscoveryMixedCaseVolumetricsPersistenceService discoveryMixedCaseVolumetricsPersistenceService;
  @Mock
  private DiscoveryCombinedCaseVolTotalsService discoveryCombinedCaseVolTotalsService;
  @Mock
  private TotalVolumesTaskListSectionService totalVolumesTaskListSectionService;

  @InjectMocks
  private ReservoirTotalsCleanAndCopyService reservoirTotalsCleanAndCopyService;

  @Captor
  private ArgumentCaptor<GasCaseVolumetricTotal> gasCaseVolumetricTotalArgumentCaptor;

  @Captor
  private ArgumentCaptor<CombinedCaseVolumetricTotal> combinedCaseVolumetricTotalArgumentCaptor;

  @Test
  void copySingleReservoirIntoTotals_whenNotADiscovery() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    reservoirTotalsCleanAndCopyService.cleanAndCopyDiscoveryVolumetricTotals(submissionDetail);
    verifyNoInteractions(reservoirSubmissionDetailPersistenceService);
    verifyNoInteractions(probabilityDistributionPersistenceService);
    verifyNoInteractions(generalReservoirInformationService);
  }

  @Test
  void copySingleReservoirIntoTotals_whenMoreThanOneReservoir() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(new ReservoirSubmissionDetail(), new ReservoirSubmissionDetail()));
    reservoirTotalsCleanAndCopyService.cleanAndCopyDiscoveryVolumetricTotals(submissionDetail);
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
    reservoirTotalsCleanAndCopyService.cleanAndCopyDiscoveryVolumetricTotals(submissionDetail);
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
    reservoirTotalsCleanAndCopyService.cleanAndCopyDiscoveryVolumetricTotals(submissionDetail);
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
    reservoirTotalsCleanAndCopyService.cleanAndCopyDiscoveryVolumetricTotals(submissionDetail);
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
    var mixedCaseOutput = new MixedCaseOutput();
    mixedCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    mixedCaseOutput.setHasAssociatedCondensateRecoverableResources(true);
    when(discoveryMixedCaseVolumetricsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail)).thenReturn(Optional.of(mixedCaseOutput));
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)).thenReturn(false);
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)).thenReturn(false);
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)).thenReturn(true);
    reservoirTotalsCleanAndCopyService.cleanAndCopyDiscoveryVolumetricTotals(submissionDetail);
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
    verify(discoveryCombinedCaseVolTotalsService, times(1))
        .saveDiscoveryCombinedCaseVolumetricTotals(combinedCaseVolumetricTotalArgumentCaptor.capture());
    assertThat(combinedCaseVolumetricTotalArgumentCaptor.getValue().getHasAssociatedCondensateRecoverableResources()).isTrue();
    assertThat(combinedCaseVolumetricTotalArgumentCaptor.getValue().getSubmissionDetail()).isEqualTo(submissionDetail);
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
    var mixedCaseOutput = new MixedCaseOutput();
    mixedCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    mixedCaseOutput.setHasAssociatedCondensateRecoverableResources(false);
    when(discoveryMixedCaseVolumetricsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail)).thenReturn(Optional.of(mixedCaseOutput));
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)).thenReturn(false);
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)).thenReturn(false);
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)).thenReturn(true);
    reservoirTotalsCleanAndCopyService.cleanAndCopyDiscoveryVolumetricTotals(submissionDetail);
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

  @Test
  void cleanTotals_oilOnlyCase_deleteGasTotals() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var reservoirSubmissionDetails = mock(List.class);
    when(reservoirSubmissionDetails.size()).thenReturn(2);
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(totalVolumesTaskListSectionService.isOilCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);

    reservoirTotalsCleanAndCopyService.cleanAndCopyDiscoveryVolumetricTotals(submissionDetail);

    verifyDeleteGasOnlyDiscoveryTotals(submissionDetail);
  }

  @Test
  void cleanTotals_gasOnlyCase_deleteOilTotals() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var reservoirSubmissionDetails = mock(List.class);
    when(reservoirSubmissionDetails.size()).thenReturn(2);
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(totalVolumesTaskListSectionService.isGasCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);

    reservoirTotalsCleanAndCopyService.cleanAndCopyDiscoveryVolumetricTotals(submissionDetail);

    verifyDeleteOilOnlyDiscoveryTotals(submissionDetail);
  }

  void verifyDeleteOilOnlyDiscoveryTotals(SubmissionDetail submissionDetail) {
    List<ProbabilityDistributionFieldInstance> totals = List.of(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    );

    verify(probabilityDistributionPersistenceService, times(1)).deleteProbabilityDistributions(totals, submissionDetail);
    verifyNoMoreInteractions(probabilityDistributionPersistenceService);
  }

  void verifyDeleteGasOnlyDiscoveryTotals(SubmissionDetail submissionDetail) {
    List<ProbabilityDistributionFieldInstance> totals = List.of(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY
    );

    verify(probabilityDistributionPersistenceService, times(1)).deleteProbabilityDistributions(totals, submissionDetail);
    verifyNoMoreInteractions(probabilityDistributionPersistenceService);
  }
}
