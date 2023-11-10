package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
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
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);
    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenInvalidForm_thenValidationErrors_oilCase(ValidationType validationType) {
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)).thenReturn(true);

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
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)).thenReturn(true);

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

  @Test
  void validatePartial_whenEmptyForm_thenNoValidationErrors_oilCase() {
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)).thenReturn(true);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validatePartial_whenEmptyForm_thenNoValidationErrors_gasCase() {
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)).thenReturn(true);

    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validateFull_whenEmptyForm_thenValidationErrors_oilCase() {
    when(generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)).thenReturn(true);

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
    when(generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)).thenReturn(true);

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