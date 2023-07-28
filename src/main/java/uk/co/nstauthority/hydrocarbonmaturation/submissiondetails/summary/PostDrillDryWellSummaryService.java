package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;


import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class PostDrillDryWellSummaryService {

  private final DryWellDetailsSummaryService dryWellDetailsSummaryService;
  private final PermeabilitySummaryService permeabilitySummaryService;
  private final GeneralDetailSummaryService generalDetailSummaryService;
  private final ReservoirPropertiesSummaryService reservoirPropertiesSummaryService;

  public PostDrillDryWellSummaryService(DryWellDetailsSummaryService dryWellDetailsSummaryService,
                                        PermeabilitySummaryService permeabilitySummaryService,
                                        GeneralDetailSummaryService generalDetailSummaryService,
                                        ReservoirPropertiesSummaryService reservoirPropertiesSummaryService) {
    this.dryWellDetailsSummaryService = dryWellDetailsSummaryService;
    this.permeabilitySummaryService = permeabilitySummaryService;
    this.generalDetailSummaryService = generalDetailSummaryService;
    this.reservoirPropertiesSummaryService = reservoirPropertiesSummaryService;
  }

  public DryWellDetailsSummaryView getDryWellDetailSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return dryWellDetailsSummaryService.getDryWellDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  public GeneralDetailSummaryView getGeneralDetailSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return generalDetailSummaryService.getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  public PermeabilityDetailSummaryView getPermeabilitySummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return permeabilitySummaryService.getPermeabilitySummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  public ReservoirPropertiesSummaryView getReservoirPropertiesSummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return reservoirPropertiesSummaryService
        .getReservoirPropertiesSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }
}
