package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals.DiscoveryGasCaseVolumetricTotalsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals.DiscoveryGasCaseVolumetricTotalsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals.DiscoveryOilCaseVolumetricTotalsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals.DiscoveryOilCaseVolumetricTotalsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestResultsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters.GasCaseVolumetricParametersSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetricparameters.MixedCaseVolumetricParametersSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters.OilCaseVolumetricParametersSummaryService;
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
  private final MixedCaseVolumetricParametersSummaryService mixedCaseVolumetricParametersSummaryService;
  private final OilCaseVolumetricParametersSummaryService oilCaseVolumetricParametersSummaryService;
  private final OilCaseOutputsSummaryService oilCaseOutputsSummaryService;
  private final GasCaseVolumetricParametersSummaryService gasCaseVolumetricParametersSummaryService;
  private final GasCaseOutputsSummaryService gasCaseOutputsSummaryService;
  private final DrillStemTestResultsSummaryService drillStemTestResultsSummaryService;
  private final DiscoveryWellDetailsSummaryService discoveryWellDetailsSummaryService;
  private final OutlineForMaximumCaseSummaryService outlineForMaximumCaseSummaryService;
  private final OpportunityDetailsSummaryService opportunityDetailsSummaryService;
  private final TrapPropertiesSummaryService trapPropertiesSummaryService;
  private final SourceAndSealSummaryService sourceAndSealSummaryService;
  private final ReservoirSummaryService reservoirSummaryService;
  private final DiscoveryOilCaseVolumetricTotalsSummaryService discoveryOilCaseVolumetricTotalsSummaryService;
  private final DiscoveryGasCaseVolumetricTotalsSummaryService discoveryGasCaseVolumetricTotalsSummaryService;

  @Autowired
  DiscoverySummaryService(GasFluidPropertySummaryService gasFluidPropertySummaryService,
                          OilFluidPropertySummaryService oilFluidPropertySummaryService,
                          GeneralFluidPropertySummaryService generalFluidPropertySummaryService,
                          PermeabilitySummaryService permeabilitySummaryService,
                          GeneralDetailSummaryService generalDetailSummaryService,
                          ReservoirPropertiesSummaryService reservoirPropertiesSummaryService,
                          DrillStemTestResultsSummaryService drillStemTestResultsSummaryService,
                          MixedCaseVolumetricParametersSummaryService mixedCaseVolumetricParametersSummaryService,
                          OilCaseVolumetricParametersSummaryService oilCaseVolumetricParametersSummaryService,
                          OilCaseOutputsSummaryService oilCaseOutputsSummaryService,
                          GasCaseVolumetricParametersSummaryService gasCaseVolumetricParametersSummaryService,
                          GasCaseOutputsSummaryService gasCaseOutputsSummaryService,
                          DiscoveryWellDetailsSummaryService discoveryWellDetailsSummaryService,
                          OutlineForMaximumCaseSummaryService outlineForMaximumCaseSummaryService,
                          OpportunityDetailsSummaryService opportunityDetailsSummaryService,
                          TrapPropertiesSummaryService trapPropertiesSummaryService,
                          SourceAndSealSummaryService sourceAndSealSummaryService,
                          ReservoirSummaryService reservoirSummaryService,
                          DiscoveryOilCaseVolumetricTotalsSummaryService discoveryOilCaseVolumetricTotalsSummaryService,
                          DiscoveryGasCaseVolumetricTotalsSummaryService discoveryGasCaseVolumetricTotalsSummaryService) {
    this.gasFluidPropertySummaryService = gasFluidPropertySummaryService;
    this.oilFluidPropertySummaryService = oilFluidPropertySummaryService;
    this.generalFluidPropertySummaryService = generalFluidPropertySummaryService;
    this.permeabilitySummaryService = permeabilitySummaryService;
    this.generalDetailSummaryService = generalDetailSummaryService;
    this.reservoirPropertiesSummaryService = reservoirPropertiesSummaryService;
    this.drillStemTestResultsSummaryService = drillStemTestResultsSummaryService;
    this.mixedCaseVolumetricParametersSummaryService = mixedCaseVolumetricParametersSummaryService;
    this.oilCaseVolumetricParametersSummaryService = oilCaseVolumetricParametersSummaryService;
    this.oilCaseOutputsSummaryService = oilCaseOutputsSummaryService;
    this.gasCaseVolumetricParametersSummaryService = gasCaseVolumetricParametersSummaryService;
    this.gasCaseOutputsSummaryService = gasCaseOutputsSummaryService;
    this.discoveryWellDetailsSummaryService = discoveryWellDetailsSummaryService;
    this.outlineForMaximumCaseSummaryService = outlineForMaximumCaseSummaryService;
    this.opportunityDetailsSummaryService = opportunityDetailsSummaryService;
    this.trapPropertiesSummaryService = trapPropertiesSummaryService;
    this.sourceAndSealSummaryService = sourceAndSealSummaryService;
    this.reservoirSummaryService = reservoirSummaryService;
    this.discoveryOilCaseVolumetricTotalsSummaryService = discoveryOilCaseVolumetricTotalsSummaryService;
    this.discoveryGasCaseVolumetricTotalsSummaryService = discoveryGasCaseVolumetricTotalsSummaryService;
  }

  public DiscoveryWellDetailsSummaryView getDiscoveryWellDetailsSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return discoveryWellDetailsSummaryService.getDiscoveryWellDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  public SourceAndSealSummaryView getSourceAndSealSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return sourceAndSealSummaryService.getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);
  }

  public GasFluidPropertySummaryView getGasFluidPropertySummaryViewFromSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return gasFluidPropertySummaryService.getGasFluidPropertySummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  public GeneralFluidPropertySummaryView getGeneralFluidPropertySummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return generalFluidPropertySummaryService.getGeneralFluidPropertySummaryFromSubmissionDetail(reservoirSubmissionDetail);
  }

  public OilFluidPropertySummaryView getOilFluidPropertySummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return oilFluidPropertySummaryService.getOilFluidPropertySummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  public PermeabilityDetailSummaryView getPermeabilitySummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return permeabilitySummaryService.getPermeabilitySummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  public ReservoirPropertiesSummaryView getReservoirPropertySummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return reservoirPropertiesSummaryService
        .getReservoirPropertiesSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  public DrillStemTestSummaryView getDrillStemTestSummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return drillStemTestResultsSummaryService.getDrillStemTestSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  public GeneralDetailSummaryView getGeneralDetailsSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return generalDetailSummaryService.getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  public OilCaseOutputsSummaryView getOilCaseOutputsSummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return oilCaseOutputsSummaryService.getOilCaseOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  public ProbabilityDistributionsFormSummaryView getOilCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return oilCaseVolumetricParametersSummaryService.getOilCaseVolumetricsSummaryView(reservoirSubmissionDetail);
  }

  public GasCaseOutputsSummaryView getGasCaseOutputsSummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return gasCaseOutputsSummaryService.getGasCaseOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  public ProbabilityDistributionsFormSummaryView getGasCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return gasCaseVolumetricParametersSummaryService.getGasCaseVolumetricsSummaryView(reservoirSubmissionDetail);
  }

  public ProbabilityDistributionsFormSummaryView getMixedCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return mixedCaseVolumetricParametersSummaryService.getMixedCaseVolumetricsSummaryView(reservoirSubmissionDetail);
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

  public List<ReservoirSummary> getReservoirSummariesFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return reservoirSummaryService.getReservoirSummaries(submissionDetail);
  }

  public DiscoveryOilCaseVolumetricTotalsSummaryView getOilCaseVolumetricTotalsFromSubmissionDetail(
      SubmissionDetail submissionDetail
  ) {
    return discoveryOilCaseVolumetricTotalsSummaryService.createSummaryView(submissionDetail);
  }

  public DiscoveryGasCaseVolumetricTotalsSummaryView getGasCaseVolumetricTotalsFromSubmissionDetail(
      SubmissionDetail submissionDetail
  ) {
    return discoveryGasCaseVolumetricTotalsSummaryService.getGasCaseVolumetricTotalsSummaryFromSubmissionDetail(submissionDetail);
  }
}