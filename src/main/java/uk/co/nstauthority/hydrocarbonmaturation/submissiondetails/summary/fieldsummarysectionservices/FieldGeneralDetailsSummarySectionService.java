package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.fieldsummarysectionservices;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus.ConsideringCcusSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.FieldComplexitySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class FieldGeneralDetailsSummarySectionService implements SummarySectionService<SubmissionDetail> {
  public static final int GENERAL_DETAIL_SECTION_ORDER = 10;
  public static final String GENERAL_DETAILS = "General details";
  public static final String GENERAL_DETAILS_SUMMARY_TEXT = "General details, considering CCUS, field complexity";

  private final GeneralDetailSummaryService generalDetailSummaryService;
  private final ConsideringCcusSummaryService consideringCcusSummaryService;
  private final FieldComplexitySummaryService fieldComplexitySummaryService;

  @Autowired
  public FieldGeneralDetailsSummarySectionService(GeneralDetailSummaryService generalDetailSummaryService,
                                                  ConsideringCcusSummaryService consideringCcusSummaryService,
                                                  FieldComplexitySummaryService fieldComplexitySummaryService) {
    this.generalDetailSummaryService = generalDetailSummaryService;
    this.consideringCcusSummaryService = consideringCcusSummaryService;
    this.fieldComplexitySummaryService = fieldComplexitySummaryService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(SubmissionDetail submissionDetail) {
    if (!submissionDetail.isFieldSubmission()) {
      return Optional.empty();
    }
    var summaryItems = List.of(
        getGeneralDetailsSummaryItem(submissionDetail)
    );
    return Optional.of(new SummarySection(GENERAL_DETAIL_SECTION_ORDER, summaryItems, GENERAL_DETAILS_SUMMARY_TEXT));
  }

  private SummaryItem getGeneralDetailsSummaryItem(SubmissionDetail submissionDetail) {
    var cards = List.of(
        generalDetailSummaryService.getGeneralDetailsSummary((submissionDetail)),
        consideringCcusSummaryService.getConsideringCcusSummary(submissionDetail),
        fieldComplexitySummaryService.getFieldComplexitySummary(submissionDetail)
    );
    return SummaryItem.withCards(GENERAL_DETAILS, cards);
  }
}
