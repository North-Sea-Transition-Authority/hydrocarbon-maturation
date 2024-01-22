package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldoilvolumetricparameters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

@ExtendWith(MockitoExtension.class)
class FieldOilVolumetricParametersSummaryServiceTest {
  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;
  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  @InjectMocks
  private FieldOilVolumetricParametersSummaryService fieldOilVolumetricParametersSummaryService;

  private SubmissionDetail submissionDetail;
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createFieldSubmissionDetail(SubmissionType.CREATE_CSR);
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
  }

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void getSummarySection_nonFieldSubmission_assertEmpty(SubmissionType submissionType) {
    var nonFieldSubmissionDetail =
        SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    var nonFieldReservoirSubmissionDetail =
        ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(nonFieldSubmissionDetail);

    assertThat(fieldOilVolumetricParametersSummaryService.getSummarySection(nonFieldReservoirSubmissionDetail))
        .isEmpty();
  }

  @Test
  void getSummarySection_noReservoirHydrocarbon_assertEmpty() {
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var captor = ArgumentCaptor.forClass(ReservoirHydrocarbons.class);
    assertThat(fieldOilVolumetricParametersSummaryService.getSummarySection(reservoirSubmissionDetail))
        .isEmpty();

    verify(generalReservoirInformationService).isOilOnly(captor.capture());
    assertThat(captor.getValue())
        .usingRecursiveComparison()
        .isEqualTo(new ReservoirHydrocarbons());
  }

  @Test
  void getSummarySection_nonOilOrCombinedCase_assertEmpty() {
    var gasReservoirHydrocarbon = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(gasReservoirHydrocarbon));

    when(generalReservoirInformationService.isOilOnly(gasReservoirHydrocarbon))
        .thenReturn(false);
    when(generalReservoirInformationService.isCombinedOilAndGas(gasReservoirHydrocarbon))
        .thenReturn(false);

    assertThat(fieldOilVolumetricParametersSummaryService.getSummarySection(reservoirSubmissionDetail))
        .isEmpty();
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getSummarySection_oilOrCombinedOilAndGas_assertProperties(boolean isOilCase) {
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    when(generalReservoirInformationService.isOilOnly(reservoirHydrocarbons))
        .thenReturn(isOilCase);
    if (!isOilCase) {
      when(generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons))
          .thenReturn(!isOilCase);
    }

    var fieldOilVolumetricParametersInstances = List.of(
        ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_FIELD,
        ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_FIELD,
        ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD_CONSTANT
    );

    var summaryViews = List.of(ProbabilityDistributionSummaryView.empty("Test 1"));
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            fieldOilVolumetricParametersInstances,
            submissionDetail,
            reservoirSubmissionDetail)
    ).thenReturn(summaryViews);

    var saturationSummaryView = ProbabilityDistributionSummaryView.empty("Test 2");
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_FIELD,
            submissionDetail,
            reservoirSubmissionDetail)
    ).thenReturn(saturationSummaryView);

    var summarySection = fieldOilVolumetricParametersSummaryService.getSummarySection(reservoirSubmissionDetail);
    assertThat(summarySection)
        .isPresent()
        .get()
        .extracting(
            SummarySection::summaryText,
            SummarySection::displayOrder
        ).containsExactly(
            "",
            FieldOilVolumetricParametersSummaryService.FIELD_OIL_VOLUMETRIC_PARAMETERS_DISPLAY_ORDER
        );

    assertThat(summarySection.get().summaryItems())
        .hasSize(2)
        .extracting(
            SummaryItem::displayName
        ).containsOnly(
            FieldOilVolumetricParametersSummaryService.FIELD_OIL_VOLUMETRIC_PARAMETERS
        );

    assertThat(summarySection.get().summaryItems().get(0).summaryCards())
        .hasSize(1)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        ).containsExactly(
            Tuple.tuple(
                null,
                SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
                summaryViews
            )
        );

    assertThat(summarySection.get().summaryItems().get(1).summaryCards())
        .hasSize(1)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        ).containsExactly(
            Tuple.tuple(
                null,
                SummaryCardType.MIN_MAX_MOST_LIKELY_SUMMARY,
                List.of(saturationSummaryView)
            )
        );
  }

  private static Stream<Arguments> allNonFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isFieldSubmission(submissionType))
        .map(Arguments::of);
  }
}