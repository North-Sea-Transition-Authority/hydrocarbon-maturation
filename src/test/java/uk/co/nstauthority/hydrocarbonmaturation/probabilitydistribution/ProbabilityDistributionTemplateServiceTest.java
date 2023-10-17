package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@ExtendWith(MockitoExtension.class)
class ProbabilityDistributionTemplateServiceTest {

  @InjectMocks
  ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  @Test
  void getProbabilityDistributionTemplateForInstance_CustomType() {
    var instance = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY;

    var result = probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(instance);

    assertThat(result.getUnits()).isEqualTo(instance.getUnits());
    assertThat(result.getProbabilityDistributionTypeWithInputsList())
        .hasSize(1)
        .extracting(ProbabilityDistributionTypeWithInputs::getProbabilityDistributionType)
        .containsExactly(ProbabilityDistributionType.CUSTOM);
    assertThat(result.getProbabilityDistributionTypeWithInputsList().get(0).getProbabilityDistributionTypeInputs())
        .hasSize(4)
        .extracting(
            ProbabilityDistributionTypeInput::getProbabilityOfExceedanceType,
            ProbabilityDistributionTypeInput::getPrompt,
            ProbabilityDistributionTypeInput::getRequired
        )
        .containsExactly(
            tuple(ProbabilityOfExceedanceType.P90, "P90", true),
            tuple(ProbabilityOfExceedanceType.P50, "P50", true),
            tuple(ProbabilityOfExceedanceType.MEAN, "Mean", true),
            tuple(ProbabilityOfExceedanceType.P10, "P10", true)
        );
  }

  @Test
  void getProbabilityDistributionTemplateForInstanceAndPromptOverrides_CustomType() {
    var instance = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY;
    var contingentUncertaintyCategoryPromptOverrides = ProbabilityOfExceedanceTypeUtil.getContingentUncertaintyCategoryPromptOverrides();

    var result = probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(instance, contingentUncertaintyCategoryPromptOverrides);

    assertThat(result.getUnits()).isEqualTo(instance.getUnits());
    assertThat(result.getProbabilityDistributionTypeWithInputsList())
        .hasSize(1)
        .extracting(ProbabilityDistributionTypeWithInputs::getProbabilityDistributionType)
        .containsExactly(ProbabilityDistributionType.CUSTOM);
    assertThat(result.getProbabilityDistributionTypeWithInputsList().get(0).getProbabilityDistributionTypeInputs())
        .hasSize(4)
        .extracting(
            ProbabilityDistributionTypeInput::getProbabilityOfExceedanceType,
            ProbabilityDistributionTypeInput::getPrompt,
            ProbabilityDistributionTypeInput::getRequired
        )
        .containsExactly(
            tuple(ProbabilityOfExceedanceType.P90, "P90 or 1C", true),
            tuple(ProbabilityOfExceedanceType.P50, "P50 or 2C", true),
            tuple(ProbabilityOfExceedanceType.MEAN, "Mean", true),
            tuple(ProbabilityOfExceedanceType.P10, "P10 or 3C", true)
        );
  }

  @Test
  void getProbabilityDistributionTypeError_NoProbabilityDistributionType_EmptyError() {
    var bindingResult = Mockito.mock(BindingResult.class);
    var formName = "test";
    var probabilityDistributionForm = new ProbabilityDistributionForm();

    var probabilityDistributionTypeError = probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
        bindingResult, formName, probabilityDistributionForm);

    assertThat(probabilityDistributionTypeError).isEqualTo(ProbabilityDistributionTypeError.emptyError());
  }

  @Test
  void getProbabilityDistributionTypeError_NoError_EmptyError() {
    var bindingResult = Mockito.mock(BindingResult.class);
    var formName = "test";
    var probabilityDistributionType = ProbabilityDistributionType.NORMAL;
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(probabilityDistributionType);
    var typePath = probabilityDistributionForm.getProbabilityDistributionType().getPath();
    when(bindingResult.getFieldError(formName + "." + typePath + ".error")).thenReturn(null);

    var probabilityDistributionTypeError = probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
        bindingResult, formName, probabilityDistributionForm);

    assertThat(probabilityDistributionTypeError).isEqualTo(ProbabilityDistributionTypeError.emptyError());
  }

  @Test
  void getProbabilityDistributionTypeError_HasError_EmptyError() {
    var bindingResult = Mockito.mock(BindingResult.class);
    var formName = "test";
    var probabilityDistributionType = ProbabilityDistributionType.NORMAL;
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(probabilityDistributionType);
    var typePath = probabilityDistributionForm.getProbabilityDistributionType().getPath();
    var fieldError = Mockito.mock(FieldError.class);
    var errorPath = formName + "." + typePath + ".error";
    when(bindingResult.getFieldError(errorPath)).thenReturn(fieldError);

    var probabilityDistributionTypeError = probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
        bindingResult, formName, probabilityDistributionForm);

    assertThat(probabilityDistributionTypeError)
        .extracting(
            ProbabilityDistributionTypeError::hasError,
            ProbabilityDistributionTypeError::fullErrorPath,
            ProbabilityDistributionTypeError::probabilityDistributionType,
            ProbabilityDistributionTypeError::errorId
        )
        .containsExactly(
            true,
            "form." + errorPath,
            probabilityDistributionType,
            formName + "-" + typePath
        );
  }

  @Test
  void getProbabilityDistributionTypeError_HasError_EmptyError_noPeriodInFormNameErrorId() {
    var bindingResult = Mockito.mock(BindingResult.class);
    var formName = "testForm.testValuesForm";
    var formattedFormName = "testForm-testValuesForm";
    var probabilityDistributionType = ProbabilityDistributionType.NORMAL;
    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(probabilityDistributionType);
    var typePath = probabilityDistributionForm.getProbabilityDistributionType().getPath();
    var fieldError = Mockito.mock(FieldError.class);
    var errorPath = formName + "." + typePath + ".error";
    when(bindingResult.getFieldError(errorPath)).thenReturn(fieldError);

    var probabilityDistributionTypeError = probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
        bindingResult, formName, probabilityDistributionForm);

    assertThat(probabilityDistributionTypeError)
        .extracting(
            ProbabilityDistributionTypeError::hasError,
            ProbabilityDistributionTypeError::fullErrorPath,
            ProbabilityDistributionTypeError::probabilityDistributionType,
            ProbabilityDistributionTypeError::errorId
        )
        .containsExactly(
            true,
            "form." + errorPath,
            probabilityDistributionType,
            formattedFormName + "-" + typePath
        );
  }
}