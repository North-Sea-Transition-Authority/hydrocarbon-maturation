package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StandardLithology;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
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
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getPopulatedSourceAndSealForm();
    var bindingResult = validateSourceAndSealForm_fullValidation(form);

    assertFalse(bindingResult.hasErrors());
  }

  // Non field submission tests

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void validateFull_whenEmptyForm_nonField_thenValidationErrors(SubmissionType submissionType) {
    var form = new SourceAndSealForm();
    submissionDetail.setSubmissionType(submissionType);
    var bindingResult = validateSourceAndSealForm_fullValidation(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("sourceAgeStage", Set.of("sourceAgeStage.required")),
            entry("sourceGroupName", Set.of("sourceGroupName.required")),
            entry("sourceFormationName", Set.of("sourceFormationName.required")),
            entry("sourceLithology", Set.of("sourceLithology.required")),
            entry("topSealAgeStage", Set.of("topSealAgeStage.required")),
            entry("topSealGroupName", Set.of("topSealGroupName.required")),
            entry("topSealFormationName", Set.of("topSealFormationName.required")),
            entry("topSealLithology", Set.of("topSealLithology.required"))
        );
  }

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void validatePartial_whenEmptyForm_nonField_thenNoValidationErrors(SubmissionType submissionType) {
    var form = new SourceAndSealForm();
    submissionDetail.setSubmissionType(submissionType);
    var bindingResult = validateSourceAndSealForm_partialValidation(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void validateFull_whenInvalidForm_nonField_thenValidationErrors(SubmissionType submissionType) {
    var form = getInvalidForm();
    var errors = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    assertFalse(sourceAndSealFormValidator.isValid(form, errors, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(extractedErrors)
        .containsExactly(
            entry("sourceAgeStage", Set.of("sourceAgeStage.required")),
            entry("sourceGroupName", Set.of("sourceGroupName.required")),
            entry("sourceFormationName", Set.of("sourceFormationName.required")),
            entry("sourceLithologyOther", Set.of("sourceLithologyOther.required")),
            entry("topSealAgeStage", Set.of("topSealAgeStage.required")),
            entry("topSealGroupName", Set.of("topSealGroupName.required")),
            entry("topSealFormationName", Set.of("topSealFormationName.required")),
            entry("topSealLithologyOther", Set.of("topSealLithologyOther.required"))
        ).doesNotContainKeys(
            "sealThickness.decimalInputValue",
            "topSealFracturePressure.decimalInputValue"
        );
  }

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void validatePartial_whenInvalidForm_nonField_thenValidationErrors(SubmissionType submissionType) {
    var form = getInvalidForm();

    var errors = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    assertTrue(sourceAndSealFormValidator.isValid(form, errors, submissionDetail, ValidationType.PARTIAL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(errors);
    assertThat(extractedErrors).isEmpty();
  }

  // Field submission tests

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void validateFull_whenEmptyForm_field_thenValidationErrors(SubmissionType submissionType) {
    var form = new SourceAndSealForm();
    submissionDetail.setSubmissionType(submissionType);
    var bindingResult = validateSourceAndSealForm_fullValidation(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("sourceAgeStage", Set.of("sourceAgeStage.required")),
            entry("sourceGroupName", Set.of("sourceGroupName.required")),
            entry("sourceFormationName", Set.of("sourceFormationName.required")),
            entry("sourceLithology", Set.of("sourceLithology.required")),
            entry("topSealAgeStage", Set.of("topSealAgeStage.required")),
            entry("topSealGroupName", Set.of("topSealGroupName.required")),
            entry("topSealFormationName", Set.of("topSealFormationName.required")),
            entry("topSealLithology", Set.of("topSealLithology.required")),
            entry("sealThickness.decimalInputValue", Set.of("sealThickness.decimalInputValue.required"))
        );
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void validatePartial_whenEmptyForm_field_thenNoValidationErrors(SubmissionType submissionType) {
    var form = new SourceAndSealForm();
    submissionDetail.setSubmissionType(submissionType);
    var bindingResult = validateSourceAndSealForm_partialValidation(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void validateFull_whenInvalidForm_field_thenValidationErrors(SubmissionType submissionType) {
    var form = getInvalidForm();
    var errors = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    assertFalse(sourceAndSealFormValidator.isValid(form, errors, submissionDetail, ValidationType.FULL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(extractedErrors)
        .containsExactly(
            entry("sourceAgeStage", Set.of("sourceAgeStage.required")),
            entry("sourceGroupName", Set.of("sourceGroupName.required")),
            entry("sourceFormationName", Set.of("sourceFormationName.required")),
            entry("sourceLithologyOther", Set.of("sourceLithologyOther.required")),
            entry("topSealAgeStage", Set.of("topSealAgeStage.required")),
            entry("topSealGroupName", Set.of("topSealGroupName.required")),
            entry("topSealFormationName", Set.of("topSealFormationName.required")),
            entry("topSealLithologyOther", Set.of("topSealLithologyOther.required")),
            entry("sealThickness.decimalInputValue", Set.of("sealThickness.decimalInputValue.notNumeric")),
            entry("topSealFracturePressure.decimalInputValue", Set.of("topSealFracturePressure.decimalInputValue.notNumeric"))
        );
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void validatePartial_whenInvalidForm_field_thenValidationErrors(SubmissionType submissionType) {
    var form = getInvalidForm();

    var errors = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    assertFalse(sourceAndSealFormValidator.isValid(form, errors, submissionDetail, ValidationType.PARTIAL));

    var extractedErrors = ValidatorTestingUtil.extractErrors(errors);

    assertThat(extractedErrors)
        .containsExactly(
            entry("sealThickness.decimalInputValue", Set.of("sealThickness.decimalInputValue.notNumeric")),
            entry("topSealFracturePressure.decimalInputValue", Set.of("topSealFracturePressure.decimalInputValue.notNumeric"))
        );
  }

  // All submission types

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class)
  void validate_whenOtherLithologyNotProvided_thenValidationErrors(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    var form = getPopulatedSourceAndSealForm();
    form.setSourceLithology(StandardLithology.OTHER);
    form.setTopSealLithology(StandardLithology.OTHER);
    var bindingResult = validateSourceAndSealForm_fullValidation(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("sourceLithologyOther", Set.of("sourceLithologyOther.required")),
            entry("topSealLithologyOther", Set.of("topSealLithologyOther.required"))
        );
  }

  private BindingResult validateSourceAndSealForm_fullValidation(SourceAndSealForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    sourceAndSealFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
    return bindingResult;
  }

  private BindingResult validateSourceAndSealForm_partialValidation(SourceAndSealForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    sourceAndSealFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL);
    return bindingResult;
  }

  private SourceAndSealForm getInvalidForm() {
    var invalidDecimalInput = new DecimalInput("a");

    var form = new SourceAndSealForm();
    form.setSourceFormationName("");
    form.setTopSealFormationName("");
    form.setSourceAgeStage(null);
    form.setSourceLithology(StandardLithology.OTHER);
    form.setSourceLithologyOther("");
    form.setTopSealAgeStage(null);
    form.setSourceGroupName(null);
    form.setTopSealGroupName(null);
    form.setTopSealLithology(StandardLithology.OTHER);
    form.setTopSealLithologyOther("");
    form.setSealThickness(invalidDecimalInput);
    form.setTopSealFracturePressure(invalidDecimalInput);
    return form;
  }

  private SourceAndSealForm getPopulatedSourceAndSealForm() {
    var form = new SourceAndSealForm();
    form.setSourceFormationName("source formation name");
    form.setTopSealFormationName("top seal formation name");
    form.setSourceAgeStage(AgeSeries.LOWER_CARBONIFEROUS);
    form.setSourceLithology(StandardLithology.COAL);
    form.setTopSealAgeStage(AgeSeries.LOWER_CRETACEOUS);
    form.setSourceGroupName(GroupName.BRENT);
    form.setTopSealGroupName(GroupName.CLAIR);
    form.setTopSealLithology(StandardLithology.COAL);
    form.setSealThickness(DecimalInput.createDecimalInput(1));
    form.setTopSealFracturePressure(DecimalInput.createDecimalInput(1));
    return form;
  }

  private static Stream<Arguments> allFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(SubmissionDetailUtil::isFieldSubmission)
        .map(Arguments::of);
  }

  private static Stream<Arguments> allNonFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isFieldSubmission(submissionType))
        .map(Arguments::of);
  }
}