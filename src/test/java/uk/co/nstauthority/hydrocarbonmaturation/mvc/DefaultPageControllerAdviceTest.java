package uk.co.nstauthority.hydrocarbonmaturation.mvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.branding.IncludeServiceBrandingConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.AnalyticsConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.IncludeAnalyticsConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.cookies.CookiesController;
import uk.co.nstauthority.hydrocarbonmaturation.feedback.FeedbackController;
import uk.co.nstauthority.hydrocarbonmaturation.footer.AccessibilityStatementController;
import uk.co.nstauthority.hydrocarbonmaturation.footer.ContactInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.IncludeTechnicalSupportConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.topnavigation.TopNavigationService;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;


@WebMvcTest
@ActiveProfiles("test")
@IncludeServiceBrandingConfigurationProperties
@IncludeTechnicalSupportConfigurationProperties
@IncludeAnalyticsConfigurationProperties
@ContextConfiguration(classes = {
    DefaultPageControllerAdviceTest.TestController.class,
    DefaultPageControllerAdvice.class,
    TopNavigationService.class
})
class DefaultPageControllerAdviceTest {

  @Autowired
  protected MockMvc mockMvc;

  @MockitoBean
  protected UserService userService;

  @MockitoBean
  protected InternalTeamService internalTeamService;

  @Test
  void addDefaultModelAttributes_verifyDefaultAttributes() throws Exception {

    var loggedInUser = UserTestUtil.Builder().build();
    when(userService.getOptionalUserDetail()).thenReturn(Optional.of(loggedInUser));

    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(TestController.class).testEndpoint()))
        .with(user(loggedInUser)))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();

    var modelMap = modelAndView.getModel();

    assertThat(modelMap).containsOnlyKeys(
        "customerBranding",
        "org.springframework.validation.BindingResult.customerBranding",
        "serviceBranding",
        "org.springframework.validation.BindingResult.serviceBranding",
        "technicalSupport",
        "org.springframework.validation.BindingResult.technicalSupport",
        "serviceHomeUrl",
        "navigationItems",
        "currentEndPoint",
        "submitPrimaryButtonText",
        "submitSecondaryButtonText",
        "loggedInUser",
        "feedbackPageUrl",
        "contactInformationUrl",
        "accessibilityStatementUrl",
        "org.springframework.validation.BindingResult.loggedInUser",
        "cookiesStatementUrl",
        "analytics",
        "org.springframework.validation.BindingResult.analytics"
    );

    assertThat((CustomerConfigurationProperties) modelMap.get("customerBranding")).hasNoNullFieldsOrProperties();
    assertThat((ServiceConfigurationProperties) modelMap.get("serviceBranding")).hasNoNullFieldsOrProperties();
    assertThat((TechnicalSupportConfigurationProperties) modelMap.get("technicalSupport")).hasNoNullFieldsOrProperties();
    assertThat((AnalyticsConfigurationProperties) modelMap.get("analytics")).hasNoNullFieldsOrProperties();
    Map<String, Object> commonPropertiesMap = new HashMap<>();
    commonPropertiesMap.put("serviceHomeUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)));
    commonPropertiesMap.put("submitPrimaryButtonText", "Save and complete");
    commonPropertiesMap.put("feedbackPageUrl",
        ReverseRouter.route(on(FeedbackController.class).getFeedback(null)));
    commonPropertiesMap.put("submitSecondaryButtonText", "Save and complete later");
    commonPropertiesMap.put("loggedInUser", loggedInUser);
    commonPropertiesMap.put("contactInformationUrl",
        ReverseRouter.route(on(ContactInformationController.class).getContactInformationPage()));
    commonPropertiesMap.put("accessibilityStatementUrl",
        ReverseRouter.route(on(AccessibilityStatementController.class).getAccessibilityStatement()));
    commonPropertiesMap.put("cookiesStatementUrl",
        ReverseRouter.route(on(CookiesController.class).getCookiePreferences()));
    assertThat(modelMap).containsAllEntriesOf(commonPropertiesMap);
  }

  // Dummy application to stop the @WebMvcTest loading more than it needs
  @SpringBootApplication
  static class TestApplication {
  }

  @Controller
  @RequestMapping("/endpoint")
  static class TestController {

    private static final String VIEW_NAME = "test-view";

    @GetMapping()
    ModelAndView testEndpoint() {
      return new ModelAndView(VIEW_NAME);
    }
  }

}