package uk.co.nstauthority.hydrocarbonmaturation.gis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.Unauthenticated;

@Controller
@RequestMapping("/devtool/migration/shapefiles")
@Profile("devtools")
@Unauthenticated
class ShapefileMigrationController {

  private final ShapefileMigrationService shapefileMigrationService;

  @Autowired
  ShapefileMigrationController(ShapefileMigrationService shapefileMigrationService) {
    this.shapefileMigrationService = shapefileMigrationService;
  }

  @GetMapping
  ResponseEntity<String> migrateShapefiles() {
    shapefileMigrationService.migrateShapefilesFromS3();

    return ResponseEntity.ok().body("OK");
  }
}
