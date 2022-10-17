package uk.co.nstauthority.hydrocarbonmaturation.mvc.error;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Service
public class ErrorService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ErrorService.class);
  private static final String SAFE_CHARACTERS = "BCDFGHJKMPQRTVWXY346789";

  private final ErrorConfiguration errorConfiguration;

  @Autowired
  public ErrorService(ErrorConfiguration errorConfiguration) {
    this.errorConfiguration = errorConfiguration;
  }

  public ModelAndView addErrorAttributesToModel(ModelAndView modelAndView, Throwable throwable) {
    if (throwable != null) {
      addStackTraceToModel(modelAndView, throwable);
      addErrorReference(modelAndView, throwable);
    }
    addCommonUrls(modelAndView);
    return modelAndView;
  }

  private String generateErrorReference() {
    return RandomStringUtils.random(9, SAFE_CHARACTERS.toUpperCase());
  }

  private boolean isStackTraceEnabled() {
    return errorConfiguration.getErrorConfigurationProperties().stackTraceEnabled();
  }

  private void addStackTraceToModel(ModelAndView modelAndView, Throwable throwable) {
    if (isStackTraceEnabled() && throwable != null) {
      modelAndView.addObject("stackTrace", ExceptionUtils.getStackTrace(throwable));
    }
  }

  private void addErrorReference(ModelAndView modelAndView, Throwable throwable) {
    if (throwable != null) {
      var errorReference = generateErrorReference();
      modelAndView.addObject("errorRef", errorReference);
      LOGGER.error("Caught unhandled exception (errorRef {})", errorReference, throwable);
    }
  }

  private void addCommonUrls(ModelAndView modelAndView) {
    modelAndView.addObject("serviceHomeUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea()));
  }
}
