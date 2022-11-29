package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static org.assertj.core.api.Assertions.assertThat;
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
}