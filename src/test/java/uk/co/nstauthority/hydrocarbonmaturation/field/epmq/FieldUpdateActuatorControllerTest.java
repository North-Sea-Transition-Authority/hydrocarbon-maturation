package uk.co.nstauthority.hydrocarbonmaturation.field.epmq;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.ActuatorConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;

@AutoConfigureMockMvc
@IntegrationTest
@EnableConfigurationProperties({ActuatorConfigurationProperties.class})
class FieldUpdateActuatorControllerTest {

  private static final String FIELD_UPDATED_MESSAGE_URL_FORMAT =
      "/actuator/field-updated-message/%s";

  @MockBean
  private FieldSnsService fieldSnsService;

  @MockBean
  private FieldPersistenceService fieldPersistenceService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ActuatorConfigurationProperties actuatorConfigurationProperties;

  @Test
  void publishMessage() throws Exception {
    var field = new Field();

    when(fieldPersistenceService.getField(1)).thenReturn(Optional.of(field));

    var actuatorUrl = FIELD_UPDATED_MESSAGE_URL_FORMAT.formatted(1);

    mockMvc.perform(
            post(actuatorUrl)
                .with(httpBasic("admin", actuatorConfigurationProperties.adminUserPassword()))
        )
        .andExpect(status().isOk());

    verify(fieldSnsService).publishFieldUpdatedSnsMessage(field);
  }

  @Test
  void publishMessage_fieldNotFound() throws Exception {
    when(fieldPersistenceService.getField(1)).thenReturn(Optional.empty());

    var actuatorUrl = FIELD_UPDATED_MESSAGE_URL_FORMAT.formatted(1);

    mockMvc.perform(
            post(actuatorUrl)
                .with(httpBasic("admin", actuatorConfigurationProperties.adminUserPassword()))
        )
        .andExpect(status().isBadRequest());

    verify(fieldSnsService, never()).publishFieldUpdatedSnsMessage(any(Field.class));
  }

  @SecurityTest
  void publishMessage_whenNotUnauthorized() throws Exception {
    var actuatorUrl = FIELD_UPDATED_MESSAGE_URL_FORMAT.formatted(1);

    mockMvc.perform(post(actuatorUrl).with(httpBasic("admin", "invalid-password")))
        .andExpect(status().isUnauthorized());

    verify(fieldSnsService, never()).publishFieldUpdatedSnsMessage(any(Field.class));
  }

}
