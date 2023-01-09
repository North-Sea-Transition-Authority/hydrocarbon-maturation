package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics.MixedCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView;

@Service
class DiscoverySummaryService {

  private final GasFluidPropertySummaryService gasFluidPropertySummaryService;
  private final OilFluidPropertySummaryService oilFluidPropertySummaryService;
  private final GeneralFluidPropertySummaryService generalFluidPropertySummaryService;
  private final PermeabilitySummaryService permeabilitySummaryService;
  private final GeneralDetailSummaryService generalDetailSummaryService;
  private final ReservoirPropertiesSummaryService reservoirPropertiesSummaryService;
  private final MixedCaseVolumetricsSummaryService mixedCaseVolumetricsSummaryService;
  private final MixedCaseOutputsSummaryService mixedCaseOutputsSummaryService;
  private final OilCaseVolumetricsSummaryService oilCaseVolumetricsSummaryService;
  private final OilCaseOutputsSummaryService oilCaseOutputsSummaryService;
  private final GasCaseVolumetricsSummaryService gasCaseVolumetricsSummaryService;
  private final GasCaseOutputsSummaryService gasCaseOutputsSummaryService;

  @Autowired
  DiscoverySummaryService(GasFluidPropertySummaryService gasFluidPropertySummaryService,
                          OilFluidPropertySummaryService oilFluidPropertySummaryService,
                          GeneralFluidPropertySummaryService generalFluidPropertySummaryService,
                          PermeabilitySummaryService permeabilitySummaryService,
                          GeneralDetailSummaryService generalDetailSummaryService,
                          ReservoirPropertiesSummaryService reservoirPropertiesSummaryService,
                          MixedCaseVolumetricsSummaryService mixedCaseVolumetricsSummaryService,
                          MixedCaseOutputsSummaryService mixedCaseOutputsSummaryService,
                          OilCaseVolumetricsSummaryService oilCaseVolumetricsSummaryService,
                          OilCaseOutputsSummaryService oilCaseOutputsSummaryService,
                          GasCaseVolumetricsSummaryService gasCaseVolumetricsSummaryService,
                          GasCaseOutputsSummaryService gasCaseOutputsSummaryService) {
    this.gasFluidPropertySummaryService = gasFluidPropertySummaryService;
    this.oilFluidPropertySummaryService = oilFluidPropertySummaryService;
    this.generalFluidPropertySummaryService = generalFluidPropertySummaryService;
    this.permeabilitySummaryService = permeabilitySummaryService;
    this.generalDetailSummaryService = generalDetailSummaryService;
    this.reservoirPropertiesSummaryService = reservoirPropertiesSummaryService;
    this.mixedCaseVolumetricsSummaryService = mixedCaseVolumetricsSummaryService;
    this.mixedCaseOutputsSummaryService = mixedCaseOutputsSummaryService;
    this.oilCaseVolumetricsSummaryService = oilCaseVolumetricsSummaryService;
    this.oilCaseOutputsSummaryService = oilCaseOutputsSummaryService;
    this.gasCaseVolumetricsSummaryService = gasCaseVolumetricsSummaryService;
    this.gasCaseOutputsSummaryService = gasCaseOutputsSummaryService;
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

  public ReservoirPropertiesSummaryView getReservoirPropertySummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromSubmissionDetail(submissionDetail);
  }

  public GeneralDetailSummaryView getGeneralDetailsSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return generalDetailSummaryService.getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  public OilCaseOutputsSummaryView getOilCaseOutputsSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return oilCaseOutputsSummaryService.getOilCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
  }

  public ProbabilityDistributionsFormSummaryView getOilCaseVolumetricsSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return oilCaseVolumetricsSummaryService.getOilCaseVolumetricsSummaryView(submissionDetail);
  }

  public GasCaseOutputsSummaryView getGasCaseOutputsSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return gasCaseOutputsSummaryService.getGasCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
  }

  public ProbabilityDistributionsFormSummaryView getGasCaseVolumetricsSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return gasCaseVolumetricsSummaryService.getGasCaseVolumetricsSummaryView(submissionDetail);
  }

  public MixedCaseOutputsSummaryView getMixedCaseOutputsSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return mixedCaseOutputsSummaryService.getMixedCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
  }

  public ProbabilityDistributionsFormSummaryView getMixedCaseVolumetricsSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return mixedCaseVolumetricsSummaryService.getMixedCaseVolumetricsSummaryView(submissionDetail);
  }
}
