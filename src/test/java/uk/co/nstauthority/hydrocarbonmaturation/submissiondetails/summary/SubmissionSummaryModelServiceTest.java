package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals.DiscoveryGasCaseVolumetricTotalsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals.DiscoveryOilCaseVolumetricTotalsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdate;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TotalVolumesTaskListSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

@ExtendWith(MockitoExtension.class)
class SubmissionSummaryModelServiceTest {

  private static final String SUBMISSION_UPDATE_REASON = "reason for update here";

  @Mock
  private ProspectSummaryService prospectSummaryService;
  @Mock
  private HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  @Mock
  private DiscoverySummaryService discoverySummaryService;
  @Mock
  private PostDrillDryWellSummaryService postDrillDryWellSummaryService;
  @Mock
  private SubmissionUpdateService submissionUpdateService;
  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  @Mock
  private SubmissionSummaryService submissionSummaryService;
  @Mock
  private FieldSummaryService fieldSummaryService;
  @Mock
  private SubmissionResourceService submissionResourceService;
  @Mock
  private TotalVolumesTaskListSectionService totalVolumesTaskListSectionService;

  @InjectMocks
  private SubmissionSummaryModelService submissionSummaryModelService;

  private GeneralDetailSummaryView generalDetailSummaryView;
  private SourceAndSealSummaryView sourceAndSealSummaryView;
  private HydrocarbonCaseSummaryView hydrocarbonCaseSummaryView;
  private ChanceOfSuccessSummaryView chanceOfSuccessSummaryView;
  private ReservoirPropertiesSummaryView reservoirPropertiesSummaryView;
  private ProbabilityDistributionsFormSummaryView oilCaseVolumetricsSummaryView;
  private OilCaseOutputsSummaryView oilCaseOutputsSummaryView;
  private ProbabilityDistributionsFormSummaryView gasCaseVolumetricsSummaryView;
  private GasCaseOutputsSummaryView gasCaseOutputsSummaryView;
  private ProbabilityDistributionsFormSummaryView mixedCaseVolumetricsSummaryView;
  private MixedCaseOutputsSummaryView mixedCaseOutputsSummaryView;
  private TrapPropertiesSummaryView trapPropertiesSummaryView;
  private OpportunityDetailsSummaryView opportunityDetailsSummaryView;
  private PermeabilityDetailSummaryView permeabilityDetailSummaryView;
  private DryWellDetailsSummaryView dryWellDetailsSummaryView;
  private GasFluidPropertySummaryView gasFluidPropertySummaryView;
  private ResourceDetailSummaryView resourceDetailSummaryView;
  private List<SummarySection> summarySections;
  private List<ReservoirSummary> reservoirSummaries;
  private OutlineForMaximumCaseSummaryView outlineForMaximumCaseSummaryView;
  private DiscoveryWellDetailsSummaryView discoveryWellDetailsSummaryView;
  private DiscoveryOilCaseVolumetricTotalsSummaryView discoveryOilCaseVolumetricTotalsSummaryView;
  private DiscoveryGasCaseVolumetricTotalsSummaryView discoveryGasCaseVolumetricTotalsSummaryView;

  @BeforeEach
  void setUp() {
    generalDetailSummaryView =  GeneralDetailSummaryView.empty(SubmissionType.CREATE_PROSPECT, "prospect");
    sourceAndSealSummaryView =  SourceAndSealSummaryView.empty();
    hydrocarbonCaseSummaryView = HydrocarbonCaseSummaryView.empty();
    reservoirPropertiesSummaryView = new ReservoirPropertiesSummaryView(
        SubmissionType.CREATE_PROSPECT,
        "formation",
        "sub unit",
        "age",
        "group name",
        "lithology",
        "facies",
        "connectivity",
        new ArrayList<>(),
        new ArrayList<>());
    trapPropertiesSummaryView = TrapPropertiesSummaryView.empty();
    chanceOfSuccessSummaryView = ChanceOfSuccessSummaryView.empty();
    oilCaseVolumetricsSummaryView = new ProbabilityDistributionsFormSummaryView(new ArrayList<>());
    oilCaseOutputsSummaryView = OilCaseOutputsSummaryView.empty();
    gasCaseVolumetricsSummaryView = new ProbabilityDistributionsFormSummaryView(new ArrayList<>());
    gasCaseOutputsSummaryView = GasCaseOutputsSummaryView.empty();
    mixedCaseVolumetricsSummaryView = new ProbabilityDistributionsFormSummaryView(new ArrayList<>());
    mixedCaseOutputsSummaryView = MixedCaseOutputsSummaryView.empty();
    opportunityDetailsSummaryView = OpportunityDetailsSummaryView.empty();
    dryWellDetailsSummaryView =  DryWellDetailsSummaryView.empty();
    permeabilityDetailSummaryView = PermeabilityDetailSummaryView.empty();
    reservoirPropertiesSummaryView = new ReservoirPropertiesSummaryView(
        SubmissionType.POST_DRILL_DRY_HOLE,
        "formation",
        "sub unit",
        "age",
        "group name",
        "lithology",
        "facies",
        "connectivity",
        new ArrayList<>(),
        new ArrayList<>());

    gasFluidPropertySummaryView =  GasFluidPropertySummaryView.empty();
    resourceDetailSummaryView = new ResourceDetailSummaryView(
        "", "", false, "", "Offshore", "resourceType");
    summarySections = Collections.emptyList();
    reservoirSummaries = Collections.emptyList();
    outlineForMaximumCaseSummaryView = OutlineForMaximumCaseSummaryView.empty();
    discoveryWellDetailsSummaryView = DiscoveryWellDetailsSummaryView.empty();
    discoveryOilCaseVolumetricTotalsSummaryView = DiscoveryOilCaseVolumetricTotalsSummaryView.empty();
    discoveryGasCaseVolumetricTotalsSummaryView = DiscoveryGasCaseVolumetricTotalsSummaryView.empty();
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FIELD"}, mode = EnumSource.Mode.EXCLUDE)
  void createSummaryForSubmissionTypeToModelAndView(SubmissionType submissionType) {
    var submissionSummaryModelServiceSpy = spy(submissionSummaryModelService);

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    var viewName = "view-name";

    if (!submissionDetail.isFieldSubmission()) {
      when(prospectSummaryService.getResourceDetailSummaryViewFromSubmissionDetail(submissionDetail))
          .thenReturn(resourceDetailSummaryView);
    }

    submissionSummaryModelServiceSpy.createSummaryForSubmissionTypeToModelAndView(submissionDetail, viewName);

    switch (submissionDetail.getSubmissionType()) {
      case CREATE_PROSPECT, PROSPECT_UPDATE, RELINQUISHMENT_PROSPECT ->
          verify(submissionSummaryModelServiceSpy).createProspectSubmissionSummaryModelAndView(submissionDetail, viewName);
      case POST_DRILL_DRY_HOLE ->
          verify(submissionSummaryModelServiceSpy).createPostDrillDryWellSubmissionSummaryModelAndView(submissionDetail, viewName);
      case CREATE_DISCOVERY, UPDATE_DISCOVERY, CREATE_DISCOVERY_FROM_PROSPECT, RELINQUISHMENT_DISCOVERY ->
          verify(submissionSummaryModelServiceSpy).createDiscoverySubmissionSummaryModelAndView(submissionDetail, viewName);
      case CREATE_CSR, CREATE_FDP, CREATE_FDPA, CREATE_COP ->
        verify(submissionSummaryModelServiceSpy).createFieldSubmissionSummaryModelAndView(submissionDetail, viewName);
    }
  }

  @Test
  void createProspectSubmissionSummaryModelAndView() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var reservoirSubmissionDetail = Mockito.mock(ReservoirSubmissionDetail.class);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    when(prospectSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalDetailSummaryView);
    when(prospectSummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(sourceAndSealSummaryView);
    when(prospectSummaryService.getHydrocarbonCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(hydrocarbonCaseSummaryView);
    when(prospectSummaryService.getReservoirPropertiesSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(reservoirPropertiesSummaryView);
    when(prospectSummaryService.getChanceOfSuccessSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(chanceOfSuccessSummaryView);
    when(prospectSummaryService.getOilCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(oilCaseVolumetricsSummaryView);
    when(prospectSummaryService.getOilCaseOutputsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(oilCaseOutputsSummaryView);
    when(prospectSummaryService.getGasCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(gasCaseVolumetricsSummaryView);
    when(prospectSummaryService.getGasCaseOutputsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(gasCaseOutputsSummaryView);
    when(prospectSummaryService.getMixedCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(mixedCaseVolumetricsSummaryView);
    when(prospectSummaryService.getMixedCaseOutputsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(mixedCaseOutputsSummaryView);
    when(prospectSummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(trapPropertiesSummaryView);
    when(prospectSummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(opportunityDetailsSummaryView);
    when(prospectSummaryService.getOutlineForMaximumCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(outlineForMaximumCaseSummaryView);
    when(prospectSummaryService.getResourceDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(resourceDetailSummaryView);

    when(submissionResourceService.getResourceNameOrDefault(submissionDetail)).thenReturn("test");
    var modelAndView =
        submissionSummaryModelService.createProspectSubmissionSummaryModelAndView(submissionDetail, "view-name");

    assertThat(modelAndView.getModelMap())
        .containsExactly(
            entry("generalDetailsSummaryView", generalDetailSummaryView),
            entry("sourceAndSealSummaryView", sourceAndSealSummaryView),
            entry("chanceOfSuccessSummaryView", chanceOfSuccessSummaryView),
            entry("hydrocarbonCaseSummaryView", hydrocarbonCaseSummaryView),
            entry("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView),
            entry("trapPropertiesSummaryView", trapPropertiesSummaryView),
            entry("oilCaseVolumetricsSummaryView", oilCaseVolumetricsSummaryView),
            entry("oilCaseOutputsSummaryView", oilCaseOutputsSummaryView),
            entry("gasCaseVolumetricsSummaryView", gasCaseVolumetricsSummaryView),
            entry("gasCaseOutputsSummaryView", gasCaseOutputsSummaryView),
            entry("mixedCaseVolumetricsSummaryView", mixedCaseVolumetricsSummaryView),
            entry("mixedCaseOutputsSummaryView", mixedCaseOutputsSummaryView),
            entry("opportunityDetailsSummaryView", opportunityDetailsSummaryView),
            entry("outlineForMaximumCaseSummaryView", outlineForMaximumCaseSummaryView),
            entry("gasCase", false),
            entry("oilCase", false),
            entry("mixedCase", false),
            entry("accordionId", submissionDetail.getId()),
            entry("isSubmissionUpdate", false),
            entry("resourceName", "test"),
            entry("resourceDetailSummaryView", resourceDetailSummaryView)
        );
  }

  @Test
  void createPostDrillDryWellSubmissionSummaryModelAndView() {
    var submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    var reservoirSubmissionDetail = Mockito.mock(ReservoirSubmissionDetail.class);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    when(postDrillDryWellSummaryService.getDryWellDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(dryWellDetailsSummaryView);
    when(postDrillDryWellSummaryService.getPermeabilitySummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(permeabilityDetailSummaryView);
    when(postDrillDryWellSummaryService.getReservoirPropertiesSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(reservoirPropertiesSummaryView);
    when(postDrillDryWellSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalDetailSummaryView);
    when(prospectSummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(trapPropertiesSummaryView);
    when(prospectSummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(opportunityDetailsSummaryView);
    when(prospectSummaryService.getOutlineForMaximumCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(outlineForMaximumCaseSummaryView);
    when(prospectSummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(sourceAndSealSummaryView);
    when(prospectSummaryService.getResourceDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(resourceDetailSummaryView);

    when(submissionResourceService.getResourceNameOrDefault(submissionDetail)).thenReturn("test");
    var modelAndView =
        submissionSummaryModelService.createPostDrillDryWellSubmissionSummaryModelAndView(submissionDetail, "view-name");

    assertThat(modelAndView.getModelMap())
        .containsExactly(
            entry("dryWellDetailsSummaryView", dryWellDetailsSummaryView),
            entry("generalDetailsSummaryView", generalDetailSummaryView),
            entry("permeabilitySummaryView", permeabilityDetailSummaryView),
            entry("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView),
            entry("opportunityDetailsSummaryView", opportunityDetailsSummaryView),
            entry("outlineForMaximumCaseSummaryView", outlineForMaximumCaseSummaryView),
            entry("sourceAndSealSummaryView", sourceAndSealSummaryView),
            entry("trapPropertiesSummaryView", trapPropertiesSummaryView),
            entry("accordionId", submissionDetail.getId()),
            entry("isSubmissionUpdate", false),
            entry("resourceName", "test"),
            entry("resourceDetailSummaryView", resourceDetailSummaryView)
        );
  }

  @Test
  void createDiscoverySubmissionSummaryModelAndView() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();

    when(discoverySummaryService.getGeneralDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalDetailSummaryView);
    when(discoverySummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(trapPropertiesSummaryView);
    when(discoverySummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(opportunityDetailsSummaryView);
    when(prospectSummaryService.getResourceDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(resourceDetailSummaryView);
    when(discoverySummaryService.getReservoirSummariesFromSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSummaries);
    when(discoverySummaryService.getOutlineForMaximumCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(outlineForMaximumCaseSummaryView);
    when(discoverySummaryService.getDiscoveryWellDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(discoveryWellDetailsSummaryView);
    when(discoverySummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(sourceAndSealSummaryView);

    when(submissionResourceService.getResourceNameOrDefault(submissionDetail)).thenReturn("test");
    when(totalVolumesTaskListSectionService.sectionIsPresent(submissionDetail)).thenReturn(false);
    var modelAndView =
        submissionSummaryModelService.createDiscoverySubmissionSummaryModelAndView(submissionDetail, "view-name");

    assertThat(modelAndView.getModelMap())
        .containsExactly(
            entry("generalDetailsSummaryView", generalDetailSummaryView),
            entry("opportunityDetailsSummaryView", opportunityDetailsSummaryView),
            entry("outlineForMaximumCaseSummaryView", outlineForMaximumCaseSummaryView),
            entry("discoveryWellDetailsSummaryView", discoveryWellDetailsSummaryView),
            entry("sourceAndSealSummaryView", sourceAndSealSummaryView),
            entry("trapPropertiesSummaryView", trapPropertiesSummaryView),
            entry("reservoirSummaries", reservoirSummaries),
            entry("totalsRequired", false),
            entry("accordionId", submissionDetail.getId()),
            entry("isSubmissionUpdate", false),
            entry("resourceName", "test"),
            entry("resourceDetailSummaryView", resourceDetailSummaryView)
        );
  }

  @Test
  void createDiscoverySubmissionSummaryModelAndView_whenTotalsAreRequired() {
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();

    when(discoverySummaryService.getGeneralDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalDetailSummaryView);
    when(discoverySummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(trapPropertiesSummaryView);
    when(discoverySummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(opportunityDetailsSummaryView);
    when(prospectSummaryService.getResourceDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(resourceDetailSummaryView);
    when(discoverySummaryService.getReservoirSummariesFromSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSummaries);
    when(discoverySummaryService.getOutlineForMaximumCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(outlineForMaximumCaseSummaryView);
    when(discoverySummaryService.getDiscoveryWellDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(discoveryWellDetailsSummaryView);
    when(discoverySummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(sourceAndSealSummaryView);

    when(submissionResourceService.getResourceNameOrDefault(submissionDetail)).thenReturn("test");

    when(totalVolumesTaskListSectionService.sectionIsPresent(submissionDetail)).thenReturn(true);
    var reservoirSubmissionDetails = List.of(ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail));
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    when(totalVolumesTaskListSectionService.isOilCaseOnly(reservoirSubmissionDetails)).thenReturn(true);
    when(totalVolumesTaskListSectionService.isGasCaseOnly(reservoirSubmissionDetails)).thenReturn(true);
    when(totalVolumesTaskListSectionService.isCombinedCase(reservoirSubmissionDetails)).thenReturn(true);
    when(discoverySummaryService.getOilCaseVolumetricTotalsFromSubmissionDetail(submissionDetail)).thenReturn(discoveryOilCaseVolumetricTotalsSummaryView);
    when(discoverySummaryService.getGasCaseVolumetricTotalsFromSubmissionDetail(submissionDetail)).thenReturn(discoveryGasCaseVolumetricTotalsSummaryView);
    var modelAndView =
        submissionSummaryModelService.createDiscoverySubmissionSummaryModelAndView(submissionDetail, "view-name");

    assertThat(modelAndView.getModelMap())
        .containsExactly(
            entry("generalDetailsSummaryView", generalDetailSummaryView),
            entry("opportunityDetailsSummaryView", opportunityDetailsSummaryView),
            entry("outlineForMaximumCaseSummaryView", outlineForMaximumCaseSummaryView),
            entry("discoveryWellDetailsSummaryView", discoveryWellDetailsSummaryView),
            entry("sourceAndSealSummaryView", sourceAndSealSummaryView),
            entry("trapPropertiesSummaryView", trapPropertiesSummaryView),
            entry("reservoirSummaries", reservoirSummaries),
            entry("totalsRequired", true),
            entry("isOilCaseOnly", true),
            entry("isGasCaseOnly", true),
            entry("isCombinedCase", true),
            entry("oilCaseVolumetricTotalsSummaryView", discoveryOilCaseVolumetricTotalsSummaryView),
            entry("gasCaseVolumetricTotalsSummaryView", discoveryGasCaseVolumetricTotalsSummaryView),
            entry("combinedCaseVolumetricTotalsSummaryView", null),
            entry("accordionId", submissionDetail.getId()),
            entry("isSubmissionUpdate", false),
            entry("resourceName", "test"),
            entry("resourceDetailSummaryView", resourceDetailSummaryView)
        );
  }

  @Test
  void createFieldSubmissionSummaryModelAndView() {
    var submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    when(submissionResourceService.getResourceNameOrDefault(submissionDetail)).thenReturn("test");
    when(submissionSummaryService.getSummarySections(submissionDetail)).thenReturn(summarySections);
    when(fieldSummaryService.getReservoirSummariesFromSubmissionDetail(submissionDetail)).thenReturn(reservoirSummaries);

    var modelAndView =
        submissionSummaryModelService.createFieldSubmissionSummaryModelAndView(submissionDetail, "view-name");

    assertThat(modelAndView.getModelMap())
        .containsExactly(
            entry("summarySections", summarySections),
            entry("reservoirSummaries", reservoirSummaries),
            entry("accordionId", submissionDetail.getId()),
            entry("isSubmissionUpdate", false),
            entry("resourceName", "test")
        );
  }

  @Test
  void checkCommonAttributes_whenSubmissionUpdatePresent_whenOffshore() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setVersionNumber(2);
    when(submissionResourceService.getResourceNameOrDefault(submissionDetail)).thenReturn("test");
    var submissionUpdate = new SubmissionUpdate();
    submissionUpdate.setReason(SUBMISSION_UPDATE_REASON);
    when(submissionUpdateService.getLatestUnresolvedUpdateForUpdateSubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(submissionUpdate));
    when(prospectSummaryService.getResourceDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(resourceDetailSummaryView);

    var modelAndView =
        submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(submissionDetail, "view-name");
    assertThat(modelAndView.getModelMap())
        .contains(
            entry("accordionId", submissionDetail.getId()),
            entry("submissionUpdateReason", submissionUpdate.getReason()),
            entry("isSubmissionUpdate", true),
            entry("resourceDetailSummaryView", resourceDetailSummaryView)
        );
  }

  @Test
  void checkCommonAttributes_whenSubmissionUpdatePresent_whenOnshore() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setVersionNumber(2);
    when(submissionResourceService.getResourceNameOrDefault(submissionDetail)).thenReturn("test");
    var submissionUpdate = new SubmissionUpdate();
    submissionUpdate.setReason(SUBMISSION_UPDATE_REASON);
    var onshoreResourceDetailSummaryView = new ResourceDetailSummaryView(
        "", "", false, "", "Onshore", "resourceType");

    when(submissionUpdateService.getLatestUnresolvedUpdateForUpdateSubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(submissionUpdate));
    when(prospectSummaryService.getResourceDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(onshoreResourceDetailSummaryView);

    var modelAndView =
        submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(submissionDetail, "view-name");
    assertThat(modelAndView.getModelMap())
        .contains(
            entry("accordionId", submissionDetail.getId()),
            entry("submissionUpdateReason", submissionUpdate.getReason()),
            entry("isSubmissionUpdate", true),
            entry("resourceDetailSummaryView", onshoreResourceDetailSummaryView)
        );
  }

  @Test
  void checkCommonAttributes_whenUpdateNotPresent_assertNotSubmissionUpdate() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    when(submissionResourceService.getResourceNameOrDefault(submissionDetail)).thenReturn("test");
    when(submissionUpdateService.getLatestUnresolvedUpdateForUpdateSubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());
    when(prospectSummaryService.getResourceDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(resourceDetailSummaryView);

    var modelAndView =
        submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(submissionDetail, "view-name");
    assertThat(modelAndView.getModelMap())
        .contains(
            entry("accordionId", submissionDetail.getId()),
            entry("isSubmissionUpdate", false))
        .doesNotContainKey("submissionUpdateReason");
  }
}