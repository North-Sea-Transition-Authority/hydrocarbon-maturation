package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class FieldReservoirVolumetricsFormValidatorTest {
  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  private FieldReservoirVolumetricsFormValidator fieldReservoirVolumetricsFormValidator;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    fieldReservoirVolumetricsFormValidator = new FieldReservoirVolumetricsFormValidator(generalReservoirInformationService);

    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors_withInitiallyInPlaceQuestion(ValidationType validationType) {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.CREATE_FDP);
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);
    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors_withoutInitiallyInPlaceQuestion(ValidationType validationType) {
    //when CSR and blank initially in place answer
    var form = getValidForm();
    form.setInitiallyInPlaceInformationCorrect(null);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);

    // then no validation errors on empty question
    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenInvalidForm_thenValidationErrors_oilCase(ValidationType validationType) {
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isOilOnly(reservoirHydrocarbons)).thenReturn(true);

    var form = getInvalidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);

    assertTrue(bindingResult.hasErrors());

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenInvalidForm_thenValidationErrors_gasCase(ValidationType validationType) {
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isGasOnly(reservoirHydrocarbons)).thenReturn(true);

    var form = getInvalidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);

    assertTrue(bindingResult.hasErrors());

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenInvalidForm_thenValidationErrors_combinedCase(ValidationType validationType) {
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)).thenReturn(true);

    var form = getInvalidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);

    assertTrue(bindingResult.hasErrors());

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
        );
  }

  @Test
  void validatePartial_whenEmptyForm_thenNoValidationErrors_oilCase() {
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isOilOnly(reservoirHydrocarbons)).thenReturn(true);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validatePartial_whenEmptyForm_thenNoValidationErrors_gasCase() {
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isGasOnly(reservoirHydrocarbons)).thenReturn(true);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validatePartial_whenEmptyForm_thenNoValidationErrors_combinedCase() {
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)).thenReturn(true);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validateFull_whenEmptyForm_thenValidationErrors_oilCase() {
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isOilOnly(reservoirHydrocarbons)).thenReturn(true);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);

    assertTrue(bindingResult.hasErrors());

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
        );
  }

  @Test
  void validateFull_whenEmptyForm_thenValidationErrors_gasCase() {
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isGasOnly(reservoirHydrocarbons)).thenReturn(true);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);

    assertTrue(bindingResult.hasErrors());

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void validateFull_whenEmptyForm_thenValidationErrors_combinedCase_initiallyInPlaceQuestion(SubmissionType submissionType) {
    // when FDP, FDPA or CSR
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(submissionType);
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)).thenReturn(true);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);

    assertTrue(bindingResult.hasErrors());

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    // then validate the initially in place question
    assertThat(extractedErrors)
        .contains(
            entry("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("initiallyInPlaceInformationCorrect", Set.of("initiallyInPlaceInformationCorrect.required"))
        );
  }

  @Test
  void validateFull_whenEmptyForm_thenValidationErrors_combinedCase_noInitiallyInPlaceQuestion() {
    // when CSR
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.CREATE_CSR);
    var reservoirHydrocarbons = mock(ReservoirHydrocarbons.class);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    when(generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)).thenReturn(true);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);

    assertTrue(bindingResult.hasErrors());

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    // then do not validate the initially in place question
    assertThat(extractedErrors)
        .doesNotContainKey("initiallyInPlaceInformationCorrect")
        .contains(
            entry("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("oilInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error")),
            entry("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error",
                Set.of("gasInPlace." + ProbabilityDistributionType.CUSTOM.getPath() + ".error.error"))
        );
  }

  private FieldReservoirVolumetricsForm getValidForm() {
    var form = new FieldReservoirVolumetricsForm();

    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setP90(DecimalInput.createDecimalInput(1));
    probabilityOfExceedanceForm.setP50(DecimalInput.createDecimalInput(2));
    probabilityOfExceedanceForm.setMean(DecimalInput.createDecimalInput(3));
    probabilityOfExceedanceForm.setP10(DecimalInput.createDecimalInput(4));

    var hydrocarbonInPlace = new ProbabilityDistributionForm();
    hydrocarbonInPlace.setCustom(probabilityOfExceedanceForm);
    hydrocarbonInPlace.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);

    form.setOilInPlace(hydrocarbonInPlace);
    form.setGasInPlace(hydrocarbonInPlace);

    form.setInitiallyInPlaceInformationCorrect(Boolean.TRUE);

    return form;
  }

  private FieldReservoirVolumetricsForm getEmptyForm() {
    var form = new FieldReservoirVolumetricsForm();

    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();

    var hydrocarbonInPlace = new ProbabilityDistributionForm();
    hydrocarbonInPlace.setCustom(probabilityOfExceedanceForm);
    hydrocarbonInPlace.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);

    form.setOilInPlace(hydrocarbonInPlace);
    form.setGasInPlace(hydrocarbonInPlace);

    return form;
  }

  private FieldReservoirVolumetricsForm getInvalidForm() {
    var form = new FieldReservoirVolumetricsForm();

    var invalidDecimalInput = new DecimalInput("a");
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setP90(invalidDecimalInput);
    probabilityOfExceedanceForm.setP50(invalidDecimalInput);
    probabilityOfExceedanceForm.setMean(invalidDecimalInput);
    probabilityOfExceedanceForm.setP10(invalidDecimalInput);

    var hydrocarbonInPlace = new ProbabilityDistributionForm();
    hydrocarbonInPlace.setCustom(probabilityOfExceedanceForm);
    hydrocarbonInPlace.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);

    form.setOilInPlace(hydrocarbonInPlace);
    form.setGasInPlace(hydrocarbonInPlace);

    return form;
  }
}