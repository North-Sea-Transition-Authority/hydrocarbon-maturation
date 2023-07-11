package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdate;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView;

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
  private DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;
  @Mock
  private PostDrillDryWellSummaryService postDrillDryWellSummaryService;
  @Mock
  private SubmissionUpdateService submissionUpdateService;

  @InjectMocks
  private SubmissionSummaryModelService submissionSummaryModelService;

  private GeneralDetailSummaryView generalDetailSummaryView;
  private SourceAndSealSummaryView sourceAndSealSummaryView;
  private HydrocarbonCaseSummaryView hydrocarbonCaseSummaryView;
  private ChanceOfSuccessSummaryView chanceOfSuccessSummaryView;
  private ReservoirPropertiesSummaryView reservoirPropertiesSummaryView;
  private ProbabilityDistributionsFormSummaryView oilCaseVolumetricsSummaryView;
  private ProbabilityDistributionsFormSummaryView gasCaseVolumetricsSummaryView;
  private OilCaseOutputsSummaryView oilCaseOutputsSummaryView;
  private ProbabilityDistributionsFormSummaryView mixedCaseVolumetricsSummaryView;
  private MixedCaseOutputsSummaryView mixedCaseOutputsSummaryView;
  private TrapPropertiesSummaryView trapPropertiesSummaryView;
  private OpportunityDetailsSummaryView opportunityDetailsSummaryView;
  private PermeabilityDetailSummaryView permeabilityDetailSummaryView;
  private DryWellDetailsSummaryView dryWellDetailsSummaryView;
  private GasFluidPropertySummaryView gasFluidPropertySummaryView;
  private OilFluidPropertySummaryView oilFluidPropertySummaryView;
  private GeneralFluidPropertySummaryView generalFluidPropertySummaryView;
  private ResourceDetailSummaryView resourceDetailSummaryView;

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
    oilFluidPropertySummaryView =  OilFluidPropertySummaryView.empty();
    generalFluidPropertySummaryView = GeneralFluidPropertySummaryView.empty();
    resourceDetailSummaryView = new ResourceDetailSummaryView(
        "", "", false, "", "Offshore", "resourceType");
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FIELD", "CREATE_CSR"}, mode = EnumSource.Mode.EXCLUDE)
  void createSummaryForSubmissionTypeToModelAndView(SubmissionType submissionType) {
    var submissionSummaryModelServiceSpy = spy(submissionSummaryModelService);

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    var viewName = "view-name";

    when(prospectSummaryService.getResourceDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(resourceDetailSummaryView);

    submissionSummaryModelServiceSpy.createSummaryForSubmissionTypeToModelAndView(submissionDetail, viewName);

    switch (submissionDetail.getSubmissionType()) {
      case CREATE_PROSPECT, PROSPECT_UPDATE ->
          verify(submissionSummaryModelServiceSpy).createProspectSubmissionSummaryModelAndView(submissionDetail, viewName);
      case POST_DRILL_DRY_HOLE ->
          verify(submissionSummaryModelServiceSpy).createPostDrillDryWellSubmissionSummaryModelAndView(submissionDetail, viewName);
      case CREATE_DISCOVERY, UPDATE_DISCOVERY ->
          verify(submissionSummaryModelServiceSpy).createDiscoverySubmissionSummaryModelAndView(submissionDetail, viewName);
    }
  }

  @Test
  void createProspectSubmissionSummaryModelAndView() {
    var submissionDetail = Mockito.mock(SubmissionDetail.class);
    when(prospectSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalDetailSummaryView);
    when(prospectSummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(sourceAndSealSummaryView);
    when(prospectSummaryService.getHydrocarbonCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(hydrocarbonCaseSummaryView);
    when(prospectSummaryService.getReservoirPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(reservoirPropertiesSummaryView);
    when(prospectSummaryService.getChanceOfSuccessSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(chanceOfSuccessSummaryView);
    when(prospectSummaryService.getOilCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(oilCaseVolumetricsSummaryView);
    when(prospectSummaryService.getGasCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(gasCaseVolumetricsSummaryView);
    when(prospectSummaryService.getOilCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(oilCaseOutputsSummaryView);
    when(prospectSummaryService.getMixedCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(mixedCaseVolumetricsSummaryView);
    when(prospectSummaryService.getMixedCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(mixedCaseOutputsSummaryView);
    when(prospectSummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(trapPropertiesSummaryView);
    when(prospectSummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(opportunityDetailsSummaryView);
    when(prospectSummaryService.getResourceDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(resourceDetailSummaryView);

    var modelAndView =
        submissionSummaryModelService.createProspectSubmissionSummaryModelAndView(submissionDetail, "view-name");

    assertThat(modelAndView.getModelMap())
        .contains(
            entry("generalDetailsSummaryView", generalDetailSummaryView),
            entry("sourceAndSealSummaryView", sourceAndSealSummaryView),
            entry("hydrocarbonCaseSummaryView", hydrocarbonCaseSummaryView),
            entry("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView),
            entry("chanceOfSuccessSummaryView", chanceOfSuccessSummaryView),
            entry("oilCaseVolumetricsSummaryView", oilCaseVolumetricsSummaryView),
            entry("gasCaseVolumetricsSummaryView", gasCaseVolumetricsSummaryView),
            entry("oilCaseOutputsSummaryView", oilCaseOutputsSummaryView),
            entry("mixedCaseVolumetricsSummaryView", mixedCaseVolumetricsSummaryView),
            entry("mixedCaseOutputsSummaryView", mixedCaseOutputsSummaryView),
            entry("trapPropertiesSummaryView", trapPropertiesSummaryView),
            entry("opportunityDetailsSummaryView", opportunityDetailsSummaryView)
        );
  }

  @Test
  void createPostDrillDryWellSubmissionSummaryModelAndView() {
    var submissionDetail = Mockito.mock(SubmissionDetail.class);
    when(postDrillDryWellSummaryService.getDryWellDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(dryWellDetailsSummaryView);
    when(postDrillDryWellSummaryService.getPermeabilitySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(permeabilityDetailSummaryView);
    when(postDrillDryWellSummaryService.getReservoirPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(reservoirPropertiesSummaryView);
    when(postDrillDryWellSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalDetailSummaryView);
    when(prospectSummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(trapPropertiesSummaryView);
    when(prospectSummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(opportunityDetailsSummaryView);
    when(prospectSummaryService.getResourceDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(resourceDetailSummaryView);

    var modelAndView =
        submissionSummaryModelService.createPostDrillDryWellSubmissionSummaryModelAndView(submissionDetail, "view-name");

    assertThat(modelAndView.getModelMap())
        .contains(
            entry("dryWellDetailsSummaryView", dryWellDetailsSummaryView),
            entry("permeabilitySummaryView", permeabilityDetailSummaryView),
            entry("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView),
            entry("generalDetailsSummaryView", generalDetailSummaryView),
            entry("trapPropertiesSummaryView", trapPropertiesSummaryView)
            );
  }

  @Test
  void createDiscoverySubmissionSummaryModelAndView() {
    var submissionDetail = Mockito.mock(SubmissionDetail.class);
    when(discoverySummaryService.getPermeabilitySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(permeabilityDetailSummaryView);
    when(discoverySummaryService.getOilFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(oilFluidPropertySummaryView);
    when(discoverySummaryService.getGasFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(gasFluidPropertySummaryView);
    when(discoverySummaryService.getGeneralFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalFluidPropertySummaryView);
    when(discoverySummaryService.getGeneralDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(generalDetailSummaryView);
    when(prospectSummaryService.getOilCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(oilCaseVolumetricsSummaryView);
    when(prospectSummaryService.getGasCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(gasCaseVolumetricsSummaryView);
    when(prospectSummaryService.getOilCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(oilCaseOutputsSummaryView);
    when(prospectSummaryService.getMixedCaseVolumetricsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(mixedCaseVolumetricsSummaryView);
    when(prospectSummaryService.getMixedCaseOutputsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(mixedCaseOutputsSummaryView);
    when(prospectSummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(trapPropertiesSummaryView);
    when(prospectSummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(opportunityDetailsSummaryView);
    when(prospectSummaryService.getResourceDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .thenReturn(resourceDetailSummaryView);

    var modelAndView =
        submissionSummaryModelService.createDiscoverySubmissionSummaryModelAndView(submissionDetail, "view-name");

    assertThat(modelAndView.getModelMap())
        .contains(
            entry("gasFluidPropertiesSummaryView", gasFluidPropertySummaryView),
            entry("generalFluidPropertiesSummaryView", generalFluidPropertySummaryView),
            entry("oilFluidPropertiesSummaryView", oilFluidPropertySummaryView),
            entry("permeabilitySummaryView", permeabilityDetailSummaryView),
            entry("generalDetailsSummaryView", generalDetailSummaryView),
            entry("trapPropertiesSummaryView", trapPropertiesSummaryView),
            entry("gasOnlyDiscovery", false),
            entry("oilOnlyDiscovery", false),
            entry("mixedDiscovery", false)
        );
  }

  @Test
  void checkCommonAttributes_whenSubmissionUpdatePresent_whenOffshore() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setVersionNumber(2);
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