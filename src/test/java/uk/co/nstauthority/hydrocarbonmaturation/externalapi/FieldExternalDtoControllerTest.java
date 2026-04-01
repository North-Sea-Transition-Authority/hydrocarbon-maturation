package uk.co.nstauthority.hydrocarbonmaturation.externalapi;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.co.nstauthority.hydrocarbonmaturation.RealSecurityAbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = FieldExternalDtoController.class)
class FieldExternalDtoControllerTest extends RealSecurityAbstractControllerTest {
  @MockitoBean
  private FieldExternalDtoService fieldExternalDtoService;
  private static final String PRE_SHARED_KEY = "testKey1";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void searchFields() throws Exception {
    var fieldIdentifier  = 1;
    var fieldName = "name";
    var fieldStatus = FieldStatus.STATUS_200;

    var result = Stream.of(
            FieldInternalDtoBuilder.builder()
                .withFieldIdentifier(fieldIdentifier)
                .withFieldName(fieldName)
                .withFieldStatus(fieldStatus)
                .withDeterminationStatus(DeterminationStatus.DETERMINED)
                .build()
        )
        .map(FieldExternalDto::fromInternal)
        .toList();

    var resultJson = MAPPER.writeValueAsString(result);

    var fieldStatusIn = Collections.singletonList("STATUS200");
    when(fieldExternalDtoService.searchFields(
        List.of(fieldIdentifier),
        fieldName,
        fieldStatusIn
    )).thenReturn(result);

    mockMvc.perform(get(
            ReverseRouter.route(on(FieldExternalDtoController.class).searchFields(
                Collections.singletonList(fieldIdentifier),
                fieldName,
                fieldStatusIn
            ))).header("Authorization", String.format("Bearer %s", PRE_SHARED_KEY)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(resultJson));
  }

  @SecurityTest
  void searchFields_NoBearerToken_AssertUnauthorized() throws Exception {
    mockMvc.perform(post(
            ReverseRouter.route(on(FieldExternalDtoController.class)
                .searchFields(null, null, null))))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }
}