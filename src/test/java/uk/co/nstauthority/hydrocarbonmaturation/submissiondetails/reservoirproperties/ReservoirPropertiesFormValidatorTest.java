package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class ReservoirPropertiesFormValidatorTest {

  private ReservoirPropertiesFormValidator reservoirPropertiesFormValidator;

  @BeforeEach
  void setup() {
    reservoirPropertiesFormValidator = new ReservoirPropertiesFormValidator();
  }

  @Test
  void supports_whenSourceAndSealFormClass_thenTrue() {
    var supportedClass = ReservoirPropertiesForm.class;
    assertTrue(reservoirPropertiesFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNonSupportedClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(reservoirPropertiesFormValidator.supports(nonSupportedClass));
  }


  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = new ReservoirPropertiesForm();
    form.setFormationName("source formation name");
    form.setAgeStageOrSeries("age stage");
    form.setLithology("lithology");
    var bindingResult = validateReservoirProperties(form);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInValidForm_thenValidationErrors() {
    var form = new ReservoirPropertiesForm();
    var bindingResult = validateReservoirProperties(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("formationName", Set.of("formationName.required")),
            entry("ageStageOrSeries", Set.of("ageStageOrSeries.required")),
            entry("lithology", Set.of("lithology.required"))
        );
  }

  private BindingResult validateReservoirProperties(ReservoirPropertiesForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    reservoirPropertiesFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}