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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus.ConsideringCcusSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.FieldComplexitySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

@ExtendWith(MockitoExtension.class)
class FieldGeneralDetailsSummarySectionServiceTest {
  @Mock
  private GeneralDetailSummaryService generalDetailSummaryService;
  @Mock
  private ConsideringCcusSummaryService consideringCcusSummaryService;
  @Mock
  private FieldComplexitySummaryService fieldComplexitySummaryService;
  @InjectMocks
  private FieldGeneralDetailsSummarySectionService fieldGeneralDetailsSummarySectionService;

  @Test
  void getSummarySection_notFieldSubmission_assertEmpty() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);

    assertThat(fieldGeneralDetailsSummarySectionService.getSummarySection(submissionDetail))
        .isEmpty();
  }

  @Test
  void getSummarySection_fieldSubmission_assertProperties() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);

    var generalDetailsCard = new SummaryCard("General details", SummaryCardType.SIMPLE_SUMMARY, null);
    var consideringCcusCard = new SummaryCard("Considering CCUS", SummaryCardType.SIMPLE_SUMMARY, null);
    var fieldComplexityCard = new SummaryCard("Field complexity", SummaryCardType.SIMPLE_SUMMARY, null);

    when(generalDetailSummaryService.getGeneralDetailsSummary(submissionDetail))
        .thenReturn(generalDetailsCard);
    when(consideringCcusSummaryService.getConsideringCcusSummary(submissionDetail))
        .thenReturn(consideringCcusCard);
    when(fieldComplexitySummaryService.getFieldComplexitySummary(submissionDetail))
        .thenReturn(fieldComplexityCard);

    var expectedSummaryItem = SummaryItem.withCards(
        FieldGeneralDetailsSummarySectionService.GENERAL_DETAILS,
        List.of(
            generalDetailsCard,
            consideringCcusCard,
            fieldComplexityCard
        ));

    var expectedSummarySection = Optional.of(new SummarySection(
        FieldGeneralDetailsSummarySectionService.GENERAL_DETAIL_SECTION_ORDER,
        List.of(expectedSummaryItem),
        FieldGeneralDetailsSummarySectionService.GENERAL_DETAILS_SUMMARY_TEXT));

    assertThat(fieldGeneralDetailsSummarySectionService.getSummarySection(submissionDetail))
        .isPresent()
        .usingRecursiveComparison()
        .isEqualTo(expectedSummarySection);
  }
}