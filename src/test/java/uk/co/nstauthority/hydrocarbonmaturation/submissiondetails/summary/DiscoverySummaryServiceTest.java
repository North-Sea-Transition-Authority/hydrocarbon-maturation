package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverygascasevolumetrictotals.DiscoveryGasCaseVolumetricTotalsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryoilcasevolumetrictotals.DiscoveryOilCaseVolumetricTotalsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestResultsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters.GasCaseVolumetricParametersSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetricparameters.MixedCaseVolumetricParametersSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters.OilCaseVolumetricParametersSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryService;

@ExtendWith(MockitoExtension.class)
class DiscoverySummaryServiceTest {

  @Mock
  private GasFluidPropertySummaryService gasFluidPropertySummaryService;

  @Mock
  private OilFluidPropertySummaryService oilFluidPropertySummaryService;

  @Mock
  private GeneralFluidPropertySummaryService generalFluidPropertySummaryService;

  @Mock
  private PermeabilitySummaryService permeabilitySummaryService;

  @Mock
  private ReservoirPropertiesSummaryService reservoirPropertiesSummaryService;

  @Mock
  private GeneralDetailSummaryService generalDetailSummaryService;

  @Mock
  private SourceAndSealSummaryService sourceAndSealSummaryService;

  @Mock
  private DrillStemTestResultsSummaryService drillStemTestResultsSummaryService;

  @Mock
  private OilCaseVolumetricParametersSummaryService oilCaseVolumetricParametersSummaryService;

  @Mock
  private OilCaseOutputsSummaryService oilCaseOutputsSummaryService;

  @Mock
  private GasCaseVolumetricParametersSummaryService gasCaseVolumetricParametersSummaryService;

  @Mock
  private GasCaseOutputsSummaryService gasCaseOutputsSummaryService;

  @Mock
  private MixedCaseVolumetricParametersSummaryService mixedCaseVolumetricParametersSummaryService;

  @Mock
  private DiscoveryWellDetailsSummaryService discoveryWellDetailsSummaryService;

  @Mock
  private TrapPropertiesSummaryService trapPropertiesSummaryService;

  @Mock
  private OpportunityDetailsSummaryService opportunityDetailsSummaryService;

  @Mock
  private OutlineForMaximumCaseSummaryService outlineForMaximumCaseSummaryService;

  @Mock
  private ReservoirSummaryService reservoirSummaryService;

  @Mock
  private DiscoveryOilCaseVolumetricTotalsSummaryService discoveryOilCaseVolumetricTotalsSummaryService;

  @Mock
  private DiscoveryGasCaseVolumetricTotalsSummaryService discoveryGasCaseVolumetricTotalsSummaryService;

  @InjectMocks
  private DiscoverySummaryService discoverySummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createDiscoveryFromProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

  @Test
  void getDiscoveryWellDetailsSummaryViewFromReservoirSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getDiscoveryWellDetailsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(discoveryWellDetailsSummaryService, times(1))
        .getDiscoveryWellDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getGasFluidPropertySummaryViewFromReservoirSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getGasFluidPropertySummaryViewFromSubmissionDetail(reservoirSubmissionDetail);

    verify(gasFluidPropertySummaryService, times(1))
        .getGasFluidPropertySummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getOilFluidPropertySummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getOilFluidPropertySummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(oilFluidPropertySummaryService, times(1))
        .getOilFluidPropertySummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getGeneralFluidPropertySummaryViewFromReservoirSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getGeneralFluidPropertySummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(generalFluidPropertySummaryService, times(1))
        .getGeneralFluidPropertySummaryFromSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getSourceAndSealSummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail);

    verify(sourceAndSealSummaryService, times(1))
        .getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getPermeabilitySummaryViewFromReservoirSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getPermeabilitySummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(permeabilitySummaryService, times(1))
        .getPermeabilitySummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getReservoirPropertySummaryViewFromReservoirSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getReservoirPropertySummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(reservoirPropertiesSummaryService, times(1))
        .getReservoirPropertiesSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getGeneralDetailsSummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getGeneralDetailsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(generalDetailSummaryService, times(1))
        .getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getDrillStemTestSummaryViewFromSubmissionDetail() {
    discoverySummaryService.getDrillStemTestSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(drillStemTestResultsSummaryService, times(1))
        .getDrillStemTestSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getOilCaseVolumetricsSummaryViewFromReservoirSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getOilCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(oilCaseVolumetricParametersSummaryService, times(1))
        .getOilCaseVolumetricsSummaryView(reservoirSubmissionDetail);
  }

  @Test
  void getOilCaseOutputsSummaryViewFromReservoirSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getOilCaseOutputsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(oilCaseOutputsSummaryService, times(1))
        .getOilCaseOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getGasCaseVolumetricsSummaryViewFromReservoirSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getGasCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(gasCaseVolumetricParametersSummaryService, times(1))
        .getGasCaseVolumetricsSummaryView(reservoirSubmissionDetail);
  }

  @Test
  void getGasCaseOutputsSummaryViewFromReservoirSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getGasCaseOutputsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(gasCaseOutputsSummaryService, times(1))
        .getGasCaseOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getMixedCaseVolumetricsSummaryViewFromReservoirSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getMixedCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(mixedCaseVolumetricParametersSummaryService, times(1))
        .getMixedCaseVolumetricsSummaryView(reservoirSubmissionDetail);
  }

  @Test
  void getTrapPropertiesSummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail);

    verify(trapPropertiesSummaryService, times(1))
        .getTrapPropertiesSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getOpportunityDetailsSummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(opportunityDetailsSummaryService, times(1))
        .getOpportunityDetailsSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getOutlineForMaximumCaseSummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getOutlineForMaximumCaseSummaryViewFromSubmissionDetail(submissionDetail);

    verify(outlineForMaximumCaseSummaryService, times(1))
        .getOutlineForMaximumCaseSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getReservoirSummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getReservoirSummariesFromSubmissionDetail(submissionDetail);

    verify(reservoirSummaryService, times(1))
        .getReservoirSummaries(submissionDetail);
  }

  @Test
  void getOilCaseVolumetricTotalsFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getOilCaseVolumetricTotalsFromSubmissionDetail(submissionDetail);
    verify(discoveryOilCaseVolumetricTotalsSummaryService, times(1))
        .createSummaryView(submissionDetail);
  }

  @Test
  void getGasCaseVolumetricTotalsFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getGasCaseVolumetricTotalsFromSubmissionDetail(submissionDetail);
    verify(discoveryGasCaseVolumetricTotalsSummaryService, times(1))
        .getGasCaseVolumetricTotalsSummaryFromSubmissionDetail(submissionDetail);
  }
}