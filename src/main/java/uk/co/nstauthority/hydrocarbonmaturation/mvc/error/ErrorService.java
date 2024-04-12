package uk.co.nstauthority.hydrocarbonmaturation.mvc.error;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceBrandingConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.AnalyticsConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.cookies.CookiesController;
import uk.co.nstauthority.hydrocarbonmaturation.feedback.FeedbackController;
import uk.co.nstauthority.hydrocarbonmaturation.footer.AccessibilityStatementController;
import uk.co.nstauthority.hydrocarbonmaturation.footer.ContactInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Service
public class ErrorService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ErrorService.class);
  private static final String SAFE_CHARACTERS = "BCDFGHJKMPQRTVWXY346789";

  private final ErrorConfiguration errorConfiguration;
  private final ServiceBrandingConfigurationProperties serviceBrandingConfigurationProperties;
  private final TechnicalSupportConfiguration technicalSupportConfiguration;
  private final AnalyticsConfigurationProperties analyticsConfigurationProperties;

  @Autowired
  public ErrorService(ErrorConfiguration errorConfiguration,
                      ServiceBrandingConfigurationProperties serviceBrandingConfigurationProperties,
                      TechnicalSupportConfiguration technicalSupportConfiguration,
                      AnalyticsConfigurationProperties analyticsConfigurationProperties) {
    this.errorConfiguration = errorConfiguration;
    this.serviceBrandingConfigurationProperties = serviceBrandingConfigurationProperties;
    this.technicalSupportConfiguration = technicalSupportConfiguration;
    this.analyticsConfigurationProperties = analyticsConfigurationProperties;
  }

  public ModelAndView addErrorAttributesToModel(
      ModelAndView modelAndView,
      Throwable throwable,
      String url,
      int statusCode) {
    LOGGER.info("Error found for URL: {}", url);
    if (throwable != null && !expectedErrorStatuses().contains(statusCode)) {
      addStackTraceToModel(modelAndView, throwable);
      addErrorReference(modelAndView, throwable);
    }
    addBrandingAttributes(modelAndView);
    addTechnicalSupportContactInfo(modelAndView);
    addCommonUrls(modelAndView);
    addAnalytics(modelAndView);
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

  private void addBrandingAttributes(ModelAndView modelAndView) {
    modelAndView.addObject(
        "serviceBranding",
        serviceBrandingConfigurationProperties.getServiceConfigurationProperties()
    );
    modelAndView.addObject(
        "customerBranding",
        serviceBrandingConfigurationProperties.getCustomerConfigurationProperties()
    );
  }

  private void addTechnicalSupportContactInfo(ModelAndView modelAndView) {
    modelAndView.addObject("technicalSupport", technicalSupportConfiguration.getTechnicalSupportConfigurationProperties());
  }

  private void addCommonUrls(ModelAndView modelAndView) {
    modelAndView.addObject("serviceHomeUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)));
    modelAndView.addObject("accessibilityStatementUrl",
        ReverseRouter.route(on(AccessibilityStatementController.class).getAccessibilityStatement()));
    modelAndView.addObject("feedbackPageUrl", ReverseRouter.route(on(FeedbackController.class)
        .getFeedback(null)));
    modelAndView.addObject("contactInformationUrl", ReverseRouter.route(on(ContactInformationController.class)
        .getContactInformationPage()));
    modelAndView.addObject("cookiesStatementUrl", ReverseRouter.route(on(CookiesController.class)
        .getCookiePreferences()));
  }

  private void addAnalytics(ModelAndView modelAndView) {
    modelAndView.addObject("analytics", analyticsConfigurationProperties);
  }

  private static List<Integer> expectedErrorStatuses() {
    return List.of(SC_NOT_FOUND, SC_METHOD_NOT_ALLOWED, SC_FORBIDDEN, SC_UNAUTHORIZED);
  }
}
