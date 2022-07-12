package uk.co.nstauthority.hydrocarbonmaturation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.nstauthority.hydrocarbonmaturation.branding.IncludeServiceBrandingConfigurationProperties;

@AutoConfigureMockMvc
@IncludeServiceBrandingConfigurationProperties
public abstract class AbstractControllerTest {

  @Autowired
  protected MockMvc mockMvc;

}