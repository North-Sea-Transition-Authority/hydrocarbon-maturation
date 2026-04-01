package uk.co.nstauthority.hydrocarbonmaturation.gis;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.RealSecurityAbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = ShapefileMigrationController.class)
class ShapefileMigrationControllerTest extends RealSecurityAbstractControllerTest {

  @MockitoBean
  ShapefileMigrationService shapefileMigrationService;

  @SecurityTest
  void assertRedirects() throws Exception {

    mockMvc.perform(
            get(ReverseRouter.route(on(ShapefileMigrationController.class).migrateShapefiles(null, null))))
        .andExpect(status().is3xxRedirection());

    verify(shapefileMigrationService, never()).migrateShapefilesFromS3(any());
  }
}