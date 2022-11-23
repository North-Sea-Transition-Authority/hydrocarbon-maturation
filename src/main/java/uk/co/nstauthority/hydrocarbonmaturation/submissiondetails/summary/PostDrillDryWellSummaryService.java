package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;


import java.util.List;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListSectionService;

@Service
class PostDrillDryWellSummaryService {

  private final DryWellDetailsSummaryService dryWellDetailsSummaryService;
  private final PermeabilitySummaryService permeabilitySummaryService;
  private final List<TaskListSectionService> postWellDrillTaskListSections;

  public PostDrillDryWellSummaryService(DryWellDetailsSummaryService dryWellDetailsSummaryService,
                                        PermeabilitySummaryService permeabilitySummaryService,
                                        List<TaskListSectionService> postWellDrillTaskListSections) {
    this.dryWellDetailsSummaryService = dryWellDetailsSummaryService;
    this.permeabilitySummaryService = permeabilitySummaryService;
    this.postWellDrillTaskListSections = postWellDrillTaskListSections;
  }

  public DryWellDetailsSummaryView getDryWellDetailSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return dryWellDetailsSummaryService.getDryWellDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  public PermeabilityDetailSummaryView getPermeabilitySummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return permeabilitySummaryService.getPermeabilitySummaryFromSubmissionDetail(submissionDetail);
  }
}
