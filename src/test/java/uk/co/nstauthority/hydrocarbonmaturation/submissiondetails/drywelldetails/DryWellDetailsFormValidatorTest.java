package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class DryWellDetailsFormValidatorTest {

  private DryWellDetailsFormValidator dryWellDetailsFormValidator;

  @BeforeEach
  void setUp() {
    dryWellDetailsFormValidator = new DryWellDetailsFormValidator();
  }

  @Test
  void supports_whenDryWellDetailsFormClass_thenTrue() {
    var supportedClass = DryWellDetailsForm.class;
    assertTrue(dryWellDetailsFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenDryWellDetailsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(dryWellDetailsFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    dryWellDetailsFormValidator.validate(form, bindingResult, ValidationType.FULL);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyForm_thenValidationErrors() {
    var form = new DryWellDetailsForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    dryWellDetailsFormValidator.validate(form, bindingResult, ValidationType.FULL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("dryWell", Set.of("dryWell.required")),
            entry("coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue",
                Set.of("coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue.required")),
            entry("coordinateInputForm.longitudeCoordinates.minutes.decimalInputValue",
                Set.of("coordinateInputForm.longitudeCoordinates.minutes.decimalInputValue.required")),
            entry("reservoirDatum", Set.of("reservoirDatum.required")),
            entry("totalDepthFormation", Set.of("totalDepthFormation.required")),
            entry("dataAcquisitionProgramme", Set.of("dataAcquisitionProgramme.required")),
            entry("dryHoleType", Set.of("dryHoleType.required")),
            entry("dryHoleCost.decimalInputValue", Set.of("dryHoleCost.decimalInputValue.required"))
        );
  }

  @Test
  void validate_whenGivenFormPartialValidation_wrongCoordinateValues_thenValidationErrors() {
    var form = getValidForm();

    form.getCoordinateInputForm().setLatitudeCoordinates(CoordinateInput.createCoordinateInputForForm(
        BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE));
    form.getCoordinateInputForm().setLongitudeCoordinates(CoordinateInput.createCoordinateInputForForm(
        BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE));

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    dryWellDetailsFormValidator.validate(form, bindingResult, ValidationType.PARTIAL);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue",
                Set.of("coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue.lessThanMinValue"))
        );
  }

  private DryWellDetailsForm getValidForm() {
    var form = new DryWellDetailsForm();
    form.setDryWell("dryWellName");
    form.setDryHoleComments("dryHoleComments");
    form.setDryHoleScope("dryHoleScope");
    form.setReservoirDatum(ReservoirDatum.ED50);
    form.setDryHoleDescription("description");
    form.getCoordinateInputForm().setLatitudeCoordinates(
        CoordinateInput.createCoordinateInputForForm(BigDecimal.valueOf(46), BigDecimal.valueOf(35), BigDecimal.valueOf(35)));
    form.getCoordinateInputForm().setLongitudeCoordinates(
        CoordinateInput.createCoordinateInputForForm(BigDecimal.valueOf(25), BigDecimal.valueOf(25), BigDecimal.valueOf(25)));
    form.getCoordinateInputForm().setLongitudeHemisphere(Hemisphere.EAST);
    form.setDataAcquisitionProgramme("data");
    form.setGeophysicalUpdate("update");
    form.setTotalDepthFormation("formation");
    form.setDryHoleType(DryHoleType.GAS);
    form.setDryHoleCost(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setTotalDepthFormationComments("Depth comments");
    return form;
  }
}