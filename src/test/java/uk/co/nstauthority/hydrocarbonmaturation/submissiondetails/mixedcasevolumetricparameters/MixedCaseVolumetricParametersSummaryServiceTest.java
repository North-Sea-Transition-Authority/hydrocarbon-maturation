package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetricparameters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class MixedCaseVolumetricParametersSummaryServiceTest {

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @InjectMocks
  private MixedCaseVolumetricParametersSummaryService mixedCaseVolumetricParametersSummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void getMixedCaseVolumetricsSummaryView_whenProspect() {
    var tableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            List.of(
                ProbabilityDistributionFieldInstance.MIXED_OIL_FORMATION_VOLUME_FACTOR_PROSPECT,
                ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION_PROSPECT,
                ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
                ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR_PROSPECT,
                ProbabilityDistributionFieldInstance.MIXED_GAS_FORMATION_VOLUME_FACTOR_PROSPECT,
                ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION_PROSPECT,
                ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
                ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR
            ),
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(tableView);

    var mixedCaseVolumetricsSummaryView = mixedCaseVolumetricParametersSummaryService.getMixedCaseVolumetricsSummaryView(
        reservoirSubmissionDetail);

    assertThat(mixedCaseVolumetricsSummaryView)
        .isEqualTo(tableView);
  }

  @Test
  void getMixedCaseVolumetricsSummaryView_whenDiscovery() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);

    var tableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            List.of(
                ProbabilityDistributionFieldInstance.MIXED_OIL_FORMATION_VOLUME_FACTOR_DISCOVERY,
                ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION_DISCOVERY,
                ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
                ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR_DISCOVERY,
                ProbabilityDistributionFieldInstance.MIXED_GAS_FORMATION_VOLUME_FACTOR_DISCOVERY,
                ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION_DISCOVERY,
                ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
                ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR
            ),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(tableView);

    var mixedCaseVolumetricsSummaryView = mixedCaseVolumetricParametersSummaryService.getMixedCaseVolumetricsSummaryView(
        discoveryReservoirSubmissionDetail);

    assertThat(mixedCaseVolumetricsSummaryView)
        .isEqualTo(tableView);
  }
}