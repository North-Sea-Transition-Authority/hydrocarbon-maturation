package uk.co.nstauthority.hydrocarbonmaturation.mvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.branding.IncludeServiceBrandingConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.feedback.FeedbackController;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.IncludeTechnicalSupportConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.topnavigation.TopNavigationService;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;


@WebMvcTest
@ActiveProfiles("test")
@IncludeServiceBrandingConfigurationProperties
@IncludeTechnicalSupportConfigurationProperties
@ContextConfiguration(classes = {
    DefaultPageControllerAdviceTest.TestController.class,
    DefaultPageControllerAdvice.class,
    TopNavigationService.class
})
class DefaultPageControllerAdviceTest {

  @Autowired
  protected MockMvc mockMvc;

  @MockBean
  protected UserDetailService userDetailService;

  @Test
  void addDefaultModelAttributes_verifyDefaultAttributes() throws Exception {

    doCallRealMethod().when(userDetailService).getUserDetail();

    var loggedInUser = ServiceUserDetailTestUtil.Builder().build();

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
        "org.springframework.validation.BindingResult.loggedInUser"
    );

    assertThat((CustomerConfigurationProperties) modelMap.get("customerBranding")).hasNoNullFieldsOrProperties();
    assertThat((ServiceConfigurationProperties) modelMap.get("serviceBranding")).hasNoNullFieldsOrProperties();
    assertThat((TechnicalSupportConfigurationProperties) modelMap.get("technicalSupport")).hasNoNullFieldsOrProperties();
    Map<String, Object> commonPropertiesMap = new HashMap<>();
    commonPropertiesMap.put("serviceHomeUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea()));
    commonPropertiesMap.put("submitPrimaryButtonText", "Save and complete");
    commonPropertiesMap.put("feedbackPageUrl",
        ReverseRouter.route(on(FeedbackController.class).getFeedback(null)));
    commonPropertiesMap.put("submitSecondaryButtonText", "Save and complete later");
    commonPropertiesMap.put("loggedInUser", loggedInUser);
    assertThat(modelMap).containsAllEntriesOf(commonPropertiesMap);
  }

  // Dummy application to stop the @WebMvcTest loading more than it needs
  @SpringBootApplication
  static class TestApplication {
  }

  @RequestMapping("/endpoint")
  static class TestController {

    @GetMapping()
    ModelAndView testEndpoint() {
      return new ModelAndView();
    }
  }

}