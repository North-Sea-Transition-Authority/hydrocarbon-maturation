package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GeneralDetailsFormValidatorTest {

  private GeneralDetailsFormValidator generalDetailsFormValidator;

  @BeforeEach
  void setup() {
    generalDetailsFormValidator = new GeneralDetailsFormValidator();
  }

  @Test
  void supports_whenGeneralDetailsFormClass_thenTrue() {
    var supportedClass = GeneralDetailsForm.class;
    assertTrue(generalDetailsFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotGeneralDetailsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(generalDetailsFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidForm();
    form.setInformationSource(InformationSource.OTHER);
    form.setOtherSourceOfInformation("Other information");

    var bindingResult = validateGeneralDetailsForm(form, ValidationType.FULL);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInValidForm_thenValidationErrors() {
    var form = new GeneralDetailsForm();
    var bindingResult = validateGeneralDetailsForm(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("prospectName", Set.of("prospectName.required")),
            entry("onshoreOffshore", Set.of("onshoreOffshore.required")),
            entry("prospectLocatedInUkcs", Set.of("prospectLocatedInUkcs.required")),
            entry("informationSource", Set.of("informationSource.required")),
            entry("quadrantBlockSelect", Set.of("quadrantBlockSelect.notEmpty"))
        );
  }

  @Test
  void validate_whenInValidForm_noOtherSourceEntered_thenValidationErrors() {
    var form = getValidForm();
    form.setInformationSource(InformationSource.OTHER);

    var bindingResult = validateGeneralDetailsForm(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("otherSourceOfInformation", Set.of("otherSourceOfInformation.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noQuadrantBlock_thenValidationErrors() {
    var form = getValidForm();
    form.setQuadrantBlocks(Collections.emptyList());

    var bindingResult = validateGeneralDetailsForm(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("quadrantBlockSelect", Set.of("quadrantBlockSelect.notEmpty"))
        );
  }

  @ParameterizedTest
  @MethodSource("invalidPercentageArguments")
  void validate_whenInvalidForm_thenValidationErrors(
      String decimalInputValue,
      String errorCode)
  {
    var form = getValidForm();
    form.setPercentageLocatedInUkcs(new DecimalInput(decimalInputValue));

    var bindingResult = validateGeneralDetailsForm(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("percentageLocatedInUkcs.decimalInputValue", Set.of(errorCode))
        );
  }

  private static Stream<Arguments> invalidPercentageArguments() {
    return Stream.of(
        Arguments.of(null, "percentageLocatedInUkcs.decimalInputValue.required"),
        Arguments.of("abcde", "percentageLocatedInUkcs.decimalInputValue.notNumeric"),
        Arguments.of("-5", "percentageLocatedInUkcs.decimalInputValue.lessThanMinValue"),
        Arguments.of("101", "percentageLocatedInUkcs.decimalInputValue.moreThanMaxValue"),
        Arguments.of("10.567", "percentageLocatedInUkcs.decimalInputValue.incorrectDecimalPlaces")
    );
  }

  @Test
  void partialValidation_withValidForm() {
    var form = new GeneralDetailsForm();
    form.setProspectName("Prospect name");
    form.setProspectLocatedInUkcs(false);
    form.setPercentageLocatedInUkcs(new DecimalInput("50.0"));

    var bindingResult = validateGeneralDetailsForm(form, ValidationType.PARTIAL);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void partialValidation_noProspectName() {
    var form = new GeneralDetailsForm();

    var bindingResult = validateGeneralDetailsForm(form, ValidationType.PARTIAL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("prospectName", Set.of("prospectName.required"))
        );
  }

  @Test
  void partialValidation_prospectLocatedInUkcs_invalidPercentage() {
    var form = new GeneralDetailsForm();
    form.setProspectName("Prospect name");
    form.setProspectLocatedInUkcs(false);
    form.setPercentageLocatedInUkcs(new DecimalInput("abc"));
    var bindingResult = validateGeneralDetailsForm(form, ValidationType.PARTIAL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("percentageLocatedInUkcs.decimalInputValue", Set.of("percentageLocatedInUkcs.decimalInputValue.notNumeric"))
        );
  }

  private BindingResult validateGeneralDetailsForm(GeneralDetailsForm form, ValidationType validationType) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    generalDetailsFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }

  private GeneralDetailsForm getValidForm() {
    var form = new GeneralDetailsForm();
    form.setProspectName("Prospect name");
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    form.setProspectLocatedInUkcs(false);
    form.setPercentageLocatedInUkcs(new DecimalInput("50.0"));
    form.setInformationSource(InformationSource.WELL_INVESTMENT_DECISION);
    form.setQuadrantBlocks(List.of("27/10"));

    return form;
  }

  private static class NonSupportedClass {
  }
}