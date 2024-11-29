package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import static org.assertj.core.api.Assertions.assertThat;
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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.SummaryBehaviour;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

@ExtendWith(MockitoExtension.class)
class OilCaseOutputsSummaryServiceTest {
  private static final User USER = UserTestUtil.Builder().build();

  static final List<ProbabilityDistributionFieldInstance> OIL_VOLUMETRIC_INSTANCES_PROSPECTS = List.of(
      ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT,
      ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.OIL_OUTPUTS_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT
  );

  static final List<ProbabilityDistributionFieldInstance> OIL_VOLUMETRIC_INSTANCES_PROSPECTS_INDUSTRY_STANDARD = List.of(
      ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT,
      ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.OIL_OUTPUTS_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT,
      ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_PROSPECT
  );

  @Mock
  private OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private OilCaseOutputsSummaryService oilCaseOutputsSummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
      submissionDetail);
  private final OilCaseOutput oilCaseOutput = new OilCaseOutput();

  @BeforeEach
  void setUp() {
    oilCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getOilCaseOutputsSummaryFromReservoirSubmissionDetail_whenPresent_whenProspect_fullSummary() {
    var tableView = ProbabilityDistributionTableView.empty();

    when(oilCaseOutputsPersistenceService.getOilCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(oilCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            OIL_VOLUMETRIC_INSTANCES_PROSPECTS_INDUSTRY_STANDARD,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(tableView);

    oilCaseOutput.setHasAssociatedGasRecoverableResources(true);
    oilCaseOutput.setUseIndustryStandardConversion(true);

    var view = oilCaseOutputsSummaryService.getOilCaseOutputsSummaryFromReservoirSubmissionDetail(
        reservoirSubmissionDetail);
    assertThat(view)
        .extracting(
            OilCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            OilCaseOutputsSummaryView::useIndustryStandardConversion,
            OilCaseOutputsSummaryView::tableView)
        .containsExactly(
            "Yes",
            "Yes",
            tableView
        );
  }

  @Test
  void getOilCaseOutputsSummaryFromReservoirSubmissionDetail_whenPresent_notEmptySummary_whenDiscovery() {
    var tableView = ProbabilityDistributionTableView.empty();
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        discoverySubmissionDetail);
    oilCaseOutput.setReservoirSubmissionDetail(discoveryReservoirSubmissionDetail);

    var instances = List.of(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    );

    when(oilCaseOutputsPersistenceService.getOilCaseOutput(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(oilCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            instances,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(tableView);

    oilCaseOutput.setHasAssociatedGasRecoverableResources(true);

    var view = oilCaseOutputsSummaryService.getOilCaseOutputsSummaryFromReservoirSubmissionDetail(
        discoveryReservoirSubmissionDetail);
    assertThat(view)
        .extracting(
            OilCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            OilCaseOutputsSummaryView::tableView)
        .containsExactly(
            "Yes",
            tableView
        );
  }

  @Test
  void getOilCaseOutputsSummaryFromReservoirSubmissionDetail_whenPresent_notEmptySummary_whenDiscovery_whenUseIndustryStandardConversion() {
    var tableView = ProbabilityDistributionTableView.empty();
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        discoverySubmissionDetail);
    oilCaseOutput.setReservoirSubmissionDetail(discoveryReservoirSubmissionDetail);
    oilCaseOutput.setHasAssociatedGasRecoverableResources(true);
    oilCaseOutput.setUseIndustryStandardConversion(true);

    var instances = List.of(
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY
        );

    when(oilCaseOutputsPersistenceService.getOilCaseOutput(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(oilCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            instances,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(tableView);

    var view = oilCaseOutputsSummaryService.getOilCaseOutputsSummaryFromReservoirSubmissionDetail(
        discoveryReservoirSubmissionDetail);
    assertThat(view)
        .extracting(
            OilCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            OilCaseOutputsSummaryView::useIndustryStandardConversion,
            OilCaseOutputsSummaryView::tableView)
        .containsExactly(
            "Yes",
            "Yes",
            tableView
        );
  }

  @Test
  void getOilCaseOutputsSummaryFromReservoirSubmissionDetail_whenNotPresent_notEmptySummary() {
    var summaryBehaviour = new SummaryBehaviour();
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.CREATE_PROSPECT);

    when(oilCaseOutputsPersistenceService.getOilCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());
    when(probabilityDistributionSummaryService
        .getSummaryBehaviour(OIL_VOLUMETRIC_INSTANCES_PROSPECTS))
        .thenReturn(summaryBehaviour);

    var view = oilCaseOutputsSummaryService.getOilCaseOutputsSummaryFromReservoirSubmissionDetail(
        reservoirSubmissionDetail);
    assertThat(view)
        .extracting(
            OilCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            OilCaseOutputsSummaryView::useIndustryStandardConversion,
            OilCaseOutputsSummaryView::tableView)
        .containsExactly(
            "",
            "",
            new ProbabilityDistributionTableView(
                summaryBehaviour,
                List.of(
                    ProbabilityDistributionSummaryView.empty("Oil initially in place"),
                    ProbabilityDistributionSummaryView.empty("Oil recoverable resources"),
                    ProbabilityDistributionSummaryView.empty("Associated gas recoverable resources"),
                    ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources"),
                    ProbabilityDistributionSummaryView.empty("Risked oil equivalent recoverable")
                )
            )
        );
  }

  @Test
  void getSummarySection_nonDiscoverySubmission() {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.PROSPECT_UPDATE);

    assertThat(oilCaseOutputsSummaryService.getSummarySection(reservoirSubmissionDetail, USER))
        .isEmpty();
  }

  @Test
  void getSummarySection_nonOilOnlySubmission() {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(false);

    assertThat(oilCaseOutputsSummaryService.getSummarySection(reservoirSubmissionDetail, USER))
        .isEmpty();
  }

  @Test
  @SuppressWarnings("unchecked")
  void getSummarySection_discoverySubmissionAndOilOnly_assertProperties() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        discoverySubmissionDetail);

    when(generalReservoirInformationService.isOilOnly(discoveryReservoirSubmissionDetail))
        .thenReturn(true);

    var captor = ArgumentCaptor.forClass(List.class);
    assertThat(oilCaseOutputsSummaryService.getSummarySection(discoveryReservoirSubmissionDetail, USER))
        .isPresent()
        .get()
        .extracting(
            SummarySection::displayOrder,
            SummarySection::summaryText
        ).contains(
            OilCaseOutputsSummaryService.OIL_VOLUMETRICS_DISPLAY_ORDER,
            ""
        );

    verify(probabilityDistributionSummaryService).getProbabilityDistributionTableViewFromInstancesAndSubmission(
        captor.capture(), eq(discoverySubmissionDetail), eq(discoveryReservoirSubmissionDetail)
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