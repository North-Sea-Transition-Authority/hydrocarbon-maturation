package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.prospectmixedcaseoutputs.ProspectMixedCaseOutputsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;

@ExtendWith(MockitoExtension.class)
class HydrocarbonCasePersistenceServiceTest {

  @Mock
  private HydrocarbonCaseRepository hydrocarbonCaseRepository;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Mock
  private OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;

  @Mock
  private GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;

  @Mock
  private ProspectMixedCaseOutputsPersistenceService prospectMixedCaseOutputsPersistenceService;

  private HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  private SubmissionDetail submissionDetail;

  private HydrocarbonCaseDetail hydrocarbonCase;

  @BeforeEach
  void setup() {
    hydrocarbonCasePersistenceService = new HydrocarbonCasePersistenceService(
        hydrocarbonCaseRepository,
        probabilityDistributionPersistenceService,
        reservoirSubmissionDetailPersistenceService,
        oilCaseOutputsPersistenceService,
        gasCaseOutputsPersistenceService,
        prospectMixedCaseOutputsPersistenceService);

    UUID submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
    hydrocarbonCase = new HydrocarbonCaseDetail();
  }

  @Test
  void saveHydrocarbonCaseDetails_whenGivenAForm_verifyCreatedEntity() {
    var form = new HydrocarbonCaseForm();
    form.setOilOnlyCaseType(true);
    form.setGasOnlyCaseType(true);
    form.setOilAndGasCaseType(true);

    form.setOilOnlyProbability(new DecimalInput("100"));
    form.setGasOnlyProbability(new DecimalInput("20"));
    form.setOilAndGasProbability(new DecimalInput("30"));

    var hydrocarbonCaseArgumentCaptor = ArgumentCaptor.forClass(HydrocarbonCaseDetail.class);

    hydrocarbonCasePersistenceService.saveHydrocarbonCase(form, submissionDetail);

    verify(hydrocarbonCaseRepository, times(1)).save(hydrocarbonCaseArgumentCaptor.capture());
    var savedHydrocarbonCase = (HydrocarbonCaseDetail) hydrocarbonCaseArgumentCaptor.getValue();

    assertThat(savedHydrocarbonCase)
        .extracting(
            HydrocarbonCaseDetail::getOilOnly,
            HydrocarbonCaseDetail::getGasOnly,
            HydrocarbonCaseDetail::getOilAndGas,
            HydrocarbonCaseDetail::getGasOnlyProbability,
            HydrocarbonCaseDetail::getOilOnlyProbability,
            HydrocarbonCaseDetail::getOilAndGasProbability,
            HydrocarbonCaseDetail::getGasOnlyComments,
            HydrocarbonCaseDetail::getOilAndGasComments,
            HydrocarbonCaseDetail::getOilOnlyComments
        )
        .containsExactly(
            form.getOilOnlyCaseType(),
            form.getGasOnlyCaseType(),
            form.getOilAndGasCaseType(),
            form.getGasOnlyProbability().createBigDecimalOrNull(),
            form.getOilOnlyProbability().createBigDecimalOrNull(),
            form.getOilAndGasProbability().createBigDecimalOrNull(),
            form.getGasOnlyComments(),
            form.getOilAndGasComments(),
            form.getOilOnlyComments()
        );
  }

  @Test
  void saveHydrocarbonCaseDetails_givenEmptyForm_verifyCreatedEntity() {
    var form = new HydrocarbonCaseForm();

    var hydrocarbonCaseArgumentCaptor = ArgumentCaptor.forClass(HydrocarbonCaseDetail.class);

    hydrocarbonCasePersistenceService.saveHydrocarbonCase(form, submissionDetail);

    verify(hydrocarbonCaseRepository, times(1)).save(hydrocarbonCaseArgumentCaptor.capture());
    var savedHydrocarbonCase = (HydrocarbonCaseDetail) hydrocarbonCaseArgumentCaptor.getValue();

    assertThat(savedHydrocarbonCase)
        .extracting(
            HydrocarbonCaseDetail::getOilOnly,
            HydrocarbonCaseDetail::getGasOnly,
            HydrocarbonCaseDetail::getOilAndGas,
            HydrocarbonCaseDetail::getGasOnlyProbability,
            HydrocarbonCaseDetail::getOilOnlyProbability,
            HydrocarbonCaseDetail::getOilAndGasProbability,
            HydrocarbonCaseDetail::getGasOnlyComments,
            HydrocarbonCaseDetail::getOilAndGasComments,
            HydrocarbonCaseDetail::getOilOnlyComments
        )
        .containsExactly(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
  }

  @Test
  void getHydrocarbonCaseDetail_verifyMethodCall() {
    hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail);
    verify(hydrocarbonCaseRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }

  @Test
  void isGasOnlyPresent_whenGasOnly_isTrue() {
    hydrocarbonCase.setGasOnly(true);
    assertTrue(hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCase));
  }

  @Test
  void isGasOnlyPresent_whenOilAndGas_isFalse() {
    hydrocarbonCase.setOilAndGas(true);
    hydrocarbonCase.setOilAndGasProbability(BigDecimal.valueOf(1.5));
    assertFalse(hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCase));
  }

  @Test
  void isGasOnlyPresent_whenNoGas_isFalse() {
    assertFalse(hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCase));
  }

  @Test
  void isMixedPresent_whenGasAndOil_isTrue() {
    hydrocarbonCase.setOilAndGas(true);
    assertTrue(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCase));
  }

  @Test
  void isMixedPresent_whenOilOnlyAndGasOnly_isFalse() {
    hydrocarbonCase.setOilOnly(true);
    hydrocarbonCase.setGasOnly(true);
    assertFalse(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCase));
  }

  @Test
  void isOilOnlyPresent_whenGasOnly_isTrue() {
    hydrocarbonCase.setOilOnly(true);
    assertTrue(hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCase));
  }

  @Test
  void isOilOnlyPresent_whenOilAndGas_isFalse() {
    hydrocarbonCase.setOilAndGas(true);
    assertFalse(hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCase));
  }

  @Test
  void isOilOnlyPresent_whenNoGas_isFalse() {
    assertFalse(hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCase));
  }

  @Test
  void hydrocarbonsNotPresent_assertCleanUp() {
    var form = new HydrocarbonCaseForm();
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);
    hydrocarbonCasePersistenceService.saveHydrocarbonCase(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1)).deleteProbabilityDistributions(
        List.of(
            ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_PROSPECT,
            ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
            ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR,
            ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR,
            ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_PROSPECT,
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT,
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
        ),
        reservoirSubmissionDetail
    );

    verify(gasCaseOutputsPersistenceService, times(1))
        .deleteGasCaseOutputForReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1)).deleteProbabilityDistributions(
        List.of(
            ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_PROSPECT,
            ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_PROSPECT,
            ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_PROSPECT,
            ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT,
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT,
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
        ),
        reservoirSubmissionDetail
    );

    verify(oilCaseOutputsPersistenceService, times(1))
        .deleteOilCaseOutputForReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1)).deleteProbabilityDistributions(
        List.of(
            ProbabilityDistributionFieldInstance.MIXED_OIL_FORMATION_VOLUME_FACTOR_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_GAS_FORMATION_VOLUME_FACTOR_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
            ProbabilityDistributionFieldInstance.MIXED_GAS_EXPANSION_FACTOR,
            ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
            ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR,
            ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
        ),
        reservoirSubmissionDetail
    );

    verify(prospectMixedCaseOutputsPersistenceService, times(1))
        .deleteMixedCaseOutputForReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void gasCasePresent_noGasCleanUp() {
    var form = new HydrocarbonCaseForm();
    form.setGasOnlyCaseType(true);
    form.setGasOnlyProbability(new DecimalInput("100"));
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);
    hydrocarbonCasePersistenceService.saveHydrocarbonCase(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, never()).deleteProbabilityDistributions(
        List.of(
            ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_PROSPECT,
            ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
            ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR,
            ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR,
            ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_PROSPECT,
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT,
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
        ),
        reservoirSubmissionDetail
    );

    verifyNoInteractions(gasCaseOutputsPersistenceService);
  }

  @Test
  void oilCasePresent_noOilCleanUp() {
    var form = new HydrocarbonCaseForm();
    form.setOilOnlyCaseType(true);
    form.setOilOnlyProbability(new DecimalInput("100"));
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);
    hydrocarbonCasePersistenceService.saveHydrocarbonCase(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, never()).deleteProbabilityDistributions(
        List.of(
            ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_PROSPECT,
            ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_PROSPECT,
            ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_PROSPECT,
            ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT,
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT,
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
        ),
        reservoirSubmissionDetail
    );

    verifyNoInteractions(oilCaseOutputsPersistenceService);
  }

  @Test
  void mixedCasePresent_noGasCleanUp() {
    var form = new HydrocarbonCaseForm();
    form.setOilAndGasCaseType(true);
    form.setOilAndGasProbability(new DecimalInput("100"));
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);
    hydrocarbonCasePersistenceService.saveHydrocarbonCase(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, never()).deleteProbabilityDistributions(
        List.of(
            ProbabilityDistributionFieldInstance.MIXED_OIL_FORMATION_VOLUME_FACTOR_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_GAS_FORMATION_VOLUME_FACTOR_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
            ProbabilityDistributionFieldInstance.MIXED_GAS_EXPANSION_FACTOR,
            ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
            ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR,
            ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
            ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
        ),
        reservoirSubmissionDetail
    );

    verifyNoInteractions(prospectMixedCaseOutputsPersistenceService);
  }

  @Test
  void getHydrocarbonsDiscoveredForReporting_whenNoHydrocarbonCase_returnsNull() {
    when(hydrocarbonCaseRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.empty());

    var result = hydrocarbonCasePersistenceService.getHydrocarbonsDiscoveredForReporting(submissionDetail);

    assertThat(result).isNull();
  }

  @Test
  void getHydrocarbonsDiscoveredForReporting_whenMixedOilAndGas_returnsMixed() {
    hydrocarbonCase.setOilAndGas(true);
    when(hydrocarbonCaseRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(hydrocarbonCase));

    var result = hydrocarbonCasePersistenceService.getHydrocarbonsDiscoveredForReporting(submissionDetail);

    assertThat(result).isEqualTo(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
  }

  @Test
  void getHydrocarbonsDiscoveredForReporting_whenGasOnlyTrue_returnsGasOnly() {
    hydrocarbonCase.setGasOnly(true);
    hydrocarbonCase.setOilOnly(false);
    when(hydrocarbonCaseRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(hydrocarbonCase));

    var result = hydrocarbonCasePersistenceService.getHydrocarbonsDiscoveredForReporting(submissionDetail);

    assertThat(result).isEqualTo(HydrocarbonsDiscovered.GAS_ONLY);
  }

  @Test
  void getHydrocarbonsDiscoveredForReporting_whenOilOnlyTrue_returnsOilOnly() {
    hydrocarbonCase.setOilOnly(true);
    hydrocarbonCase.setGasOnly(false);
    when(hydrocarbonCaseRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(hydrocarbonCase));

    var result = hydrocarbonCasePersistenceService.getHydrocarbonsDiscoveredForReporting(submissionDetail);

    assertThat(result).isEqualTo(HydrocarbonsDiscovered.OIL_ONLY);
  }

  @Test
  void getHydrocarbonsDiscoveredForReporting_whenBothPresent_gasProbabilityGreater_returnsGasOnly() {
    hydrocarbonCase.setGasOnly(true);
    hydrocarbonCase.setOilOnly(true);
    hydrocarbonCase.setGasOnlyProbability(BigDecimal.valueOf(0.7));
    hydrocarbonCase.setOilOnlyProbability(BigDecimal.valueOf(0.3));
    when(hydrocarbonCaseRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(hydrocarbonCase));

    var result = hydrocarbonCasePersistenceService.getHydrocarbonsDiscoveredForReporting(submissionDetail);

    assertThat(result).isEqualTo(HydrocarbonsDiscovered.GAS_ONLY);
  }

  @Test
  void getHydrocarbonsDiscoveredForReporting_whenBothPresent_oilProbabilityGreater_returnsOilOnly() {
    hydrocarbonCase.setGasOnly(true);
    hydrocarbonCase.setOilOnly(true);
    hydrocarbonCase.setGasOnlyProbability(BigDecimal.valueOf(0.2));
    hydrocarbonCase.setOilOnlyProbability(BigDecimal.valueOf(0.8));
    when(hydrocarbonCaseRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(hydrocarbonCase));

    var result = hydrocarbonCasePersistenceService.getHydrocarbonsDiscoveredForReporting(submissionDetail);

    assertThat(result).isEqualTo(HydrocarbonsDiscovered.OIL_ONLY);
  }

  @Test
  void getHydrocarbonsDiscoveredForReporting_whenBothPresent_probabilitiesEqual_returnsNull() {
    hydrocarbonCase.setGasOnly(true);
    hydrocarbonCase.setOilOnly(true);
    hydrocarbonCase.setGasOnlyProbability(BigDecimal.valueOf(0.5));
    hydrocarbonCase.setOilOnlyProbability(BigDecimal.valueOf(0.5));
    when(hydrocarbonCaseRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(hydrocarbonCase));

    var result = hydrocarbonCasePersistenceService.getHydrocarbonsDiscoveredForReporting(submissionDetail);

    assertThat(result).isNull();
  }
}