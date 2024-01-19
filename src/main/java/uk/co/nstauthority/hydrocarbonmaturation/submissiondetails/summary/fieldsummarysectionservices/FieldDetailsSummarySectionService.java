package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.fieldsummarysectionservices;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties.FieldTrapPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class FieldDetailsSummarySectionService implements SummarySectionService<SubmissionDetail> {

  public static final String FIELD_DETAILS = "Field details";
  public static final String FIELD_DETAILS_SUMMARY_TEXT = "Trap properties, source and seal";
  public static final int FIELD_DETAILS_DISPLAY_ORDER = 20;

  private final FieldTrapPropertiesSummaryService fieldTrapPropertiesSummaryService;
  private final SourceAndSealSummaryService sourceAndSealSummaryService;

  @Autowired
  public FieldDetailsSummarySectionService(FieldTrapPropertiesSummaryService fieldTrapPropertiesSummaryService,
                                           SourceAndSealSummaryService sourceAndSealSummaryService) {
    this.fieldTrapPropertiesSummaryService = fieldTrapPropertiesSummaryService;
    this.sourceAndSealSummaryService = sourceAndSealSummaryService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(SubmissionDetail submissionDetail) {
    if (!submissionDetail.isFieldSubmission()) {
      return Optional.empty();
    }

    var summaryCards = List.of(
        fieldTrapPropertiesSummaryService.getFieldTrapPropertiesSummaryCard(submissionDetail),
        fieldTrapPropertiesSummaryService.getFieldTrapPropertiesProbabilitySummaryCard(submissionDetail),
        sourceAndSealSummaryService.getFieldSourceAndSealSummaryCard(submissionDetail)
    );
    var summaryItem = SummaryItem.withCards(FIELD_DETAILS, summaryCards);

    return Optional.of(new SummarySection(FIELD_DETAILS_DISPLAY_ORDER, List.of(summaryItem), FIELD_DETAILS_SUMMARY_TEXT));
  }
}
