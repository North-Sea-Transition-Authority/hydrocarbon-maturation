package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation.AdditionalInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics.MixedCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.ReservoirTaskListSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.ReviewAndSubmitTaskListSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListSectionService;
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
  private AdditionalInformationSummaryService additionalInformationSummaryService;

  @Mock
  private OpportunityDetailsSummaryService opportunityDetailsSummaryService;

  @Mock
  private PermeabilitySubmissionService permeabilitySubmissionService;

  @Mock
  private ReservoirPropertiesSubmissionService reservoirPropertiesSubmissionService;

  private ReservoirTaskListSectionService reservoirTaskListSectionService;

  private ReviewAndSubmitTaskListSectionService reviewAndSubmitTaskListSectionService;

  private ProspectSummaryService prospectSummaryService;
  private SubmissionDetail submissionDetail;
  private List<TaskListSectionService> listOfTaskListSections;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    listOfTaskListSections = new ArrayList<>();
    reservoirTaskListSectionService = new ReservoirTaskListSectionService(reservoirPropertiesSubmissionService,
        permeabilitySubmissionService);
    reviewAndSubmitTaskListSectionService = new ReviewAndSubmitTaskListSectionService();
    listOfTaskListSections.add(reservoirTaskListSectionService);
    listOfTaskListSections.add(reviewAndSubmitTaskListSectionService);

    prospectSummaryService = new ProspectSummaryService(
        generalDetailSummaryService,
        sourceAndSealSummaryService,
        chanceOfSuccessSummaryService,
        hydrocarbonCaseSummaryService,
        reservoirPropertiesSummaryService,
        trapPropertiesSummaryService,
        oilCaseVolumetricsSummaryService,
        oilCaseOutputsSummaryService,
        gasCaseVolumetricsSummaryService,
        gasCaseOutputsSummaryService,
        mixedCaseVolumetricsSummaryService,
        mixedCaseOutputsSummaryService,
        additionalInformationSummaryService,
        opportunityDetailsSummaryService,
        listOfTaskListSections);
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
  void getOpportunityDetailsSummaryViewFromSubmissionDetail_verifyMethodCall() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    prospectSummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail);

    verify(opportunityDetailsSummaryService, times(1))
        .getOpportunityDetailsSummaryFromSubmissionDetail(submissionDetail);
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
  void getTrapPropertiesSummaryViewFromSubmissionDetail_verifyMethodCall() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    prospectSummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail);

    verify(trapPropertiesSummaryService, times(1))
        .getTrapPropertiesSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getAdditionalInformationSummaryViewFromSubmissionDetail_verifyMethodCall() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    prospectSummaryService.getAdditionalInformationSummaryViewFromSubmissionDetail(submissionDetail);

    verify(additionalInformationSummaryService, times(1))
        .getAdditionalInformationSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getProspectSubNavigation_whenTwoSections_oneResult() {
    var subNavItems = prospectSummaryService.getProspectSubNavigation(submissionDetail);

    assertThat(subNavItems).hasSize(1);
  }

  @Test
  void getProspectSubNavigation_whenOneWrongSection_noResults() {
    listOfTaskListSections = List.of(reviewAndSubmitTaskListSectionService);
    prospectSummaryService = new ProspectSummaryService(
        generalDetailSummaryService,
        sourceAndSealSummaryService,
        chanceOfSuccessSummaryService,
        hydrocarbonCaseSummaryService,
        reservoirPropertiesSummaryService,
        trapPropertiesSummaryService,
        oilCaseVolumetricsSummaryService,
        oilCaseOutputsSummaryService,
        gasCaseVolumetricsSummaryService,
        gasCaseOutputsSummaryService,
        mixedCaseVolumetricsSummaryService,
        mixedCaseOutputsSummaryService,
        additionalInformationSummaryService,
        opportunityDetailsSummaryService,
        listOfTaskListSections);

    var subNavItems = prospectSummaryService.getProspectSubNavigation(submissionDetail);

    assertThat(subNavItems).isEmpty();
  }
}