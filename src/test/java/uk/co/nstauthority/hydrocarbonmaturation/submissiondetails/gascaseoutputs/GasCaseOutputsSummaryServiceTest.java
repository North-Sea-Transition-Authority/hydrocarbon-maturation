package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

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
class GasCaseOutputSummaryServiceTest {
  private static final User USER = UserTestUtil.Builder().build();
  @Mock
  private GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private GasCaseOutputsSummaryService gasCaseOutputsSummaryService;

  static final List<ProbabilityDistributionFieldInstance> PROSPECT_INSTANCES_INDUSTRY_STANDARD = List.of(
      ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT,
      ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_PROSPECT
  );

  static final List<ProbabilityDistributionFieldInstance> DISCOVERY_INSTANCES = List.of(
      ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
      ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
      ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
      ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
  );


  private final SubmissionDetail prospectSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail prospectReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
      prospectSubmissionDetail);
  private final SubmissionDetail discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  private final ReservoirSubmissionDetail discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
      discoverySubmissionDetail
  );
  private final GasCaseOutput gasCaseOutput = new GasCaseOutput();

  @BeforeEach
  void setUp() {
    gasCaseOutput.setReservoirSubmissionDetail(prospectReservoirSubmissionDetail);
  }

  @Test
  void getGasCaseOutputsSummaryFromReservoirSubmissionDetail_whenPresent_whenProspect_fullSummary() {
    var probabilityTableView = ProbabilityDistributionTableView.empty();

    when(gasCaseOutputsPersistenceService.getGasCaseOutput(prospectReservoirSubmissionDetail))
        .thenReturn(Optional.of(gasCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            PROSPECT_INSTANCES_INDUSTRY_STANDARD,
            prospectSubmissionDetail,
            prospectReservoirSubmissionDetail))
        .thenReturn(probabilityTableView);

    gasCaseOutput.setHasAssociatedCondensateRecoverableResources(true);
    gasCaseOutput.setUseIndustryStandardConversion(true);

    var view = gasCaseOutputsSummaryService.getGasCaseOutputsSummaryFromReservoirSubmissionDetail(
        prospectReservoirSubmissionDetail);
    assertThat(view)
        .extracting(
            GasCaseOutputsSummaryView::hasAssociatedCondensateRecoverableResources,
            GasCaseOutputsSummaryView::useIndustryStandardConversion,
            GasCaseOutputsSummaryView::tableView)
        .containsExactly(
            "Yes",
            "Yes",
            probabilityTableView
        );
  }

  @Test
  void getGasCaseOutputsSummaryFromReservoirSubmissionDetail_whenPresent_whenDiscovery_fullSummary() {
    gasCaseOutput.setReservoirSubmissionDetail(discoveryReservoirSubmissionDetail);
    gasCaseOutput.setHasAssociatedCondensateRecoverableResources(true);

    var probabilityTableView = ProbabilityDistributionTableView.empty();

    when(gasCaseOutputsPersistenceService.getGasCaseOutput(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(gasCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            DISCOVERY_INSTANCES,
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(probabilityTableView);

    gasCaseOutput.setHasAssociatedCondensateRecoverableResources(true);

    var view = gasCaseOutputsSummaryService.getGasCaseOutputsSummaryFromReservoirSubmissionDetail(
        discoveryReservoirSubmissionDetail);
    assertThat(view)
        .extracting(
            GasCaseOutputsSummaryView::hasAssociatedCondensateRecoverableResources,
            GasCaseOutputsSummaryView::tableView)
        .containsExactly(
            "Yes",
            probabilityTableView
        );
  }

  @Test
  void getGasCaseOutputsSummaryFromReservoirSubmissionDetail_whenNotPresent_notEmptySummary() {
    var summaryBehaviour = new SummaryBehaviour();

    var instances = List.of(
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT,
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT,
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
    );

    when(gasCaseOutputsPersistenceService.getGasCaseOutput(prospectReservoirSubmissionDetail))
        .thenReturn(Optional.empty());
    when(probabilityDistributionSummaryService.getSummaryBehaviour(instances))
        .thenReturn(summaryBehaviour);

    var view = gasCaseOutputsSummaryService.getGasCaseOutputsSummaryFromReservoirSubmissionDetail(
        prospectReservoirSubmissionDetail);
    assertThat(view)
        .extracting(
            GasCaseOutputsSummaryView::hasAssociatedCondensateRecoverableResources,
            GasCaseOutputsSummaryView::useIndustryStandardConversion,
            GasCaseOutputsSummaryView::tableView)
        .containsExactly(
            "",
            "",
            new ProbabilityDistributionTableView(
                summaryBehaviour,
                List.of(ProbabilityDistributionSummaryView.empty("Gas initially in place"),
                    ProbabilityDistributionSummaryView.empty("Gas recoverable resources"),
                    ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources")
                )
            )
        );
  }

  @Test
  void getSummarySection_nonDiscoverySubmission() {
    prospectReservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.PROSPECT_UPDATE);

    assertThat(gasCaseOutputsSummaryService.getSummarySection(prospectReservoirSubmissionDetail, USER))
        .isEmpty();
  }

  @Test
  void getSummarySection_nonGasOnlySubmission() {
    prospectReservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    when(generalReservoirInformationService.isGasOnly(prospectReservoirSubmissionDetail))
        .thenReturn(false);

    assertThat(gasCaseOutputsSummaryService.getSummarySection(prospectReservoirSubmissionDetail, USER))
        .isEmpty();
  }

  @Test
  @SuppressWarnings("unchecked")
  void getSummarySection_discoverySubmissionAndGasOnly_hasAssociatedCondensate_assertProperties() {
    when(generalReservoirInformationService.isGasOnly(discoveryReservoirSubmissionDetail))
        .thenReturn(true);

    gasCaseOutput.setHasAssociatedCondensateRecoverableResources(true);
    when(gasCaseOutputsPersistenceService.getGasCaseOutput(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(gasCaseOutput));

    var captor = ArgumentCaptor.forClass(List.class);
    assertThat(gasCaseOutputsSummaryService.getSummarySection(discoveryReservoirSubmissionDetail, USER))
        .isPresent()
        .get()
        .extracting(
            SummarySection::displayOrder,
            SummarySection::summaryText
        ).contains(
            GasCaseOutputsSummaryService.GAS_VOLUMETRICS_DISPLAY_ORDER,
            ""
        );

    verify(probabilityDistributionSummaryService).getProbabilityDistributionTableViewFromInstancesAndSubmission(
        captor.capture(), eq(discoverySubmissionDetail), eq(discoveryReservoirSubmissionDetail)
    );
    verifyNoMoreInteractions(probabilityDistributionSummaryService);

    assertThat(captor.getValue())
        .usingRecursiveComparison()
        .isEqualTo(DISCOVERY_INSTANCES);
  }

  @Test
  @SuppressWarnings("unchecked")
  void getSummarySection_discoverySubmissionAndGasOnly_hasNoAssociatedCondensate_assertProperties() {
    when(generalReservoirInformationService.isGasOnly(discoveryReservoirSubmissionDetail))
        .thenReturn(true);

    gasCaseOutput.setHasAssociatedCondensateRecoverableResources(false);
    when(gasCaseOutputsPersistenceService.getGasCaseOutput(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(gasCaseOutput));

    var captor = ArgumentCaptor.forClass(List.class);
    assertThat(gasCaseOutputsSummaryService.getSummarySection(discoveryReservoirSubmissionDetail, USER))
        .isPresent()
        .get()
        .extracting(
            SummarySection::displayOrder,
            SummarySection::summaryText
        ).contains(
            GasCaseOutputsSummaryService.GAS_VOLUMETRICS_DISPLAY_ORDER,
            ""
        );

    verify(probabilityDistributionSummaryService).getProbabilityDistributionTableViewFromInstancesAndSubmission(
        captor.capture(), eq(discoverySubmissionDetail), eq(discoveryReservoirSubmissionDetail)
    );
    verifyNoMoreInteractions(probabilityDistributionSummaryService);

    var expectedProbabilityInstances = List.of(
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    );

    assertThat(captor.getValue())
        .usingRecursiveComparison()
        .isEqualTo(expectedProbabilityInstances);
  }

  @Test
  @SuppressWarnings("unchecked")
  void getSummarySection_discoverySubmissionAndGasOnly_useIndustryStandardConversion_assertProperties() {
    when(generalReservoirInformationService.isGasOnly(discoveryReservoirSubmissionDetail))
        .thenReturn(true);

    gasCaseOutput.setHasAssociatedCondensateRecoverableResources(false);
    gasCaseOutput.setUseIndustryStandardConversion(true);
    when(gasCaseOutputsPersistenceService.getGasCaseOutput(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.of(gasCaseOutput));

    var captor = ArgumentCaptor.forClass(List.class);
    assertThat(gasCaseOutputsSummaryService.getSummarySection(discoveryReservoirSubmissionDetail, USER))
        .isPresent()
        .get()
        .extracting(
            SummarySection::displayOrder,
            SummarySection::summaryText
        ).contains(
            GasCaseOutputsSummaryService.GAS_VOLUMETRICS_DISPLAY_ORDER,
            ""
        );

    verify(probabilityDistributionSummaryService).getProbabilityDistributionTableViewFromInstancesAndSubmission(
        captor.capture(), eq(discoverySubmissionDetail), eq(discoveryReservoirSubmissionDetail)
    );
    verifyNoMoreInteractions(probabilityDistributionSummaryService);

    var expectedProbabilityInstances = List.of(
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_DISCOVERY
    );

    assertThat(captor.getValue())
        .usingRecursiveComparison()
        .isEqualTo(expectedProbabilityInstances);
  }

  @Test
  @SuppressWarnings("unchecked")
  void getSummarySection_discoverySubmissionAndGasOnly_hasNotAnsweredAssociatedCondensate_assertProperties() {
    when(generalReservoirInformationService.isGasOnly(discoveryReservoirSubmissionDetail))
        .thenReturn(true);

    when(gasCaseOutputsPersistenceService.getGasCaseOutput(discoveryReservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var captor = ArgumentCaptor.forClass(List.class);
    assertThat(gasCaseOutputsSummaryService.getSummarySection(discoveryReservoirSubmissionDetail, USER))
        .isPresent()
        .get()
        .extracting(
            SummarySection::displayOrder,
            SummarySection::summaryText
        ).contains(
            GasCaseOutputsSummaryService.GAS_VOLUMETRICS_DISPLAY_ORDER,
            ""
        );

    verify(probabilityDistributionSummaryService).getProbabilityDistributionTableViewFromInstancesAndSubmission(
        captor.capture(), eq(discoverySubmissionDetail), eq(discoveryReservoirSubmissionDetail)
    );
    verifyNoMoreInteractions(probabilityDistributionSummaryService);

    var expectedProbabilityInstances = List.of(
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
        ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
        ProbabilityDistributionFieldInstance.OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY
    );

    assertThat(captor.getValue())
        .usingRecursiveComparison()
        .isEqualTo(expectedProbabilityInstances);
  }
}