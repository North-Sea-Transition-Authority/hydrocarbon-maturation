package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class SourceAndSealFormValidatorTest {

  private SourceAndSealFormValidator sourceAndSealFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    sourceAndSealFormValidator = new SourceAndSealFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void supports_whenSourceAndSealFormClass_thenTrue() {
    var supportedClass = SourceAndSealForm.class;
    assertTrue(sourceAndSealFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNonSupportedClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(sourceAndSealFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getPopulatedSourceAndSealForm();
    var bindingResult = validateSourceAndSealForm_fullValidation(form);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInValidForm_thenValidationErrors() {
    var form = new SourceAndSealForm();
    var bindingResult = validateSourceAndSealForm_fullValidation(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("sourceFormationName", Set.of("sourceFormationName.required")),
            entry("sourceAgeStage", Set.of("sourceAgeStage.required")),
            entry("sourceGroupName", Set.of("sourceGroupName.required")),
            entry("topSealFormationName", Set.of("topSealFormationName.required")),
            entry("topSealAgeStage", Set.of("topSealAgeStage.required")),
            entry("topSealGroupName", Set.of("topSealGroupName.required")),
            entry("topSealLithology", Set.of("topSealLithology.required"))
        );
  }

  @Test
  void validate_whenOtherLithologyNotProvided_thenValidationErrors() {
    var form = getPopulatedSourceAndSealForm();
    form.setTopSealLithology(TopSealLithology.OTHER);
    var bindingResult = validateSourceAndSealForm_fullValidation(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("topSealLithologyOther", Set.of("topSealLithologyOther.required"))
        );
  }

  @Test
  void validate_whenInValidFormAndPartialValidation_thenNoValidationErrors() {
    var form = new SourceAndSealForm();
    var bindingResult = validateSourceAndSealForm_partialValidation(form);

    assertFalse(bindingResult.hasErrors());
  }


  private BindingResult validateSourceAndSealForm_fullValidation(SourceAndSealForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    sourceAndSealFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));
    return bindingResult;
  }

  private BindingResult validateSourceAndSealForm_partialValidation(SourceAndSealForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    sourceAndSealFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.PARTIAL, submissionDetail));
    return bindingResult;
  }

  private SourceAndSealForm getPopulatedSourceAndSealForm() {
    var form = new SourceAndSealForm();
    form.setSourceFormationName("source formation name");
    form.setTopSealFormationName("top seal formation name");
    form.setSourceAgeStage(AgeSeries.LOWER_CARBONIFEROUS);
    form.setTopSealAgeStage(AgeSeries.LOWER_CRETACEOUS);
    form.setSourceGroupName(GroupName.BRENT);
    form.setTopSealGroupName(GroupName.CLAIR);
    form.setTopSealLithology(TopSealLithology.CEMENTED_SANDSTONE);
    return form;
  }
}