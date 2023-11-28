package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
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
class ConsideringCcusFormValidatorTest {

  private ConsideringCcusFormValidator consideringCcusFormValidator;

  private ConsideringCcusForm form;
  private BindingResult bindingResult;
  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    consideringCcusFormValidator = new ConsideringCcusFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void isValid_validForm_thenNoErrors(ValidationType validationType) {
    form = getValidForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(consideringCcusFormValidator.isValid(form, bindingResult, submissionDetail, validationType));
    assertThat(ValidatorTestingUtil.extractErrors(bindingResult)).isEmpty();
  }

  @Test
  void isValid_partial_invalidForm_whiteSpace_thenNoErrors() {
    form = new ConsideringCcusForm();
    form.setIsConnectedToProject(true);
    form.setConnectedProjects("");
    form.setIsAnyShallowerHorizons(true);
    form.setAgeAndFormationOfHorizons("");

    bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(consideringCcusFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));
    assertThat(ValidatorTestingUtil.extractErrors(bindingResult)).isEmpty();
  }

  @Test
  void isValid_partial_invalidForm_nullValues_thenNoErrors() {
    form = new ConsideringCcusForm();
    form.setIsConnectedToProject(true);
    form.setConnectedProjects(null);
    form.setIsAnyShallowerHorizons(true);
    form.setAgeAndFormationOfHorizons(null);

    bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(consideringCcusFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));
    assertThat(ValidatorTestingUtil.extractErrors(bindingResult)).isEmpty();
  }

  @Test
  void isValid_full_invalidForm_whiteSpace_thenErrors() {
    form = new ConsideringCcusForm();
    form.setIsConnectedToProject(true);
    form.setConnectedProjects("");
    form.setIsAnyShallowerHorizons(true);
    form.setAgeAndFormationOfHorizons("");

    bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(consideringCcusFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));
    assertThat(ValidatorTestingUtil.extractErrors(bindingResult))
        .containsExactly(
            entry("connectedProjects", Set.of("connectedProjects.required")),
            entry("ageAndFormationOfHorizons", Set.of("ageAndFormationOfHorizons.required"))
        );
  }

  @Test
  void isValid_full_invalidForm_nullValues_thenErrors() {
    form = new ConsideringCcusForm();
    form.setIsConnectedToProject(true);
    form.setConnectedProjects(null);
    form.setIsAnyShallowerHorizons(true);
    form.setAgeAndFormationOfHorizons(null);

    bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(consideringCcusFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));
    assertThat(ValidatorTestingUtil.extractErrors(bindingResult))
        .containsExactly(
            entry("connectedProjects", Set.of("connectedProjects.required")),
            entry("ageAndFormationOfHorizons", Set.of("ageAndFormationOfHorizons.required"))
        );
  }

  @Test
  void isValid_partial_emptyForm_thenNoErrors() {
    form = new ConsideringCcusForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");

    assertTrue(consideringCcusFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));
    assertThat(ValidatorTestingUtil.extractErrors(bindingResult)).isEmpty();
  }

  @Test
  void isValid_full_emptyForm_thenErrors() {
    form = new ConsideringCcusForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");

    assertFalse(consideringCcusFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));
    assertThat(ValidatorTestingUtil.extractErrors(bindingResult))
        .containsExactly(
            entry("isConnectedToProject", Set.of("isConnectedToProject.required")),
            entry("isAnyShallowerHorizons", Set.of("isAnyShallowerHorizons.required"))
        );
  }

  private ConsideringCcusForm getValidForm() {
    var form = new ConsideringCcusForm();
    form.setIsConnectedToProject(true);
    form.setConnectedProjects("Connected");
    form.setIsAnyShallowerHorizons(false);
    form.setAgeAndFormationOfHorizons(null);
    return form;
  }
}