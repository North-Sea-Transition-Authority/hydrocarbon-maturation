package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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

@ExtendWith(MockitoExtension.class)
class ReservoirPropertiesSummaryServiceTest {

  @Mock
  private ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

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
    whenGetProbabilityDistributionSummaryCalled(submissionDetail.getSubmissionType());

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromSubmissionDetail(submissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::formationName,
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
    reservoirProperty.setFormationName("formation");
    reservoirProperty.setLithology("lithology");
    reservoirProperty.setAgeStage("ageStage");
    reservoirProperty.setReservoirFacies("facies");
    reservoirProperty.setReservoirConnectivity("connectivity");

    whenGetProbabilityDistributionSummaryCalled(submissionDetail.getSubmissionType());
    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromSubmissionDetail(
        submissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::submissionType,
            ReservoirPropertiesSummaryView::formationName,
            ReservoirPropertiesSummaryView::ageStageOrSeries,
            ReservoirPropertiesSummaryView::lithology,
            ReservoirPropertiesSummaryView::reservoirFacies,
            ReservoirPropertiesSummaryView::reservoirConnectivity,
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews,
            ReservoirPropertiesSummaryView::listOfDepthToContactDistributions)
        .containsExactly(
            SubmissionType.CREATE_PROSPECT,
            "formation",
            "ageStage",
            "lithology",
            "facies",
            "connectivity",
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
  void getReservoirPropertiesSummaryFromSubmissionDetail_whenDryHole_reservoirProperty() {
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);

    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setFormationName("formation");
    reservoirProperty.setLithology("lithology");
    reservoirProperty.setAgeStage("ageStage");
    reservoirProperty.setReservoirFacies("facies");
    reservoirProperty.setReservoirConnectivity("connectivity");

    whenGetProbabilityDistributionSummaryCalled(SubmissionType.POST_DRILL_DRY_HOLE);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromSubmissionDetail(
        submissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::submissionType,
            ReservoirPropertiesSummaryView::formationName,
            ReservoirPropertiesSummaryView::ageStageOrSeries,
            ReservoirPropertiesSummaryView::lithology,
            ReservoirPropertiesSummaryView::reservoirFacies,
            ReservoirPropertiesSummaryView::reservoirConnectivity,
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews,
            ReservoirPropertiesSummaryView::listOfDepthToContactDistributions)
        .containsExactly(
            SubmissionType.POST_DRILL_DRY_HOLE,
            "formation",
            "ageStage",
            "lithology",
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
    reservoirProperty.setFormationName("formation");
    reservoirProperty.setLithology("lithology");
    reservoirProperty.setAgeStage("ageStage");
    reservoirProperty.setReservoirFacies("facies");
    reservoirProperty.setReservoirConnectivity("connectivity");

    whenGetProbabilityDistributionSummaryCalled(SubmissionType.CREATE_DISCOVERY);
    var expectedList = whenDepthToProbabilityDistributionSummaryCalled(hydrocarbonsDiscovered);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    var reservoirPropertiesSummaryView = reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromSubmissionDetail(
        submissionDetail);

    assertThat(reservoirPropertiesSummaryView)
        .extracting(
            ReservoirPropertiesSummaryView::submissionType,
            ReservoirPropertiesSummaryView::formationName,
            ReservoirPropertiesSummaryView::ageStageOrSeries,
            ReservoirPropertiesSummaryView::lithology,
            ReservoirPropertiesSummaryView::reservoirFacies,
            ReservoirPropertiesSummaryView::reservoirConnectivity,
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews,
            ReservoirPropertiesSummaryView::listOfDepthToContactDistributions)
        .containsExactly(
            SubmissionType.CREATE_DISCOVERY,
            "formation",
            "ageStage",
            "lithology",
            "facies",
            "connectivity",
            List.of(
                averageNetPayThickness,
                grossReservoirThickness,
                grossRockVolume,
                percentOfGrvOilBearing,
                netToGross,
                netPorosity),
            expectedList
        );
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

  private void whenGetProbabilityDistributionSummaryCalled(SubmissionType submissionType) {
    if (submissionDetail.isDryWellOrDiscoverySubmission()) {
      when(probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DRY_WELL_DISCOVERY,
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
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING,
            submissionDetail
        )).thenReturn(percentOfGrvOilBearing);
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