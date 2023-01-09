package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestResultsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics.MixedCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySummaryService;

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
  private GeneralDetailSummaryService generalDetailSummaryService;

  @Mock
  private DrillStemTestResultsSummaryService drillStemTestResultsSummaryService;

  @Mock
  private OilCaseVolumetricsSummaryService oilCaseVolumetricsSummaryService;

  @Mock
  private OilCaseOutputsSummaryService oilCaseOutputsSummaryService;

  @Mock
  private GasCaseVolumetricsSummaryService gasCaseVolumetricsSummaryService;

  @Mock
  private GasCaseOutputsSummaryService gasCaseOutputsSummaryService;

  @Mock
  private MixedCaseVolumetricsSummaryService mixedCaseVolumetricsSummaryService;

  @Mock
  private MixedCaseOutputsSummaryService mixedCaseOutputsSummaryService;

  @InjectMocks
  private DiscoverySummaryService discoverySummaryService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

  @Test
  void getGasFluidPropertySummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getGasFluidPropertySummaryViewFromSubmissionDetail(submissionDetail);

    verify(gasFluidPropertySummaryService, times(1))
        .getGasFluidPropertySummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getOilFluidPropertySummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getOilFluidPropertySummaryViewFromSubmissionDetail(submissionDetail);

    verify(oilFluidPropertySummaryService, times(1))
        .getOilFluidPropertySummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getGeneralFluidPropertySummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getGeneralFluidPropertySummaryViewFromSubmissionDetail(submissionDetail);

    verify(generalFluidPropertySummaryService, times(1))
        .getGeneralFluidPropertySummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getPermeabilitySummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getPermeabilitySummaryViewFromSubmissionDetail(submissionDetail);

    verify(permeabilitySummaryService, times(1))
        .getPermeabilitySummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getGeneralDetailsSummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getGeneralDetailsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(generalDetailSummaryService, times(1))
        .getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getDrillStemTestSummaryViewFromSubmissionDetail() {
    discoverySummaryService.getDrillStemTestSummaryViewFromSubmissionDetail(submissionDetail);

    verify(drillStemTestResultsSummaryService, times(1))
        .getDrillStemTestSummaryViewFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getOilCaseVolumetricsSummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getOilCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(oilCaseVolumetricsSummaryService, times(1))
        .getOilCaseVolumetricsSummaryView(submissionDetail);
  }

  @Test
  void getOilCaseOutputsSummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getOilCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(oilCaseOutputsSummaryService, times(1))
        .getOilCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getGasCaseVolumetricsSummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getGasCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(gasCaseVolumetricsSummaryService, times(1))
        .getGasCaseVolumetricsSummaryView(submissionDetail);
  }

  @Test
  void getGasCaseOutputsSummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getGasCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(gasCaseOutputsSummaryService, times(1))
        .getGasCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getMixedCaseVolumetricsSummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getMixedCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(mixedCaseVolumetricsSummaryService, times(1))
        .getMixedCaseVolumetricsSummaryView(submissionDetail);
  }

  @Test
  void getMixedCaseOutputsSummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getMixedCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(mixedCaseOutputsSummaryService, times(1))
        .getMixedCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
  }
}