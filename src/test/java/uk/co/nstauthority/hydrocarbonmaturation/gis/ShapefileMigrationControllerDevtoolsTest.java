package uk.co.nstauthority.hydrocarbonmaturation.gis;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import uk.co.fivium.fileuploadlibrary.s3.S3Exception;
import uk.co.nstauthority.hydrocarbonmaturation.RealSecurityAbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.ListS3FilesResult;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ContextConfiguration(classes = ShapefileMigrationController.class)
@ActiveProfiles("devtools")
class ShapefileMigrationControllerDevtoolsTest extends RealSecurityAbstractControllerTest {

  @MockBean
  ShapefileMigrationService shapefileMigrationService;

  @SecurityTest
  void assertStatusOk_WhenResponseNotTruncated() throws Exception {
    var listS3FilesResult = Mockito.mock(ListS3FilesResult.class);
    when(listS3FilesResult.truncated()).thenReturn(false);
    when(shapefileMigrationService.migrateShapefilesFromS3(null)).thenReturn(listS3FilesResult);

    mockMvc.perform(
            get(ReverseRouter.route(on(ShapefileMigrationController.class).migrateShapefiles(null, null))))
        .andExpect(status().isOk())
        .andExpect(content().string("Successfully migrated all shapefiles"));
  }

  @Test
  void assertStatusFound_WhenResponseTruncated() throws Exception {

    var nextContinuationToken = "token2";
    var listS3FilesResult = Mockito.mock(ListS3FilesResult.class);
    when(listS3FilesResult.truncated()).thenReturn(true);
    when(listS3FilesResult.nextContinuationToken()).thenReturn(nextContinuationToken);

    var continuationToken = "token";
    when(shapefileMigrationService.migrateShapefilesFromS3(continuationToken)).thenReturn(listS3FilesResult);

    mockMvc.perform(
            get(ReverseRouter.route(on(ShapefileMigrationController.class).migrateShapefiles(null, continuationToken))))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ShapefileMigrationController.class).migrateShapefiles(null, nextContinuationToken))));
  }

  @Test
  void assertStatusInternalServerError_WhenExceptionIsThrown() throws Exception {

    when(shapefileMigrationService.migrateShapefilesFromS3(any())).thenThrow(S3Exception.class);

    mockMvc.perform(
            get(ReverseRouter.route(on(ShapefileMigrationController.class).migrateShapefiles(null, null))))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Failed to migrate shapefiles"));
  }
}