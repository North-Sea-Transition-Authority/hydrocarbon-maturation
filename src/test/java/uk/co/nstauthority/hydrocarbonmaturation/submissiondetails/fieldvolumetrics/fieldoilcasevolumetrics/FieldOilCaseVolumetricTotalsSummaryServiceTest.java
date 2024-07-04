package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.FieldOilCaseVolumetricTotalsSummaryService.TOTAL_OIL_VOLUMETRICS_ORDER;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.FieldOilCaseVolumetricTotalsSummaryService.TOTAL_VOLUMETRICS_CORRECT;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrictotals.OilCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrictotals.OilCaseVolumetricTotalsRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;

@ExtendWith(MockitoExtension.class)
class FieldOilCaseVolumetricTotalsSummaryServiceTest {

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private OilCaseVolumetricTotalsRepository oilCaseVolumetricTotalsRepository;

  @InjectMocks
  private FieldOilCaseVolumetricTotalsSummaryService fieldOilCaseVolumetricTotalsSummaryService;

  @Test
  void getSummarySection_notField_thenEmpty() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    assertThat(fieldOilCaseVolumetricTotalsSummaryService.getSummarySection(discoverySubmissionDetail))
        .isEmpty();
  }

  @Test
  void getSummarySection_notOilCase_thenEmpty() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);

    var reservoirSubmissionDetails = mock(List.class);
    when(reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail)
    ).thenReturn(reservoirSubmissionDetails);

    when(reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails))
        .thenReturn(false);

    assertThat(fieldOilCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail))
        .isEmpty();
  }

  @Test
  void getSummarySection_emptyForm_andFieldSubmissionNotCarriedForward_thenEmptyValues_andNoVolumetricCard() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_COP);

    when(submissionDetailService.hasFieldSubmissionDataBeenCarriedForward(submissionDetail))
        .thenReturn(false);

    var reservoirSubmissionDetails = mock(List.class);
    when(reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail)
    ).thenReturn(reservoirSubmissionDetails);

    when(reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);

    var probabilityTableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            any(),
            eq(submissionDetail),
            eq(null)
        )).thenReturn(probabilityTableView);

    assertThat(fieldOilCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail)).isPresent();

    var summarySection = fieldOilCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail).get();
    assertThat(summarySection.displayOrder()).isEqualTo(TOTAL_OIL_VOLUMETRICS_ORDER);

    var summaryItems = summarySection.summaryItems();
    assertThat(summaryItems).hasSize(1);

    var summaryCards = summaryItems.get(0).summaryCards();
    assertThat(summaryCards).hasSize(2);

    // Oil initially in place card
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
  void getSummarySection_emptyForm_andNotFdpFdpaOrCop_thenEmptyValues_andNoVolumetricCard() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);

    when(submissionDetailService.hasFieldSubmissionDataBeenCarriedForward(submissionDetail))
        .thenReturn(true);

    var reservoirSubmissionDetails = mock(List.class);
    when(reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail)
    ).thenReturn(reservoirSubmissionDetails);

    when(reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);

    var probabilityTableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            any(),
            eq(submissionDetail),
            eq(null)
        )).thenReturn(probabilityTableView);

    assertThat(fieldOilCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail)).isPresent();

    var summarySection = fieldOilCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail).get();
    assertThat(summarySection.displayOrder()).isEqualTo(TOTAL_OIL_VOLUMETRICS_ORDER);

    var summaryItems = summarySection.summaryItems();
    assertThat(summaryItems).hasSize(1);

    var summaryCards = summaryItems.get(0).summaryCards();
    assertThat(summaryCards).hasSize(2);

    // Oil in place card
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
  void getSummarySection_emptyForm_andFieldSubmissionCarriedForward_thenEmptyValues_withVolumetricCard() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_COP);

    var oilCaseVolumetricTotal = new OilCaseVolumetricTotal();
    oilCaseVolumetricTotal.setFieldVolumetricInformationCorrect(true);
    when(submissionDetailService.hasFieldSubmissionDataBeenCarriedForward(submissionDetail))
        .thenReturn(true);
    when(oilCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(oilCaseVolumetricTotal));

    var reservoirSubmissionDetails = mock(List.class);
    when(reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail)
    ).thenReturn(reservoirSubmissionDetails);

    when(reservoirHydrocarbonCaseService.isOilCaseOnly(reservoirSubmissionDetails))
        .thenReturn(true);

    var probabilityTableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            any(),
            eq(submissionDetail),
            eq(null)
        )).thenReturn(probabilityTableView);

    assertThat(fieldOilCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail)).isPresent();

    var summarySection = fieldOilCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail).get();
    assertThat(summarySection.displayOrder()).isEqualTo(TOTAL_OIL_VOLUMETRICS_ORDER);

    var summaryItems = summarySection.summaryItems();
    assertThat(summaryItems).hasSize(1);

    var summaryCards = summaryItems.get(0).summaryCards();
    assertThat(summaryCards).hasSize(3);

    // Oil in place card
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
}