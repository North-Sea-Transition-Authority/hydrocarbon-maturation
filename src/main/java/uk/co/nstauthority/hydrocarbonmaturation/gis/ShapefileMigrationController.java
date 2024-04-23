package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import uk.co.fivium.fileuploadlibrary.s3.S3Exception;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.Unauthenticated;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.ListS3FilesResult;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Controller
@RequestMapping("/devtool/migration/shapefiles")
@Profile("devtools")
@Unauthenticated
class ShapefileMigrationController {
  private static final Logger LOGGER = LoggerFactory.getLogger(ShapefileMigrationController.class);

  private final ShapefileMigrationService shapefileMigrationService;

  @Autowired
  ShapefileMigrationController(ShapefileMigrationService shapefileMigrationService) {
    this.shapefileMigrationService = shapefileMigrationService;
  }

  @GetMapping
  ResponseEntity<String> migrateShapefiles(HttpServletRequest request,
                                           @RequestParam("continuationToken") @Nullable String continuationToken) {
    try {
      var result = shapefileMigrationService.migrateShapefilesFromS3(continuationToken);
      if (result.truncated()) {
        // redirect to same route with new continuation token
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(getRedirectRoute(request.getContextPath(), result))
            .build();
      }
    } catch (S3Exception | IOException e) {
      var message = "Failed to migrate shapefiles";
      LOGGER.error(message, e);
      return ResponseEntity.internalServerError().body(message);
    }

    return ResponseEntity.ok().body("Successfully migrated all shapefiles");
  }

  private static URI getRedirectRoute(String contextPath, ListS3FilesResult result) {
    var redirectUrl = contextPath + ReverseRouter.route(on(ShapefileMigrationController.class)
        .migrateShapefiles(null, UriUtils.encode(result.nextContinuationToken(), "UTF-8")));

    return URI.create(redirectUrl);
  }
}
