package uk.co.nstauthority.hydrocarbonmaturation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.nstauthority.hydrocarbonmaturation.branding.IncludeServiceBrandingConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.WebMvcConfiguration;

@WebMvcTest
@Import(value = {WebMvcConfiguration.class, AbstractControllerTest.TestConfig.class})
@ActiveProfiles("test")
@IncludeServiceBrandingConfigurationProperties
@WithDefaultPageControllerAdvice
public abstract class AbstractControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @TestConfiguration
  public static class TestConfig {

    @Bean
    public ControllerHelperService controllerHelperService() {
      return new ControllerHelperService();
    }

  }

}