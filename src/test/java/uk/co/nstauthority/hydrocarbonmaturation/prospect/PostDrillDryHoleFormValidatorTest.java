package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.PostDrillDryHoleForm;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.PostDrillDryHoleFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class PostDrillDryHoleFormValidatorTest {

  private final PostDrillDryHoleFormValidator postDrillDryHoleFormValidator = new PostDrillDryHoleFormValidator();

  @Test
  void supports_whenPostDrillDryHoleForm_thenTrue() {
    var supportedClass = PostDrillDryHoleForm.class;
    assertTrue(postDrillDryHoleFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenPostDrillDryHoleForm_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(postDrillDryHoleFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    postDrillDryHoleFormValidator.validate(form, bindingResult);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyFields_thenValidationErrors() {
    var form = getValidForm();
    form.setProspectSelectedOption(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    postDrillDryHoleFormValidator.validate(form, bindingResult);

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("prospectSelectedOption", Set.of("prospectOption.required"))
        );
  }

  private PostDrillDryHoleForm getValidForm() {
    var form = new PostDrillDryHoleForm();
    form.setProspectSelectedOption(UUID.randomUUID().toString());

    return form;
  }
}