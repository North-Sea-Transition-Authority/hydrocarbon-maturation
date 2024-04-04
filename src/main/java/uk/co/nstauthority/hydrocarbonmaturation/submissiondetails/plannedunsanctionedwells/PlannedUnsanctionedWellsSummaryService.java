package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.util.StringUtil;

@Service
public class PlannedUnsanctionedWellsSummaryService {
  public static final String PLANNED_UNSANCTIONED_WELLS = "Planned unsanctioned wells";

  public static final String TOTAL_CAPEX = "Total CAPEX for all planned unsanctioned wells";
  public static final String TOTAL_NUMBER_OF_PLANNED_UNSANCTIONED_WELLS = "Total number of planned unsanctioned wells";

  private final PlannedUnsanctionedWellsRepository plannedUnsanctionedWellsRepository;

  @Autowired
  public PlannedUnsanctionedWellsSummaryService(PlannedUnsanctionedWellsRepository plannedUnsanctionedWellsRepository) {
    this.plannedUnsanctionedWellsRepository = plannedUnsanctionedWellsRepository;
  }

  public SummaryCard getPlannedSanctionedWellsCard(SubmissionDetail submissionDetail) {
    var summaryDataView = new SummaryDataView(new ArrayList<>());
    plannedUnsanctionedWellsRepository.findBySubmissionDetail(submissionDetail).ifPresentOrElse(
        plannedUnsanctionedWells -> {
          summaryDataView.addKeyValue(TOTAL_CAPEX, getFormattedTotalCapex(plannedUnsanctionedWells));
          summaryDataView.addKeyValue(TOTAL_NUMBER_OF_PLANNED_UNSANCTIONED_WELLS,
              plannedUnsanctionedWells.getNumberOfPlannedUnsanctionedWells());
        },
        () -> {
          summaryDataView.addKeyValue(TOTAL_CAPEX, "");
          summaryDataView.addKeyValue(TOTAL_NUMBER_OF_PLANNED_UNSANCTIONED_WELLS, "");
        });

    return SummaryCard.simpleSummaryCardWithHeading(PLANNED_UNSANCTIONED_WELLS, summaryDataView);
  }

  private String getFormattedTotalCapex(PlannedUnsanctionedWells plannedUnsanctionedWells) {
    var totalCapex = "";
    if (plannedUnsanctionedWells.getTotalCapex() != null) {
      totalCapex = StringUtil.formatMoneyInMillions(plannedUnsanctionedWells.getTotalCapex());
    }
    return totalCapex;
  }
}
