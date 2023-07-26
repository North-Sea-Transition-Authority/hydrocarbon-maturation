package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestResultsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestSummaryView;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView;

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
  private final DrillStemTestResultsSummaryService drillStemTestResultsSummaryService;
  private final DiscoveryWellDetailsSummaryService discoveryWellDetailsSummaryService;
  private final OutlineForMaximumCaseSummaryService outlineForMaximumCaseSummaryService;
  private final OpportunityDetailsSummaryService opportunityDetailsSummaryService;
  private final TrapPropertiesSummaryService trapPropertiesSummaryService;
  private final SourceAndSealSummaryService sourceAndSealSummaryService;

  @Autowired
  DiscoverySummaryService(GasFluidPropertySummaryService gasFluidPropertySummaryService,
                          OilFluidPropertySummaryService oilFluidPropertySummaryService,
                          GeneralFluidPropertySummaryService generalFluidPropertySummaryService,
                          PermeabilitySummaryService permeabilitySummaryService,
                          GeneralDetailSummaryService generalDetailSummaryService,
                          ReservoirPropertiesSummaryService reservoirPropertiesSummaryService,
                          DrillStemTestResultsSummaryService drillStemTestResultsSummaryService,
                          MixedCaseVolumetricsSummaryService mixedCaseVolumetricsSummaryService,
                          MixedCaseOutputsSummaryService mixedCaseOutputsSummaryService,
                          OilCaseVolumetricsSummaryService oilCaseVolumetricsSummaryService,
                          OilCaseOutputsSummaryService oilCaseOutputsSummaryService,
                          GasCaseVolumetricsSummaryService gasCaseVolumetricsSummaryService,
                          GasCaseOutputsSummaryService gasCaseOutputsSummaryService,
                          DiscoveryWellDetailsSummaryService discoveryWellDetailsSummaryService,
                          OutlineForMaximumCaseSummaryService outlineForMaximumCaseSummaryService,
                          OpportunityDetailsSummaryService opportunityDetailsSummaryService,
                          TrapPropertiesSummaryService trapPropertiesSummaryService,
                          SourceAndSealSummaryService sourceAndSealSummaryService) {
    this.gasFluidPropertySummaryService = gasFluidPropertySummaryService;
    this.oilFluidPropertySummaryService = oilFluidPropertySummaryService;
    this.generalFluidPropertySummaryService = generalFluidPropertySummaryService;
    this.permeabilitySummaryService = permeabilitySummaryService;
    this.generalDetailSummaryService = generalDetailSummaryService;
    this.reservoirPropertiesSummaryService = reservoirPropertiesSummaryService;
    this.drillStemTestResultsSummaryService = drillStemTestResultsSummaryService;
    this.mixedCaseVolumetricsSummaryService = mixedCaseVolumetricsSummaryService;
    this.mixedCaseOutputsSummaryService = mixedCaseOutputsSummaryService;
    this.oilCaseVolumetricsSummaryService = oilCaseVolumetricsSummaryService;
    this.oilCaseOutputsSummaryService = oilCaseOutputsSummaryService;
    this.gasCaseVolumetricsSummaryService = gasCaseVolumetricsSummaryService;
    this.gasCaseOutputsSummaryService = gasCaseOutputsSummaryService;
    this.discoveryWellDetailsSummaryService = discoveryWellDetailsSummaryService;
    this.outlineForMaximumCaseSummaryService = outlineForMaximumCaseSummaryService;
    this.opportunityDetailsSummaryService = opportunityDetailsSummaryService;
    this.trapPropertiesSummaryService = trapPropertiesSummaryService;
    this.sourceAndSealSummaryService = sourceAndSealSummaryService;
  }

  public DiscoveryWellDetailsSummaryView getDiscoveryWellDetailsSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return discoveryWellDetailsSummaryService.getDiscoveryWellDetailSummaryFromSubmissionDetail(submissionDetail);
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

  public SourceAndSealSummaryView getSourceAndSealSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return sourceAndSealSummaryService.getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);
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

  public DrillStemTestSummaryView getDrillStemTestSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return drillStemTestResultsSummaryService.getDrillStemTestSummaryViewFromSubmissionDetail(submissionDetail);
  }

  public OilCaseOutputsSummaryView getOilCaseOutputsSummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return oilCaseOutputsSummaryService.getOilCaseOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  public ProbabilityDistributionsFormSummaryView getOilCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return oilCaseVolumetricsSummaryService.getOilCaseVolumetricsSummaryView(reservoirSubmissionDetail);
  }

  public GasCaseOutputsSummaryView getGasCaseOutputsSummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return gasCaseOutputsSummaryService.getGasCaseOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  public ProbabilityDistributionsFormSummaryView getGasCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return gasCaseVolumetricsSummaryService.getGasCaseVolumetricsSummaryView(reservoirSubmissionDetail);
  }

  public MixedCaseOutputsSummaryView getMixedCaseOutputsSummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return mixedCaseOutputsSummaryService.getMixedCaseOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  public ProbabilityDistributionsFormSummaryView getMixedCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return mixedCaseVolumetricsSummaryService.getMixedCaseVolumetricsSummaryView(reservoirSubmissionDetail);
  }

  public TrapPropertiesSummaryView getTrapPropertiesSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return trapPropertiesSummaryService.getTrapPropertiesSummaryFromSubmissionDetail(submissionDetail);
  }

  public OpportunityDetailsSummaryView getOpportunityDetailsSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return opportunityDetailsSummaryService.getOpportunityDetailsSummaryFromSubmissionDetail(submissionDetail);
  }

  public OutlineForMaximumCaseSummaryView getOutlineForMaximumCaseSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return outlineForMaximumCaseSummaryService.getOutlineForMaximumCaseSummaryFromSubmissionDetail(submissionDetail);
  }
}