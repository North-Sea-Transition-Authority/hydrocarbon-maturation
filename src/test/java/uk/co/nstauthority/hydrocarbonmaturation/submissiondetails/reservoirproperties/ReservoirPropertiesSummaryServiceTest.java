package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;

@ExtendWith(MockitoExtension.class)
class ReservoirPropertiesSummaryServiceTest {

  @Mock
  private ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  @Mock
  private HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @InjectMocks
  private ReservoirPropertiesSummaryService reservoirPropertiesSummaryService;

  private static SubmissionDetail submissionDetail;

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
  }

  @Test
  void getReservoirPropertiesSummaryFromSubmissionDetail_whenProspect_noReservoirProperty() {
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail))
        .thenReturn(Optional.empty());
    whenGetProbabilityDistributionSummaryCalled(false);

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromSubmissionDetail(submissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::ageStageOrSeries,
            ReservoirPropertiesSummaryView::lithology,
            ReservoirPropertiesSummaryView::reservoirFacies,
            ReservoirPropertiesSummaryView::reservoirConnectivity,
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews,
            ReservoirPropertiesSummaryView::listOfDepthToContactDistributions)
        .containsExactly(
            "",
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
  void getReservoirPropertiesSummaryFromSubmissionDetail_whenProspect_whenMixedCase() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setLithology(ReservoirLithology.CARBONATE);
    reservoirProperty.setAgeStage(AgeSeries.LOWER_CARBONIFEROUS);
    reservoirProperty.setGroupName(GroupName.CLAIR);

    whenGetProbabilityDistributionSummaryCalled(true);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail))
        .thenReturn(Optional.of(reservoirProperty));
    var hydrocarbonCaseDetail = new HydrocarbonCaseDetail();
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail))
        .thenReturn(Optional.of(hydrocarbonCaseDetail));
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCaseDetail))
        .thenReturn(true);

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromSubmissionDetail(
        submissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::submissionType,
            ReservoirPropertiesSummaryView::ageStageOrSeries,
            ReservoirPropertiesSummaryView::groupName,
            ReservoirPropertiesSummaryView::lithology,
            ReservoirPropertiesSummaryView::reservoirFacies,
            ReservoirPropertiesSummaryView::reservoirConnectivity,
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews,
            ReservoirPropertiesSummaryView::listOfDepthToContactDistributions)
        .containsExactly(
            SubmissionType.CREATE_PROSPECT,
            AgeSeries.LOWER_CARBONIFEROUS.getDisplayName(),
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
  void getReservoirPropertiesSummaryFromSubmissionDetail_whenProspect_reservoirProperty() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setLithology(ReservoirLithology.CHALK);
    reservoirProperty.setAgeStage(AgeSeries.LOWER_CRETACEOUS);
    reservoirProperty.setGroupName(GroupName.BRUCE);
    reservoirProperty.setReservoirFacies("facies");
    reservoirProperty.setReservoirConnectivity("connectivity");

    whenGetProbabilityDistributionSummaryCalled(false);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromSubmissionDetail(
        submissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::submissionType,
            ReservoirPropertiesSummaryView::ageStageOrSeries,
            ReservoirPropertiesSummaryView::groupName,
            ReservoirPropertiesSummaryView::lithology,
            ReservoirPropertiesSummaryView::reservoirFacies,
            ReservoirPropertiesSummaryView::reservoirConnectivity,
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews,
            ReservoirPropertiesSummaryView::listOfDepthToContactDistributions)
        .containsExactly(
            SubmissionType.CREATE_PROSPECT,
            AgeSeries.LOWER_CRETACEOUS.getDisplayName(),
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
  void getReservoirPropertiesSummaryFromSubmissionDetail_withOtherLithology() {
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setLithology(ReservoirLithology.OTHER);
    var otherLithology = "Other lithology";
    reservoirProperty.setLithologyOther(otherLithology);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromSubmissionDetail(
        submissionDetail);

    assertThat(reservoirPropertiesSummaryView.lithology()).isEqualTo(otherLithology);
  }

  @Test
  void getReservoirPropertiesSummaryFromSubmissionDetail_whenDryHole_reservoirProperty() {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);

    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setAgeStage(AgeSeries.LOWER_DEVONIAN);
    reservoirProperty.setGroupName(GroupName.COAL_MEASURES);
    reservoirProperty.setLithology(ReservoirLithology.CARBONATE);
    reservoirProperty.setReservoirFacies("facies");
    reservoirProperty.setReservoirConnectivity("connectivity");

    whenGetProbabilityDistributionSummaryCalled(true);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromSubmissionDetail(
        submissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::submissionType,
            ReservoirPropertiesSummaryView::ageStageOrSeries,
            ReservoirPropertiesSummaryView::groupName,
            ReservoirPropertiesSummaryView::lithology,
            ReservoirPropertiesSummaryView::reservoirFacies,
            ReservoirPropertiesSummaryView::reservoirConnectivity,
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews,
            ReservoirPropertiesSummaryView::listOfDepthToContactDistributions)
        .containsExactly(
            SubmissionType.POST_DRILL_DRY_HOLE,
            AgeSeries.LOWER_DEVONIAN.getDisplayName(),
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
  @EnumSource(HydrocarbonsDiscovered.class)
  void getReservoirPropertiesSummaryFromSubmissionDetail_whenDiscovery_reservoirProperty(
      HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setAgeStage(AgeSeries.LOWER_JURASSIC);
    reservoirProperty.setGroupName(GroupName.DUNLIN);
    reservoirProperty.setLithology(ReservoirLithology.CHALK);
    reservoirProperty.setReservoirFacies("facies");
    reservoirProperty.setReservoirConnectivity("connectivity");
    List<ProbabilityDistributionSummaryView> expectedList;
    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      var discoveryWellDetail = new DiscoveryWellDetail();
      when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.of(discoveryWellDetail));
      whenGetProbabilityDistributionSummaryCalled(true);
      expectedList = List.of(
          averageNetPayThickness,
          grossReservoirThickness,
          grossRockVolume,
          percentOfGrvOilBearing,
          netToGross,
          netPorosity);
    } else {
      whenGetProbabilityDistributionSummaryCalled(false);
      expectedList = List.of(
          averageNetPayThickness,
          grossReservoirThickness,
          grossRockVolume,
          netToGross,
          netPorosity);
    }
    var depthToExpectedList = whenDepthToProbabilityDistributionSummaryCalled(hydrocarbonsDiscovered);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromSubmissionDetail(
        submissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::submissionType,
            ReservoirPropertiesSummaryView::ageStageOrSeries,
            ReservoirPropertiesSummaryView::groupName,
            ReservoirPropertiesSummaryView::lithology,
            ReservoirPropertiesSummaryView::reservoirFacies,
            ReservoirPropertiesSummaryView::reservoirConnectivity,
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews,
            ReservoirPropertiesSummaryView::listOfDepthToContactDistributions)
        .containsExactly(
            SubmissionType.CREATE_DISCOVERY,
            AgeSeries.LOWER_JURASSIC.getDisplayName(),
            GroupName.DUNLIN.getDisplayName(),
            ReservoirLithology.CHALK.getDisplayName(),
            "facies",
            "connectivity",
            expectedList,
            depthToExpectedList
        );
  }

  @Test
  void getReservoirPropertySummaryFromSubmissionDetail_formationEnumView() {
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setFormationName(Formation.AUK);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var summaryView = reservoirPropertiesSummaryService
        .getReservoirPropertiesSummaryFromSubmissionDetail(submissionDetail);

    assertThat(summaryView.formationName()).isEqualTo(Formation.AUK.getDisplayName());
  }

  @Test
  void getReservoirPropertySummaryFromSubmissionDetail_manualFormationView() {
    var formation = "manual formation";
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setFormationNameManual(formation);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var summaryView = reservoirPropertiesSummaryService
        .getReservoirPropertiesSummaryFromSubmissionDetail(submissionDetail);

    assertThat(summaryView.formationName()).isEqualTo(formation);
  }

  @Test
  void getReservoirPropertySummaryFromSubmissionDetail_subUnitEnumView() {
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setSubUnit(ReservoirSubUnit.T10);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var summaryView = reservoirPropertiesSummaryService
        .getReservoirPropertiesSummaryFromSubmissionDetail(submissionDetail);

    assertThat(summaryView.subUnit()).isEqualTo(ReservoirSubUnit.T10.getDisplayName());
  }

  @Test
  void getReservoirPropertySummaryFromSubmissionDetail_manualSubUnitView() {
    var subUnit = "manual sub unit";
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setSubUnitManual(subUnit);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var summaryView = reservoirPropertiesSummaryService
        .getReservoirPropertiesSummaryFromSubmissionDetail(submissionDetail);

    assertThat(summaryView.subUnit()).isEqualTo(subUnit);
  }

  private List<ProbabilityDistributionSummaryView> whenDepthToProbabilityDistributionSummaryCalled(HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    var discoveryWellDetails = new DiscoveryWellDetail();
    discoveryWellDetails.setHydrocarbonsDiscovered(hydrocarbonsDiscovered);
    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(discoveryWellDetails));

    if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.GAS_ONLY) {
      when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT, submissionDetail))
          .thenReturn(depthToGasWaterContact);
      return List.of(depthToGasWaterContact);

    } else if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY) {
      when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
          ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT, submissionDetail))
          .thenReturn(depthToOilWaterContact);
      return List.of(depthToOilWaterContact);
    } else {
      when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
          ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT, submissionDetail))
          .thenReturn(depthToOilWaterContact);
      when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT, submissionDetail))
          .thenReturn(depthToGasWaterContact);
      when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
          ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT, submissionDetail))
          .thenReturn(depthToGasOilContact);
      return List.of(depthToOilWaterContact, depthToGasWaterContact, depthToGasOilContact);
    }
  }

  private void whenGetProbabilityDistributionSummaryCalled(boolean addPercentOfGrvOilBearing) {
    if (submissionDetail.isDryWellSubmission()) {
      when(probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL,
              submissionDetail
          )).thenReturn(averageNetPayThickness);
    } else if (submissionDetail.isDiscoverySubmission()) {
        when(probabilityDistributionSummaryService
            .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
                ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY,
                submissionDetail
            )).thenReturn(averageNetPayThickness);
    } else {
      when(probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT,
              submissionDetail
          )).thenReturn(averageNetPayThickness);
    }

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY,
            submissionDetail
        )).thenReturn(grossReservoirThickness);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME,
            submissionDetail
        )).thenReturn(grossRockVolume);
    if (addPercentOfGrvOilBearing) {
      when(probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING,
              submissionDetail
          )).thenReturn(percentOfGrvOilBearing);
    }
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT_DISCOVERY,
            submissionDetail
        )).thenReturn(netToGross);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT_DISCOVERY,
            submissionDetail
        )).thenReturn(netPorosity);
    if (submissionDetail.isProspectSubmission()) {
      when(probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT,
              submissionDetail
          )).thenReturn(depthToHydrocarbonContact);
    }
  }
}