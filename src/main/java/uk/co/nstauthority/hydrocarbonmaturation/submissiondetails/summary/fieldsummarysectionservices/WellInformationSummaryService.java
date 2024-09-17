package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.fieldsummarysectionservices;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells.PlannedSanctionedWellsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells.PlannedUnsanctionedWellsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class WellInformationSummaryService implements SummarySectionService<SubmissionDetail> {

  public static final String WELL_INFORMATION = "Well information";
  public static final String SUMMARY_DESCRIPTION = "Planned sanctioned wells, planned unsanctioned wells";
  public static final int WELL_INFORMATION_SECTION_ORDER = 20;

  private final PlannedSanctionedWellsSummaryService plannedSanctionedWellsSummaryService;
  private final PlannedUnsanctionedWellsSummaryService plannedUnsanctionedWellsSummaryService;

  @Autowired
  public WellInformationSummaryService(PlannedSanctionedWellsSummaryService plannedSanctionedWellsSummaryService,
                                       PlannedUnsanctionedWellsSummaryService plannedUnsanctionedWellsSummaryService) {
    this.plannedSanctionedWellsSummaryService = plannedSanctionedWellsSummaryService;
    this.plannedUnsanctionedWellsSummaryService = plannedUnsanctionedWellsSummaryService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(SubmissionDetail submissionDetail, User user) {
    var isNotFdpOrFdpa = !(submissionDetail.isFieldDevelopmentPlanSubmission()
        || submissionDetail.isFieldDevelopmentPlanAddendumSubmission());
    if (isNotFdpOrFdpa) {
      return Optional.empty();
    }
    var summaryItems = List.of(getSummaryItem(submissionDetail));
    return Optional.of(new SummarySection(WELL_INFORMATION_SECTION_ORDER, summaryItems, SUMMARY_DESCRIPTION));
  }

  private SummaryItem getSummaryItem(SubmissionDetail submissionDetail) {
    var summaryCards = new ArrayList<SummaryCard>();

    // add sanctioned
    summaryCards.add(plannedSanctionedWellsSummaryService.getPlannedSanctionedWellsCard(submissionDetail));
    summaryCards.add(plannedSanctionedWellsSummaryService.getHoleCompletionTypesSummaryCard(submissionDetail));
    summaryCards.add(plannedSanctionedWellsSummaryService.getPlannedCompletionTypesSummaryCard(submissionDetail));
    summaryCards.add(plannedSanctionedWellsSummaryService.getArtificialLiftMechanismsSummaryCard(submissionDetail));

    // add unsanctioned
    summaryCards.add(plannedUnsanctionedWellsSummaryService.getPlannedSanctionedWellsCard(submissionDetail));

    return SummaryItem.withCards(WELL_INFORMATION, summaryCards);
  }
}
