package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.fieldsummarysectionservices;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fielddevelopmentplanaddendumincreasedvolumes.FieldDevelopmentPlanAddendumIncreasedVolumesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class FieldDevelopmentPlanAddendumIncreasedVolumesSummarySectionService
    implements SummarySectionService<SubmissionDetail> {

  public static final String SUMMARY_TITLE = "Increased volumes";
  public static final String SUMMARY_DESCRIPTION = "Changes to volumetrics as a result of this FDPA";
  public static final int SECTION_ORDER = 50;

  private final FieldDevelopmentPlanAddendumIncreasedVolumesSummaryService fdpaIncreasedVolumesService;

  @Autowired
  public FieldDevelopmentPlanAddendumIncreasedVolumesSummarySectionService(
      FieldDevelopmentPlanAddendumIncreasedVolumesSummaryService fdpaIncreasedVolumesService) {
    this.fdpaIncreasedVolumesService = fdpaIncreasedVolumesService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(SubmissionDetail submissionDetail) {
    if (!submissionDetail.isFieldDevelopmentPlanAddendumSubmission()) {
      return Optional.empty();
    }

    var summaryItems = List.of(getSummaryItem(submissionDetail));
    return Optional.of(new SummarySection(SECTION_ORDER, summaryItems, SUMMARY_DESCRIPTION));
  }

  private SummaryItem getSummaryItem(SubmissionDetail submissionDetail) {
    var summaryCards = new ArrayList<SummaryCard>();

    summaryCards.add(fdpaIncreasedVolumesService.getGeneralInformationSummaryCard(submissionDetail));
    summaryCards.addAll(fdpaIncreasedVolumesService.getVolumetricsSummaryCards(submissionDetail));

    return SummaryItem.withCards(SUMMARY_TITLE, summaryCards);
  }
}
