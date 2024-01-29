package uk.co.nstauthority.hydrocarbonmaturation.mvc.error;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.Unauthenticated;

@Controller
@Unauthenticated
public class DefaultErrorController implements ErrorController {

  private final ErrorService errorService;

  @Autowired
  public DefaultErrorController(ErrorService errorService) {
    this.errorService = errorService;
  }

  /**
   * Handles framework-level errors (404s, authorisation failures, filter exceptions) for browser clients. Errors thrown
   * by app code (controller methods and below) are handled in DefaultExceptionResolver.
   */
  @GetMapping("/error")
  public ModelAndView handleError(HttpServletRequest request) {

    var statusCode = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE))
        .map(Integer.class::cast);

    var viewName = statusCode.map(this::getViewName).orElse(ErrorView.DEFAULT_ERROR.getViewName());
    var modelAndView = new ModelAndView(viewName);

    //Look for the Spring specific exception first, fall back to the Servlet exception if not available
    var dispatcherException = request.getAttribute(DispatcherServlet.EXCEPTION_ATTRIBUTE);
    var servletException = request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE);
    var throwable = (Throwable) ObjectUtils.defaultIfNull(dispatcherException, servletException);
    var url = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
    errorService.addErrorAttributesToModel(modelAndView, throwable, url, statusCode.orElse(SC_INTERNAL_SERVER_ERROR));

    return modelAndView;
  }

  private String getViewName(int statusCode) {
    return switch (statusCode) {
      case SC_NOT_FOUND, SC_METHOD_NOT_ALLOWED -> ErrorView.PAGE_NOT_FOUND.getViewName();
      case SC_FORBIDDEN, SC_UNAUTHORIZED -> ErrorView.UNAUTHORISED.getViewName();
      default -> ErrorView.DEFAULT_ERROR.getViewName();
    };
  }
}