package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySummaryService;

@Service
class DiscoverySummaryService {

  private final GasFluidPropertySummaryService gasFluidPropertySummaryService;
  private final OilFluidPropertySummaryService oilFluidPropertySummaryService;
  private final GeneralFluidPropertySummaryService generalFluidPropertySummaryService;
  private final PermeabilitySummaryService permeabilitySummaryService;

  @Autowired
  DiscoverySummaryService(GasFluidPropertySummaryService gasFluidPropertySummaryService,
                          OilFluidPropertySummaryService oilFluidPropertySummaryService,
                          GeneralFluidPropertySummaryService generalFluidPropertySummaryService,
                          PermeabilitySummaryService permeabilitySummaryService) {
    this.gasFluidPropertySummaryService = gasFluidPropertySummaryService;
    this.oilFluidPropertySummaryService = oilFluidPropertySummaryService;
    this.generalFluidPropertySummaryService = generalFluidPropertySummaryService;
    this.permeabilitySummaryService = permeabilitySummaryService;
  }

  public GasFluidPropertySummaryView getGasFluidPropertySummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return gasFluidPropertySummaryService.getGasFluidPropertySummaryFromSubmissionDetail(submissionDetail);
  }

  public OilFluidPropertySummaryView getOilFluidPropertySummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return oilFluidPropertySummaryService.getOilFluidPropertySummaryFromSubmissionDetail(submissionDetail);
  }

  public GeneralFluidPropertySummaryView getGeneralFluidPropertySummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return generalFluidPropertySummaryService.getGeneralFluidPropertySummaryFromSubmissionDetail(submissionDetail);
  }

  public PermeabilityDetailSummaryView getPermeabilitySummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return permeabilitySummaryService.getPermeabilitySummaryFromSubmissionDetail(submissionDetail);
  }
}
