package uk.co.nstauthority.hydrocarbonmaturation.gis;


import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.RealSecurityAbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = ShapefileMigrationController.class)
class ShapefileMigrationControllerTest extends RealSecurityAbstractControllerTest {

  @MockBean
  ShapefileMigrationService shapefileMigrationService;

  @SecurityTest
  void assertRedirects() throws Exception {

    mockMvc.perform(
            get(ReverseRouter.route(on(ShapefileMigrationController.class).migrateShapefiles())))
        .andExpect(status().is3xxRedirection());

    verify(shapefileMigrationService, never()).migrateShapefilesFromS3();
  }
}