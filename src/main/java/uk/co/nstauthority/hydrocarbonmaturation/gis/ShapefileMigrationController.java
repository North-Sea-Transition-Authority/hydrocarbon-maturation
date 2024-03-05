package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.co.fivium.fileuploadlibrary.s3.S3Exception;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.Unauthenticated;

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
  ResponseEntity<String> migrateShapefiles() {
    try {
      shapefileMigrationService.migrateShapefilesFromS3();
    } catch (S3Exception | IOException e) {
      var message = "Failed to migrate shapefiles";
      LOGGER.error(message, e);
      return ResponseEntity.internalServerError().body(message);
    }

    return ResponseEntity.ok().body("OK");
  }
}
