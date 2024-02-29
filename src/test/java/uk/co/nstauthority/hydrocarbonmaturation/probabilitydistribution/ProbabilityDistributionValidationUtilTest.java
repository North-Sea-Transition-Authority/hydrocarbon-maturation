package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

class ProbabilityDistributionValidationUtilTest {

  @Test
  void validateRequiredDistributionType_noTypeSelected_thenErrors() {
    var formField = "dummy";
    var form = new DummyForm();
    var errors = new BeanPropertyBindingResult(form, "form");

    ProbabilityDistributionValidationUtil.validateRequiredDistributionType(
        errors,
        formField,
        "dummy"
    );

    assertTrue(errors.hasErrors());
    assertThat(errors.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .containsExactly("Select the distribution used for dummy");
  }

  @Test
  void validateRequiredDistributionType_typeSelected_thenNoErrors() {
    var formField = "dummy";
    var form = new DummyForm();

    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    form.setDummy(probabilityDistributionForm);

    var errors = new BeanPropertyBindingResult(form, "form");

    ProbabilityDistributionValidationUtil.validateRequiredDistributionType(
        errors,
        formField,
        "dummy"
    );

    assertFalse(errors.hasErrors());
  }

  @Test
  void validateInput_FullValidation_RequiredField() {
    var isAvailable = true;
    var isRequired = true;
    var formField = "dummy";
    var form = new DummyForm();
    var typePath = ProbabilityDistributionType.NORMAL.getPath();
    var inputPath = ProbabilityOfExceedanceType.MIN.getPath();
    var input = DecimalInput.createDecimalInput(1);
    var units = ProbabilityOfExceedanceUnits.M_TVDSS;
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
    var units = ProbabilityOfExceedanceUnits.M_TVDSS;
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
    var units = ProbabilityOfExceedanceUnits.M_TVDSS;
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

  @Test
  void validateMinMax_wrongOrder() {
    var dummyForm = new DummyForm();
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.RECTANGULAR);
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setMin(DecimalInput.createDecimalInput(100));
    probabilityOfExceedanceForm.setMax(DecimalInput.createDecimalInput(50));
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);
    dummyForm.setDummy(probabilityDistributionForm);
    var errors = new BeanPropertyBindingResult(dummyForm, "form");
    ProbabilityDistributionValidationUtil.validateFull(
        dummyForm.getDummy(),
        errors,
        "dummy",
        "dummy"
    );
    assertThat(errors.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(ProbabilityDistributionValidationError.MIN_MAX.getErrorMessage().formatted("dummy"));
  }

  @Test
  void validateMinMax_correctOrder() {
    var dummyForm = new DummyForm();
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.RECTANGULAR);
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setMin(DecimalInput.createDecimalInput(10));
    probabilityOfExceedanceForm.setMax(DecimalInput.createDecimalInput(50));
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);
    dummyForm.setDummy(probabilityDistributionForm);
    var errors = new BeanPropertyBindingResult(dummyForm, "form");
    ProbabilityDistributionValidationUtil.validateFull(
        dummyForm.getDummy(),
        errors,
        "dummy",
        "dummy"
    );
    assertThat(errors.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .doesNotContain(ProbabilityDistributionValidationError.MIN_MAX.getErrorMessage().formatted("dummy"));
  }

  @Test
  void validateMinMax_allowEquivalentValues_wrongOrder() {
    var dummyForm = new DummyForm();
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setMin(DecimalInput.createDecimalInput(100));
    probabilityOfExceedanceForm.setMostLikely(DecimalInput.createDecimalInput(10));
    probabilityOfExceedanceForm.setMax(DecimalInput.createDecimalInput(50));
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);
    dummyForm.setDummy(probabilityDistributionForm);
    var errors = new BeanPropertyBindingResult(dummyForm, "form");
    ProbabilityDistributionValidationUtil.validateCustomFull(
        dummyForm.getDummy(),
        errors,
        "dummy",
        "dummy",
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD
    );
    assertThat(errors.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(ProbabilityDistributionValidationError.MIN_MAX_ALLOW_EQUIVALENT.getErrorMessage().formatted("dummy"));
  }

  @Test
  void validateMinMax_allowEquivalentValues_correctOrder() {
    var dummyForm = new DummyForm();
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setMin(DecimalInput.createDecimalInput(50));
    probabilityOfExceedanceForm.setMostLikely(DecimalInput.createDecimalInput(10));
    probabilityOfExceedanceForm.setMax(DecimalInput.createDecimalInput(50));
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);
    dummyForm.setDummy(probabilityDistributionForm);
    var errors = new BeanPropertyBindingResult(dummyForm, "form");
    ProbabilityDistributionValidationUtil.validateCustomFull(
        dummyForm.getDummy(),
        errors,
        "dummy",
        "dummy",
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD
    );
    assertThat(errors.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .doesNotContain(ProbabilityDistributionValidationError.MIN_MAX_ALLOW_EQUIVALENT.getErrorMessage().formatted("dummy"));
  }

  @Test
  void validateP99ToP1_wrongOrder() {
    var dummyForm = new DummyForm();
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.NORMAL);
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setP99(DecimalInput.createDecimalInput(100));
    probabilityOfExceedanceForm.setP90(DecimalInput.createDecimalInput(90));
    probabilityOfExceedanceForm.setP50(DecimalInput.createDecimalInput(80));
    probabilityOfExceedanceForm.setP10(DecimalInput.createDecimalInput(70));
    probabilityOfExceedanceForm.setP1(DecimalInput.createDecimalInput(60));
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);
    dummyForm.setDummy(probabilityDistributionForm);
    var errors = new BeanPropertyBindingResult(dummyForm, "form");
    ProbabilityDistributionValidationUtil.validateFull(
        dummyForm.getDummy(),
        errors,
        "dummy",
        "dummy"
    );
    assertThat(errors.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(ProbabilityDistributionValidationError.P99_TO_P1.getErrorMessage().formatted("dummy"));
  }

  @Test
  void validateP99ToP1_correctOrder() {
    var dummyForm = new DummyForm();
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.NORMAL);
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setP99(DecimalInput.createDecimalInput(60));
    probabilityOfExceedanceForm.setP90(DecimalInput.createDecimalInput(70));
    probabilityOfExceedanceForm.setP50(DecimalInput.createDecimalInput(80));
    probabilityOfExceedanceForm.setP10(DecimalInput.createDecimalInput(90));
    probabilityOfExceedanceForm.setP1(DecimalInput.createDecimalInput(100));
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);
    dummyForm.setDummy(probabilityDistributionForm);
    var errors = new BeanPropertyBindingResult(dummyForm, "form");
    ProbabilityDistributionValidationUtil.validateFull(
        dummyForm.getDummy(),
        errors,
        "dummy",
        "dummy"
    );
    assertThat(errors.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .doesNotContain(ProbabilityDistributionValidationError.P99_TO_P1.getErrorMessage().formatted("dummy"));
  }

  @Test
  void validateP90ToP10_wrongOrder() {
    var dummyForm = new DummyForm();
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setP90(DecimalInput.createDecimalInput(90));
    probabilityOfExceedanceForm.setP50(DecimalInput.createDecimalInput(80));
    probabilityOfExceedanceForm.setMean(DecimalInput.createDecimalInput(80));
    probabilityOfExceedanceForm.setP10(DecimalInput.createDecimalInput(70));
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);
    dummyForm.setDummy(probabilityDistributionForm);
    var errors = new BeanPropertyBindingResult(dummyForm, "form");
    ProbabilityDistributionValidationUtil.validateCustomFull(
        dummyForm.getDummy(),
        errors,
        "dummy",
        "dummy",
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
    );
    assertThat(errors.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(ProbabilityDistributionValidationError.P90_TO_P10.getErrorMessage().formatted("dummy"));
  }

  @Test
  void validateP90ToP10_correctOrder() {
    var dummyForm = new DummyForm();
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setP90(DecimalInput.createDecimalInput(70));
    probabilityOfExceedanceForm.setP50(DecimalInput.createDecimalInput(80));
    probabilityOfExceedanceForm.setMean(DecimalInput.createDecimalInput(80));
    probabilityOfExceedanceForm.setP10(DecimalInput.createDecimalInput(90));
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);
    dummyForm.setDummy(probabilityDistributionForm);
    var errors = new BeanPropertyBindingResult(dummyForm, "form");
    ProbabilityDistributionValidationUtil.validateCustomFull(
        dummyForm.getDummy(),
        errors,
        "dummy",
        "dummy",
        ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY
    );
    assertThat(errors.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .doesNotContain(ProbabilityDistributionValidationError.P90_TO_P10.getErrorMessage().formatted("dummy"));
  }

  @Test
  void validateP90ToP10_allowEquivalentValues_wrongOrder() {
    var dummyForm = new DummyForm();
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setP90(DecimalInput.createDecimalInput(90));
    probabilityOfExceedanceForm.setP50(DecimalInput.createDecimalInput(80));
    probabilityOfExceedanceForm.setMean(DecimalInput.createDecimalInput(80));
    probabilityOfExceedanceForm.setP10(DecimalInput.createDecimalInput(70));
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);
    dummyForm.setDummy(probabilityDistributionForm);
    var errors = new BeanPropertyBindingResult(dummyForm, "form");
    ProbabilityDistributionValidationUtil.validateCustomFull(
        dummyForm.getDummy(),
        errors,
        "dummy",
        "dummy",
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );
    assertThat(errors.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(ProbabilityDistributionValidationError.P90_TO_P10_ALLOW_EQUIVALENT.getErrorMessage().formatted("dummy"));
  }

  @Test
  void validateP90ToP10_allowEquivalentValues_correctOrder() {
    var dummyForm = new DummyForm();
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setP90(DecimalInput.createDecimalInput(70));
    probabilityOfExceedanceForm.setP50(DecimalInput.createDecimalInput(80));
    probabilityOfExceedanceForm.setMean(DecimalInput.createDecimalInput(80));
    probabilityOfExceedanceForm.setP10(DecimalInput.createDecimalInput(90));
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);
    dummyForm.setDummy(probabilityDistributionForm);
    var errors = new BeanPropertyBindingResult(dummyForm, "form");
    ProbabilityDistributionValidationUtil.validateCustomFull(
        dummyForm.getDummy(),
        errors,
        "dummy",
        "dummy",
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    );
    assertThat(errors.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .doesNotContain(ProbabilityDistributionValidationError.P90_TO_P10_ALLOW_EQUIVALENT.getErrorMessage().formatted("dummy"));
  }

  @Test
  void validate1C2C3C_wrongOrder() {
    var dummyForm = new DummyForm();
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setOneC(DecimalInput.createDecimalInput(3));
    probabilityOfExceedanceForm.setTwoC(DecimalInput.createDecimalInput(2));
    probabilityOfExceedanceForm.setThreeC(DecimalInput.createDecimalInput(1));
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);
    dummyForm.setDummy(probabilityDistributionForm);
    var errors = new BeanPropertyBindingResult(dummyForm, "form");
    ProbabilityDistributionValidationUtil.validateCustomFull(
        dummyForm.getDummy(),
        errors,
        "dummy",
        "dummy",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD
    );
    assertThat(errors.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(ProbabilityDistributionValidationError.ONE_C_TO_THREE_C.getErrorMessage().formatted("dummy"));
  }

  @Test
  void validate1C2C3C_correctOrder() {
    var dummyForm = new DummyForm();
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setOneC(DecimalInput.createDecimalInput(1));
    probabilityOfExceedanceForm.setTwoC(DecimalInput.createDecimalInput(2));
    probabilityOfExceedanceForm.setThreeC(DecimalInput.createDecimalInput(3));
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);
    dummyForm.setDummy(probabilityDistributionForm);
    var errors = new BeanPropertyBindingResult(dummyForm, "form");
    ProbabilityDistributionValidationUtil.validateCustomFull(
        dummyForm.getDummy(),
        errors,
        "dummy",
        "dummy",
        ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD
    );
    assertThat(errors.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .doesNotContain(ProbabilityDistributionValidationError.ONE_C_TO_THREE_C.getErrorMessage().formatted("dummy"));
  }

  @Test
  void validate1P2P3P_wrongOrder() {
    var dummyForm = new DummyForm();
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setOneP(DecimalInput.createDecimalInput(3));
    probabilityOfExceedanceForm.setTwoP(DecimalInput.createDecimalInput(2));
    probabilityOfExceedanceForm.setThreeP(DecimalInput.createDecimalInput(1));
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);
    dummyForm.setDummy(probabilityDistributionForm);
    var errors = new BeanPropertyBindingResult(dummyForm, "form");
    ProbabilityDistributionValidationUtil.validateCustomFull(
        dummyForm.getDummy(),
        errors,
        "dummy",
        "dummy",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
    );
    assertThat(errors.getAllErrors())
            .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
            .contains(ProbabilityDistributionValidationError.ONE_P_TO_THREE_P.getErrorMessage().formatted("dummy"));
  }

  @Test
  void validate1P2P3P_correctOrder() {
    var dummyForm = new DummyForm();
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CUSTOM);
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setOneP(DecimalInput.createDecimalInput(1));
    probabilityOfExceedanceForm.setTwoP(DecimalInput.createDecimalInput(2));
    probabilityOfExceedanceForm.setThreeP(DecimalInput.createDecimalInput(3));
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);
    dummyForm.setDummy(probabilityDistributionForm);
    var errors = new BeanPropertyBindingResult(dummyForm, "form");
    ProbabilityDistributionValidationUtil.validateCustomFull(
        dummyForm.getDummy(),
        errors,
        "dummy",
        "dummy",
        ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD
    );
    assertThat(errors.getAllErrors())
            .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
            .doesNotContain(ProbabilityDistributionValidationError.ONE_P_TO_THREE_P.getErrorMessage().formatted("dummy"));
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