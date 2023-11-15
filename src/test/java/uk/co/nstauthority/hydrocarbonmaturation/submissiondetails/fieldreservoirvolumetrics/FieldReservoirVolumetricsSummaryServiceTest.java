package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;

@ExtendWith(MockitoExtension.class)
class FieldReservoirVolumetricsSummaryServiceTest {

  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private FieldReservoirVolumetricsSummaryService fieldReservoirVolumetricsSummaryService;

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void getSummarySection_notFieldSubmission_empty(SubmissionType submissionType) {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionType);

    assertThat(fieldReservoirVolumetricsSummaryService.getSummarySection(reservoirSubmissionDetail))
        .isEmpty();
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void getSummarySection_fieldSubmission_oilCase_present(SubmissionType submissionType) {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionType);
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.ofNullable(reservoirHydrocarbons));
    when(generalReservoirInformationService.isOilOnly(reservoirHydrocarbons))
        .thenReturn(true);

    var listOfInstances = List.of(ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD);
    var summaryViews = List.of(ProbabilityDistributionSummaryView.empty("test"));
    when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
        listOfInstances,
        submissionDetail,
        reservoirSubmissionDetail)
    ).thenReturn(summaryViews);

    var summarySection = fieldReservoirVolumetricsSummaryService.getSummarySection(reservoirSubmissionDetail);
    assertThat(summarySection).isPresent();

    var expectedCard = SummaryCard.probabilitySummaryCard(summaryViews);
    var expectedItem = SummaryItem.withCard(FieldReservoirVolumetricsSummaryService.OIL_VOLUMETRICS, expectedCard);

    assertThat(summarySection.get().summaryItems()).hasSize(1);

    var summaryItem = summarySection.get().summaryItems().get(0);
    assertThat(summaryItem)
        .usingRecursiveComparison()
        .isEqualTo(expectedItem);

  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void getSummarySection_fieldSubmission_gasCase_present(SubmissionType submissionType) {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionType);
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.ofNullable(reservoirHydrocarbons));
    when(generalReservoirInformationService.isGasOnly(reservoirHydrocarbons))
        .thenReturn(true);

    var listOfInstances = List.of(ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD);
    var summaryViews = List.of(ProbabilityDistributionSummaryView.empty("test"));
    when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
        listOfInstances,
        submissionDetail,
        reservoirSubmissionDetail)
    ).thenReturn(summaryViews);

    var summarySection = fieldReservoirVolumetricsSummaryService.getSummarySection(reservoirSubmissionDetail);
    assertThat(summarySection).isPresent();

    var expectedCard = SummaryCard.probabilitySummaryCard(summaryViews);
    var expectedItem = SummaryItem.withCard(FieldReservoirVolumetricsSummaryService.GAS_VOLUMETRICS, expectedCard);

    assertThat(summarySection.get().summaryItems()).hasSize(1);

    var summaryItem = summarySection.get().summaryItems().get(0);
    assertThat(summaryItem)
        .usingRecursiveComparison()
        .isEqualTo(expectedItem);

  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void getSummarySection_fieldSubmission_combinedCase_present(SubmissionType submissionType) {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionType);
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.ofNullable(reservoirHydrocarbons));
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons))
        .thenReturn(true);

    var listOfInstances = List.of(
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD,
        ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
    );
    var summaryViews = List.of(ProbabilityDistributionSummaryView.empty("test"));
    when(probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
        listOfInstances,
        submissionDetail,
        reservoirSubmissionDetail)
    ).thenReturn(summaryViews);

    var summarySection = fieldReservoirVolumetricsSummaryService.getSummarySection(reservoirSubmissionDetail);
    assertThat(summarySection).isPresent();

    var expectedCard = SummaryCard.probabilitySummaryCard(summaryViews);
    var expectedItem = SummaryItem.withCard(FieldReservoirVolumetricsSummaryService.OIL_AND_GAS_VOLUMETRICS, expectedCard);

    assertThat(summarySection.get().summaryItems()).hasSize(1);

    var summaryItem = summarySection.get().summaryItems().get(0);
    assertThat(summaryItem)
        .usingRecursiveComparison()
        .isEqualTo(expectedItem);

  }

  private static Stream<Arguments> allFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(SubmissionDetailUtil::isFieldSubmission)
        .map(Arguments::of);
  }

  private static Stream<Arguments> allNonFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isFieldSubmission(submissionType))
        .map(Arguments::of);
  }
}