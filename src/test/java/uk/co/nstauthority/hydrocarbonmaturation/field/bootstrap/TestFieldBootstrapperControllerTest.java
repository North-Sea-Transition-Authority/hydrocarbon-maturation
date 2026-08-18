package uk.co.nstauthority.hydrocarbonmaturation.field.bootstrap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.RealSecurityAbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldForm;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@ActiveProfiles({"test", "test-field-bootstrap"})
@ContextConfiguration(classes = TestFieldBootstrapperController.class)
class TestFieldBootstrapperControllerTest extends RealSecurityAbstractControllerTest {

  private static final String URL = "/api/internal/v1/bootstrap-test-fields";

  private static final String PRE_SHARED_KEY = "testKey1";

  @MockitoBean
  private TestFieldBootstrapService testFieldBootstrapService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void bootstrapTestFields_returnsCreated() throws Exception {
    var request = new BootstrapFieldRequest(
        "Test Field",
        OnshoreOffshore.OFFSHORE,
        GeographicalArea.NORTHERN_NORTH_SEA,
        null,
        null,
        null
    );

    when(testFieldBootstrapService.bootstrapField(any(BootstrapFieldRequest.class)))
        .thenReturn(Optional.of("Test Field"));

    mockMvc.perform(post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(List.of(request)))
            .header("Authorization", String.format("Bearer %s", PRE_SHARED_KEY)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$[0]").value("Test Field"));
  }

  @Test
  void bootstrapTestFields_whenFieldAlreadyExists_returnsCreatedWithEmptyList() throws Exception {
    var request = new BootstrapFieldRequest(
        "Test Field",
        OnshoreOffshore.OFFSHORE,
        GeographicalArea.NORTHERN_NORTH_SEA,
        null,
        null,
        null
    );

    when(testFieldBootstrapService.bootstrapField(any(BootstrapFieldRequest.class)))
        .thenReturn(Optional.empty());

    mockMvc.perform(post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(List.of(request)))
            .header("Authorization", String.format("Bearer %s", PRE_SHARED_KEY)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  void bootstrapTestFields_whenValidationFails_returnsBadRequest() throws Exception {
    var request = new BootstrapFieldRequest(
        "Test Field",
        OnshoreOffshore.OFFSHORE,
        GeographicalArea.NORTHERN_NORTH_SEA,
        null,
        DeterminationStatus.DETERMINED,
        FieldStatus.STATUS_400
    );

    var bindingResult = new BeanPropertyBindingResult(new FieldForm(), "form");
    bindingResult.rejectValue("correctedLegalFieldName", "required", "Field name is required");

    when(testFieldBootstrapService.bootstrapField(any(BootstrapFieldRequest.class)))
        .thenThrow(new BootstrapFieldValidationException(bindingResult));

    mockMvc.perform(post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(List.of(request)))
            .header("Authorization", String.format("Bearer %s", PRE_SHARED_KEY)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.correctedLegalFieldName[0]").value("Field name is required"));
  }

  @SecurityTest
  void bootstrapTestFields_whenNoBearerToken_returnsUnauthorized() throws Exception {
    var request = new BootstrapFieldRequest(
        "Test Field",
        OnshoreOffshore.OFFSHORE,
        GeographicalArea.NORTHERN_NORTH_SEA,
        null,
        null,
        null
    );

    mockMvc.perform(post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(List.of(request))))
        .andExpect(status().isUnauthorized());
  }
}
