package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldgascasevolumetrics.FieldGasCaseVolumetricTotalsSummaryService.TOTAL_GAS_VOLUMETRICS_ORDER;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TotalVolumesTaskListSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;

@ExtendWith(MockitoExtension.class)
class FieldGasCaseVolumetricTotalsSummaryServiceTest {

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Mock
  private TotalVolumesTaskListSectionService totalVolumesTaskListSectionService;

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
    when(totalVolumesTaskListSectionService.isOilCaseOnly(anyList()))
        .thenReturn(true);
    assertThat(fieldGasCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail))
        .isEmpty();
  }

  @Test
  void getSummarySection_validForm_assertStructure() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_COP);

    var probabilitySummaryView = ProbabilityDistributionSummaryView.empty("");
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            any(),
            eq(submissionDetail),
            eq(null)
        )).thenReturn(List.of(probabilitySummaryView));

    assertThat(fieldGasCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail)).isPresent();

    var summarySection = fieldGasCaseVolumetricTotalsSummaryService.getSummarySection(submissionDetail).get();
    assertThat(summarySection.displayOrder()).isEqualTo(TOTAL_GAS_VOLUMETRICS_ORDER);

    var summaryItems = summarySection.summaryItems();
    assertThat(summaryItems).hasSize(1);

    var summaryCards = summaryItems.get(0).summaryCards();
    assertThat(summaryCards).hasSize(2);

    // Gas in place card
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
}