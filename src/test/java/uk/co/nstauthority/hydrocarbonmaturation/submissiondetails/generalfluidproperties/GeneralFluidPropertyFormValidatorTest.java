package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

class GeneralFluidPropertyFormValidatorTest {

  private GeneralFluidPropertyFormValidator generalFluidPropertyFormValidator;

  private SubmissionDetail submissionDetail;
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    generalFluidPropertyFormValidator = new GeneralFluidPropertyFormValidator();
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    generalFluidPropertyFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);

    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_invalidForm_isCop_thenValidationErrors(ValidationType validationType) {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    var form = new GeneralFluidPropertyForm();
    form.setWaterSalinity(new DecimalInput("-200"));
    form.setDatumDepth(new DecimalInput("-10"));
    form.setTemperatureAtDatumDepth(new DecimalInput("something"));
    form.setPressureAtDatumDepth(new DecimalInput("something"));
    form.setSaturationPressureAtDatumDepth(new DecimalInput("something"));
    form.setInitialTemperature(new DecimalInput("something"));
    form.setInitialPressure(new DecimalInput("-100"));
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    generalFluidPropertyFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("datumDepth.decimalInputValue", Set.of("datumDepth.decimalInputValue.lessThanMinValue")),
            entry("temperatureAtDatumDepth.decimalInputValue", Set.of("temperatureAtDatumDepth.decimalInputValue.notNumeric")),
            entry("pressureAtDatumDepth.decimalInputValue", Set.of("pressureAtDatumDepth.decimalInputValue.notNumeric")),
            entry("saturationPressureAtDatumDepth.decimalInputValue", Set.of("saturationPressureAtDatumDepth.decimalInputValue.notNumeric")),
            entry("waterSalinity.decimalInputValue", Set.of("waterSalinity.decimalInputValue.lessThanMinValue")),
            entry("initialPressure.decimalInputValue", Set.of("initialPressure.decimalInputValue.lessThanMinValue")),
            entry("initialTemperature.decimalInputValue", Set.of("initialTemperature.decimalInputValue.notNumeric"))
        );
  }

  @ParameterizedTest
  @EnumSource(
          value = SubmissionType.class,
          names = {"RELINQUISHMENT_DISCOVERY", "RELINQUISHMENT_PROSPECT", "CREATE_COP"},
          mode = EnumSource.Mode.EXCLUDE
  )
  void validate_fullValidationWhenEmptyForm_nonRelinquishment_thenValidationErrors(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    var form = new GeneralFluidPropertyForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    generalFluidPropertyFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
                entry("datumDepth.decimalInputValue", Set.of("datumDepth.decimalInputValue.required")),
                entry("temperatureAtDatumDepth.decimalInputValue", Set.of("temperatureAtDatumDepth.decimalInputValue.required")),
                entry("pressureAtDatumDepth.decimalInputValue", Set.of("pressureAtDatumDepth.decimalInputValue.required")),
                entry("saturationPressureAtDatumDepth.decimalInputValue", Set.of("saturationPressureAtDatumDepth.decimalInputValue.required")),
                entry("waterSalinity.decimalInputValue", Set.of("waterSalinity.decimalInputValue.required"))
        );
  }

  @ParameterizedTest
  @EnumSource(
          value = SubmissionType.class,
          names = {"RELINQUISHMENT_DISCOVERY", "RELINQUISHMENT_PROSPECT"},
          mode = EnumSource.Mode.INCLUDE
  )
  void validate_fullValidationWhenEmptyForm_relinquishment_thenValidationErrors(SubmissionType submissionType) {
    var form = new GeneralFluidPropertyForm();
    submissionDetail.setSubmissionType(submissionType);

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    generalFluidPropertyFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors).isEmpty();
  }

  private GeneralFluidPropertyForm getValidForm() {
    var form = new GeneralFluidPropertyForm();
    form.setWaterSalinity(new DecimalInput("1"));
    form.setDatumDepth(new DecimalInput("2"));
    form.setTemperatureAtDatumDepth(new DecimalInput("3"));
    form.setPressureAtDatumDepth(new DecimalInput("4"));
    form.setSaturationPressureAtDatumDepth(new DecimalInput("5"));
    form.setInitialPressure(new DecimalInput("6"));
    form.setInitialTemperature(new DecimalInput("7"));
    return form;
  }
}