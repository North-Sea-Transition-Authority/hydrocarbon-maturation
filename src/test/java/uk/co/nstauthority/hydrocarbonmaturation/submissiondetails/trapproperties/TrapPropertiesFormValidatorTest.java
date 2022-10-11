package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceForm;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class TrapPropertiesFormValidatorTest {

  private TrapPropertiesFormValidator trapPropertiesFormValidator;

  @BeforeEach
  void setup() {
    trapPropertiesFormValidator = new TrapPropertiesFormValidator();
  }

  @Test
  void supports_whenTrapPropertiesFormClass_thenTrue() {
    var supportedClass = TrapPropertiesForm.class;
    assertTrue(trapPropertiesFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotTrapPropertiesFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(trapPropertiesFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidForm();

    var bindingResult = validateTrapPropertiesForm(form);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInvalidForm_thenValidationErrors() {
    var form = new TrapPropertiesForm();
    var bindingResult = validateTrapPropertiesForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("trapDescription", Set.of("trapDescription.required")),
            entry("trapType", Set.of("trapType.required")),
            entry("latitudeCoordinates.degrees.decimalInputValue",
                Set.of("latitudeCoordinates.degrees.decimalInputValue.required")),
            entry("latitudeCoordinates.minutes.decimalInputValue",
                Set.of("latitudeCoordinates.minutes.decimalInputValue.required")),
            entry("latitudeCoordinates.seconds.decimalInputValue",
                Set.of("latitudeCoordinates.seconds.decimalInputValue.required")),
            entry("longitudeCoordinates.degrees.decimalInputValue",
                Set.of("longitudeCoordinates.degrees.decimalInputValue.required")),
            entry("longitudeCoordinates.minutes.decimalInputValue",
                Set.of("longitudeCoordinates.minutes.decimalInputValue.required")),
            entry("longitudeCoordinates.seconds.decimalInputValue",
                Set.of("longitudeCoordinates.seconds.decimalInputValue.required")),
            entry("longitudeHemisphere", Set.of("longitudeHemisphere.required")),
            entry("depthToCrest.probabilityDistributionType",
                Set.of("depthToCrest.probabilityDistributionType.required")),
            entry("depthToSpillPoint.probabilityDistributionType",
                Set.of("depthToSpillPoint.probabilityDistributionType.required")),
            entry("productiveArea.probabilityDistributionType",
                Set.of("productiveArea.probabilityDistributionType.required"))
        );
  }

  @Test
  void validate_whenInvalidForm_noOtherTypeEntered_thenValidationErrors() {
    var form = getValidForm();
    form.setTrapType(TrapType.OTHER);
    var bindingResult = validateTrapPropertiesForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("otherTrapType", Set.of("otherTrapType.required"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      names = {"CUSTOM"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void validate_whenEmptyExceedanceForm_thenValidationErrors(ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithEmptyDistributions(probabilityDistributionType);
    var bindingResult = validateTrapPropertiesForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("depthToCrest." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToCrest." + probabilityDistributionType.getPath() + ".error.error")),
            entry("depthToSpillPoint." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToSpillPoint." + probabilityDistributionType.getPath() + ".error.error")),
            entry("productiveArea." + probabilityDistributionType.getPath() + ".error",
                Set.of("productiveArea." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      names = {"CUSTOM"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void validate_whenInvalidExceedanceForm_thenValidationErrors(ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithInvalidDistributions(probabilityDistributionType);
    var bindingResult = validateTrapPropertiesForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("depthToCrest." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToCrest." + probabilityDistributionType.getPath() + ".error.error")),
            entry("depthToSpillPoint." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToSpillPoint." + probabilityDistributionType.getPath() + ".error.error")),
            entry("productiveArea." + probabilityDistributionType.getPath() + ".error",
                Set.of("productiveArea." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  private CoordinateInput getValidLongitudeCoordinates() {
    var coordinateInput = new CoordinateInput();
    coordinateInput.setDegrees(new DecimalInput("15"));
    coordinateInput.setMinutes(new DecimalInput("15"));
    coordinateInput.setSeconds(new DecimalInput("15"));

    return coordinateInput;
  }

  private CoordinateInput getValidLatitudeCoordinates() {
    var coordinateInput = new CoordinateInput();
    coordinateInput.setDegrees(new DecimalInput("45"));
    coordinateInput.setMinutes(new DecimalInput("15"));
    coordinateInput.setSeconds(new DecimalInput("15"));

    return coordinateInput;
  }

  private TrapPropertiesForm getValidForm() {
    var form = new TrapPropertiesForm();
    form.setTrapDescription("description");
    form.setTrapType(TrapType.COMBINATION);
    form.setLatitudeCoordinates(getValidLatitudeCoordinates());
    form.setLongitudeCoordinates(getValidLongitudeCoordinates());
    form.setLongitudeHemisphere(Hemisphere.EAST);
    form.setDepthToCrest(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setDepthToSpillPoint(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setProductiveArea(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    return form;
  }

  private TrapPropertiesForm getFormWithEmptyDistributions(ProbabilityDistributionType probabilityDistributionType) {
    var form = getValidForm();
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            new ProbabilityOfExceedanceForm());
    form.setDepthToCrest(probabilityDistributionForm);
    form.setDepthToSpillPoint(probabilityDistributionForm);
    form.setProductiveArea(probabilityDistributionForm);
    return form;
  }

  private TrapPropertiesForm getFormWithInvalidDistributions(ProbabilityDistributionType probabilityDistributionType) {
    var form = getValidForm();
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            ProbabilityDistributionTestUtil.getInvalidProbabilityOfExceedanceForm());
    form.setDepthToCrest(probabilityDistributionForm);
    form.setDepthToSpillPoint(probabilityDistributionForm);
    form.setProductiveArea(probabilityDistributionForm);
    return form;
  }

  private BindingResult validateTrapPropertiesForm(TrapPropertiesForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    trapPropertiesFormValidator.validate(form, bindingResult, ValidationType.FULL);
    return bindingResult;
  }

  private static class NonSupportedClass {
  }
}