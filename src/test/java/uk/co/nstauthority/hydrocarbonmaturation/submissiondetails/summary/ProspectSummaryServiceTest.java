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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics.MixedCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryService;

@ExtendWith(MockitoExtension.class)
class ProspectSummaryServiceTest {

  @Mock
  private GeneralDetailSummaryService generalDetailSummaryService;

  @Mock
  private SourceAndSealSummaryService sourceAndSealSummaryService;

  @Mock
  private HydrocarbonCaseSummaryService hydrocarbonCaseSummaryService;

  @Mock
  private ChanceOfSuccessSummaryService chanceOfSuccessSummaryService;

  @Mock
  private ReservoirPropertiesSummaryService reservoirPropertiesSummaryService;

  @Mock
  private GasCaseVolumetricsSummaryService gasCaseVolumetricsSummaryService;

  @Mock
  private GasCaseOutputsSummaryService gasCaseOutputsSummaryService;

  @Mock
  private OilCaseVolumetricsSummaryService oilCaseVolumetricsSummaryService;

  @Mock
  private OilCaseOutputsSummaryService oilCaseOutputsSummaryService;

  @Mock
  private MixedCaseVolumetricsSummaryService mixedCaseVolumetricsSummaryService;

  @Mock
  private MixedCaseOutputsSummaryService mixedCaseOutputsSummaryService;

  @Mock
  private TrapPropertiesSummaryService trapPropertiesSummaryService;

  @Mock
  private OpportunityDetailsSummaryService opportunityDetailsSummaryService;

  @Mock
  private ResourceDetailSummaryService resourceDetailSummaryService;

  @Mock
  private OutlineForMaximumCaseSummaryService outlineForMaximumCaseSummaryService;

  @InjectMocks
  private ProspectSummaryService prospectSummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

  @Test
  void getGeneralDetailSummaryViewFromSubmissionDetail_verifyMethodCall() {
    prospectSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail);

    verify(generalDetailSummaryService, times(1))
        .getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getResourceDetailSummaryViewFromSubmissionDetail_verifyMethodCall() {
    prospectSummaryService.getResourceDetailSummaryViewFromSubmissionDetail(submissionDetail);

    verify(resourceDetailSummaryService, times(1))
        .getResourceDetailSummaryFromSubmissionDetail(submissionDetail);
  }


  @Test
  void getSourceAndSealSummaryViewFromSubmissionDetail_verifyMethodCall() {
    prospectSummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail);

    verify(sourceAndSealSummaryService, times(1))
        .getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getChanceOfSuccessSummaryViewFromSubmissionDetail_verifyMethodCall() {
    prospectSummaryService.getChanceOfSuccessSummaryViewFromSubmissionDetail(submissionDetail);

    verify(chanceOfSuccessSummaryService, times(1))
        .getChanceOfSuccessSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getHydrocarbonCaseSummaryViewFromSubmissionDetail_verifyMethodCall() {
    prospectSummaryService.getHydrocarbonCaseSummaryViewFromSubmissionDetail(submissionDetail);

    verify(hydrocarbonCaseSummaryService, times(1))
        .getHydrocarbonSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getReservoirPropertiesSummaryViewFromReservoirSubmissionDetail_verifyMethodCall() {
    prospectSummaryService.getReservoirPropertiesSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(reservoirPropertiesSummaryService, times(1))
        .getReservoirPropertiesSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getOilCaseVolumetricsSummaryViewFromReservoirSubmissionDetail() {
    prospectSummaryService.getOilCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(oilCaseVolumetricsSummaryService, times(1))
        .getOilCaseVolumetricsSummaryView(reservoirSubmissionDetail);
  }

  @Test
  void getOilCaseOutputsSummaryViewFromReservoirSubmissionDetail() {
    prospectSummaryService.getOilCaseOutputsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(oilCaseOutputsSummaryService, times(1))
        .getOilCaseOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getGasCaseVolumetricsSummaryViewFromReservoirSubmisionDetail() {
    prospectSummaryService.getGasCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(gasCaseVolumetricsSummaryService, times(1))
        .getGasCaseVolumetricsSummaryView(reservoirSubmissionDetail);
  }

  @Test
  void getGasCaseOutputsSummaryViewFromReservoirSubmissionDetail() {
    prospectSummaryService.getGasCaseOutputsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(gasCaseOutputsSummaryService, times(1))
        .getGasCaseOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getMixedCaseOutputsSummaryViewFromReservoirSubmissionDetail() {
    prospectSummaryService.getMixedCaseOutputsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(mixedCaseOutputsSummaryService, times(1))
        .getMixedCaseOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getMixedCaseVolumetricsSummaryViewFromReservoirSubmissionDetail() {
    prospectSummaryService.getMixedCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    verify(mixedCaseVolumetricsSummaryService, times(1))
        .getMixedCaseVolumetricsSummaryView(reservoirSubmissionDetail);
  }

  @Test
  void getTrapPropertiesSummaryViewFromSubmissionDetail_verifyMethodCall() {
    prospectSummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail);

    verify(trapPropertiesSummaryService, times(1))
        .getTrapPropertiesSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getOpportunityDetailsSummaryViewFromSubmissionDetail_verifyMethodCall() {
    prospectSummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(opportunityDetailsSummaryService, times(1))
        .getOpportunityDetailsSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getOutlineForMaximumCaseSummaryViewFromSubmissionDetail_verifyMethodCall() {
    prospectSummaryService.getOutlineForMaximumCaseSummaryViewFromSubmissionDetail(submissionDetail);

    verify(outlineForMaximumCaseSummaryService, times(1))
        .getOutlineForMaximumCaseSummaryFromSubmissionDetail(submissionDetail);
  }
}