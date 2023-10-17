package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService.RESERVOIR_PROPERTIES;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService.RESERVOIR_PROPERTIES_DISPLAY_ORDER;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummaryTestUtil.assertSummarySection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;

@ExtendWith(MockitoExtension.class)
class ReservoirPropertiesSummaryServiceTest {

  @Mock
  private ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private ReservoirPropertiesSummaryService reservoirPropertiesSummaryService;

  private static SubmissionDetail submissionDetail;
  private static ReservoirSubmissionDetail reservoirSubmissionDetail;

  private final ProbabilityDistributionSummaryView averageNetPayThickness =
      ProbabilityDistributionSummaryView.empty("averageNetPayThickness");
  private final ProbabilityDistributionSummaryView grossReservoirThickness =
      ProbabilityDistributionSummaryView.empty("grossReservoirThickness");
  private final ProbabilityDistributionSummaryView grossRockVolume =
      ProbabilityDistributionSummaryView.empty("grossRockVolume");
  private final ProbabilityDistributionSummaryView percentOfGrvOilBearing =
      ProbabilityDistributionSummaryView.empty("percentOfGrvOilBearing");
  private final ProbabilityDistributionSummaryView netToGross =
      ProbabilityDistributionSummaryView.empty("netToGross");
  private final ProbabilityDistributionSummaryView netPorosity =
      ProbabilityDistributionSummaryView.empty("netPorosity");
  private final ProbabilityDistributionSummaryView depthToHydrocarbonContact =
      ProbabilityDistributionSummaryView.empty("depthToHydrocarbonContact");

  private final ProbabilityDistributionSummaryView depthToGasWaterContact =
      ProbabilityDistributionSummaryView.empty("depthToGasWater");
  private final ProbabilityDistributionSummaryView depthToGasOilContact =
      ProbabilityDistributionSummaryView.empty("depthToGasOilContact");
  private final ProbabilityDistributionSummaryView depthToOilWaterContact =
      ProbabilityDistributionSummaryView.empty("depthToOilWater");
  @BeforeAll
  static void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
  }

  @Test
  void getReservoirPropertiesSummaryFromReservoirSubmissionDetail_whenProspect_noReservoirProperty() {
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());
    whenGetProbabilityDistributionSummaryCalled(false, ResourceType.PROSPECT, reservoirSubmissionDetail);

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::lithology,
            ReservoirPropertiesSummaryView::reservoirFacies,
            ReservoirPropertiesSummaryView::reservoirConnectivity,
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews,
            ReservoirPropertiesSummaryView::listOfDepthToContactDistributions)
        .containsExactly(
            "",
            "",
            "",
            List.of(
                averageNetPayThickness,
                grossReservoirThickness,
                grossRockVolume,
                netToGross,
                netPorosity,
                depthToHydrocarbonContact),
            List.of()
        );
  }

  @Test
  void getReservoirPropertiesSummaryFromReservoirSubmissionDetail_whenProspect_whenMixedCase() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setLithology(ReservoirLithology.CARBONATE);
    reservoirProperty.setGroupName(GroupName.CLAIR);

    whenGetProbabilityDistributionSummaryCalled(true, ResourceType.PROSPECT, reservoirSubmissionDetail);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));
    var hydrocarbonCaseDetail = new HydrocarbonCaseDetail();
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail))
        .thenReturn(Optional.of(hydrocarbonCaseDetail));
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCaseDetail))
        .thenReturn(true);

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromReservoirSubmissionDetail(
        reservoirSubmissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::submissionType,
            ReservoirPropertiesSummaryView::groupName,
            ReservoirPropertiesSummaryView::lithology,
            ReservoirPropertiesSummaryView::reservoirFacies,
            ReservoirPropertiesSummaryView::reservoirConnectivity,
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews,
            ReservoirPropertiesSummaryView::listOfDepthToContactDistributions)
        .containsExactly(
            SubmissionType.CREATE_PROSPECT,
            GroupName.CLAIR.getDisplayName(),
            ReservoirLithology.CARBONATE.getDisplayName(),
            "",
            "",
            List.of(
                averageNetPayThickness,
                grossReservoirThickness,
                grossRockVolume,
                percentOfGrvOilBearing,
                netToGross,
                netPorosity,
                depthToHydrocarbonContact),
            List.of()
        );
  }

  @Test
  void getReservoirPropertiesSummaryFromReservoirSubmissionDetail_whenProspect_reservoirProperty() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setLithology(ReservoirLithology.CHALK);
    reservoirProperty.setGroupName(GroupName.BRUCE);
    reservoirProperty.setReservoirFacies("facies");
    reservoirProperty.setReservoirConnectivity("connectivity");

    whenGetProbabilityDistributionSummaryCalled(false, ResourceType.PROSPECT, reservoirSubmissionDetail);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromReservoirSubmissionDetail(
        reservoirSubmissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::submissionType,
            ReservoirPropertiesSummaryView::groupName,
            ReservoirPropertiesSummaryView::lithology,
            ReservoirPropertiesSummaryView::reservoirFacies,
            ReservoirPropertiesSummaryView::reservoirConnectivity,
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews,
            ReservoirPropertiesSummaryView::listOfDepthToContactDistributions)
        .containsExactly(
            SubmissionType.CREATE_PROSPECT,
            GroupName.BRUCE.getDisplayName(),
            ReservoirLithology.CHALK.getDisplayName(),
            "facies",
            "connectivity",
            List.of(
                averageNetPayThickness,
                grossReservoirThickness,
                grossRockVolume,
                netToGross,
                netPorosity,
                depthToHydrocarbonContact),
            List.of()
        );
  }

  @Test
  void getReservoirPropertiesSummaryFromReservoirSubmissionDetail_withOtherLithology() {
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setLithology(ReservoirLithology.OTHER);
    var otherLithology = "Other lithology";
    reservoirProperty.setLithologyOther(otherLithology);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromReservoirSubmissionDetail(
        reservoirSubmissionDetail);

    assertThat(reservoirPropertiesSummaryView.lithology()).isEqualTo(otherLithology);
  }

  @Test
  void getReservoirPropertiesSummaryFromReservoirSubmissionDetail_whenDryHole_reservoirProperty() {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);

    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setGroupName(GroupName.COAL_MEASURES);
    reservoirProperty.setLithology(ReservoirLithology.CARBONATE);
    reservoirProperty.setReservoirFacies("facies");
    reservoirProperty.setReservoirConnectivity("connectivity");

    whenGetProbabilityDistributionSummaryCalled(true, ResourceType.PROSPECT, reservoirSubmissionDetail);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromReservoirSubmissionDetail(
        reservoirSubmissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::submissionType,
            ReservoirPropertiesSummaryView::groupName,
            ReservoirPropertiesSummaryView::lithology,
            ReservoirPropertiesSummaryView::reservoirFacies,
            ReservoirPropertiesSummaryView::reservoirConnectivity,
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews,
            ReservoirPropertiesSummaryView::listOfDepthToContactDistributions)
        .containsExactly(
            SubmissionType.POST_DRILL_DRY_HOLE,
            GroupName.COAL_MEASURES.getDisplayName(),
            ReservoirLithology.CARBONATE.getDisplayName(),
            "facies",
            "connectivity",
            List.of(
                averageNetPayThickness,
                grossReservoirThickness,
                grossRockVolume,
                percentOfGrvOilBearing,
                netToGross,
                netPorosity),
            List.of()
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = HydrocarbonsDiscovered.class,
      names = {"MIXED_OIL_AND_GAS"},
      mode = EnumSource.Mode.EXCLUDE)
  void getReservoirPropertiesSummaryFromReservoirSubmissionDetail_whenDiscovery_oilOnly_reservoirProperty() {
    var hydrocarbonsDiscovered = HydrocarbonsDiscovered.OIL_ONLY;
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(hydrocarbonsDiscovered);

    var discoverySubmissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);

    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setGroupName(GroupName.DUNLIN);
    reservoirProperty.setLithology(ReservoirLithology.CHALK);
    reservoirProperty.setReservoirFacies("facies");
    reservoirProperty.setReservoirConnectivity("connectivity");
    List<ProbabilityDistributionSummaryView> expectedList;
    when( generalReservoirInformationService.getReservoirHydrocarbons(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    whenGetProbabilityDistributionSummaryCalled(false, ResourceType.DISCOVERY, discoveryReservoirSubmissionDetail);
    expectedList = List.of(
        averageNetPayThickness,
        grossReservoirThickness,
        grossRockVolume,
        netToGross,
        netPorosity);

    var depthToExpectedList = whenDepthToProbabilityDistributionSummaryCalled(hydrocarbonsDiscovered, discoveryReservoirSubmissionDetail);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromReservoirSubmissionDetail(
        discoveryReservoirSubmissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::submissionType,
            ReservoirPropertiesSummaryView::groupName,
            ReservoirPropertiesSummaryView::lithology,
            ReservoirPropertiesSummaryView::reservoirFacies,
            ReservoirPropertiesSummaryView::reservoirConnectivity,
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews,
            ReservoirPropertiesSummaryView::listOfDepthToContactDistributions)
        .containsExactly(
            SubmissionType.CREATE_DISCOVERY,
            GroupName.DUNLIN.getDisplayName(),
            ReservoirLithology.CHALK.getDisplayName(),
            "facies",
            "connectivity",
            expectedList,
            depthToExpectedList
        );
  }

  @Test
  void getReservoirPropertiesSummaryFromReservoirSubmissionDetail_whenDiscovery_mixedOilAndGas_reservoirProperty() {
    var hydrocarbonsDiscovered = HydrocarbonsDiscovered.MIXED_OIL_AND_GAS;
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(hydrocarbonsDiscovered);

    var discoverySubmissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);

    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setGroupName(GroupName.DUNLIN);
    reservoirProperty.setLithology(ReservoirLithology.CHALK);
    reservoirProperty.setReservoirFacies("facies");
    reservoirProperty.setReservoirConnectivity("connectivity");
    List<ProbabilityDistributionSummaryView> expectedList;
    when( generalReservoirInformationService.getReservoirHydrocarbons(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    whenGetProbabilityDistributionSummaryCalled(true, ResourceType.DISCOVERY, discoveryReservoirSubmissionDetail);
    expectedList = List.of(
        averageNetPayThickness,
        grossReservoirThickness,
        grossRockVolume,
        percentOfGrvOilBearing,
        netToGross,
        netPorosity);

    var depthToExpectedList = whenDepthToProbabilityDistributionSummaryCalled(hydrocarbonsDiscovered, discoveryReservoirSubmissionDetail);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromReservoirSubmissionDetail(
        discoveryReservoirSubmissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::submissionType,
            ReservoirPropertiesSummaryView::groupName,
            ReservoirPropertiesSummaryView::lithology,
            ReservoirPropertiesSummaryView::reservoirFacies,
            ReservoirPropertiesSummaryView::reservoirConnectivity,
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews,
            ReservoirPropertiesSummaryView::listOfDepthToContactDistributions)
        .containsExactly(
            SubmissionType.CREATE_DISCOVERY,
            GroupName.DUNLIN.getDisplayName(),
            ReservoirLithology.CHALK.getDisplayName(),
            "facies",
            "connectivity",
            expectedList,
            depthToExpectedList
        );
  }

  @Test
  void getReservoirPropertySummaryFromReservoirSubmissionDetail_formationEnumView() {
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setFormationName(Formation.AUK);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var summaryView = reservoirPropertiesSummaryService
        .getReservoirPropertiesSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    assertThat(summaryView.formationName()).isEqualTo(Formation.AUK.getDisplayName());
  }

  @Test
  void getReservoirPropertySummaryFromReservoirSubmissionDetail_manualFormationView() {
    var formation = "manual formation";
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setFormationNameManual(formation);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var summaryView = reservoirPropertiesSummaryService
        .getReservoirPropertiesSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    assertThat(summaryView.formationName()).isEqualTo(formation);
  }

  @Test
  void getReservoirPropertySummaryFromReservoirSubmissionDetail_nullFormationView() {
    var reservoirProperty = new ReservoirProperty();

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var summaryView = reservoirPropertiesSummaryService
        .getReservoirPropertiesSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    assertThat(summaryView.formationName()).isEmpty();
  }

  @Test
  void getReservoirPropertySummaryFromReservoirSubmissionDetail_subUnitEnumView() {
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setSubUnit(ReservoirSubUnit.T10);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var summaryView = reservoirPropertiesSummaryService
        .getReservoirPropertiesSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    assertThat(summaryView.subUnit()).isEqualTo(ReservoirSubUnit.T10.getDisplayName());
  }

  @Test
  void getReservoirPropertySummaryFromReservoirSubmissionDetail_manualSubUnitView() {
    var subUnit = "manual sub unit";
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setSubUnitManual(subUnit);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var summaryView = reservoirPropertiesSummaryService
        .getReservoirPropertiesSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    assertThat(summaryView.subUnit()).isEqualTo(subUnit);
  }

  @Test
  void getReservoirPropertySummaryFromReservoirSubmissionDetail_nullSubUnitView() {
    var reservoirProperty = new ReservoirProperty();

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var summaryView = reservoirPropertiesSummaryService
        .getReservoirPropertiesSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    assertThat(summaryView.subUnit()).isEmpty();
  }

  @Test
  void getReservoirPropertySummaryFromReservoirSubmissionDetail_ageStageEnumView() {
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setAgeStage(AgeSeries.LOWER_DEVONIAN);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var summaryView = reservoirPropertiesSummaryService
        .getReservoirPropertiesSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    assertThat(summaryView.ageStageOrSeries()).isEqualTo(AgeSeries.LOWER_DEVONIAN.getDisplayName());
  }

  @Test
  void getReservoirPropertySummaryFromReservoirSubmissionDetail_manualAgeStageView() {
    var ageStage = "manual age stage";
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setAgeStageManual(ageStage);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var summaryView = reservoirPropertiesSummaryService
        .getReservoirPropertiesSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    assertThat(summaryView.ageStageOrSeries()).isEqualTo(ageStage);
  }

  @Test
  void getReservoirPropertySummaryFromReservoirSubmissionDetail_nullAgeStageView() {
    var reservoirProperty = new ReservoirProperty();

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var summaryView = reservoirPropertiesSummaryService
        .getReservoirPropertiesSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    assertThat(summaryView.ageStageOrSeries()).isEmpty();
  }

  @Test
  void getSummarySection_emptyReservoirPropertiesAndProbabilityDistributions() {
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var summaryDataView = SummaryDataView.newWithKeyValue(
        ReservoirPropertiesSummaryService.FORMATION_NAME, ""
    );
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.SUB_UNIT, "");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.AGE_STAGE_OR_SERIES, "");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.GROUP_NAME, "");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.LITHOLOGY, "");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.RESERVOIR_FACIES, "");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.RESERVOIR_CONNECTIVITY, "");

    var summaryCard1 = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem1 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard1);

    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY);

    List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews = List.of();

    when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityDistributionSummaryViews);

    var summaryCard2 = SummaryCard.probabilitySummaryCard(
        probabilityDistributionSummaryViews
    );
    var summaryItem2 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard2);

    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    var summaryItems = List.of(summaryItem1, summaryItem2);
    assertThat(summarySection.get().summaryItems()).usingRecursiveComparison().isEqualTo(summaryItems);
  }

  @ParameterizedTest
  @EnumSource(
      value = ReservoirLithology.class,
      names = {"OTHER"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void getSummarySection_nonEmpty_gasOnly_nonOtherLithology_ReservoirPropertiesAndProbabilityDistributions() {
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setFormationName(Formation.ASGARD);
    reservoirProperty.setSubUnit(ReservoirSubUnit.M1);
    reservoirProperty.setAgeStage(AgeSeries.LOWER_CARBONIFEROUS);
    reservoirProperty.setGroupName(GroupName.CHALK);
    reservoirProperty.setLithology(ReservoirLithology.SANDSTONE);
    reservoirProperty.setReservoirFacies("Test Facies");
    reservoirProperty.setReservoirConnectivity("Connected");

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(false);
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail))
        .thenReturn(false);
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(true);

    // build expected card for regular fields
    var summaryDataView = SummaryDataView.newWithKeyValue(
        ReservoirPropertiesSummaryService.FORMATION_NAME, Formation.ASGARD.getDisplayName()
    );
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.SUB_UNIT, ReservoirSubUnit.M1.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.AGE_STAGE_OR_SERIES, AgeSeries.LOWER_CARBONIFEROUS.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.GROUP_NAME, GroupName.CHALK.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.LITHOLOGY, ReservoirLithology.SANDSTONE.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.RESERVOIR_FACIES, "Test Facies");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.RESERVOIR_CONNECTIVITY, "Connected");

    var summaryCard1 = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem1 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard1);

    // create mock for expected probability distributions for hydrocarbon type
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY);

    var probabilityDistributionSummaryView1 = ProbabilityDistributionSummaryView.empty("test1");
    var probabilityDistributionSummaryView2 = ProbabilityDistributionSummaryView.empty("test2");
    List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews = List.of(
        probabilityDistributionSummaryView1,
        probabilityDistributionSummaryView2
    );

    when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityDistributionSummaryViews);

    // build expected card for probability distributions
    var summaryCard2 = SummaryCard.probabilitySummaryCard(
        probabilityDistributionSummaryViews
    );
    var summaryItem2 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard2);

    // check properties of cards returned
    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    var summaryItems = List.of(summaryItem1, summaryItem2);
    assertThat(summarySection.get().summaryItems()).usingRecursiveComparison().isEqualTo(summaryItems);
  }

  @Test
  void getSummarySection_nonEmpty_gasOnly_otherLithology_ReservoirPropertiesAndProbabilityDistributions() {
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setFormationName(Formation.ASGARD);
    reservoirProperty.setSubUnit(ReservoirSubUnit.M1);
    reservoirProperty.setAgeStage(AgeSeries.LOWER_CARBONIFEROUS);
    reservoirProperty.setGroupName(GroupName.CHALK);
    reservoirProperty.setLithology(ReservoirLithology.OTHER);
    reservoirProperty.setLithologyOther("Something else");
    reservoirProperty.setReservoirFacies("Test Facies");
    reservoirProperty.setReservoirConnectivity("Connected");

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(false);
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail))
        .thenReturn(false);
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(true);

    // build expected card for regular fields
    var summaryDataView = SummaryDataView.newWithKeyValue(
        ReservoirPropertiesSummaryService.FORMATION_NAME, Formation.ASGARD.getDisplayName()
    );
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.SUB_UNIT, ReservoirSubUnit.M1.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.AGE_STAGE_OR_SERIES, AgeSeries.LOWER_CARBONIFEROUS.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.GROUP_NAME, GroupName.CHALK.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.LITHOLOGY, "Something else");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.RESERVOIR_FACIES, "Test Facies");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.RESERVOIR_CONNECTIVITY, "Connected");

    var summaryCard1 = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem1 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard1);

    // create mock for expected probability distributions for hydrocarbon type
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY);

    var probabilityDistributionSummaryView1 = ProbabilityDistributionSummaryView.empty("test1");
    var probabilityDistributionSummaryView2 = ProbabilityDistributionSummaryView.empty("test2");
    List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews = List.of(
        probabilityDistributionSummaryView1,
        probabilityDistributionSummaryView2
    );

    when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityDistributionSummaryViews);

    // build expected card for probability distributions
    var summaryCard2 = SummaryCard.probabilitySummaryCard(
        probabilityDistributionSummaryViews
    );
    var summaryItem2 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard2);

    // check properties of cards returned
    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    var summaryItems = List.of(summaryItem1, summaryItem2);
    assertThat(summarySection.get().summaryItems()).usingRecursiveComparison().isEqualTo(summaryItems);
  }

  @Test
  void getSummarySection_nonEmpty_oilOnly_ReservoirPropertiesAndProbabilityDistributions() {
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setFormationName(Formation.ASGARD);
    reservoirProperty.setSubUnit(ReservoirSubUnit.M1);
    reservoirProperty.setAgeStage(AgeSeries.LOWER_CARBONIFEROUS);
    reservoirProperty.setGroupName(GroupName.CHALK);
    reservoirProperty.setLithology(ReservoirLithology.SANDSTONE);
    reservoirProperty.setReservoirFacies("Test Facies");
    reservoirProperty.setReservoirConnectivity("Connected");

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(true);
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail))
        .thenReturn(false);
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(false);

    // build expected card for regular fields
    var summaryDataView = SummaryDataView.newWithKeyValue(
        ReservoirPropertiesSummaryService.FORMATION_NAME, Formation.ASGARD.getDisplayName()
    );
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.SUB_UNIT, ReservoirSubUnit.M1.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.AGE_STAGE_OR_SERIES, AgeSeries.LOWER_CARBONIFEROUS.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.GROUP_NAME, GroupName.CHALK.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.LITHOLOGY, ReservoirLithology.SANDSTONE.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.RESERVOIR_FACIES, "Test Facies");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.RESERVOIR_CONNECTIVITY, "Connected");

    var summaryCard1 = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem1 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard1);

    // create mock for expected probability distributions for hydrocarbon type
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY);

    var probabilityDistributionSummaryView1 = ProbabilityDistributionSummaryView.empty("test1");
    var probabilityDistributionSummaryView2 = ProbabilityDistributionSummaryView.empty("test2");
    List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews = List.of(
        probabilityDistributionSummaryView1,
        probabilityDistributionSummaryView2
    );

    when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityDistributionSummaryViews);

    // build expected card for probability distributions
    var summaryCard2 = SummaryCard.probabilitySummaryCard(
        probabilityDistributionSummaryViews
    );
    var summaryItem2 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard2);

    // check properties of cards returned
    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    var summaryItems = List.of(summaryItem1, summaryItem2);
    assertThat(summarySection.get().summaryItems()).usingRecursiveComparison().isEqualTo(summaryItems);
  }

  @Test
  void getSummarySection_nonEmpty_mixedGasAndOil_ReservoirPropertiesAndProbabilityDistributions() {
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setFormationName(Formation.ASGARD);
    reservoirProperty.setSubUnit(ReservoirSubUnit.M1);
    reservoirProperty.setAgeStage(AgeSeries.LOWER_CARBONIFEROUS);
    reservoirProperty.setGroupName(GroupName.CHALK);
    reservoirProperty.setLithology(ReservoirLithology.SANDSTONE);
    reservoirProperty.setReservoirFacies("Test Facies");
    reservoirProperty.setReservoirConnectivity("Connected");

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(false);
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail))
        .thenReturn(true);
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(false);

    // build expected card for regular fields
    var summaryDataView = SummaryDataView.newWithKeyValue(
        ReservoirPropertiesSummaryService.FORMATION_NAME, Formation.ASGARD.getDisplayName()
    );
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.SUB_UNIT, ReservoirSubUnit.M1.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.AGE_STAGE_OR_SERIES, AgeSeries.LOWER_CARBONIFEROUS.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.GROUP_NAME, GroupName.CHALK.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.LITHOLOGY, ReservoirLithology.SANDSTONE.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.RESERVOIR_FACIES, "Test Facies");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.RESERVOIR_CONNECTIVITY, "Connected");

    var summaryCard1 = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem1 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard1);

    // create mock for expected probability distributions for hydrocarbon type
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY);

    var probabilityDistributionSummaryView1 = ProbabilityDistributionSummaryView.empty("test1");
    var probabilityDistributionSummaryView2 = ProbabilityDistributionSummaryView.empty("test2");
    List<ProbabilityDistributionSummaryView> probabilityDistributionSummaryViews = List.of(
        probabilityDistributionSummaryView1,
        probabilityDistributionSummaryView2
    );

    when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityDistributionSummaryViews);

    // build expected card for probability distributions
    var summaryCard2 = SummaryCard.probabilitySummaryCard(
        probabilityDistributionSummaryViews
    );
    var summaryItem2 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard2);

    // check properties of cards returned
    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    var summaryItems = List.of(summaryItem1, summaryItem2);
    assertThat(summarySection.get().summaryItems()).usingRecursiveComparison().isEqualTo(summaryItems);
  }

  private List<ProbabilityDistributionSummaryView> whenDepthToProbabilityDistributionSummaryCalled(HydrocarbonsDiscovered hydrocarbonsDiscovered, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY) {
      when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY, submissionDetail, reservoirSubmissionDetail))
          .thenReturn(depthToGasWaterContact);
      return List.of(depthToGasWaterContact);

    } else if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY) {
      when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
          ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY, submissionDetail, reservoirSubmissionDetail))
          .thenReturn(depthToOilWaterContact);
      return List.of(depthToOilWaterContact);
    } else {
      when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
          ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY, submissionDetail, reservoirSubmissionDetail))
          .thenReturn(depthToOilWaterContact);
      when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY, submissionDetail, reservoirSubmissionDetail))
          .thenReturn(depthToGasWaterContact);
      when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY, submissionDetail, reservoirSubmissionDetail))
          .thenReturn(depthToGasOilContact);
      return List.of(depthToOilWaterContact, depthToGasWaterContact, depthToGasOilContact);
    }
  }

  private void whenGetProbabilityDistributionSummaryCalled(boolean addPercentOfGrvOilBearing, ResourceType resourceType, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    if (submissionDetail.isDryWellSubmission()) {
      when(probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL,
              submissionDetail, reservoirSubmissionDetail
          )).thenReturn(averageNetPayThickness);
    } else if (submissionDetail.isDiscoverySubmission()) {
        when(probabilityDistributionSummaryService
            .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
                ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY,
                submissionDetail, reservoirSubmissionDetail
            )).thenReturn(averageNetPayThickness);
    } else {
      when(probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT,
              submissionDetail, reservoirSubmissionDetail
          )).thenReturn(averageNetPayThickness);
    }

    if (resourceType == ResourceType.PROSPECT) {
      when(probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT,
              submissionDetail, reservoirSubmissionDetail
          )).thenReturn(grossReservoirThickness);
      when(probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT,
              submissionDetail, reservoirSubmissionDetail
          )).thenReturn(grossRockVolume);
      if (addPercentOfGrvOilBearing) {
        when(probabilityDistributionSummaryService
            .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
                ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT,
                submissionDetail, reservoirSubmissionDetail
            )).thenReturn(percentOfGrvOilBearing);
      }
      when(probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT,
              submissionDetail, reservoirSubmissionDetail
          )).thenReturn(netToGross);
      when(probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT,
              submissionDetail, reservoirSubmissionDetail
          )).thenReturn(netPorosity);
    } else if (resourceType == ResourceType.DISCOVERY) {
      when(probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY,
              submissionDetail, reservoirSubmissionDetail
          )).thenReturn(grossReservoirThickness);
      when(probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY,
              submissionDetail, reservoirSubmissionDetail
          )).thenReturn(grossRockVolume);
      if (addPercentOfGrvOilBearing) {
        when(probabilityDistributionSummaryService
            .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
                ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY,
                submissionDetail, reservoirSubmissionDetail
            )).thenReturn(percentOfGrvOilBearing);
      }
      when(probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY,
              submissionDetail, reservoirSubmissionDetail
          )).thenReturn(netToGross);
      when(probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY,
              submissionDetail, reservoirSubmissionDetail
          )).thenReturn(netPorosity);
    }

    if (submissionDetail.isProspectSubmission()) {
      when(probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT_PROSPECT,
              submissionDetail, reservoirSubmissionDetail
          )).thenReturn(depthToHydrocarbonContact);
    }
  }
}