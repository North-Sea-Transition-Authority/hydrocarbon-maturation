package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldcombinedcasedvolumetrictotals;

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

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.combinedcasevolumetrictotals.CombinedCaseVolumetricTotalsRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TotalVolumesTaskListSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;

@ExtendWith(MockitoExtension.class)
class FieldCombinedCaseVolumetricTotalsSummaryServiceTest {
  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Mock
  private TotalVolumesTaskListSectionService totalVolumesTaskListSectionService;

  @Mock
  private CombinedCaseVolumetricTotalsRepository combinedCaseVolumetricTotalsRepository;

  @InjectMocks
  private FieldCombinedCaseVolumetricTotalsSummaryService fieldCombinedCaseVolumetricTotalsSummaryService;

  @Test
  void getSummarySection_notField_thenEmpty() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    assertThat(fieldCombinedCaseVolumetricTotalsSummaryService.getSummarySection(discoverySubmissionDetail))
        .isEmpty();
  }

  @Test
  void getSummarySection_notApplicableHydrocarbon_thenEmpty() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    when(totalVolumesTaskListSectionService.isCombinedCase(anyList()))
        .thenReturn(false);
    assertThat(fieldCombinedCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail))
        .isEmpty();
  }

  @Test
  void getSummarySection_validForm_hasAssociatedCondensate_assertStructure() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_COP);

    var probabilitySummaryView = ProbabilityDistributionSummaryView.empty("");
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            any(),
            eq(submissionDetail),
            eq(null)
        )).thenReturn(List.of(probabilitySummaryView));
    when(totalVolumesTaskListSectionService.isCombinedCase(anyList()))
        .thenReturn(true);

    assertThat(fieldCombinedCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail)).isPresent();

    var summarySection = fieldCombinedCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail).get();
    assertThat(summarySection.displayOrder()).isEqualTo(TOTAL_GAS_VOLUMETRICS_ORDER);

    var summaryItems = summarySection.summaryItems();
    assertThat(summaryItems).hasSize(1);

    var summaryCards = summaryItems.get(0).summaryCards();
    assertThat(summaryCards).hasSize(2);

    // Hydrocarbons in place card
    var card1 = summaryCards.get(0);
    assertThat(card1)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType
        ).containsExactly(
            null,
            SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY
        );
    var card1Data = (List<ProbabilityDistributionSummaryView>) card1.summaryData();
    assertThat(card1Data)
        .containsOnly(probabilitySummaryView);

    // Resources and reserves card
    var card2 = summaryCards.get(1);
    assertThat(card2)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType
        ).containsExactly(
            null,
            SummaryCardType.FIELD_PROBABILITY_DISTRIBUTION_SUMMARY
        );
    var card2Data = (List<ProbabilityDistributionSummaryView>) card2.summaryData();
    assertThat(card2Data)
        .containsOnly(probabilitySummaryView);
  }

  @Test
  void getSummarySection_isCsr_thenNoReserves() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);

    var probabilitySummaryView = ProbabilityDistributionSummaryView.empty("");
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            any(),
            eq(submissionDetail),
            eq(null)
        )).thenReturn(List.of(probabilitySummaryView));
    when(totalVolumesTaskListSectionService.isCombinedCase(anyList()))
        .thenReturn(true);

    var resourceOnlyInstances = List.of(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD

    );

    fieldCombinedCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail);

    verify(probabilityDistributionSummaryService, times(1))
        .getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            resourceOnlyInstances,
            submissionDetail,
            null
    );

    verifyNoMoreInteractions(probabilityDistributionSummaryService);
  }

  @Test
  void getSummarySection_isNotCsr_thenReserves() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_COP);

    var probabilitySummaryView = ProbabilityDistributionSummaryView.empty("");
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            any(),
            eq(submissionDetail),
            eq(null)
        )).thenReturn(List.of(probabilitySummaryView));
    when(totalVolumesTaskListSectionService.isCombinedCase(anyList()))
        .thenReturn(true);

    var resourceAndReserveInstances = List.of(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD,

        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    );

    fieldCombinedCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail);

    verify(probabilityDistributionSummaryService, times(1))
        .getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            resourceAndReserveInstances,
            submissionDetail,
            null
        );

    verifyNoMoreInteractions(probabilityDistributionSummaryService);
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(booleans =  {false})
  void getSummarySection_hasNoAssociatedCondensate(Boolean hasAssociatedCondensate) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_COP);

    var probabilitySummaryView = ProbabilityDistributionSummaryView.empty("");
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            any(),
            eq(submissionDetail),
            eq(null)
        )).thenReturn(List.of(probabilitySummaryView));
    when(totalVolumesTaskListSectionService.isCombinedCase(anyList()))
        .thenReturn(true);

    var combinedCaseVolumetricTotal = mock(CombinedCaseVolumetricTotal.class);
    when(combinedCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources())
        .thenReturn(hasAssociatedCondensate);
    when(combinedCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(combinedCaseVolumetricTotal));

    fieldCombinedCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail);

    var resourceAndReserveInstances = List.of(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD,

        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    );

    verify(probabilityDistributionSummaryService, times(1))
        .getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            resourceAndReserveInstances,
            submissionDetail,
            null
        );

    verifyNoMoreInteractions(probabilityDistributionSummaryService);
  }

  @Test
  void getSummarySection_hasAssociatedCondensate() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_COP);

    var probabilitySummaryView = ProbabilityDistributionSummaryView.empty("");
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            any(),
            eq(submissionDetail),
            eq(null)
        )).thenReturn(List.of(probabilitySummaryView));
    when(totalVolumesTaskListSectionService.isCombinedCase(anyList()))
        .thenReturn(true);

    var combinedCaseVolumetricTotal = mock(CombinedCaseVolumetricTotal.class);
    when(combinedCaseVolumetricTotal.getHasAssociatedCondensateRecoverableResources())
        .thenReturn(true);
    when(combinedCaseVolumetricTotalsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(combinedCaseVolumetricTotal));

    fieldCombinedCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail);

    var resourceAndReserveInstances = List.of(
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD,

        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_GAS_VOLUMETRICS_GAS_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD,
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD
    );

    verify(probabilityDistributionSummaryService, times(1))
        .getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            resourceAndReserveInstances,
            submissionDetail,
            null
        );

    verifyNoMoreInteractions(probabilityDistributionSummaryService);
  }
}