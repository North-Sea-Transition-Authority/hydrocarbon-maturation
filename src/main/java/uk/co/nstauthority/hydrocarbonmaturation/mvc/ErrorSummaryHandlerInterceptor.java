package uk.co.nstauthority.hydrocarbonmaturation.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;

@Component
public class ErrorSummaryHandlerInterceptor implements HandlerInterceptor {

  public static final String MODEL_FORM = "form";

  private final ControllerHelperService controllerHelperService;

  public ErrorSummaryHandlerInterceptor(ControllerHelperService controllerHelperService) {
    this.controllerHelperService = controllerHelperService;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                         ModelAndView modelAndView) {
    if (modelAndView != null) {
      var bindingResult = (BindingResult) modelAndView.getModelMap().get(BindingResult.MODEL_KEY_PREFIX + MODEL_FORM);

      if (bindingResult != null) {
        controllerHelperService.addFieldValidationErrors(modelAndView, bindingResult);
      }
    }
  }
}
