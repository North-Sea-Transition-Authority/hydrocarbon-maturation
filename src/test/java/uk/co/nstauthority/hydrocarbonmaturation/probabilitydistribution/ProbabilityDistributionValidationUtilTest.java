package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

class ProbabilityDistributionValidationUtilTest {

  @Test
  void validateInput_FullValidation_RequiredField() {
    var isAvailable = true;
    var isRequired = true;
    var formField = "dummy";
    var form = new DummyForm();
    var typePath = ProbabilityDistributionType.NORMAL.getPath();
    var inputPath = ProbabilityOfExceedanceType.MIN.getPath();
    var input = DecimalInput.createDecimalInput(1);
    var units = ProbabilityOfExceedanceUnits.FT_TVDSS;
    var validationType = ValidationType.FULL;
    var errors = new BeanPropertyBindingResult(form, "form");

    ProbabilityDistributionValidationUtil.validateInput(
        isAvailable,
        isRequired,
        formField,
        typePath,
        inputPath,
        input,
        units,
        validationType,
        errors
    );

    assertThat(errors.getAllErrors())
        .extracting(
            DefaultMessageSourceResolvable::getCode
        )
        .containsExactly(
            "dummy.normal.min.decimalInputValue.required"
        );
  }

  @Test
  void validateInput_FullValidation_NotRequiredField() {
    var isAvailable = true;
    var isRequired = false;
    var formField = "dummy";
    var form = new DummyForm();
    var typePath = ProbabilityDistributionType.NORMAL.getPath();
    var inputPath = ProbabilityOfExceedanceType.MIN.getPath();
    var input = DecimalInput.createDecimalInput(1);
    var units = ProbabilityOfExceedanceUnits.FT_TVDSS;
    var validationType = ValidationType.FULL;
    var errors = new BeanPropertyBindingResult(form, "form");

    ProbabilityDistributionValidationUtil.validateInput(
        isAvailable,
        isRequired,
        formField,
        typePath,
        inputPath,
        input,
        units,
        validationType,
        errors
    );

    assertThat(errors.getAllErrors()).isEmpty();
  }

  @Test
  void validateInput_PartialValidation_RequiredField() {
    var isAvailable = true;
    var isRequired = true;
    var formField = "dummy";
    var form = new DummyForm();
    var typePath = ProbabilityDistributionType.NORMAL.getPath();
    var inputPath = ProbabilityOfExceedanceType.MIN.getPath();
    var input = DecimalInput.createDecimalInput(1);
    var units = ProbabilityOfExceedanceUnits.FT_TVDSS;
    var validationType = ValidationType.PARTIAL;
    var errors = new BeanPropertyBindingResult(form, "form");

    ProbabilityDistributionValidationUtil.validateInput(
        isAvailable,
        isRequired,
        formField,
        typePath,
        inputPath,
        input,
        units,
        validationType,
        errors
    );

    assertThat(errors.getAllErrors()).isEmpty();
  }

  @Test
  void validateInput_NotAvailable_NotValidated() {
    var isAvailable = false;
    var isRequired = true;
    var form = new DummyForm();
    var errors = new BeanPropertyBindingResult(form, "form");

    ProbabilityDistributionValidationUtil.validateInput(
        isAvailable,
        isRequired,
        null,
        null,
        null,
        null,
        null,
        null,
        errors
    );

    assertThat(errors.getAllErrors()).isEmpty();
  }

  static class DummyForm {
    private ProbabilityDistributionForm dummy;

    public ProbabilityDistributionForm getDummy() {
      return dummy;
    }

    public void setDummy(ProbabilityDistributionForm dummy) {
      this.dummy = dummy;
    }
  }
}