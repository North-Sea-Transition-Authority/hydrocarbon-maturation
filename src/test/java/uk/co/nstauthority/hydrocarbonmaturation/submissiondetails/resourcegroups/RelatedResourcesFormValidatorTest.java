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
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class RelatedResourcesFormValidatorTest {

  @InjectMocks
  private RelatedResourcesFormValidator relatedResourcesFormValidator;

  static private SubmissionDetail submissionDetail;

  @BeforeAll
  static void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void supports_whenRelatedProspectsFormClass_thenTrue() {
    var supportedClass = RelatedResourcesForm.class;
    assertTrue(relatedResourcesFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotRelatedProspectsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(relatedResourcesFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidRelatedProspectsForm();

    var bindingResult = validateRelatedProspectsForm_fullValidation(form);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyForm_thenValidationErrors() {
    var form = new RelatedResourcesForm();
    var extractedErrors = ValidatorTestingUtil.extractErrors(validateRelatedProspectsForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("relatedToAnotherResource", Set.of("relatedToAnotherResource.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noRelatedToAnotherProspect_thenValidationErrors() {
    var form = getValidRelatedProspectsForm();
    form.setRelatedToAnotherResource(null);

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateRelatedProspectsForm_fullValidation(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("relatedToAnotherResource", Set.of("relatedToAnotherResource.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noRelatedSubmissionDetail_thenValidationErrors() {
    submissionDetail.getSubmission().getResource().setType(ResourceType.PROSPECT);
    var form = getValidRelatedProspectsForm();
    form.setRelatedToAnotherResource(true);
    form.setSubmissionDetailIdOfRelatedResource(null);
    form.setPreferredView(false);

    var bindingResult = validateRelatedProspectsForm_fullValidation(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("submissionDetailIdOfRelatedResource", Set.of("submissionDetailIdOfRelatedResource.required"))
        );
    assertThat(extractedMessages)
        .containsExactly(
            entry("submissionDetailIdOfRelatedResource", Set.of("Select a prospect or prospect group"))
        );
  }

  @Test
  void validate_whenInValidForm_noPreferredView_thenValidationErrors() {
    submissionDetail.getSubmission().getResource().setType(ResourceType.PROSPECT);
    var form = getValidRelatedProspectsForm();
    form.setRelatedToAnotherResource(true);
    form.setSubmissionDetailIdOfRelatedResource("1a3");
    form.setPreferredView(null);

    var bindingResult = validateRelatedProspectsForm_fullValidation(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("preferredView", Set.of("preferredView.required"))
        );

    assertThat(extractedMessages)
        .containsExactly(
            entry("preferredView", Set.of("Select whether this is the NSTA preferred view of the prospect?"))
        );
  }

  @Test
  void validate_whenDiscovery_noPreferredView_thenValidationErrors_assertResourceType() {
    submissionDetail.getSubmission().getResource().setType(ResourceType.DISCOVERY);
    var form = getValidRelatedProspectsForm();
    form.setRelatedToAnotherResource(true);
    form.setSubmissionDetailIdOfRelatedResource(null);
    form.setPreferredView(null);
    var bindingResult = validateRelatedProspectsForm_fullValidation(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("submissionDetailIdOfRelatedResource", Set.of("submissionDetailIdOfRelatedResource.required")),
            entry("preferredView", Set.of("preferredView.required"))
        );

    assertThat(extractedMessages)
        .containsExactly(
            entry("submissionDetailIdOfRelatedResource", Set.of("Select a discovery or discovery group")),
            entry("preferredView", Set.of("Select whether this is the NSTA preferred view of the discovery?"))
        );
  }

  @Test
  void validate_whenEmptyFormAndPartialValidation_thenNoValidationErrors() {
    var form = new RelatedResourcesForm();
    var bindingResult = validateRelatedProspectsForm_partialValidation(form);
    assertFalse(bindingResult.hasErrors());
  }

  private BindingResult validateRelatedProspectsForm_fullValidation(RelatedResourcesForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    relatedResourcesFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    return bindingResult;
  }

  private BindingResult validateRelatedProspectsForm_partialValidation(RelatedResourcesForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    relatedResourcesFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.PARTIAL, submissionDetail));

    return bindingResult;
  }

  private RelatedResourcesForm getValidRelatedProspectsForm() {
    var form = new RelatedResourcesForm();
    form.setRelatedToAnotherResource(false);
    return form;
  }
}