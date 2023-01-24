package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class RelatedProspectsFormValidatorTest {

  @InjectMocks
  private RelatedProspectsFormValidator relatedProspectsFormValidator;

  static private SubmissionDetail submissionDetail;

  @BeforeAll
  static void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void supports_whenRelatedProspectsFormClass_thenTrue() {
    var supportedClass = RelatedProspectsForm.class;
    assertTrue(relatedProspectsFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotRelatedProspectsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(relatedProspectsFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidRelatedProspectsForm();

    var bindingResult = validateRelatedProspectsForm_fullValidation(form);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyForm_thenValidationErrors() {
    var form = new RelatedProspectsForm();
    var extractedErrors = ValidatorTestingUtil.extractErrors(validateRelatedProspectsForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("relatedToAnotherProspect", Set.of("relatedToAnotherProspect.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noRelatedToAnotherProspect_thenValidationErrors() {
    var form = getValidRelatedProspectsForm();
    form.setRelatedToAnotherProspect(null);

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateRelatedProspectsForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("relatedToAnotherProspect", Set.of("relatedToAnotherProspect.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noRelatedSubmissionDetail_thenValidationErrors() {
    var form = getValidRelatedProspectsForm();
    form.setRelatedToAnotherProspect(true);
    form.setSubmissionDetailIdOfRelatedProspect(null);
    form.setPreferredView(false);

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateRelatedProspectsForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("submissionDetailIdOfRelatedProspect", Set.of("submissionDetailIdOfRelatedProspect.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noPreferredView_thenValidationErrors() {
    var form = getValidRelatedProspectsForm();
    form.setRelatedToAnotherProspect(true);
    form.setSubmissionDetailIdOfRelatedProspect("1a3");
    form.setPreferredView(null);

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateRelatedProspectsForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("preferredView", Set.of("preferredView.required"))
        );
  }

  @Test
  void validate_whenEmptyFormAndPartialValidation_thenNoValidationErrors() {
    var form = new RelatedProspectsForm();
    var bindingResult = validateRelatedProspectsForm_partialValidation(form);
    assertFalse(bindingResult.hasErrors());
  }

  private BindingResult validateRelatedProspectsForm_fullValidation(RelatedProspectsForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    relatedProspectsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    return bindingResult;
  }

  private BindingResult validateRelatedProspectsForm_partialValidation(RelatedProspectsForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    relatedProspectsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.PARTIAL, submissionDetail));

    return bindingResult;
  }

  private RelatedProspectsForm getValidRelatedProspectsForm() {
    var form = new RelatedProspectsForm();
    form.setRelatedToAnotherProspect(false);
    return form;
  }
}