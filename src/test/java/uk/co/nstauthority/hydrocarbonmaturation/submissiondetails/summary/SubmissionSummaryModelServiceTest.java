package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView;

@ExtendWith(MockitoExtension.class)
class SubmissionSummaryModelServiceTest {

  @Mock
  private ProspectSummaryService prospectSummaryService;
  @Mock
  private HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  @Mock
  private SubmissionReadyService submissionReadyService;
  @Mock
  private DiscoverySummaryService discoverySummaryService;
  @Mock
  private DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;
  @Mock
  private PostDrillDryWellSummaryService postDrillDryWellSummaryService;

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

  @BeforeEach
  void setUp() {


    generalDetailSummaryView =  GeneralDetailSummaryView.empty(SubmissionType.CREATE_PROSPECT, "prospect");
    sourceAndSealSummaryView =  SourceAndSealSummaryView.empty();
    hydrocarbonCaseSummaryView = HydrocarbonCaseSummaryView.empty();
    reservoirPropertiesSummaryView = new ReservoirPropertiesSummaryView(
        SubmissionType.CREATE_PROSPECT,
        "formation",
        "age",
        "lithology",
        "facies",
        "connectivity",
        new ArrayList<>(),
        new ArrayList<>());
    trapPropertiesSummaryView = new TrapPropertiesSummaryView(
        "desc",
        "Other",
        "other trap type",
        "1",
        "1",
        "1",
        "10",
        "10",
        "10",
        "East",
        ReservoirDatum.ED50.getDisplayName(),
        "15/10",
        new ArrayList<>(),
        new HashMap<>()
    );
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
        "age",
        "lithology",
        "facies",
        "connectivity",
        new ArrayList<>(),
        new ArrayList<>());

    gasFluidPropertySummaryView =  GasFluidPropertySummaryView.empty();
    oilFluidPropertySummaryView =  OilFluidPropertySummaryView.empty();
    generalFluidPropertySummaryView = GeneralFluidPropertySummaryView.empty();
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class)
  void createSummaryForSubmissionTypeToModelAndView(SubmissionType submissionType) {
    var submissionSummaryModelServiceSpy = spy(submissionSummaryModelService);

    var submissionDetail = new SubmissionDetail();
    submissionDetail.setSubmissionType(submissionType);
    var viewName = "view-name";

    submissionSummaryModelServiceSpy.createSummaryForSubmissionTypeToModelAndView(submissionDetail, viewName);

    switch (submissionDetail.getSubmissionType()) {
      case CREATE_PROSPECT, PROSPECT_UPDATE ->
          verify(submissionSummaryModelServiceSpy).createProspectSubmissionSummaryModelAndView(submissionDetail, viewName);
      case POST_DRILL_DRY_HOLE ->
          verify(submissionSummaryModelServiceSpy).createPostDrillDryWellSubmissionSummaryModelAndView(submissionDetail, viewName);
      case CREATE_DISCOVERY, UPDATE_DISCOVERY ->
          verify(submissionSummaryModelServiceSpy).createDiscoverySubmissionSummaryModelAndView(submissionDetail, viewName);
    };
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

    var modelAndView =
        submissionSummaryModelService.createPostDrillDryWellSubmissionSummaryModelAndView(submissionDetail, "view-name");

    assertThat(modelAndView.getModelMap())
        .contains(
            entry("dryWellDetailsSummaryView", dryWellDetailsSummaryView),
            entry("permeabilitySummaryView", permeabilityDetailSummaryView),
            entry("reservoirPropertiesSummaryView", reservoirPropertiesSummaryView),
            entry("generalDetailsSummaryView", generalDetailSummaryView)
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

    var modelAndView =
        submissionSummaryModelService.createDiscoverySubmissionSummaryModelAndView(submissionDetail, "view-name");

    assertThat(modelAndView.getModelMap())
        .contains(
            entry("gasFluidPropertiesSummaryView", gasFluidPropertySummaryView),
            entry("generalFluidPropertiesSummaryView", generalFluidPropertySummaryView),
            entry("oilFluidPropertiesSummaryView", oilFluidPropertySummaryView),
            entry("permeabilitySummaryView", permeabilityDetailSummaryView),
            entry("generalDetailsSummaryView", generalDetailSummaryView),
            entry("gasOnlyDiscovery", false),
            entry("oilOnlyDiscovery", false),
            entry("mixedDiscovery", false)
        );
  }
}