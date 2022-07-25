package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

class OpportunityDetailsFormValidatorTest {

  private OpportunityDetailsFormValidator opportunityDetailsFormValidator;

  @BeforeEach
  void setup() {
    opportunityDetailsFormValidator = new OpportunityDetailsFormValidator();
  }

  private OpportunityDetailsForm getValidOpportunityDetailsForm() {
    var form = new OpportunityDetailsForm();
    form.setOpportunityType(OpportunityType.LEAD);
    form.setKeyEvaluationWorkCarriedOut("Work being carried out");
    form.setKeyWorkRequiredForFurtherMaturation("Key work");
    return form;
  }

  @Test
  void supports_whenOpportunityDetailsFormClass_thenTrue() {
    var supportedClass = OpportunityDetailsForm.class;
    assertTrue(opportunityDetailsFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotOpportunityDetailsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(opportunityDetailsFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    OpportunityDetailsForm form = getValidOpportunityDetailsForm();

    var bindingResult = validateOpportunityDetailsForm(form);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyForm_thenValidationErrors() {
    var form = new OpportunityDetailsForm();
    var extractedErrors = ValidatorTestingUtil.extractErrors(validateOpportunityDetailsForm(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("opportunityType", Set.of("opportunityType.required")),
            entry("keyEvaluationWorkCarriedOut", Set.of("keyEvaluationWorkCarriedOut.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noOpportunityTypeEntered_thenValidationErrors() {
    var form = getValidOpportunityDetailsForm();
    form.setOpportunityType(null);

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateOpportunityDetailsForm(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("opportunityType", Set.of("opportunityType.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noOtherTypeOfOpportunityEntered_thenValidationErrors() {
    var form = getValidOpportunityDetailsForm();
    form.setOpportunityType(OpportunityType.OTHER);
    form.setOtherTypeOfOpportunity("");

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateOpportunityDetailsForm(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("otherTypeOfOpportunity", Set.of("otherTypeOfOpportunity.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noKeyEvaluationWorkCarriedOutEntered_thenValidationErrors() {
    var form = getValidOpportunityDetailsForm();
    form.setKeyEvaluationWorkCarriedOut("");

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateOpportunityDetailsForm(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("keyEvaluationWorkCarriedOut", Set.of("keyEvaluationWorkCarriedOut.required"))
        );
  }

  private BindingResult validateOpportunityDetailsForm(OpportunityDetailsForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    opportunityDetailsFormValidator.validate(form, bindingResult);

    return bindingResult;
  }

}