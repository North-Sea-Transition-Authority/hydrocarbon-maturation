package uk.co.nstauthority.hydrocarbonmaturation.footer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.RealSecurityAbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.AccessibilityStatementConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.AccessibilityStatementConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfigurationProperties;

@ContextConfiguration(classes = {
    AccessibilityStatementController.class,
    TechnicalSupportConfiguration.class,
    AccessibilityStatementConfiguration.class})
@EnableConfigurationProperties(value = {
    TechnicalSupportConfigurationProperties.class,
    AccessibilityStatementConfigurationProperties.class})
class AccessibilityStatementControllerTest extends RealSecurityAbstractControllerTest {
  private static final User USER = UserTestUtil.Builder().build();

  @Autowired
  private TechnicalSupportConfiguration technicalSupportConfiguration;
  @Autowired
  private AccessibilityStatementConfiguration accessibilityStatementConfiguration;

  @SecurityTest
  void renderAccessibilityStatement_assertModelProperties() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(AccessibilityStatementController.class).getAccessibilityStatement()))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/footer/accessibilityStatement"))
        .andExpect(model().attribute("technicalSupport",
            technicalSupportConfiguration.getTechnicalSupportConfigurationProperties()))
        .andExpect(model().attribute("accessibilityConfig",
            accessibilityStatementConfiguration.getAccessibilityStatementConfigurationProperties()));
  }

  @SecurityTest
  void renderAccessibilityStatement_withNoUser_assertStatusOk() throws Exception {
    mockMvc.perform(get(ReverseRouter.route(on(AccessibilityStatementController.class).getAccessibilityStatement())))
        .andExpect(status().isOk());
  }
}