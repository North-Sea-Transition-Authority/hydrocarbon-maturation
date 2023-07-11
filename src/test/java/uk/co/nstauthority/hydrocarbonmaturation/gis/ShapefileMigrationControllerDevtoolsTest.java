package uk.co.nstauthority.hydrocarbonmaturation.gis;


import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.RealSecurityAbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = ShapefileMigrationController.class)
@ActiveProfiles("devtools")
class ShapefileMigrationControllerDevtoolsTest extends RealSecurityAbstractControllerTest {

  @MockBean
  ShapefileMigrationService shapefileMigrationService;

  @SecurityTest
  void assertStatusOk() throws Exception {

    mockMvc.perform(
            get(ReverseRouter.route(on(ShapefileMigrationController.class).migrateShapefiles())))
        .andExpect(status().isOk())
        .andExpect(content().string("OK"));

    verify(shapefileMigrationService).migrateShapefilesFromS3();
  }
}