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
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatusService;

@AutoConfigureMockMvc
@IntegrationTest
@EnableConfigurationProperties({ActuatorConfigurationProperties.class})
class FieldStatusActuatorControllerTest {

  private static final String FIELD_STATUS_MESSAGE_URL_FORMAT =
      "/actuator/field-status-message/%s";

  @MockBean
  private FieldStatusSnsService fieldStatusSnsService;

  @MockBean
  private FieldPersistenceService fieldPersistenceService;

  @MockBean
  private FieldStatusService fieldStatusService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ActuatorConfigurationProperties actuatorConfigurationProperties;

  @Test
  void publishMessage() throws Exception {
    var fieldId = 1;
    var field = new Field();
    field.setFieldIdentifier(fieldId);

    when(fieldPersistenceService.getField(fieldId)).thenReturn(Optional.of(field));
    when(fieldStatusService.getMostRecentFieldStatus(field)).thenReturn(Optional.of(FieldStatus.STATUS_000));

    var actuatorUrl = FIELD_STATUS_MESSAGE_URL_FORMAT.formatted(fieldId);

    mockMvc.perform(
            post(actuatorUrl)
                .with(httpBasic("admin", actuatorConfigurationProperties.adminUserPassword()))
        )
        .andExpect(status().isOk());

    verify(fieldStatusSnsService).publishFieldStatusUpdateSnsMessage(fieldId, FieldStatus.STATUS_000.getStatusValue());
  }

  @Test
  void publishMessage_fieldNotFound() throws Exception {
    when(fieldPersistenceService.getField(1)).thenReturn(Optional.empty());

    var actuatorUrl = FIELD_STATUS_MESSAGE_URL_FORMAT.formatted(1);

    mockMvc.perform(
            post(actuatorUrl)
                .with(httpBasic("admin", actuatorConfigurationProperties.adminUserPassword()))
        )
        .andExpect(status().isBadRequest());

    verify(fieldStatusSnsService, never()).publishFieldStatusUpdateSnsMessage(any(Integer.class), any(Integer.class));
  }

  @Test
  void publishMessage_statusNotFound() throws Exception {
    var fieldId = 1;
    var field = new Field();
    field.setFieldIdentifier(fieldId);

    when(fieldPersistenceService.getField(fieldId)).thenReturn(Optional.of(field));
    when(fieldStatusService.getMostRecentFieldStatus(field)).thenReturn(Optional.empty());

    var actuatorUrl = FIELD_STATUS_MESSAGE_URL_FORMAT.formatted(fieldId);

    mockMvc.perform(
            post(actuatorUrl)
                .with(httpBasic("admin", actuatorConfigurationProperties.adminUserPassword()))
        )
        .andExpect(status().isBadRequest());

    verify(fieldStatusSnsService, never()).publishFieldStatusUpdateSnsMessage(any(Integer.class), any(Integer.class));
  }

  @SecurityTest
  void publishMessage_whenNotUnauthorized() throws Exception {
    var actuatorUrl = FIELD_STATUS_MESSAGE_URL_FORMAT.formatted(1);

    mockMvc.perform(post(actuatorUrl).with(httpBasic("admin", "invalid-password")))
        .andExpect(status().isUnauthorized());

    verify(fieldStatusSnsService, never()).publishFieldStatusUpdateSnsMessage(any(Integer.class), any(Integer.class));
  }

}
