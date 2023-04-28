package uk.co.nstauthority.hydrocarbonmaturation.mvc;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.InvalidAuthenticationException;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceBrandingConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.feedback.FeedbackController;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.topnavigation.TopNavigationService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ControllerAdvice
public class DefaultPageControllerAdvice {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPageControllerAdvice.class);

  private final ServiceBrandingConfigurationProperties serviceBrandingConfigurationProperties;
  private final TopNavigationService topNavigationService;
  private final TechnicalSupportConfiguration technicalSupportConfiguration;
  private final UserDetailService userDetailService;

  @Autowired
  DefaultPageControllerAdvice(ServiceBrandingConfigurationProperties serviceBrandingConfigurationProperties,
                              TopNavigationService topNavigationService,
                              TechnicalSupportConfiguration technicalSupportConfiguration,
                              UserDetailService userDetailService) {
    this.serviceBrandingConfigurationProperties = serviceBrandingConfigurationProperties;
    this.topNavigationService = topNavigationService;
    this.technicalSupportConfiguration = technicalSupportConfiguration;
    this.userDetailService = userDetailService;
  }

  @ModelAttribute
  void addDefaultModelAttributes(Model model, HttpServletRequest request) {
    addBrandingAttributes(model);
    addCommonUrls(model);
    addTopNavigationItems(model, request);
    addSubmitButtonText(model);
    addTechnicalSupportContactInfo(model);
    addUser(model);
  }

  @InitBinder
  void initBinder(WebDataBinder binder) {
    // Trim whitespace from form fields
    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
  }

  private void addBrandingAttributes(Model model) {
    model.addAttribute(
        "serviceBranding",
        serviceBrandingConfigurationProperties.getServiceConfigurationProperties()
    );
    model.addAttribute(
        "customerBranding",
        serviceBrandingConfigurationProperties.getCustomerConfigurationProperties()
    );
  }

  private void addTopNavigationItems(Model model, HttpServletRequest request) {
    model.addAttribute("navigationItems", topNavigationService.getTopNavigationItems());
    model.addAttribute("currentEndPoint", request.getRequestURI());
  }

  private void addCommonUrls(Model model) {
    model.addAttribute("serviceHomeUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea()));
    model.addAttribute("feedbackPageUrl", ReverseRouter.route(on(FeedbackController.class)
        .getFeedback(null)));
  }

  private void addSubmitButtonText(Model model) {
    model.addAttribute("submitPrimaryButtonText", ValidationType.FULL.getButtonText());
    model.addAttribute("submitSecondaryButtonText", ValidationType.PARTIAL.getButtonText());
  }

  private void addTechnicalSupportContactInfo(Model model) {
    model.addAttribute("technicalSupport", technicalSupportConfiguration.getTechnicalSupportConfigurationProperties());
  }

  private void addUser(Model model) {
    try {
      model.addAttribute("loggedInUser", userDetailService.getUserDetail());
    } catch (InvalidAuthenticationException e) {
      LOGGER.error("Could not get logged in user", e);
    }
  }
}
