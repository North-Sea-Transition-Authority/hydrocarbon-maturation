package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService.RESERVOIR_PROPERTIES;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService.RESERVOIR_PROPERTIES_DISPLAY_ORDER;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummaryTestUtil.assertSummarySection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StandardLithology;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
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
  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @Mock
  private BarrierBaffleDetailRepository barrierBaffleDetailRepository;

  @InjectMocks
  private ReservoirPropertiesSummaryService reservoirPropertiesSummaryService;

  private static SubmissionDetail submissionDetail;
  private static ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeAll
  static void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
  }

  @Test
  void getReservoirPropertiesSummaryFromReservoirSubmissionDetail_whenProspect_noReservoirProperty() {
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    reservoirSubmissionDetail.setSubmissionDetail(submissionDetail);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var depthTableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        List.of(), submissionDetail, reservoirSubmissionDetail))
        .thenReturn(depthTableView);

    var probabilityTableView = ProbabilityDistributionTableView.empty();
    var probabilityInstances = List.of(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT,
        ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT_PROSPECT
    );
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        probabilityInstances, submissionDetail, reservoirSubmissionDetail))
        .thenReturn(probabilityTableView);

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromReservoirSubmissionDetail(
        reservoirSubmissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::lithology,
            ReservoirPropertiesSummaryView::reservoirFacies,
            ReservoirPropertiesSummaryView::reservoirConnectivity,
            ReservoirPropertiesSummaryView::tableView,
            ReservoirPropertiesSummaryView::depthToContactDistributions)
        .containsExactly(
            "",
            "",
            "",
            probabilityTableView,
            depthTableView
        );
  }

  @Test
  void getReservoirPropertiesSummaryFromReservoirSubmissionDetail_whenProspect_whenMixedCase() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setLithology(StandardLithology.CHALK);
    reservoirProperty.setGroupName(GroupName.CLAIR);

    var depthTableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        List.of(), submissionDetail, reservoirSubmissionDetail))
        .thenReturn(depthTableView);

    var probabilityTableView = ProbabilityDistributionTableView.empty();
    var probabilityInstances = List.of(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT,
        ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT_PROSPECT
    );
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        probabilityInstances, submissionDetail, reservoirSubmissionDetail))
        .thenReturn(probabilityTableView);

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
            ReservoirPropertiesSummaryView::tableView,
            ReservoirPropertiesSummaryView::depthToContactDistributions)
        .containsExactly(
            SubmissionType.CREATE_PROSPECT,
            GroupName.CLAIR.getDisplayName(),
            StandardLithology.CHALK.getDisplayName(),
            "",
            "",
            probabilityTableView,
            depthTableView
        );
  }

  @Test
  void getReservoirPropertiesSummaryFromReservoirSubmissionDetail_whenProspect_reservoirProperty() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setLithology(StandardLithology.CHALK);
    reservoirProperty.setGroupName(GroupName.BRUCE);
    reservoirProperty.setReservoirFacies("facies");
    reservoirProperty.setReservoirConnectivity("connectivity");

    var depthTableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        List.of(), submissionDetail, reservoirSubmissionDetail))
        .thenReturn(depthTableView);

    var probabilityTableView = ProbabilityDistributionTableView.empty();
    var probabilityInstances = List.of(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT,
        ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT_PROSPECT
    );
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        probabilityInstances, submissionDetail, reservoirSubmissionDetail))
        .thenReturn(probabilityTableView);

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
            ReservoirPropertiesSummaryView::tableView,
            ReservoirPropertiesSummaryView::depthToContactDistributions)
        .containsExactly(
            SubmissionType.CREATE_PROSPECT,
            GroupName.BRUCE.getDisplayName(),
            StandardLithology.CHALK.getDisplayName(),
            "facies",
            "connectivity",
            probabilityTableView,
            depthTableView
        );
  }

  @Test
  void getReservoirPropertiesSummaryFromReservoirSubmissionDetail_withOtherLithology() {
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setLithology(StandardLithology.OTHER);
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
    reservoirProperty.setLithology(StandardLithology.CHALK);
    reservoirProperty.setReservoirFacies("facies");
    reservoirProperty.setReservoirConnectivity("connectivity");

    var depthTableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        List.of(), submissionDetail, reservoirSubmissionDetail))
        .thenReturn(depthTableView);

    var probabilityTableView = ProbabilityDistributionTableView.empty();
    var probabilityInstances = List.of(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT
    );
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        probabilityInstances, submissionDetail, reservoirSubmissionDetail))
        .thenReturn(probabilityTableView);

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
            ReservoirPropertiesSummaryView::tableView,
            ReservoirPropertiesSummaryView::depthToContactDistributions)
        .containsExactly(
            SubmissionType.POST_DRILL_DRY_HOLE,
            GroupName.COAL_MEASURES.getDisplayName(),
            StandardLithology.CHALK.getDisplayName(),
            "facies",
            "connectivity",
            probabilityTableView,
            depthTableView
        );
  }

  @Test
  void getReservoirPropertiesSummaryFromReservoirSubmissionDetail_whenDiscovery_oilOnly_reservoirProperty() {
    var hydrocarbonsDiscovered = HydrocarbonsDiscovered.OIL_ONLY;
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(hydrocarbonsDiscovered);

    var discoverySubmissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(
        SubmissionType.CREATE_DISCOVERY);
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        discoverySubmissionDetail);

    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setGroupName(GroupName.DUNLIN);
    reservoirProperty.setLithology(StandardLithology.CHALK);
    reservoirProperty.setReservoirFacies("facies");
    reservoirProperty.setReservoirConnectivity("connectivity");
    when(generalReservoirInformationService.getReservoirHydrocarbons(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    var depthTableView = ProbabilityDistributionTableView.empty();
    var depthToExpectedList = List.of(ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY);
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        depthToExpectedList, discoverySubmissionDetail, discoveryReservoirSubmissionDetail))
        .thenReturn(depthTableView);

    var probabilityTableView = ProbabilityDistributionTableView.empty();
    var probabilityInstances = List.of(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY,
        ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY
    );
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        probabilityInstances, discoverySubmissionDetail, discoveryReservoirSubmissionDetail))
        .thenReturn(probabilityTableView);

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
            ReservoirPropertiesSummaryView::tableView,
            ReservoirPropertiesSummaryView::depthToContactDistributions)
        .containsExactly(
            SubmissionType.CREATE_DISCOVERY,
            GroupName.DUNLIN.getDisplayName(),
            StandardLithology.CHALK.getDisplayName(),
            "facies",
            "connectivity",
            probabilityTableView,
            depthTableView
        );
  }

  @Test
  void getReservoirPropertiesSummaryFromReservoirSubmissionDetail_whenDiscovery_mixedOilAndGas_reservoirProperty() {
    var hydrocarbonsDiscovered = HydrocarbonsDiscovered.MIXED_OIL_AND_GAS;
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(hydrocarbonsDiscovered);

    var discoverySubmissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(
        SubmissionType.CREATE_DISCOVERY);
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        discoverySubmissionDetail);

    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setGroupName(GroupName.DUNLIN);
    reservoirProperty.setLithology(StandardLithology.CHALK);
    reservoirProperty.setReservoirFacies("facies");
    reservoirProperty.setReservoirConnectivity("connectivity");
    when(generalReservoirInformationService.getReservoirHydrocarbons(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    var depthTableView = ProbabilityDistributionTableView.empty();
    var depthToExpectedList = List.of(
        ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY,
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY
    );
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        depthToExpectedList, discoverySubmissionDetail, discoveryReservoirSubmissionDetail))
        .thenReturn(depthTableView);

    var probabilityTableView = ProbabilityDistributionTableView.empty();
    var probabilityInstances = List.of(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY,
        ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY
    );
    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        probabilityInstances, discoverySubmissionDetail, discoveryReservoirSubmissionDetail))
        .thenReturn(probabilityTableView);

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
            ReservoirPropertiesSummaryView::tableView,
            ReservoirPropertiesSummaryView::depthToContactDistributions)
        .containsExactly(
            SubmissionType.CREATE_DISCOVERY,
            GroupName.DUNLIN.getDisplayName(),
            StandardLithology.CHALK.getDisplayName(),
            "facies",
            "connectivity",
            probabilityTableView,
            depthTableView
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
    reservoirProperty.setAgeStage(ReservoirAge.MIDDLE_JURASSIC);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var summaryView = reservoirPropertiesSummaryService
        .getReservoirPropertiesSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

    assertThat(summaryView.ageStageOrSeries()).isEqualTo(ReservoirAge.MIDDLE_JURASSIC.getDisplayName());
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
  void getSummarySection_discovery_emptyReservoirPropertiesAndProbabilityDistributions() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var summaryDataView = new SummaryDataView(new ArrayList<>());

    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.AGE_STAGE_OR_SERIES, "");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.GROUP_NAME, "");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.FORMATION_NAME, "");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.SUB_UNIT, "");
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

    var probabilityTableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityTableView);

    // build expected card for probability distributions
    var summaryCard2 = SummaryCard.probabilitySummaryCard(probabilityTableView);
    var summaryItem2 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard2);

    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    var summaryItems = List.of(summaryItem1, summaryItem2);
    assertThat(summarySection.get().summaryItems()).usingRecursiveComparison().isEqualTo(summaryItems);
  }

  @Test
  void getSummarySection_discovery_nonEmpty_gasOnly_nonOtherLithology_ReservoirPropertiesAndProbabilityDistributions() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setFormationName(Formation.ASGARD);
    reservoirProperty.setSubUnit(ReservoirSubUnit.M1);
    reservoirProperty.setAgeStage(ReservoirAge.MIDDLE_JURASSIC);
    reservoirProperty.setGroupName(GroupName.CHALK);
    reservoirProperty.setLithology(StandardLithology.SANDSTONE);
    reservoirProperty.setReservoirFacies("Test Facies");
    reservoirProperty.setReservoirConnectivity("Connected");

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isGasOnly(reservoirHydrocarbons))
        .thenReturn(true);

    // build expected card for regular fields
    var summaryDataView = new SummaryDataView(new ArrayList<>());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.AGE_STAGE_OR_SERIES,
        ReservoirAge.MIDDLE_JURASSIC.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.GROUP_NAME, GroupName.CHALK.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.FORMATION_NAME, Formation.ASGARD.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.SUB_UNIT, ReservoirSubUnit.M1.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.LITHOLOGY,
        StandardLithology.SANDSTONE.getDisplayName());
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

    var probabilityTableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityTableView);

    // build expected card for probability distributions
    var summaryCard2 = SummaryCard.probabilitySummaryCard(probabilityTableView);
    var summaryItem2 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard2);

    // check properties of cards returned
    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    var summaryItems = List.of(summaryItem1, summaryItem2);
    assertThat(summarySection.get().summaryItems()).usingRecursiveComparison().isEqualTo(summaryItems);
  }

  @Test
  void getSummarySection_discovery_nonEmpty_gasOnly_otherLithology_ReservoirPropertiesAndProbabilityDistributions() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setFormationName(Formation.ASGARD);
    reservoirProperty.setSubUnit(ReservoirSubUnit.M1);
    reservoirProperty.setAgeStage(ReservoirAge.MIDDLE_JURASSIC);
    reservoirProperty.setGroupName(GroupName.CHALK);
    reservoirProperty.setLithology(StandardLithology.OTHER);
    reservoirProperty.setLithologyOther("Something else");
    reservoirProperty.setReservoirFacies("Test Facies");
    reservoirProperty.setReservoirConnectivity("Connected");

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isGasOnly(reservoirHydrocarbons))
        .thenReturn(true);

    // build expected card for regular fields
    var summaryDataView = new SummaryDataView(new ArrayList<>());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.AGE_STAGE_OR_SERIES,
        ReservoirAge.MIDDLE_JURASSIC.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.GROUP_NAME, GroupName.CHALK.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.FORMATION_NAME, Formation.ASGARD.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.SUB_UNIT, ReservoirSubUnit.M1.getDisplayName());
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

    var probabilityTableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityTableView);

    // build expected card for probability distributions
    var summaryCard2 = SummaryCard.probabilitySummaryCard(probabilityTableView);
    var summaryItem2 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard2);

    // check properties of cards returned
    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    var summaryItems = List.of(summaryItem1, summaryItem2);
    assertThat(summarySection.get().summaryItems()).usingRecursiveComparison().isEqualTo(summaryItems);
  }

  @Test
  void getSummarySection_discovery_nonEmpty_oilOnly_ReservoirPropertiesAndProbabilityDistributions() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setFormationName(Formation.ASGARD);
    reservoirProperty.setSubUnit(ReservoirSubUnit.M1);
    reservoirProperty.setAgeStage(ReservoirAge.MIDDLE_JURASSIC);
    reservoirProperty.setGroupName(GroupName.CHALK);
    reservoirProperty.setLithology(StandardLithology.SANDSTONE);
    reservoirProperty.setReservoirFacies("Test Facies");
    reservoirProperty.setReservoirConnectivity("Connected");

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isOilOnly(reservoirHydrocarbons))
        .thenReturn(true);

    // build expected card for regular fields
    var summaryDataView = new SummaryDataView(new ArrayList<>());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.AGE_STAGE_OR_SERIES,
        ReservoirAge.MIDDLE_JURASSIC.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.GROUP_NAME, GroupName.CHALK.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.FORMATION_NAME, Formation.ASGARD.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.SUB_UNIT, ReservoirSubUnit.M1.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.LITHOLOGY,
        StandardLithology.SANDSTONE.getDisplayName());
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

    var probabilityTableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityTableView);

    // build expected card for probability distributions
    var summaryCard2 = SummaryCard.probabilitySummaryCard(probabilityTableView);
    var summaryItem2 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard2);

    // check properties of cards returned
    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    var summaryItems = List.of(summaryItem1, summaryItem2);
    assertThat(summarySection.get().summaryItems()).usingRecursiveComparison().isEqualTo(summaryItems);
  }

  @Test
  void getSummarySection_discovery_nonEmpty_mixedGasAndOil_ReservoirPropertiesAndProbabilityDistributions() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setFormationName(Formation.ASGARD);
    reservoirProperty.setSubUnit(ReservoirSubUnit.M1);
    reservoirProperty.setAgeStage(ReservoirAge.MIDDLE_JURASSIC);
    reservoirProperty.setGroupName(GroupName.CHALK);
    reservoirProperty.setLithology(StandardLithology.SANDSTONE);
    reservoirProperty.setReservoirFacies("Test Facies");
    reservoirProperty.setReservoirConnectivity("Connected");

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons))
        .thenReturn(true);

    // build expected card for regular fields
    var summaryDataView = new SummaryDataView(new ArrayList<>());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.AGE_STAGE_OR_SERIES,
        ReservoirAge.MIDDLE_JURASSIC.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.GROUP_NAME, GroupName.CHALK.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.FORMATION_NAME, Formation.ASGARD.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.SUB_UNIT, ReservoirSubUnit.M1.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.LITHOLOGY,
        StandardLithology.SANDSTONE.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.RESERVOIR_FACIES, "Test Facies");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.RESERVOIR_CONNECTIVITY, "Connected");

    var summaryCard1 = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem1 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard1);

    // create mock for expected probability distributions for hydrocarbon type
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY);

    var probabilityTableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityTableView);

    // build expected card for probability distributions
    var summaryCard2 = SummaryCard.probabilitySummaryCard(probabilityTableView);
    var summaryItem2 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard2);

    // check properties of cards returned
    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    var summaryItems = List.of(summaryItem1, summaryItem2);
    assertThat(summarySection.get().summaryItems()).usingRecursiveComparison().isEqualTo(summaryItems);
  }

  @Test
  void getSummarySection_nonEmpty_manualInputs() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setFormationNameManual("manual formation");
    reservoirProperty.setSubUnitManual("manual subunit");
    reservoirProperty.setAgeStageManual("manual age stage");
    reservoirProperty.setGroupName(GroupName.CHALK);
    reservoirProperty.setLithology(StandardLithology.OTHER);
    reservoirProperty.setLithologyOther("other lithology");
    reservoirProperty.setReservoirFacies("Test Facies");
    reservoirProperty.setReservoirConnectivity("Connected");

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons))
        .thenReturn(true);

    // build expected card for regular fields
    var summaryDataView = new SummaryDataView(new ArrayList<>());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.AGE_STAGE_OR_SERIES, "manual age stage");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.GROUP_NAME, GroupName.CHALK.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.FORMATION_NAME, "manual formation");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.SUB_UNIT, "manual subunit");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.LITHOLOGY, "other lithology");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.RESERVOIR_FACIES, "Test Facies");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.RESERVOIR_CONNECTIVITY, "Connected");

    var summaryCard1 = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem1 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard1);

    // create mock for expected probability distributions for hydrocarbon type
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY);

    var probabilityTableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityTableView);

    // build expected card for probability distributions
    var summaryCard2 = SummaryCard.probabilitySummaryCard(probabilityTableView);
    var summaryItem2 = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard2);

    // check properties of cards returned
    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    var summaryItems = List.of(summaryItem1, summaryItem2);
    assertThat(summarySection.get().summaryItems()).usingRecursiveComparison().isEqualTo(summaryItems);
  }

  @Test
  void getSummarySection_fieldSubmission_assertReservoirProperties() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setFormationNameManual("manual formation");
    reservoirProperty.setSubUnitManual("manual subunit");
    reservoirProperty.setAgeStageManual("manual age stage");
    reservoirProperty.setGroupName(GroupName.CHALK);
    reservoirProperty.setLithology(StandardLithology.OTHER);
    reservoirProperty.setLithologyOther("other lithology");
    reservoirProperty.setReservoirFacies("Test Facies");
    reservoirProperty.setFracturePressure(BigDecimal.ONE);
    reservoirProperty.setReservoirConnectivity("Connected");
    reservoirProperty.setDepositionalSetting(DepositionalSetting.COASTAL);
    reservoirProperty.setDepositionalEnvironment(DepositionalEnvironment.DELTA);
    reservoirProperty.setLateralBarriersBaffles(true);
    reservoirProperty.setVerticalBarriersBaffles(true);

    var lateralBarrierBaffleDetail1 = new BarrierBaffleDetail();
    lateralBarrierBaffleDetail1.setBarrierBaffle(BarrierBaffle.CEMENTATION);
    var lateralBarrierBaffleDetail2 = new BarrierBaffleDetail();
    lateralBarrierBaffleDetail2.setBarrierBaffle(BarrierBaffle.FAULTS_FRACTURES);

    when(barrierBaffleDetailRepository
        .findAllByReservoirPropertyAndVerticalLateralType(reservoirProperty, VerticalLateralType.LATERAL))
        .thenReturn(Set.of(lateralBarrierBaffleDetail1, lateralBarrierBaffleDetail2));

    var verticalBarrierBaffleDetail1 = new BarrierBaffleDetail();
    verticalBarrierBaffleDetail1.setBarrierBaffle(BarrierBaffle.TAR_MAT);
    var verticalBarrierBaffleDetail2 = new BarrierBaffleDetail();
    verticalBarrierBaffleDetail2.setBarrierBaffle(BarrierBaffle.TIGHT_INTERBEDS);

    when(barrierBaffleDetailRepository
        .findAllByReservoirPropertyAndVerticalLateralType(reservoirProperty, VerticalLateralType.VERTICAL))
        .thenReturn(Set.of(verticalBarrierBaffleDetail1, verticalBarrierBaffleDetail2));

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons))
        .thenReturn(true);

    // build expected card for regular fields
    var summaryDataView = new SummaryDataView(new ArrayList<>());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.AGE_STAGE_OR_SERIES, "manual age stage");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.GROUP_NAME, GroupName.CHALK.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.FORMATION_NAME, "manual formation");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.SUB_UNIT, "manual subunit");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.LITHOLOGY, "other lithology");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.RESERVOIR_FACIES, "Test Facies");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.RESERVOIR_CONNECTIVITY, "Connected");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.RESERVOIR_FRACTURE_PRESSURE, "1 psi");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.DEPOSITIONAL_SETTING,
        DepositionalSetting.COASTAL.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.DEPOSITIONAL_ENVIRONMENT,
        DepositionalEnvironment.DELTA.getDisplayName());
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.IS_LATERAL_BARRIERS_BAFFLES, "Yes");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.LATERAL_BARRIERS_BAFFLES,
        "Cementation, faults/fractures");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.IS_VERTICAL_BARRIERS_BAFFLES, "Yes");
    summaryDataView.addKeyValue(ReservoirPropertiesSummaryService.VERTICAL_BARRIERS_BAFFLES,
        "Tar mat, tight interbeds");

    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard);

    // check properties of card returned
    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    assertThat(summarySection.get().summaryItems().get(0)).usingRecursiveComparison()
        .isEqualTo(summaryItem);
  }

  @Test
  void getSummarySection_nonCopFieldSubmission_combinedOilAndGas_assertProbabilityInstances() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);
    var reservoirProperty = new ReservoirProperty();

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons))
        .thenReturn(true);

    // create mock for expected probability distributions for hydrocarbon type
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_TO_GROSS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_POROSITY_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_FIELD);

    var probabilityTableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityTableView);

    // build expected card for probability distributions
    var summaryCard = SummaryCard.probabilitySummaryCard(probabilityTableView);
    var summaryItem = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard);

    // check properties of cards returned
    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    assertThat(summarySection.get().summaryItems().get(1)).usingRecursiveComparison().isEqualTo(summaryItem);
  }

  @Test
  void getSummarySection_copFieldSubmission_combinedOilAndGas_assertProbabilityInstances() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);
    var reservoirProperty = new ReservoirProperty();

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons))
        .thenReturn(true);

    // create mock for expected probability distributions for hydrocarbon type
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_TO_GROSS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_POROSITY_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_OIL_WATER_CONTACT_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_OIL_WATER_CONTACT_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_WATER_CONTACT_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_WATER_CONTACT_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_OIL_CONTACT_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_OIL_CONTACT_FIELD);

    var probabilityTableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityTableView);

    // build expected card for probability distributions
    var summaryCard = SummaryCard.probabilitySummaryCard(probabilityTableView);
    var summaryItem = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard);

    // check properties of cards returned
    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    assertThat(summarySection.get().summaryItems().get(1)).usingRecursiveComparison().isEqualTo(summaryItem);
  }

  @Test
  void getSummarySection_nonCopFieldSubmission_oilOnly_assertProbabilityInstances() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);
    var reservoirProperty = new ReservoirProperty();

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isOilOnly(reservoirHydrocarbons))
        .thenReturn(true);

    // create mock for expected probability distributions for hydrocarbon type
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_TO_GROSS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_POROSITY_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_FIELD);

    var probabilityTableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityTableView);

    // build expected card for probability distributions
    var summaryCard = SummaryCard.probabilitySummaryCard(probabilityTableView);
    var summaryItem = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard);

    // check properties of cards returned
    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    assertThat(summarySection.get().summaryItems().get(1)).usingRecursiveComparison().isEqualTo(summaryItem);
  }

  @Test
  void getSummarySection_copFieldSubmission_oilOnly_assertProbabilityInstances() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);
    reservoirSubmissionDetail.setSubmissionDetail(submissionDetail);
    var reservoirProperty = new ReservoirProperty();

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isOilOnly(reservoirHydrocarbons))
        .thenReturn(true);

    // create mock for expected probability distributions for hydrocarbon type
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_TO_GROSS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_POROSITY_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_OIL_WATER_CONTACT_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_OIL_WATER_CONTACT_FIELD);

    var probabilityTableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityTableView);

    // build expected card for probability distributions
    var summaryCard = SummaryCard.probabilitySummaryCard(probabilityTableView);
    var summaryItem = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard);

    // check properties of cards returned
    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    assertThat(summarySection.get().summaryItems().get(1)).usingRecursiveComparison().isEqualTo(summaryItem);
  }

  @Test
  void getSummarySection_nonCopFieldSubmission_gasOnly_assertProbabilityInstances() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);
    var reservoirProperty = new ReservoirProperty();

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isGasOnly(reservoirHydrocarbons))
        .thenReturn(true);

    // create mock for expected probability distributions for hydrocarbon type
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_TO_GROSS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_POROSITY_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_FIELD);

    var probabilityTableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityTableView);

    // build expected card for probability distributions
    var summaryCard = SummaryCard.probabilitySummaryCard(probabilityTableView);
    var summaryItem = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard);

    // check properties of cards returned
    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    assertThat(summarySection.get().summaryItems().get(1)).usingRecursiveComparison().isEqualTo(summaryItem);
  }

  @Test
  void getSummarySection_copFieldSubmission_gasOnly_assertProbabilityInstances() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);
    var reservoirProperty = new ReservoirProperty();

    when(reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isGasOnly(reservoirHydrocarbons))
        .thenReturn(true);

    // create mock for expected probability distributions for hydrocarbon type
    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_TO_GROSS_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.NET_POROSITY_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_WATER_CONTACT_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_WATER_CONTACT_FIELD);

    var probabilityTableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(probabilityTableView);

    // build expected card for probability distributions
    var summaryCard = SummaryCard.probabilitySummaryCard(probabilityTableView);
    var summaryItem = SummaryItem.withCard(RESERVOIR_PROPERTIES, summaryCard);

    // check properties of cards returned
    var summarySection = reservoirPropertiesSummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), RESERVOIR_PROPERTIES_DISPLAY_ORDER);

    assertThat(summarySection.get().summaryItems().get(1)).usingRecursiveComparison().isEqualTo(summaryItem);
  }
}