package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscovery;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoveryTestUtils;

@ExtendWith(MockitoExtension.class)
class UndevelopedDiscoveryInformationServiceTest {

  @Mock
  private UndevelopedDiscoveryInformationRepository informationRepository;

  @Mock
  private UndevelopedDiscovery undevelopedDiscovery;

  @InjectMocks
  private UndevelopedDiscoveryInformationService informationService;

  private final ArgumentCaptor<UndevelopedDiscoveryInformation> undevelopedDiscoveryInformationCaptor = ArgumentCaptor.forClass(
      UndevelopedDiscoveryInformation.class);

  @Test
  void saveUndevelopedDiscoveryInformation_noComplexity() {
    var informationForm = new UndevelopedDiscoveryInformationForm();
    informationForm.setDiscoveryInformationDateDay("11");
    informationForm.setDiscoveryInformationDateMonth("1");
    informationForm.setDiscoveryInformationDateYear("2002");

    informationForm.setVolumetricsForm(UndevelopedDiscoveryTestUtils.getVolumetricsForm(1, 2, 3, 4, "reason"));

    informationForm.setProvideDiscoveryComplexity(Boolean.FALSE);
    informationForm.setComplexityForm(UndevelopedDiscoveryTestUtils.getComplexityForm());

    informationService.saveUndevelopedDiscoveryInformation(informationForm, undevelopedDiscovery);

    verify(informationRepository, times(1))
        .save(undevelopedDiscoveryInformationCaptor.capture());

    assertThat(undevelopedDiscoveryInformationCaptor.getValue())
        .extracting(
            UndevelopedDiscoveryInformation::getStatus,
            UndevelopedDiscoveryInformation::getInformationDate,
            UndevelopedDiscoveryInformation::getOilInPlaceP50,
            UndevelopedDiscoveryInformation::getOilRecoverableOneC,
            UndevelopedDiscoveryInformation::getOilRecoverableTwoC,
            UndevelopedDiscoveryInformation::getOilRecoverableThreeC,
            UndevelopedDiscoveryInformation::getGasInPlaceP50,
            UndevelopedDiscoveryInformation::getGasRecoverableOneC,
            UndevelopedDiscoveryInformation::getGasRecoverableTwoC,
            UndevelopedDiscoveryInformation::getGasRecoverableThreeC,
            UndevelopedDiscoveryInformation::getProvidedDiscoveryComplexity,
            UndevelopedDiscoveryInformation::getArealDensityRange,
            UndevelopedDiscoveryInformation::getVerticalHeterogeneityRange,
            UndevelopedDiscoveryInformation::getViscosityRange,
            UndevelopedDiscoveryInformation::getStructuralComplexityRange,
            UndevelopedDiscoveryInformation::getDifferenceReason,
            UndevelopedDiscoveryInformation::getPercentageGas,
            UndevelopedDiscoveryInformation::getOilEquivalentRecoverableOneC,
            UndevelopedDiscoveryInformation::getOilEquivalentRecoverableTwoC,
            UndevelopedDiscoveryInformation::getOilEquivalentRecoverableThreeC,
            UndevelopedDiscoveryInformation::getCalculatedComplexity)
        .containsExactly(
            UndevelopedDiscoveryInformationStatus.EXTANT,
            LocalDate.of(2002, 1, 11),
            BigDecimal.valueOf(4),
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(2),
            BigDecimal.valueOf(3),
            BigDecimal.valueOf(4),
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(2),
            BigDecimal.valueOf(3),
            Boolean.FALSE,
            null,
            null,
            null,
            null,
            "reason",
            new BigDecimal("14.53"),
            new BigDecimal("1.17"),
            new BigDecimal("2.34"),
            new BigDecimal("3.52"),
            null
        );
  }

  @Test
  void saveUndevelopedDiscoveryInformation_savedComplexity() {
    var informationForm = new UndevelopedDiscoveryInformationForm();
    informationForm.setDiscoveryInformationDateDay("11");
    informationForm.setDiscoveryInformationDateMonth("1");
    informationForm.setDiscoveryInformationDateYear("2002");

    informationForm.setVolumetricsForm(UndevelopedDiscoveryTestUtils.getVolumetricsForm(1, 2, 3, 4, "reason"));

    informationForm.setProvideDiscoveryComplexity(Boolean.TRUE);
    var complexityForm = UndevelopedDiscoveryTestUtils.getComplexityForm();
    informationForm.setComplexityForm(complexityForm);

    informationService.saveUndevelopedDiscoveryInformation(informationForm, undevelopedDiscovery);

    verify(informationRepository, times(1))
        .save(undevelopedDiscoveryInformationCaptor.capture());

    assertThat(undevelopedDiscoveryInformationCaptor.getValue())
        .extracting(
            UndevelopedDiscoveryInformation::getStatus,
            UndevelopedDiscoveryInformation::getInformationDate,
            UndevelopedDiscoveryInformation::getOilInPlaceP50,
            UndevelopedDiscoveryInformation::getOilRecoverableOneC,
            UndevelopedDiscoveryInformation::getOilRecoverableTwoC,
            UndevelopedDiscoveryInformation::getOilRecoverableThreeC,
            UndevelopedDiscoveryInformation::getGasInPlaceP50,
            UndevelopedDiscoveryInformation::getGasRecoverableOneC,
            UndevelopedDiscoveryInformation::getGasRecoverableTwoC,
            UndevelopedDiscoveryInformation::getGasRecoverableThreeC,
            UndevelopedDiscoveryInformation::getProvidedDiscoveryComplexity,
            UndevelopedDiscoveryInformation::getArealDensityRange,
            UndevelopedDiscoveryInformation::getVerticalHeterogeneityRange,
            UndevelopedDiscoveryInformation::getViscosityRange,
            UndevelopedDiscoveryInformation::getStructuralComplexityRange,
            UndevelopedDiscoveryInformation::getDifferenceReason,
            UndevelopedDiscoveryInformation::getPercentageGas,
            UndevelopedDiscoveryInformation::getOilEquivalentRecoverableOneC,
            UndevelopedDiscoveryInformation::getOilEquivalentRecoverableTwoC,
            UndevelopedDiscoveryInformation::getOilEquivalentRecoverableThreeC,
            UndevelopedDiscoveryInformation::getCalculatedComplexity)
        .containsExactly(
            UndevelopedDiscoveryInformationStatus.EXTANT,
            LocalDate.of(2002, 1, 11),
            BigDecimal.valueOf(4),
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(2),
            BigDecimal.valueOf(3),
            BigDecimal.valueOf(4),
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(2),
            BigDecimal.valueOf(3),
            Boolean.TRUE,
            complexityForm.getArealDensityRange(),
            complexityForm.getVerticalHeterogeneityRange(),
            complexityForm.getViscosityRange(),
            complexityForm.getStructuralComplexityRange(),
            "reason",
            new BigDecimal("14.53"),
            new BigDecimal("1.17"),
            new BigDecimal("2.34"),
            new BigDecimal("3.52"),
            new BigDecimal(14)
        );
  }

  @Test
  void saveUndevelopedDiscoveryInformation_handlesNullRecoverableResources() {
    var informationForm = new UndevelopedDiscoveryInformationForm();
    informationForm.setDiscoveryInformationDateDay("11");
    informationForm.setDiscoveryInformationDateMonth("1");
    informationForm.setDiscoveryInformationDateYear("2002");

    informationForm.setVolumetricsForm(
        UndevelopedDiscoveryTestUtils.getVolumetricsForm(null, null, null, 1, "reason"));

    informationForm.setProvideDiscoveryComplexity(Boolean.FALSE);
    informationForm.setComplexityForm(UndevelopedDiscoveryTestUtils.getComplexityForm());

    informationService.saveUndevelopedDiscoveryInformation(informationForm, undevelopedDiscovery);

    verify(informationRepository, times(1))
        .save(undevelopedDiscoveryInformationCaptor.capture());

    assertThat(undevelopedDiscoveryInformationCaptor.getValue())
        .extracting(
            UndevelopedDiscoveryInformation::getStatus,
            UndevelopedDiscoveryInformation::getInformationDate,
            UndevelopedDiscoveryInformation::getOilInPlaceP50,
            UndevelopedDiscoveryInformation::getOilRecoverableOneC,
            UndevelopedDiscoveryInformation::getOilRecoverableTwoC,
            UndevelopedDiscoveryInformation::getOilRecoverableThreeC,
            UndevelopedDiscoveryInformation::getGasInPlaceP50,
            UndevelopedDiscoveryInformation::getGasRecoverableOneC,
            UndevelopedDiscoveryInformation::getGasRecoverableTwoC,
            UndevelopedDiscoveryInformation::getGasRecoverableThreeC,
            UndevelopedDiscoveryInformation::getProvidedDiscoveryComplexity,
            UndevelopedDiscoveryInformation::getArealDensityRange,
            UndevelopedDiscoveryInformation::getVerticalHeterogeneityRange,
            UndevelopedDiscoveryInformation::getViscosityRange,
            UndevelopedDiscoveryInformation::getStructuralComplexityRange,
            UndevelopedDiscoveryInformation::getDifferenceReason,
            UndevelopedDiscoveryInformation::getPercentageGas,
            UndevelopedDiscoveryInformation::getOilEquivalentRecoverableOneC,
            UndevelopedDiscoveryInformation::getOilEquivalentRecoverableTwoC,
            UndevelopedDiscoveryInformation::getOilEquivalentRecoverableThreeC,
            UndevelopedDiscoveryInformation::getCalculatedComplexity)
        .containsExactly(
            UndevelopedDiscoveryInformationStatus.EXTANT,
            LocalDate.of(2002, 1, 11),
            BigDecimal.valueOf(1),
            null,
            null,
            null,
            BigDecimal.valueOf(1),
            null,
            null,
            null,
            Boolean.FALSE,
            null,
            null,
            null,
            null,
            "reason",
            null,
            null,
            null,
            null,
            null
        );
  }

  @Test
  void saveUndevelopedDiscoveryInformation_onlyGasRecoverable() {
    var informationForm = new UndevelopedDiscoveryInformationForm();
    informationForm.setDiscoveryInformationDateDay("11");
    informationForm.setDiscoveryInformationDateMonth("1");
    informationForm.setDiscoveryInformationDateYear("2002");

    var volumetricsForm = UndevelopedDiscoveryTestUtils.getVolumetricsForm(null, null, null, 1, "reason");
    var decimalInputOne = DecimalInput.createDecimalInput(BigDecimal.ONE);
    volumetricsForm.getGasRecoverable().setOneC(decimalInputOne);
    volumetricsForm.getGasRecoverable().setTwoC(decimalInputOne);
    volumetricsForm.getGasRecoverable().setThreeC(decimalInputOne);
    informationForm.setVolumetricsForm(volumetricsForm);

    informationForm.setProvideDiscoveryComplexity(Boolean.FALSE);
    informationForm.setComplexityForm(UndevelopedDiscoveryTestUtils.getComplexityForm());

    informationService.saveUndevelopedDiscoveryInformation(informationForm, undevelopedDiscovery);

    verify(informationRepository, times(1))
        .save(undevelopedDiscoveryInformationCaptor.capture());

    assertThat(undevelopedDiscoveryInformationCaptor.getValue())
        .extracting(
            UndevelopedDiscoveryInformation::getStatus,
            UndevelopedDiscoveryInformation::getInformationDate,
            UndevelopedDiscoveryInformation::getOilInPlaceP50,
            UndevelopedDiscoveryInformation::getOilRecoverableOneC,
            UndevelopedDiscoveryInformation::getOilRecoverableTwoC,
            UndevelopedDiscoveryInformation::getOilRecoverableThreeC,
            UndevelopedDiscoveryInformation::getGasInPlaceP50,
            UndevelopedDiscoveryInformation::getGasRecoverableOneC,
            UndevelopedDiscoveryInformation::getGasRecoverableTwoC,
            UndevelopedDiscoveryInformation::getGasRecoverableThreeC,
            UndevelopedDiscoveryInformation::getProvidedDiscoveryComplexity,
            UndevelopedDiscoveryInformation::getArealDensityRange,
            UndevelopedDiscoveryInformation::getVerticalHeterogeneityRange,
            UndevelopedDiscoveryInformation::getViscosityRange,
            UndevelopedDiscoveryInformation::getStructuralComplexityRange,
            UndevelopedDiscoveryInformation::getDifferenceReason,
            UndevelopedDiscoveryInformation::getPercentageGas,
            UndevelopedDiscoveryInformation::getOilEquivalentRecoverableOneC,
            UndevelopedDiscoveryInformation::getOilEquivalentRecoverableTwoC,
            UndevelopedDiscoveryInformation::getOilEquivalentRecoverableThreeC,
            UndevelopedDiscoveryInformation::getCalculatedComplexity)
        .containsExactly(
            UndevelopedDiscoveryInformationStatus.EXTANT,
            LocalDate.of(2002, 1, 11),
            BigDecimal.valueOf(1),
            null,
            null,
            null,
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(1),
            Boolean.FALSE,
            null,
            null,
            null,
            null,
            "reason",
            null,
            null,
            null,
            null,
            null
        );
  }

  @Test
  void getOrderedDiscoveryInformation_noValues() {
    when(informationRepository.findAllByUndevelopedDiscovery(undevelopedDiscovery))
        .thenReturn(List.of());

    assertThat(informationService.getOrderedDiscoveryInformation(undevelopedDiscovery))
        .isEqualTo(List.of());
  }

  @Test
  void getOrderedDiscoveryInformation_ordersByMostRecent_filtersByExtant() {
    var oldestInformation = new UndevelopedDiscoveryInformation();
    oldestInformation.setInformationDate(LocalDate.of(2002, 1, 11));
    oldestInformation.setStatus(UndevelopedDiscoveryInformationStatus.EXTANT);

    var newestInformation = new UndevelopedDiscoveryInformation();
    newestInformation.setInformationDate(LocalDate.of(2024, 4, 29));
    newestInformation.setStatus(UndevelopedDiscoveryInformationStatus.EXTANT);

    var deletedInformation = new UndevelopedDiscoveryInformation();
    deletedInformation.setStatus(UndevelopedDiscoveryInformationStatus.DELETED);

    when(informationRepository.findAllByUndevelopedDiscovery(undevelopedDiscovery))
        .thenReturn(List.of(oldestInformation, newestInformation, deletedInformation));

    assertThat(informationService.getOrderedDiscoveryInformation(undevelopedDiscovery))
        .isEqualTo(List.of(newestInformation, oldestInformation));
  }

  @Test
  void getUndevelopedDiscoveryInformationOrThrow() {
    var information = new UndevelopedDiscoveryInformation();
    var informationId = UUID.randomUUID();

    when(informationRepository.findById(informationId))
        .thenReturn(Optional.of(information));

    assertThat(informationService.getUndevelopedDiscoveryInformationOrThrow(informationId))
        .isEqualTo(information);
  }

  @Test
  void getUndevelopedDiscoveryInformationOrThrow_assertThrows() {
    var informationId = UUID.randomUUID();

    when(informationRepository.findById(informationId))
        .thenReturn(Optional.empty());

    assertThatExceptionOfType(RemiEntityNotFoundException.class)
        .isThrownBy(() -> informationService.getUndevelopedDiscoveryInformationOrThrow(informationId))
        .withMessage("No undeveloped discovery information found for id %s".formatted(informationId));
  }

  @Test
  void entityToForm() {
    var information = UndevelopedDiscoveryTestUtils.getUndevelopedDiscoveryInformation(
        LocalDate.of(2002, 1, 11),
        null
    );

    var expectedVolumetrics = UndevelopedDiscoveryTestUtils.getVolumetricsForm(
        1, 1, 1, 1, information.getDifferenceReason()
    );

    var expectedComplexity = UndevelopedDiscoveryTestUtils.getComplexityForm();

    var generatedForm = informationService.entityToForm(information);
    assertThat(generatedForm)
        .extracting(
            UndevelopedDiscoveryInformationForm::getDiscoveryInformationDateDay,
            UndevelopedDiscoveryInformationForm::getDiscoveryInformationDateMonth,
            UndevelopedDiscoveryInformationForm::getDiscoveryInformationDateYear,
            UndevelopedDiscoveryInformationForm::getProvideDiscoveryComplexity
        )
        .containsExactly(
            "11",
            "1",
            "2002",
            Boolean.TRUE
        );

    var generatedVolumetrics = generatedForm.getVolumetricsForm();
    assertThat(generatedVolumetrics)
        .usingRecursiveComparison()
        .isEqualTo(expectedVolumetrics);

    var generatedComplexity = generatedForm.getComplexityForm();
    assertThat(generatedComplexity)
        .usingRecursiveComparison()
        .isEqualTo(expectedComplexity);
  }

  @Test
  void entityToForm_doesNotStoreComplexity() {
    var information = UndevelopedDiscoveryTestUtils.getUndevelopedDiscoveryInformation(
        LocalDate.of(2002, 1, 11),
        null
    );
    information.setProvidedDiscoveryComplexity(Boolean.FALSE);

    var expectedVolumetrics = UndevelopedDiscoveryTestUtils.getVolumetricsForm(
        1, 1, 1, 1, information.getDifferenceReason()
    );

    var generatedForm = informationService.entityToForm(information);
    assertThat(generatedForm)
        .extracting(
            UndevelopedDiscoveryInformationForm::getDiscoveryInformationDateDay,
            UndevelopedDiscoveryInformationForm::getDiscoveryInformationDateMonth,
            UndevelopedDiscoveryInformationForm::getDiscoveryInformationDateYear,
            UndevelopedDiscoveryInformationForm::getProvideDiscoveryComplexity
        )
        .containsExactly(
            "11",
            "1",
            "2002",
            Boolean.FALSE
        );

    var generatedVolumetrics = generatedForm.getVolumetricsForm();
    assertThat(generatedVolumetrics)
        .usingRecursiveComparison()
        .isEqualTo(expectedVolumetrics);

    var generatedComplexity = generatedForm.getComplexityForm();
    assertThat(generatedComplexity)
        .usingRecursiveComparison()
        .isEqualTo(new UndevelopedDiscoveryComplexityForm());
  }

  @Test
  void deleteInformation() {
    var information = new UndevelopedDiscoveryInformation();
    information.setStatus(UndevelopedDiscoveryInformationStatus.EXTANT);

    informationService.deleteInformation(information);

    verify(informationRepository).save(undevelopedDiscoveryInformationCaptor.capture());
    assertThat(undevelopedDiscoveryInformationCaptor.getValue())
        .extracting(UndevelopedDiscoveryInformation::getStatus)
        .isEqualTo(UndevelopedDiscoveryInformationStatus.DELETED);
  }

  @Test
  void getLatestInformation() {
    var oldestInformation = UndevelopedDiscoveryTestUtils.getUndevelopedDiscoveryInformation(
        LocalDate.of(2002, 1, 11),
        null
    );

    var newestInformation = UndevelopedDiscoveryTestUtils.getUndevelopedDiscoveryInformation(
        LocalDate.of(2024, 1, 11),
        null
    );

    var middleInformation = UndevelopedDiscoveryTestUtils.getUndevelopedDiscoveryInformation(
        LocalDate.of(2010, 1, 11),
        null
    );

    when(informationRepository.findAllByUndevelopedDiscovery(undevelopedDiscovery))
        .thenReturn(List.of(middleInformation, newestInformation, oldestInformation));

    assertThat(informationService.getLatestInformation(undevelopedDiscovery))
        .isEqualTo(Optional.of(newestInformation));
  }

  @Test
  void getLatestInformation_isEmpty() {
    when(informationRepository.findAllByUndevelopedDiscovery(undevelopedDiscovery))
        .thenReturn(List.of());

    assertThat(informationService.getLatestInformation(undevelopedDiscovery))
        .isEmpty();
  }
}
