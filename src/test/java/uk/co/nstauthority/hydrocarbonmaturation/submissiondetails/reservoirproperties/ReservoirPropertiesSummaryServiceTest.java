package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@ExtendWith(MockitoExtension.class)
class ReservoirPropertiesSummaryServiceTest {

  @Mock
  private ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @InjectMocks
  private ReservoirPropertiesSummaryService reservoirPropertiesSummaryService;

  private SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

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

  @Test
  void getReservoirPropertiesSummaryFromSubmissionDetail_whenProspect_noReservoirProperty() {
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
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews)
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
                depthToHydrocarbonContact)
        );
  }

  @Test
  void getReservoirPropertiesSummaryFromSubmissionDetail_whenProspect_reservoirProperty() {
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
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews)
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
                depthToHydrocarbonContact)
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
            ReservoirPropertiesSummaryView::probabilityDistributionSummaryViews)
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
                netPorosity)
        );
  }

  private void whenGetProbabilityDistributionSummaryCalled(SubmissionType submissionType) {
    if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
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
    if (submissionType == SubmissionType.CREATE_PROSPECT || submissionType == SubmissionType.PROSPECT_UPDATE) {
      when(probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT,
              submissionDetail
          )).thenReturn(depthToHydrocarbonContact);
    }
  }
}