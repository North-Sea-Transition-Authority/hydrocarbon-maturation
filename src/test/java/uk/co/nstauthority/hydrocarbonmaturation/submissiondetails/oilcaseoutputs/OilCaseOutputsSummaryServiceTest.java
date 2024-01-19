package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

@ExtendWith(MockitoExtension.class)
class OilCaseOutputsSummaryServiceTest {
  @Mock
  private OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private OilCaseOutputsSummaryService oilCaseOutputsSummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
  private final OilCaseOutput oilCaseOutput = new OilCaseOutput();

  @BeforeEach
  void setUp() {
    oilCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getOilCaseOutputsSummaryFromReservoirSubmissionDetail_whenPresent_fullSummary() {
    var emptySummary = ProbabilityDistributionSummaryView.empty("empty");

    when(oilCaseOutputsPersistenceService.getOilCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(oilCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(any(), eq(submissionDetail), eq(reservoirSubmissionDetail)))
        .thenReturn(emptySummary);

    oilCaseOutput.setHasAssociatedGasRecoverableResources(true);

    var view = oilCaseOutputsSummaryService.getOilCaseOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
    assertThat(view)
        .extracting(
            OilCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            OilCaseOutputsSummaryView::probabilityDistributionSummaryViews)
        .containsExactly(
            "Yes",
            List.of(
                emptySummary,
                emptySummary,
                emptySummary,
                emptySummary,
                emptySummary)
        );
  }

  @Test
  void getOilCaseOutputsSummaryFromReservoirSubmissionDetail_whenPresent_notEmptySummary_whenProspect() {
    var testSummary = new ProbabilityDistributionSummaryView(
        "test", "type", "20",
        "20", "20", "20", "20", "20", "20", "20", "20", "20",
        "20", "", "20", "unit", "5", "5", "5", "comments");

    when(oilCaseOutputsPersistenceService.getOilCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(oilCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(testSummary);

    oilCaseOutput.setHasAssociatedGasRecoverableResources(true);

    var view = oilCaseOutputsSummaryService.getOilCaseOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
    assertThat(view)
        .extracting(
            OilCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            OilCaseOutputsSummaryView::probabilityDistributionSummaryViews)
        .containsExactly(
            "Yes",
            List.of(
                testSummary,
                testSummary,
                testSummary,
                testSummary,
                testSummary)
        );
  }

  @Test
  void getOilCaseOutputsSummaryFromReservoirSubmissionDetail_whenPresent_notEmptySummary_whenDiscovery() {
    var testSummary = new ProbabilityDistributionSummaryView(
        "test", "type", "20",
        "20", "20", "20", "20", "20", "20", "20", "20", "20",
        "20", "", "20", "unit", "5", "5", "5", "comments");
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);
    oilCaseOutput.setReservoirSubmissionDetail(discoveryReservoirSubmissionDetail);

    when(oilCaseOutputsPersistenceService.getOilCaseOutput(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(oilCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(testSummary);
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(testSummary);

    oilCaseOutput.setHasAssociatedGasRecoverableResources(true);

    var view = oilCaseOutputsSummaryService.getOilCaseOutputsSummaryFromReservoirSubmissionDetail(discoveryReservoirSubmissionDetail);
    assertThat(view)
        .extracting(
            OilCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            OilCaseOutputsSummaryView::probabilityDistributionSummaryViews)
        .containsExactly(
            "Yes",
            List.of(
                testSummary,
                testSummary,
                testSummary,
                testSummary
            )
        );
  }

  @Test
  void getOilCaseOutputsSummaryFromReservoirSubmissionDetail_whenNotPresent_notEmptySummary() {
    when(oilCaseOutputsPersistenceService.getOilCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var view = oilCaseOutputsSummaryService.getOilCaseOutputsSummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);
    assertThat(view)
        .extracting(
            OilCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            OilCaseOutputsSummaryView::probabilityDistributionSummaryViews)
        .containsExactly(
            "",
            List.of(ProbabilityDistributionSummaryView.empty("Oil in place"),
                ProbabilityDistributionSummaryView.empty("Oil recoverable resources"),
                ProbabilityDistributionSummaryView.empty("Associated gas recoverable resources"),
                ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources"),
                ProbabilityDistributionSummaryView.empty("Risked oil equivalent recoverable"))
        );
  }

  @Test
  void getSummarySection_nonDiscoverySubmission() {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.PROSPECT_UPDATE);

    assertThat(oilCaseOutputsSummaryService.getSummarySection(reservoirSubmissionDetail))
        .isEmpty();
  }

  @Test
  void getSummarySection_nonOilOnlySubmission() {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(false);

    assertThat(oilCaseOutputsSummaryService.getSummarySection(reservoirSubmissionDetail))
        .isEmpty();
  }

  @Test
  @SuppressWarnings("unchecked")
  void getSummarySection_discoverySubmissionAndOilOnly_assertProperties() {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(true);

    var captor = ArgumentCaptor.forClass(List.class);
    assertThat(oilCaseOutputsSummaryService.getSummarySection(reservoirSubmissionDetail))
        .isPresent()
        .get()
        .extracting(
            SummarySection::displayOrder,
            SummarySection::summaryText
        ).contains(
            OilCaseOutputsSummaryService.OIL_VOLUMETRICS_DISPLAY_ORDER,
            ""
        );

    verify(probabilityDistributionSummaryService).getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
        captor.capture(), eq(submissionDetail), eq(reservoirSubmissionDetail)
    );
    verifyNoMoreInteractions(probabilityDistributionSummaryService);

    var expectedProbabilityInstances = List.of(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    );

    assertThat(captor.getValue())
        .usingRecursiveComparison()
        .isEqualTo(expectedProbabilityInstances);
  }
}