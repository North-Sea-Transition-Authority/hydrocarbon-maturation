package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

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
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

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
    var form = new GeneralDetailsForm();
    form.setProspectName("Prospect name");
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    form.setProspectLocatedInUkcs(false);
    form.setPercentageLocatedInUkcs("50");
    form.setInformationSource(InformationSource.OTHER);
    form.setOtherSourceOfInformation("Other information");

    var bindingResult = validateGeneralDetailsForm(form);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInValidForm_thenValidationErrors() {
    var form = new GeneralDetailsForm();
    var bindingResult = validateGeneralDetailsForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("prospectName", Set.of("prospectName.required")),
            entry("onshoreOffshore", Set.of("onshoreOffshore.required")),
            entry("prospectLocatedInUkcs", Set.of("prospectLocatedInUkcs.required")),
            entry("informationSource", Set.of("informationSource.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noPercentageEntered_thenValidationErrors() {
    var form = new GeneralDetailsForm();
    form.setProspectName("Prospect name");
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    form.setProspectLocatedInUkcs(false);
    form.setInformationSource(InformationSource.WELL_INVESTMENT_DECISION);

    var bindingResult = validateGeneralDetailsForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("percentageLocatedInUkcs", Set.of("percentageLocatedInUkcs.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noOtherSourceEntered_thenValidationErrors() {
    var form = new GeneralDetailsForm();
    form.setProspectName("Prospect name");
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    form.setProspectLocatedInUkcs(false);
    form.setPercentageLocatedInUkcs("50");
    form.setInformationSource(InformationSource.OTHER);

    var bindingResult = validateGeneralDetailsForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("otherSourceOfInformation", Set.of("otherSourceOfInformation.required"))
        );
  }

  private BindingResult validateGeneralDetailsForm(GeneralDetailsForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    generalDetailsFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  private static class NonSupportedClass {
  }
}