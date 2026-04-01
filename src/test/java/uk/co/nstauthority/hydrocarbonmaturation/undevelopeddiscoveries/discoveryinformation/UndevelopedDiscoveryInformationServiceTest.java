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
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoveryTestUtils;

@ExtendWith(MockitoExtension.class)
class UndevelopedDiscoveryInformationServiceTest {

  @Mock
  private UndevelopedDiscoveryInformationRepository informationRepository;

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
    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(new Resource(), true);

    informationService.saveUndevelopedDiscoveryInformation(informationForm, undevelopedDiscovery, null);

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

    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(new Resource(), true);
    informationService.saveUndevelopedDiscoveryInformation(informationForm, undevelopedDiscovery, null);

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

    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(new Resource(), true);
    informationService.saveUndevelopedDiscoveryInformation(informationForm, undevelopedDiscovery, null);

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
            BigDecimal.ZERO,
            new BigDecimal("0.00"),
            new BigDecimal("0.00"),
            new BigDecimal("0.00"),
            null
        );
  }

  @Test
  void saveUndevelopedDiscoveryInformation_withNonNullInformationId_updatesExistingEntity() {
    var informationId = UUID.randomUUID();
    var existingInformation = new UndevelopedDiscoveryInformation();

    when(informationRepository.findById(informationId))
        .thenReturn(Optional.of(existingInformation));

    var informationForm = new UndevelopedDiscoveryInformationForm();
    informationForm.setDiscoveryInformationDateDay("11");
    informationForm.setDiscoveryInformationDateMonth("1");
    informationForm.setDiscoveryInformationDateYear("2002");
    informationForm.setVolumetricsForm(UndevelopedDiscoveryTestUtils.getVolumetricsForm(1, 2, 3, 4, "reason"));
    informationForm.setProvideDiscoveryComplexity(Boolean.FALSE);
    informationForm.setComplexityForm(UndevelopedDiscoveryTestUtils.getComplexityForm());

    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(new Resource(), true);
    informationService.saveUndevelopedDiscoveryInformation(informationForm, undevelopedDiscovery, informationId);

    verify(informationRepository).save(undevelopedDiscoveryInformationCaptor.capture());
    assertThat(undevelopedDiscoveryInformationCaptor.getValue()).isSameAs(existingInformation);
  }

  @Test
  void saveUndevelopedDiscoveryInformation_withNullProvideComplexity_complexityFieldsNotSaved() {
    var informationForm = new UndevelopedDiscoveryInformationForm();
    informationForm.setDiscoveryInformationDateDay("11");
    informationForm.setDiscoveryInformationDateMonth("1");
    informationForm.setDiscoveryInformationDateYear("2002");
    informationForm.setVolumetricsForm(UndevelopedDiscoveryTestUtils.getVolumetricsForm(1, 2, 3, 4, "reason"));
    informationForm.setProvideDiscoveryComplexity(null);
    informationForm.setComplexityForm(UndevelopedDiscoveryTestUtils.getComplexityForm());

    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(new Resource(), true);
    informationService.saveUndevelopedDiscoveryInformation(informationForm, undevelopedDiscovery, null);

    verify(informationRepository).save(undevelopedDiscoveryInformationCaptor.capture());
    assertThat(undevelopedDiscoveryInformationCaptor.getValue())
        .extracting(
            UndevelopedDiscoveryInformation::getProvidedDiscoveryComplexity,
            UndevelopedDiscoveryInformation::getArealDensityRange,
            UndevelopedDiscoveryInformation::getVerticalHeterogeneityRange,
            UndevelopedDiscoveryInformation::getViscosityRange,
            UndevelopedDiscoveryInformation::getStructuralComplexityRange,
            UndevelopedDiscoveryInformation::getCalculatedComplexity)
        .containsExactly(null, null, null, null, null, null);
  }

  @Test
  void saveUndevelopedDiscoveryInformation_withZeroOilAndNonZeroGasRecoverable_percentageGasIsOneHundred() {
    var informationForm = new UndevelopedDiscoveryInformationForm();
    informationForm.setDiscoveryInformationDateDay("11");
    informationForm.setDiscoveryInformationDateMonth("1");
    informationForm.setDiscoveryInformationDateYear("2002");

    var volumetricsForm = UndevelopedDiscoveryTestUtils.getVolumetricsForm(null, null, null, 1, "reason");
    var zeroInput = DecimalInput.createDecimalInput(BigDecimal.ZERO);
    var nonZeroInput = DecimalInput.createDecimalInput(BigDecimal.TEN);
    volumetricsForm.getOilRecoverable().setOneC(zeroInput);
    volumetricsForm.getOilRecoverable().setTwoC(zeroInput);
    volumetricsForm.getOilRecoverable().setThreeC(zeroInput);
    volumetricsForm.getGasRecoverable().setOneC(nonZeroInput);
    volumetricsForm.getGasRecoverable().setTwoC(nonZeroInput);
    volumetricsForm.getGasRecoverable().setThreeC(nonZeroInput);
    informationForm.setVolumetricsForm(volumetricsForm);

    informationForm.setProvideDiscoveryComplexity(Boolean.FALSE);
    informationForm.setComplexityForm(UndevelopedDiscoveryTestUtils.getComplexityForm());

    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(new Resource(), true);
    informationService.saveUndevelopedDiscoveryInformation(informationForm, undevelopedDiscovery, null);

    verify(informationRepository).save(undevelopedDiscoveryInformationCaptor.capture());
    assertThat(undevelopedDiscoveryInformationCaptor.getValue())
        .extracting(UndevelopedDiscoveryInformation::getPercentageGas)
        .isEqualTo(BigDecimal.valueOf(100));
  }

  @Test
  void saveUndevelopedDiscoveryInformation_withZeroOilAndZeroGasRecoverable_percentageGasIsZero() {
    var informationForm = new UndevelopedDiscoveryInformationForm();
    informationForm.setDiscoveryInformationDateDay("11");
    informationForm.setDiscoveryInformationDateMonth("1");
    informationForm.setDiscoveryInformationDateYear("2002");

    var volumetricsForm = UndevelopedDiscoveryTestUtils.getVolumetricsForm(null, null, null, 1, "reason");
    var zeroInput = DecimalInput.createDecimalInput(BigDecimal.ZERO);
    volumetricsForm.getOilRecoverable().setOneC(zeroInput);
    volumetricsForm.getOilRecoverable().setTwoC(zeroInput);
    volumetricsForm.getOilRecoverable().setThreeC(zeroInput);
    volumetricsForm.getGasRecoverable().setOneC(zeroInput);
    volumetricsForm.getGasRecoverable().setTwoC(zeroInput);
    volumetricsForm.getGasRecoverable().setThreeC(zeroInput);
    informationForm.setVolumetricsForm(volumetricsForm);

    informationForm.setProvideDiscoveryComplexity(Boolean.FALSE);
    informationForm.setComplexityForm(UndevelopedDiscoveryTestUtils.getComplexityForm());

    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(new Resource(), true);
    informationService.saveUndevelopedDiscoveryInformation(informationForm, undevelopedDiscovery, null);

    verify(informationRepository).save(undevelopedDiscoveryInformationCaptor.capture());
    assertThat(undevelopedDiscoveryInformationCaptor.getValue())
        .extracting(UndevelopedDiscoveryInformation::getPercentageGas)
        .isEqualTo(BigDecimal.ZERO);
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

    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(new Resource(), true);
    informationService.saveUndevelopedDiscoveryInformation(informationForm, undevelopedDiscovery, null);

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
            BigDecimal.valueOf(100),
            new BigDecimal("0.17"),
            new BigDecimal("0.17"),
            new BigDecimal("0.17"),
            null
        );
  }

  @Test
  void getOrderedDiscoveryInformation_noValues() {
    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(new Resource(), true);
    when(informationRepository.findAllByUndevelopedDiscoveryAndStatus(
        undevelopedDiscovery,
        UndevelopedDiscoveryInformationStatus.EXTANT
    )).thenReturn(List.of());

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

    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(new Resource(), true);
    when(informationRepository.findAllByUndevelopedDiscoveryAndStatus(
        undevelopedDiscovery,
        UndevelopedDiscoveryInformationStatus.EXTANT
    )).thenReturn(List.of(oldestInformation, newestInformation));

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
    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(new Resource(), true);
    var oldestInformation = UndevelopedDiscoveryTestUtils.getUndevelopedDiscoveryInformation(
        LocalDate.of(2002, 1, 11),
        undevelopedDiscovery
    );

    var newestInformation = UndevelopedDiscoveryTestUtils.getUndevelopedDiscoveryInformation(
        LocalDate.of(2024, 1, 11),
        undevelopedDiscovery
    );

    var middleInformation = UndevelopedDiscoveryTestUtils.getUndevelopedDiscoveryInformation(
        LocalDate.of(2010, 1, 11),
        undevelopedDiscovery
    );

    when(informationRepository.findAllByUndevelopedDiscoveryInAndStatus(
        List.of(undevelopedDiscovery),
        UndevelopedDiscoveryInformationStatus.EXTANT
    )).thenReturn(List.of(middleInformation, newestInformation, oldestInformation));

    assertThat(informationService.getLatestDiscoveryInformation(List.of(undevelopedDiscovery)))
        .isEqualTo(List.of(newestInformation));
  }

  @Test
  void getLatestInformation_isEmpty() {
    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(new Resource(), true);
    when(informationRepository.findAllByUndevelopedDiscoveryInAndStatus(
        List.of(undevelopedDiscovery),
        UndevelopedDiscoveryInformationStatus.EXTANT)
    ).thenReturn(List.of());

    assertThat(informationService.getLatestDiscoveryInformation(List.of(undevelopedDiscovery))).isEmpty();
  }
}
