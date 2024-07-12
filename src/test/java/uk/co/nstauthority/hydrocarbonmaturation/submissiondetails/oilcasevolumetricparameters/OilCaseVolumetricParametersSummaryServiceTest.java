package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters;

import static org.assertj.core.api.Assertions.assertThat;
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
class OilCaseVolumetricParametersSummaryServiceTest {

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private OilCaseVolumetricParametersSummaryService oilCaseVolumetricParametersSummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void getOilCaseVolumetricsSummaryView_whenProspect() {
    var propspectSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var prospectReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(propspectSubmissionDetail);

    var tableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            List.of(
                ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_PROSPECT,
                ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_PROSPECT,
                ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
                ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_PROSPECT
            ),
            propspectSubmissionDetail,
            prospectReservoirSubmissionDetail))
        .thenReturn(tableView);

    var oilCaseVolumetricsSummaryView = oilCaseVolumetricParametersSummaryService.getOilCaseVolumetricsSummaryView(
        prospectReservoirSubmissionDetail);

    assertThat(oilCaseVolumetricsSummaryView)
        .isEqualTo(tableView);
  }

  @Test
  void getOilCaseVolumetricsSummaryView_whenDiscovery() {
    var tableView = ProbabilityDistributionTableView.empty();
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);

    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            List.of(
                ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_DISCOVERY,
                ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_DISCOVERY,
                ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
                ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_DISCOVERY
            ),
            discoverySubmissionDetail,
            discoveryReservoirSubmissionDetail))
        .thenReturn(tableView);

    var oilCaseVolumetricsSummaryView = oilCaseVolumetricParametersSummaryService.getOilCaseVolumetricsSummaryView(
        discoveryReservoirSubmissionDetail);

    assertThat(oilCaseVolumetricsSummaryView)
        .isEqualTo(tableView);
  }

  @Test
  void getSummarySection_whenNotOilCase() {
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(false);

    assertThat(oilCaseVolumetricParametersSummaryService.getSummarySection(reservoirSubmissionDetail))
        .isEmpty();
  }

  @ParameterizedTest
  @MethodSource("allNonDiscoverySubmissionTypes")
  void getSummarySection_whenNotDiscovery(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    assertThat(oilCaseVolumetricParametersSummaryService.getSummarySection(reservoirSubmissionDetail))
        .isEmpty();
  }

  @ParameterizedTest
  @EnumSource(
      value = HydrocarbonsDiscovered.class,
      names = {"OIL_ONLY", "MIXED_OIL_AND_GAS"},
      mode = EnumSource.Mode.INCLUDE
  )
  void getSummarySection_whenOilCase() {
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail))
        .thenReturn(true);

    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_DISCOVERY);
    listOfInstances.add(ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD);
    listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_DISCOVERY);

    var tableView = ProbabilityDistributionTableView.empty();

    when(probabilityDistributionSummaryService.getProbabilityDistributionTableViewFromInstancesAndSubmission(
        listOfInstances, submissionDetail, reservoirSubmissionDetail
    )).thenReturn(tableView);

    var summarySection = oilCaseVolumetricParametersSummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), OilCaseVolumetricParametersSummaryService.OIL_CASE_VOLUMETRIC_PARAMETERS_DISPLAY_ORDER);

    var summaryCard = SummaryCard.probabilitySummaryCard(tableView);
    var summaryItem = SummaryItem.withCard(OilCaseVolumetricParametersSummaryService.OIL_CASE_VOLUMETRIC_PARAMETERS, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  private static Stream<Arguments> allNonDiscoverySubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isDiscoverySubmission(submissionType))
        .map(Arguments::of);
  }
}