package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldgasvolumetricparameters;

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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView;
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
class FieldGasVolumetricParametersSummaryServiceTest {
  private static final User USER = UserTestUtil.Builder().build();
  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;
  @Mock
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  @InjectMocks
  private FieldGasVolumetricParametersSummaryService fieldGasVolumetricParametersSummaryService;

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

    assertThat(fieldGasVolumetricParametersSummaryService.getSummarySection(nonFieldReservoirSubmissionDetail, USER))
        .isEmpty();
  }

  @Test
  void getSummarySection_noReservoirHydrocarbon_assertEmpty() {
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var captor = ArgumentCaptor.forClass(ReservoirHydrocarbons.class);
    assertThat(fieldGasVolumetricParametersSummaryService.getSummarySection(reservoirSubmissionDetail, USER))
        .isEmpty();

    verify(generalReservoirInformationService).isGasOnly(captor.capture());
    assertThat(captor.getValue())
        .usingRecursiveComparison()
        .isEqualTo(new ReservoirHydrocarbons());
  }

  @Test
  void getSummarySection_nonGasOrCombinedCase_assertEmpty() {
    var oilReservoirHydrocarbon = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(oilReservoirHydrocarbon));

    when(generalReservoirInformationService.isGasOnly(oilReservoirHydrocarbon))
        .thenReturn(false);
    when(generalReservoirInformationService.isCombinedOilAndGas(oilReservoirHydrocarbon))
        .thenReturn(false);

    assertThat(fieldGasVolumetricParametersSummaryService.getSummarySection(reservoirSubmissionDetail, USER))
        .isEmpty();
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getSummarySection_oilOrCombinedOilAndGas_assertProperties(boolean isGasCase) {
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    when(generalReservoirInformationService.isGasOnly(reservoirHydrocarbons))
        .thenReturn(isGasCase);
    if (!isGasCase) {
      when(generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons))
          .thenReturn(!isGasCase);
    }

    var fieldOilVolumetricParametersInstances = List.of(
        ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_FIELD,
        ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD_CONSTANT,
        ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR_FIELD
    );

    var fieldOilTableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            fieldOilVolumetricParametersInstances,
            submissionDetail,
            reservoirSubmissionDetail)
    ).thenReturn(fieldOilTableView);

    var saturationTableView = ProbabilityDistributionTableView.empty();
    when(probabilityDistributionSummaryService
        .getProbabilityDistributionTableViewFromInstancesAndSubmission(
            List.of(ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_FIELD),
            submissionDetail,
            reservoirSubmissionDetail)
    ).thenReturn(saturationTableView);

    var summarySection = fieldGasVolumetricParametersSummaryService.getSummarySection(reservoirSubmissionDetail, USER);
    assertThat(summarySection)
        .isPresent()
        .get()
        .extracting(
            SummarySection::summaryText,
            SummarySection::displayOrder
        ).containsExactly(
            "",
            FieldGasVolumetricParametersSummaryService.FIELD_GAS_VOLUMETRIC_PARAMETERS_DISPLAY_ORDER
        );

    assertThat(summarySection.get().summaryItems())
        .hasSize(2)
        .extracting(
            SummaryItem::displayName
        ).containsOnly(
            FieldGasVolumetricParametersSummaryService.FIELD_GAS_VOLUMETRIC_PARAMETERS
        );

    // assert probabilities card
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
                fieldOilTableView
            )
        );

    // assert minMaxMostLikelyCard card
    assertThat(summarySection.get().summaryItems().get(1).summaryCards())
        .hasSize(1)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        ).containsExactly(
            Tuple.tuple(
                null,
                SummaryCardType.PROBABILITY_DISTRIBUTION_SUMMARY,
                saturationTableView
            )
        );
  }

  private static Stream<Arguments> allNonFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isFieldSubmission(submissionType))
        .map(Arguments::of);
  }
}