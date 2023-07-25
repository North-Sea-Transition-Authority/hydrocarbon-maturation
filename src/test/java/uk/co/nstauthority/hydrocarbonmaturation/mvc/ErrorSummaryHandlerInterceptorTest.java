package uk.co.nstauthority.hydrocarbonmaturation.mvc;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem;

@ExtendWith(MockitoExtension.class)
class ErrorSummaryHandlerInterceptorTest {

  private ErrorSummaryHandlerInterceptor errorSummaryHandlerInterceptor;

  @Mock
  private ControllerHelperService controllerHelperService;

  @BeforeEach
  public void setUp() {
    errorSummaryHandlerInterceptor = new ErrorSummaryHandlerInterceptor(controllerHelperService);
  }

  @Test
  void testPostHandle_withNullModelAndView() {
    var request = mock(HttpServletRequest.class);
    var response = mock(HttpServletResponse.class);

    assertThatCode(() -> errorSummaryHandlerInterceptor.postHandle(request, response, null, null))
        .doesNotThrowAnyException();
  }

  @Test
  void testPostHandle_withNullBindingResult() {
    var request = mock(HttpServletRequest.class);
    var response = mock(HttpServletResponse.class);
    var modelAndView = new ModelAndView();

    assertThatCode(() -> errorSummaryHandlerInterceptor.postHandle(request, response, null, modelAndView))
        .doesNotThrowAnyException();
  }

  @Test
  void testPostHandle_withNoErrors() {
    var request = mock(HttpServletRequest.class);
    var response = mock(HttpServletResponse.class);
    var modelAndView = new ModelAndView();
    var bindingResult = mock(BindingResult.class);
    modelAndView.getModelMap().put(BindingResult.MODEL_KEY_PREFIX + ErrorSummaryHandlerInterceptor.MODEL_FORM, bindingResult);

    errorSummaryHandlerInterceptor.postHandle(request, response, null, modelAndView);

    assertThat(modelAndView.getModel()).doesNotContainKey("errorList");
  }

  @Test
  void testPostHandle_withErrors_andErrorOrderingOnForm() {
    var request = mock(HttpServletRequest.class);
    var response = mock(HttpServletResponse.class);
    var modelAndView = new ModelAndView();
    var bindingResult = mock(BindingResult.class);
    var form = new TestFormWithErrorOrdering();
    modelAndView.getModelMap().put(BindingResult.MODEL_KEY_PREFIX + ErrorSummaryHandlerInterceptor.MODEL_FORM, bindingResult);
    modelAndView.getModelMap().put(ErrorSummaryHandlerInterceptor.MODEL_FORM, form);

    when(bindingResult.getAllErrors()).thenReturn(List.of(
        new FieldError("", "testString", "Test string error"),
        new FieldError("", "testInteger", "Test integer error"))
    );
    when(bindingResult.hasErrors()).thenReturn(true);

    errorSummaryHandlerInterceptor.postHandle(request, response, null, modelAndView);

    assertThat(modelAndView.getModel()).contains(entry("errorList", List.of(
        new ErrorItem(1, "testString", "Test string error"),
        new ErrorItem(2, "testInteger", "Test integer error")
    )));
  }



  @Test
  void testPostHandle_withErrors_andErrorOrderingNotOnForm() {
    var request = mock(HttpServletRequest.class);
    var response = mock(HttpServletResponse.class);
    var modelAndView = new ModelAndView();
    var bindingResult = mock(BindingResult.class);
    var form = new TestFormWithoutErrorOrdering();
    modelAndView.getModelMap().put(BindingResult.MODEL_KEY_PREFIX + ErrorSummaryHandlerInterceptor.MODEL_FORM, bindingResult);
    modelAndView.getModelMap().put(ErrorSummaryHandlerInterceptor.MODEL_FORM, form);

    when(bindingResult.hasErrors()).thenReturn(true);

    errorSummaryHandlerInterceptor.postHandle(request, response, null, modelAndView);

    verify(controllerHelperService, times(1)).addFieldValidationErrors(modelAndView, bindingResult);
  }

  @ApplyFormErrorOrdering
  private static class TestFormWithErrorOrdering {

    @ErrorSummaryDisplayOrder(1)
    private String testString;

    @ErrorSummaryDisplayOrder(2)
    private Integer testInteger;
  }

  private static class TestFormWithoutErrorOrdering {

    private String testString;

    private Integer testInteger;
  }
}
