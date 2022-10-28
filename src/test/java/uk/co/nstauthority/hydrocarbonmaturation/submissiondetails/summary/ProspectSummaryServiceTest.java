package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics.MixedCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.ReviewAndSubmitTaskListSection;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.VolumetricsTaskListSection;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListSection;

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
  private ProspectTaskListItem prospectTaskListItem;

  private ProspectSummaryService prospectSummaryService;
  private SubmissionDetail submissionDetail;
  private List<ProspectTaskListSection> listOfTaskListSections;

  @BeforeEach
  void setUp() {
    listOfTaskListSections = List.of(new VolumetricsTaskListSection(), new ReviewAndSubmitTaskListSection());
    List<ProspectTaskListItem> listOfTaskListItems = List.of(prospectTaskListItem);
    prospectSummaryService = new ProspectSummaryService(
        generalDetailSummaryService,
        sourceAndSealSummaryService,
        chanceOfSuccessSummaryService,
        hydrocarbonCaseSummaryService,
        reservoirPropertiesSummaryService,
        oilCaseVolumetricsSummaryService,
        oilCaseOutputsSummaryService,
        gasCaseVolumetricsSummaryService,
        gasCaseOutputsSummaryService,
        mixedCaseVolumetricsSummaryService,
        mixedCaseOutputsSummaryService,
        listOfTaskListSections,
        listOfTaskListItems);
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void getGeneralDetailSummaryViewFromSubmissionDetail_verifyMethodCall() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    prospectSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail);

    verify(generalDetailSummaryService, times(1))
        .getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getSourceAndSealSummaryViewFromSubmissionDetail_verifyMethodCall() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    prospectSummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail);

    verify(sourceAndSealSummaryService, times(1))
        .getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getChanceOfSuccessSummaryViewFromSubmissionDetail_verifyMethodCall() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    prospectSummaryService.getChanceOfSuccessSummaryViewFromSubmissionDetail(submissionDetail);

    verify(chanceOfSuccessSummaryService, times(1))
        .getChanceOfSuccessSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getHydrocarbonCaseSummaryViewFromSubmissionDetail_verifyMethodCall() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    prospectSummaryService.getHydrocarbonCaseSummaryViewFromSubmissionDetail(submissionDetail);

    verify(hydrocarbonCaseSummaryService, times(1))
        .getHydrocarbonSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getReservoirPropertiesSummaryViewFromSubmissionDetail_verifyMethodCall() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    prospectSummaryService.getReservoirPropertiesSummaryViewFromSubmissionDetail(submissionDetail);

    verify(reservoirPropertiesSummaryService, times(1))
        .getReservoirPropertiesSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getOilCaseVolumetricsSummaryViewFromSubmissionDetail() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    prospectSummaryService.getOilCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(oilCaseVolumetricsSummaryService, times(1))
        .getOilCaseVolumetricsSummaryView(submissionDetail);
  }

  @Test
  void getOilCaseOutputsSummaryViewFromSubmissionDetail() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    prospectSummaryService.getOilCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(oilCaseOutputsSummaryService, times(1))
        .getOilCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getGasCaseVolumetricsSummaryViewFromSubmissionDetail() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    prospectSummaryService.getGasCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(gasCaseVolumetricsSummaryService, times(1))
        .getGasCaseVolumetricsSummaryView(submissionDetail);
  }

  @Test
  void getGasCaseOutputsSummaryViewFromSubmissionDetail() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    prospectSummaryService.getGasCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(gasCaseOutputsSummaryService, times(1))
        .getGasCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getMixedCaseOutputsSummaryViewFromSubmissionDetail() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    prospectSummaryService.getMixedCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(mixedCaseOutputsSummaryService, times(1))
        .getMixedCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getMixedCaseVolumetricsSummaryViewFromSubmissionDetail() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    prospectSummaryService.getMixedCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(mixedCaseVolumetricsSummaryService, times(1))
        .getMixedCaseVolumetricsSummaryView(submissionDetail);
  }

  @Test
  void getProspectSubNavigation_whenTwoSections_oneResult() {
    var subNavItems = prospectSummaryService.getProspectSubNavigation(submissionDetail);

    assertThat(subNavItems).hasSize(1);
  }

  @Test
  void getProspectSubNavigation_whenOneWrongSection_noResults() {
    listOfTaskListSections = List.of(new ReviewAndSubmitTaskListSection());
    List<ProspectTaskListItem> listOfTaskListItems = List.of(prospectTaskListItem);
    prospectSummaryService = new ProspectSummaryService(
        generalDetailSummaryService,
        sourceAndSealSummaryService,
        chanceOfSuccessSummaryService,
        hydrocarbonCaseSummaryService,
        reservoirPropertiesSummaryService,
        oilCaseVolumetricsSummaryService,
        oilCaseOutputsSummaryService,
        gasCaseVolumetricsSummaryService,
        gasCaseOutputsSummaryService,
        mixedCaseVolumetricsSummaryService,
        mixedCaseOutputsSummaryService,
        listOfTaskListSections,
        listOfTaskListItems);

    var subNavItems = prospectSummaryService.getProspectSubNavigation(submissionDetail);

    assertThat(subNavItems).isEmpty();
  }
}