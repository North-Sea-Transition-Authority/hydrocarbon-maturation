package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals.DiscoveryGasCaseVolumetricTotalsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals.DiscoveryOilCaseVolumetricTotalsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryService;

@ExtendWith(MockitoExtension.class)
class DiscoverySummaryServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private GeneralDetailSummaryService generalDetailSummaryService;

  @Mock
  private SourceAndSealSummaryService sourceAndSealSummaryService;

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

  @Mock
  private ChanceOfSuccessSummaryService chanceOfSuccessSummaryService;

  @InjectMocks
  private DiscoverySummaryService discoverySummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createDiscoveryFromProspectSubmissionDetail();

  @Test
  void getDiscoveryWellDetailsSummaryViewFromReservoirSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getDiscoveryWellDetailsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(discoveryWellDetailsSummaryService, times(1))
        .getDiscoveryWellDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getSourceAndSealSummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail);

    verify(sourceAndSealSummaryService, times(1))
        .getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getGeneralDetailsSummaryViewFromSubmissionDetail_verifyMethodCall() {
    discoverySummaryService.getGeneralDetailsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(generalDetailSummaryService, times(1))
        .getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
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
    discoverySummaryService.getReservoirSummariesFromSubmissionDetail(submissionDetail, USER);

    verify(reservoirSummaryService, times(1))
        .getReservoirSummaries(submissionDetail, USER);
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

  @Test
  void getChanceOfSuccessSummaryViewFromSubmissionDetail(){
    discoverySummaryService.getChanceOfSuccessSummaryViewFromSubmissionDetail(submissionDetail);
    verify(chanceOfSuccessSummaryService, times(1))
        .getChanceOfSuccessSummaryFromSubmissionDetail(submissionDetail);
  }
}