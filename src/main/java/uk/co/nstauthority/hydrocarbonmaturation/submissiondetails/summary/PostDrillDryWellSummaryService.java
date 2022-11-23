package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;


import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySummaryService;

@Service
class PostDrillDryWellSummaryService {

  private final DryWellDetailsSummaryService dryWellDetailsSummaryService;
  private final PermeabilitySummaryService permeabilitySummaryService;

  public PostDrillDryWellSummaryService(DryWellDetailsSummaryService dryWellDetailsSummaryService,
                                        PermeabilitySummaryService permeabilitySummaryService) {
    this.dryWellDetailsSummaryService = dryWellDetailsSummaryService;
    this.permeabilitySummaryService = permeabilitySummaryService;
  }

  public DryWellDetailsSummaryView getDryWellDetailSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return dryWellDetailsSummaryService.getDryWellDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  public PermeabilityDetailSummaryView getPermeabilitySummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return permeabilitySummaryService.getPermeabilitySummaryFromSubmissionDetail(submissionDetail);
  }
}
