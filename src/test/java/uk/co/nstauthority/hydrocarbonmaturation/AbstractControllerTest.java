package uk.co.nstauthority.hydrocarbonmaturation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.nstauthority.hydrocarbonmaturation.branding.IncludeServiceBrandingConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.DefaultPageControllerAdvice;

@AutoConfigureMockMvc
@IncludeServiceBrandingConfigurationProperties
@ContextConfiguration(classes = DefaultPageControllerAdvice.class)
public abstract class AbstractControllerTest {

  @Autowired
  protected MockMvc mockMvc;


}