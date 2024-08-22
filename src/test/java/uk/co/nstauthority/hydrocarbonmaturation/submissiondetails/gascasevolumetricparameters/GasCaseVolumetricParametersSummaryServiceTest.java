package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummaryTestUtil.assertSummarySection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;

@ExtendWith(MockitoExtension.class)
class GasCaseVolumetricParametersSummaryServiceTest {
  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private GasCaseVolumetricParametersSummaryService gasCaseVolumetricParametersSummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
      submissionDetail);

  @Test
  void getGasCaseVolumetricsTableView_emptySummaries() {
    var emptyTableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            any(),
            eq(submissionDetail),
            eq(reservoirSubmissionDetail)))
        .thenReturn(emptyTableView);

    var gasCaseVolumetricsTableView = gasCaseVolumetricParametersSummaryService.getGasCaseVolumetricsTableView(
        reservoirSubmissionDetail);

    assertThat(gasCaseVolumetricsTableView)
        .isEqualTo(emptyTableView);
  }

  @Test
  void getGasCaseVolumetricsTableView_whenProspect_filledSummaries() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    submissionDetail.getSubmission().getResource().setType(ResourceType.PROSPECT);

    var tableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            List.of(
                ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_PROSPECT,
                ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_PROSPECT,
                ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
                ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR
            ),
            submissionDetail,
            reservoirSubmissionDetail))
        .thenReturn(tableView);

    var gasCaseVolumetricsSummaryView = gasCaseVolumetricParametersSummaryService.getGasCaseVolumetricsTableView(
        reservoirSubmissionDetail);

    assertThat(gasCaseVolumetricsSummaryView)
        .isEqualTo(tableView);
  }

  @Test
  void getGasCaseVolumetricsTableView_whenDiscovery_filledSummaries() {
    var tableView = ProbabilityDistributionTableView.empty();
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        discoverySubmissionDetail);

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            List.of(
                ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY,
                ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY,
                ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
                ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR
            ),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(tableView);

    var gasCaseVolumetricsSummaryView = gasCaseVolumetricParametersSummaryService.getGasCaseVolumetricsTableView(
        discoveryReservoirSubmissionDetail);

    assertThat(gasCaseVolumetricsSummaryView)
        .isEqualTo(tableView);
  }

  @Test
  void getSummarySection_whenNotGasCase() {
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(false);

    assertThat(gasCaseVolumetricParametersSummaryService.getSummarySection(reservoirSubmissionDetail, USER))
        .isEmpty();
  }

  @ParameterizedTest
  @MethodSource("allNonDiscoverySubmissionTypes")
  void getSummarySection_whenNonDiscovery(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    assertThat(gasCaseVolumetricParametersSummaryService.getSummarySection(reservoirSubmissionDetail, USER))
        .isEmpty();
  }

  @ParameterizedTest
  @EnumSource(
      value = HydrocarbonsDiscovered.class,
      names = {"GAS_ONLY", "MIXED_OIL_AND_GAS"},
      mode = EnumSource.Mode.INCLUDE
  )
  void getSummarySection_whenValidCase() {
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail))
        .thenReturn(true);

    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR);

    var tableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(tableView);

    var summarySection = gasCaseVolumetricParametersSummaryService.getSummarySection(reservoirSubmissionDetail, USER);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(),
        GasCaseVolumetricParametersSummaryService.GAS_CASE_VOLUMETRIC_PARAMETERS_DISPLAY_ORDER);

    var summaryCard = SummaryCard.probabilitySummaryCard(tableView);
    var summaryItem = SummaryItem.withCard(GasCaseVolumetricParametersSummaryService.GAS_CASE_VOLUMETRIC_PARAMETERS,
        summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  private static Stream<Arguments> allNonDiscoverySubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isDiscoverySubmission(submissionType))
        .map(Arguments::of);
  }
}