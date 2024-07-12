package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.fieldsummarysectionservices;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpapromotedvolumes.FdpaPromotedVolumesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class FdpaPromotedVolumesSummarySectionService implements SummarySectionService<SubmissionDetail> {

  public static final String SUMMARY_TITLE = "Promoted resources";
  public static final String SUMMARY_DESCRIPTION = "Resources being promoted to reserves as a result of this FDPA";
  public static final int SECTION_ORDER = 50;

  private final FdpaPromotedVolumesSummaryService fdpaPromotedVolumesSummaryService;

  @Autowired
  public FdpaPromotedVolumesSummarySectionService(
      FdpaPromotedVolumesSummaryService fdpaPromotedVolumesSummaryService) {
    this.fdpaPromotedVolumesSummaryService = fdpaPromotedVolumesSummaryService;
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

    summaryCards.add(fdpaPromotedVolumesSummaryService.getGeneralInformationSummaryCard(submissionDetail));
    summaryCards.add(fdpaPromotedVolumesSummaryService.getPromotedResourcesVolumetricsCard(submissionDetail));

    return SummaryItem.withCards(SUMMARY_TITLE, summaryCards);
  }
}
