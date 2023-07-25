package uk.co.nstauthority.hydrocarbonmaturation.mvc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem;

@Component
public class ErrorSummaryHandlerInterceptor implements HandlerInterceptor {

  public static final String MODEL_FORM = "form";

  private final ControllerHelperService controllerHelperService;

  public ErrorSummaryHandlerInterceptor(ControllerHelperService controllerHelperService) {
    this.controllerHelperService = controllerHelperService;
  }

  @Override
  public void postHandle(HttpServletRequest request,
                         HttpServletResponse response,
                         Object handler,
                         ModelAndView modelAndView) {
    if (modelAndView != null) {
      var bindingResult = (BindingResult) modelAndView.getModelMap().get(BindingResult.MODEL_KEY_PREFIX + MODEL_FORM);
      var form = modelAndView.getModel().get(MODEL_FORM);
      List<ErrorItem> errorList = new ArrayList<>();
      if (bindingResult != null && bindingResult.hasErrors()) {
        if (form.getClass().isAnnotationPresent(ApplyFormErrorOrdering.class)) {
          bindingResult.getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
              Field field = getField(form, fieldError);
              var fieldErrorSummaryDisplayOrder = field.getAnnotation(ErrorSummaryDisplayOrder.class).value();
              errorList.add(new ErrorItem(fieldErrorSummaryDisplayOrder, fieldError.getField(), fieldError.getDefaultMessage()));
            }
          });
          modelAndView.addObject("errorList", errorList);
        } else {
          controllerHelperService.addFieldValidationErrors(modelAndView, bindingResult);
        }
      }
    }
  }

  private Field getField(Object form, FieldError fieldError) {
    return List.of(FieldUtils.getAllFields(form.getClass())).stream()
        .filter(field -> field.getName().equals(fieldError.getField()))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Could not find field " + fieldError.getField()));
  }
}
