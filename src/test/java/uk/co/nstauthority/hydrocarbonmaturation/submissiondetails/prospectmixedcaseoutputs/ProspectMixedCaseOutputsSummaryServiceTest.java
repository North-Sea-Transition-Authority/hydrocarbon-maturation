package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.prospectmixedcaseoutputs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.SummaryBehaviour;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class ProspectMixedCaseOutputsSummaryServiceTest {

  @Mock
  private ProspectMixedCaseOutputsPersistenceService prospectMixedCaseOutputsPersistenceService;

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @InjectMocks
  private ProspectMixedCaseOutputsSummaryService prospectMixedCaseOutputsSummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
      submissionDetail);
  private final MixedCaseOutput mixedCaseOutput = new MixedCaseOutput();
  private static final List<ProbabilityDistributionFieldInstance> INSTANCES = List.of(
      ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT
  );

  private static final List<ProbabilityDistributionFieldInstance> INSTANCES_INDUSTRY_STANDARD = List.of(
      ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INDUSTRY_STANDARD_PROSPECT,
      ProbabilityDistributionFieldInstance.MIXED_CASE_RISKED_OIL_EQUIVALENT_RECOVERABLE_PROSPECT
  );

  @BeforeEach
  void setUp() {
    mixedCaseOutput.setReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getMixedCaseOutputsSummaryFromReservoirSubmissionDetail_whenPresent_whenProspect() {
    var tableView = ProbabilityDistributionTableView.empty();

    when(prospectMixedCaseOutputsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.of(mixedCaseOutput));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            INSTANCES_INDUSTRY_STANDARD,
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(tableView);

    mixedCaseOutput.setHasAssociatedGasRecoverableResources(true);
    mixedCaseOutput.setHasAssociatedCondensateRecoverableResources(true);
    mixedCaseOutput.setUseIndustryStandardConversion(true);

    var view = prospectMixedCaseOutputsSummaryService.getMixedCaseOutputsSummaryFromReservoirSubmissionDetail(
        reservoirSubmissionDetail);
    assertThat(view)
        .extracting(
            MixedCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            MixedCaseOutputsSummaryView::hasAssociatedCondensateRecoverableResources,
            MixedCaseOutputsSummaryView::useIndustryStandardConversion,
            MixedCaseOutputsSummaryView::tableView)
        .containsExactly(
            "Yes",
            "Yes",
            "Yes",
            tableView
        );
  }

  @Test
  void getMixedCaseOutputsSummaryFromReservoirSubmissionDetail_whenNotPresent_prospect() {
    var summaryBehaviour = new SummaryBehaviour();
    when(prospectMixedCaseOutputsPersistenceService.getMixedCaseOutput(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());
    when(probabilityDistributionSummaryService.getSummaryBehaviour(INSTANCES))
        .thenReturn(summaryBehaviour);

    var view = prospectMixedCaseOutputsSummaryService.getMixedCaseOutputsSummaryFromReservoirSubmissionDetail(
        reservoirSubmissionDetail);
    assertThat(view)
        .extracting(
            MixedCaseOutputsSummaryView::hasAssociatedGasRecoverableResources,
            MixedCaseOutputsSummaryView::hasAssociatedCondensateRecoverableResources,
            MixedCaseOutputsSummaryView::useIndustryStandardConversion,
            MixedCaseOutputsSummaryView::tableView)
        .containsExactly(
            "",
            "",
            "",
            new ProbabilityDistributionTableView(
                summaryBehaviour,
                List.of(
                    ProbabilityDistributionSummaryView.empty("Oil initially in place"),
                    ProbabilityDistributionSummaryView.empty("Gas initially in place"),
                    ProbabilityDistributionSummaryView.empty("Oil recoverable resources"),
                    ProbabilityDistributionSummaryView.empty("Associated gas recoverable resources"),
                    ProbabilityDistributionSummaryView.empty("Gas recoverable resources"),
                    ProbabilityDistributionSummaryView.empty("Associated condensate recoverable resources"),
                    ProbabilityDistributionSummaryView.empty("Oil equivalent recoverable resources"),
                    ProbabilityDistributionSummaryView.empty("Risked oil equivalent recoverable")
                )
            )
        );
  }
}