package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilvolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilvolumetrics.FieldOilCaseVolumetricTotalsSummaryService.TOTAL_OIL_VOLUMETRICS_ORDER;

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
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;

@ExtendWith(MockitoExtension.class)
class FieldOilCaseVolumetricTotalsSummaryServiceTest {

    @Mock
    private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

    @InjectMocks
    private FieldOilCaseVolumetricTotalsSummaryService fieldOilCaseVolumetricTotalsSummaryService;

    @Test
    void getSummarySection_notField_thenEmpty() {
        var discoverySubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);
        assertThat(fieldOilCaseVolumetricTotalsSummaryService.getSummarySection(discoverySubmissionDetail))
            .isEmpty();
    }

    @Test
    void getSummarySection_emptyForm_thenEmptyValues() {
        var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_COP);

        var emptyProbabilitySummaryView = ProbabilityDistributionSummaryView.empty("");
        when(probabilityDistributionSummaryService
            .getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
                any(),
                eq(submissionDetail),
                eq(null)
            )).thenReturn(List.of(emptyProbabilitySummaryView));

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
                SummaryCard::summaryCardType
            ).containsExactly(
                null,
                SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY
            );
        var card1Data = (List<ProbabilityDistributionSummaryView>) card1.summaryData();
        assertThat(card1Data)
            .containsOnly(emptyProbabilitySummaryView);

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
            .containsOnly(emptyProbabilitySummaryView);
    }
}