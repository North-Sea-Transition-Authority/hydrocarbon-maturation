package uk.co.nstauthority.hydrocarbonmaturation.externalapi.fielddevelopmentplan;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.RealSecurityAbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = FieldDevelopmentPlanExternalController.class)
class FieldDevelopmentPlanExternalControllerTest extends RealSecurityAbstractControllerTest {

  private static final String PRE_SHARED_KEY = "testKey1";

  @MockBean
  FieldDevelopmentPlanExternalService fieldDevelopmentPlanExternalService;

  @Test
  void findByFieldIds() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    var fieldIds = Set.of(1, 2);

    var fdpaDto = new FieldDevelopmentPlanAddendumExternalDto(
        LocalDate.of(2024, 1, 1),
        "FDPA Title"
    );

    var fdpDtos = Map.of(
        1,
        new FieldDevelopmentPlanExternalDto(
            LocalDate.of(2024, 1, 1),
            "FDP Title 1",
            List.of(fdpaDto)
        ),
        2,
        new FieldDevelopmentPlanExternalDto(
            LocalDate.of(2024, 1, 1),
            "FDP Title 2",
            List.of()
        )
    );

    var resultJson = mapper.writeValueAsString(fdpDtos);

    when(fieldDevelopmentPlanExternalService.createFdpDtosByFieldIds(fieldIds)).thenReturn(fdpDtos);

    mockMvc.perform(get(
            ReverseRouter.route(on(FieldDevelopmentPlanExternalController.class).findByFieldIds(fieldIds))
        ).header("Authorization", String.format("Bearer %s", PRE_SHARED_KEY)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(resultJson));
  }

  @SecurityTest
  void findByFieldIds_NoBearerToken_AssertUnauthorized() throws Exception {
    mockMvc.perform(post(ReverseRouter.route(on(FieldDevelopmentPlanExternalController.class).findByFieldIds(Set.of()))))
        .andExpect(status().isUnauthorized())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }
}
