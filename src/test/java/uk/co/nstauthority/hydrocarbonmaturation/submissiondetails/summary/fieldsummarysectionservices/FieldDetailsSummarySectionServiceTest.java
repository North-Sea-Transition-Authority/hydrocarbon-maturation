package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.fieldsummarysectionservices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties.FieldTrapPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

@ExtendWith(MockitoExtension.class)
class FieldDetailsSummarySectionServiceTest {
  @Mock
  private FieldTrapPropertiesSummaryService fieldTrapPropertiesSummaryService;
  @Mock
  private SourceAndSealSummaryService sourceAndSealSummaryService;
  @InjectMocks
  private FieldDetailsSummarySectionService fieldDetailsSummarySectionService;

  @Test
  void getSummarySection_notFieldSubmission_assertEmpty() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);

    assertThat(fieldDetailsSummarySectionService.getSummarySection(submissionDetail))
        .isEmpty();
  }

  @Test
  void getSummarySection_fieldSubmission_assertProperties() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);

    var fieldTrapPropertiesCard = new SummaryCard("Field trap properties",
        SummaryCardType.SIMPLE_SUMMARY, null);
    var fieldTrapPropertiesProbabilitiesCard = new SummaryCard("Field trap probabilities",
        SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY, null);
    var sourceAndSealCard = new SummaryCard("Source and seal",
        SummaryCardType.SIMPLE_SUMMARY, null);

    when(fieldTrapPropertiesSummaryService.getFieldTrapPropertiesSummaryCard(submissionDetail))
        .thenReturn(fieldTrapPropertiesCard);
    when(fieldTrapPropertiesSummaryService.getFieldTrapPropertiesProbabilitySummaryCard(submissionDetail))
        .thenReturn(fieldTrapPropertiesProbabilitiesCard);
    when(sourceAndSealSummaryService.getFieldSourceAndSealSummaryCard(submissionDetail))
        .thenReturn(sourceAndSealCard);

    var expectedSummaryItem = SummaryItem.withCards(
        FieldDetailsSummarySectionService.FIELD_DETAILS,
        List.of(
            fieldTrapPropertiesCard,
            fieldTrapPropertiesProbabilitiesCard,
            sourceAndSealCard
        ));

    var expectedSummarySection = Optional.of(new SummarySection(
        FieldDetailsSummarySectionService.FIELD_DETAILS_DISPLAY_ORDER,
        List.of(expectedSummaryItem),
        FieldDetailsSummarySectionService.FIELD_DETAILS_SUMMARY_TEXT));

    assertThat(fieldDetailsSummarySectionService.getSummarySection(submissionDetail))
        .isPresent()
        .usingRecursiveComparison()
        .isEqualTo(expectedSummarySection);
  }
}