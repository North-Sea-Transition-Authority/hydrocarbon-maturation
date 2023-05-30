package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;

@IntegrationTest
@Sql(scripts = {"/manualscripts/padimigration/0001_padi_table_create.sql", "/shapefiles/insert_migration_padi_data.sql"})
class ShapefileMigrationIntegrationTest {
  private static final String MIGRATION_SHAPEFILE_ZIP_FILENAME = "migration.zip";
  private static final String MIGRATION_SHAPEFILE_ZIP_LOCATION = "classpath:shapefiles/" + MIGRATION_SHAPEFILE_ZIP_FILENAME;

  @Autowired
  private ShapefileMigrationService shapefileMigrationService;

  @Autowired
  private ShapefileZipService shapefileZipService;

  @Autowired
  private ShapefileMigrationProgressRepository shapefileMigrationProgressRepository;

  @Test
  void migrateLegacyShapefile() throws IOException {
    shapefileMigrationService.migrateLegacyShapefile(getShapefileZipContents());

    var migrationProgresses = shapefileMigrationProgressRepository.findAll();

    assertThat(migrationProgresses)
        .hasSize(10)
        .allMatch(shapefileMigrationProgress -> shapefileMigrationProgress.getStatus() == ShapefileMigrationStatus.COMPLETED);
  }

  private ShapefileZipContents getShapefileZipContents() throws IOException {
    try (var shapefileZipStream = ShapefileTestUtils.getFileInputStream(MIGRATION_SHAPEFILE_ZIP_LOCATION)) {
      return shapefileZipService.getShapefileZipContents(shapefileZipStream, MIGRATION_SHAPEFILE_ZIP_FILENAME);
    }
  }
}