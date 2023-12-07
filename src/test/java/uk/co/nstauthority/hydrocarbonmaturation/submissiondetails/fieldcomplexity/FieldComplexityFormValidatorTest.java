package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class FieldComplexityFormValidatorTest {

  private FieldComplexityFormValidator fieldComplexityFormValidator;

  private FieldComplexityForm form;

  private BindingResult bindingResult;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    fieldComplexityFormValidator = new FieldComplexityFormValidator();

    form = new FieldComplexityForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
  }

  @Test
  void validatePartial_emptyForm_thenNoErrors() {

    assertTrue(fieldComplexityFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));
    assertThat(ValidatorTestingUtil.extractErrors(bindingResult)).isEmpty();
  }

  @Test
  void validateFull_emptyForm_thenErrors() {

    assertFalse(fieldComplexityFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));
    assertThat(ValidatorTestingUtil.extractErrors(bindingResult))
        .containsExactly(
            entry("viscosityRange", Set.of("viscosityRange.required")),
            entry("verticalHeterogeneityRange", Set.of("verticalHeterogeneityRange.required")),
            entry("arealDensityRange", Set.of("arealDensityRange.required")),
            entry("structuralComplexityRange", Set.of("structuralComplexityRange.required"))
        );
  }

  @Test
  void validatePartial_incompleteForm_thenNoErrors() {
    form.setViscosityRange(ViscosityRange.ONE_TO_TEN);
    form.setVerticalHeterogeneityRange(VerticalHeterogeneityRange.ONE_TO_TEN);
    form.setArealDensityRange(null);
    form.setStructuralComplexityRange(null);

    assertTrue(fieldComplexityFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));
    assertThat(ValidatorTestingUtil.extractErrors(bindingResult)).isEmpty();
  }

  @Test
  void validateFull_incompleteForm_thenErrors() {
    form.setViscosityRange(ViscosityRange.ONE_TO_TEN);
    form.setVerticalHeterogeneityRange(VerticalHeterogeneityRange.ONE_TO_TEN);
    form.setArealDensityRange(null);
    form.setStructuralComplexityRange(null);

    assertFalse(fieldComplexityFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));
    assertThat(ValidatorTestingUtil.extractErrors(bindingResult))
        .containsExactly(
            entry("arealDensityRange", Set.of("arealDensityRange.required")),
            entry("structuralComplexityRange", Set.of("structuralComplexityRange.required"))
        );
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validateFull_validForm_thenNoErrors(ValidationType validationType) {
    form.setViscosityRange(ViscosityRange.ONE_TO_TEN);
    form.setVerticalHeterogeneityRange(VerticalHeterogeneityRange.ONE_TO_TEN);
    form.setArealDensityRange(ArealDensityRange.GREATER_THAN_FOUR_POINT_FIVE);
    form.setStructuralComplexityRange(StructuralComplexityRange.COMPLEX_GEOMETRIES_COMPARTMENTALISATION);

    assertTrue(fieldComplexityFormValidator.isValid(form, bindingResult, submissionDetail, validationType));
    assertThat(ValidatorTestingUtil.extractErrors(bindingResult)).isEmpty();
  }
}