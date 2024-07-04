package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics.FieldGasCaseVolumetricTotalsSummaryService.TOTAL_GAS_VOLUMETRICS_ORDER;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.FieldOilCaseVolumetricTotalsSummaryService.TOTAL_VOLUMETRICS_CORRECT;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrictotals.GasCaseVolumetricTotalsRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;

@ExtendWith(MockitoExtension.class)
class FieldGasCaseVolumetricTotalsSummaryServiceTest {

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Mock
  private ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @Mock
  private GasCaseVolumetricTotalsRepository gasCaseVolumetricTotalsRepository;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @InjectMocks
  private FieldGasCaseVolumetricTotalsSummaryService fieldGasCaseVolumetricTotalsSummaryService;

  @Test
  void getSummarySection_notField_thenEmpty() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    assertThat(fieldGasCaseVolumetricTotalsSummaryService.getSummarySection(discoverySubmissionDetail))
        .isEmpty();
  }

  @Test
  void getSummarySection_notApplicableHydrocarbon_thenEmpty() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    when(reservoirHydrocarbonCaseService.isGasCaseOnly(anyList()))
        .thenReturn(false);
    assertThat(fieldGasCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail))
        .isEmpty();
  }

  @Test
  void getSummarySection_validForm_andCarriedForward_assertStructure_withVolumetricQuestioncard() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_COP);

    when(submissionDetailService.hasFieldSubmissionDataBeenCarriedForward(submissionDetail))
        .thenReturn(true);

    var gasCaseVolumetricTotal = new GasCaseVolumetricTotal();
    gasCaseVolumetricTotal.setFieldVolumetricInformationCorrect(true);
    when(gasCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(gasCaseVolumetricTotal));

    when(reservoirHydrocarbonCaseService.isGasCaseOnly(anyList()))
        .thenReturn(true);

    var probabilityTableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            any(),
            eq(submissionDetail),
            eq(null)
        )).thenReturn(probabilityTableView);

    assertThat(fieldGasCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail)).isPresent();

    var summarySection = fieldGasCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail).get();
    assertThat(summarySection.displayOrder()).isEqualTo(TOTAL_GAS_VOLUMETRICS_ORDER);

    var summaryItems = summarySection.summaryItems();
    assertThat(summaryItems).hasSize(1);

    var summaryCards = summaryItems.get(0).summaryCards();
    assertThat(summaryCards).hasSize(3);

    // Gas in place card
    var card1 = summaryCards.get(0);
    assertThat(card1)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        ).containsExactly(
            null,
            SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
            probabilityTableView
        );

    // Resources and reserves card
    var card2 = summaryCards.get(1);
    assertThat(card2)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        ).containsExactly(
            null,
            SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
            probabilityTableView
        );

    // Volumetric total card
    var card3 = summaryCards.get(2);
    assertThat(card3)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType
        ).containsExactly(
            null,
            SummaryCardType.SIMPLE_SUMMARY
        );

    var card3Data = (SummaryDataView) card3.summaryData();
    assertThat(card3Data.keyValues())
        .hasSize(1)
        .containsExactly(new SummaryKeyValue(TOTAL_VOLUMETRICS_CORRECT, "Yes"));
  }

  @Test
  void getSummarySection_validForm_notFdpFdpaOrCop_assertStructure_noVolumetricQuestioncard() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);

    when(submissionDetailService.hasFieldSubmissionDataBeenCarriedForward(submissionDetail))
        .thenReturn(true);

    var gasCaseVolumetricTotal = new GasCaseVolumetricTotal();
    gasCaseVolumetricTotal.setFieldVolumetricInformationCorrect(true);
    when(gasCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(gasCaseVolumetricTotal));

    when(reservoirHydrocarbonCaseService.isGasCaseOnly(anyList()))
        .thenReturn(true);

    var probabilityTableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            any(),
            eq(submissionDetail),
            eq(null)
        )).thenReturn(probabilityTableView);

    assertThat(fieldGasCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail)).isPresent();

    var summarySection = fieldGasCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail).get();
    assertThat(summarySection.displayOrder()).isEqualTo(TOTAL_GAS_VOLUMETRICS_ORDER);

    var summaryItems = summarySection.summaryItems();
    assertThat(summaryItems).hasSize(1);

    var summaryCards = summaryItems.get(0).summaryCards();
    assertThat(summaryCards).hasSize(2);

    // Gas initially in place card
    var card1 = summaryCards.get(0);
    assertThat(card1)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        ).containsExactly(
            null,
            SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
            probabilityTableView
        );

    // Resources and reserves card
    var card2 = summaryCards.get(1);
    assertThat(card2)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        ).containsExactly(
            null,
            SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
            probabilityTableView
        );
  }

  @Test
  void getSummarySection_validForm_notCarriedForward_assertStructure_noVolumetricQuestioncard() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_COP);

    when(submissionDetailService.hasFieldSubmissionDataBeenCarriedForward(submissionDetail))
        .thenReturn(false);

    var gasCaseVolumetricTotal = new GasCaseVolumetricTotal();
    gasCaseVolumetricTotal.setFieldVolumetricInformationCorrect(true);
    when(gasCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(gasCaseVolumetricTotal));

    when(reservoirHydrocarbonCaseService.isGasCaseOnly(anyList()))
        .thenReturn(true);

    var probabilityTableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            any(),
            eq(submissionDetail),
            eq(null)
        )).thenReturn(probabilityTableView);

    assertThat(fieldGasCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail)).isPresent();

    var summarySection = fieldGasCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail).get();
    assertThat(summarySection.displayOrder()).isEqualTo(TOTAL_GAS_VOLUMETRICS_ORDER);

    var summaryItems = summarySection.summaryItems();
    assertThat(summaryItems).hasSize(1);

    var summaryCards = summaryItems.get(0).summaryCards();
    assertThat(summaryCards).hasSize(2);

    // Gas initially in place card
    var card1 = summaryCards.get(0);
    assertThat(card1)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        ).containsExactly(
            null,
            SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
            probabilityTableView
        );

    // Resources and reserves card
    var card2 = summaryCards.get(1);
    assertThat(card2)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        ).containsExactly(
            null,
            SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
            probabilityTableView
        );
  }

  @Test
  void getSummarySection_hasNoAssociatedCondensate() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_COP);

    var probabilityTableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            any(),
            eq(submissionDetail),
            eq(null)
        )).thenReturn(probabilityTableView);
    when(reservoirHydrocarbonCaseService.isGasCaseOnly(anyList()))
        .thenReturn(true);

    var combinedCaseVolumetricTotal = mock(GasCaseVolumetricTotal.class);
    when(combinedCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources())
        .thenReturn(false);
    when(gasCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(combinedCaseVolumetricTotal));

    fieldGasCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail);

    var resourceAndReserveInstances = List.of(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD,

        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    );

    verify(probabilityDistributionSummaryService, times(1))
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            resourceAndReserveInstances,
            submissionDetail,
            null
        );

    verifyNoMoreInteractions(probabilityDistributionSummaryService);
  }

  @Test
  void getSummarySection_hasAssociatedCondensate() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_COP);

    var probabilityTableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            any(),
            eq(submissionDetail),
            eq(null)
        )).thenReturn(probabilityTableView);
    when(reservoirHydrocarbonCaseService.isGasCaseOnly(anyList()))
        .thenReturn(true);

    var combinedCaseVolumetricTotal = mock(GasCaseVolumetricTotal.class);
    when(combinedCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources())
        .thenReturn(true);
    when(gasCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(combinedCaseVolumetricTotal));

    fieldGasCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail);

    var resourceAndReserveInstances = List.of(
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD,

        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    );

    verify(probabilityDistributionSummaryService, times(1))
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            resourceAndReserveInstances,
            submissionDetail,
            null
        );

    verifyNoMoreInteractions(probabilityDistributionSummaryService);
  }
}