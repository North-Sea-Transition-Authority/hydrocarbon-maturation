package uk.co.nstauthority.hydrocarbonmaturation.gis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.io.IOException;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.util.ResourceUtils;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionIntegrationTestUtils;

@IntegrationTest
@Disabled
// Before any test runs, 10 legacy resources are imported (insert_migration_padi_data.sql)
@Sql(scripts = {"/manualscripts/padimigration/00010_padi_table_create.sql", "/shapefiles/insert_migration_padi_data.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/shapefiles/teardown_migration_data.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class ShapefileMigrationIntegrationTest {
  private static final String MIGRATION_SHAPEFILES_DIR_LOCATION = ResourceUtils.CLASSPATH_URL_PREFIX + "shapefiles/migration/";

  @Autowired
  private ShapefileMigrationService shapefileMigrationService;
  @Autowired
  private ShapefileMigrationProgressRepository shapefileMigrationProgressRepository;
  @Autowired
  private EntityManager entityManager;
  @Autowired
  private SubmissionSupportingDocumentService supportingDocumentService;

  @Transactional
  @Test
  void migrateLegacyShapefile_ElevenShapes_NineMigrate_TwoFail() throws IOException {
//    doNothing().when(fileUploadStorageService).uploadFile(any(InputStream.class), any());
    // create submission detail for each legacy resource but one (resDataId = 44114, pid = "PID0007075")
    var resDataIdToIgnore = 44114;
    var submissionDetailList = createSubmissionDetailForEachResDataIdExcept(resDataIdToIgnore);

    // check that all submission details do not have a supporting document for the relevant document type
    submissionDetailList
        .forEach(submissionDetail -> {
          var supportingDocumentsForType = supportingDocumentService.getUploadedFiles(submissionDetail, ShapefileMigrationService.SUPPORTING_DOCUMENT_TYPE);
          assertThat(supportingDocumentsForType).isEmpty();
        });

    // migrate 11 shapefiles
    shapefileMigrationService.migrateShapefilesInDirectory(MIGRATION_SHAPEFILES_DIR_LOCATION);

    assertThat(shapefileMigrationProgressRepository.findAll())
        .hasSize(11)
        .extracting(
            ShapefileMigrationProgress::getPolygonId,
            ShapefileMigrationProgress::getStatus
        )
        .contains(
            tuple("PID0007075", ShapefileMigrationStatus.FAILED_LEGACY_DATA_NOT_LINKED_TO_SUBMISSION_DETAIL),
            tuple("PID0007226", ShapefileMigrationStatus.COMPLETED),
            tuple("PID0007871", ShapefileMigrationStatus.FAILED_LEGACY_DATA_NOT_FOUND),
            tuple("PID0002045", ShapefileMigrationStatus.COMPLETED),
            tuple("PID0001465", ShapefileMigrationStatus.COMPLETED),
            tuple("PID0011673", ShapefileMigrationStatus.COMPLETED),
            tuple("PID0011672", ShapefileMigrationStatus.COMPLETED),
            tuple("PID0007589", ShapefileMigrationStatus.COMPLETED),
            tuple("PID0007587", ShapefileMigrationStatus.COMPLETED),
            tuple("PID0007586", ShapefileMigrationStatus.COMPLETED),
            tuple("PID0007588", ShapefileMigrationStatus.COMPLETED)
        );

    // check that all submission details have a supporting document for the relevant document type
    submissionDetailList
        .forEach(submissionDetail -> {
          var supportingDocumentsForType = supportingDocumentService.getUploadedFiles(submissionDetail, ShapefileMigrationService.SUPPORTING_DOCUMENT_TYPE);
          assertThat(supportingDocumentsForType).isNotEmpty();
        });
  }

  private List<SubmissionDetail> createSubmissionDetailForEachResDataIdExcept(int resDataIdToIgnore) {
    return shapefileMigrationProgressRepository.findAllResDatIdFromPadiResDataHeader().stream()
        .filter(id -> !id.equals(resDataIdToIgnore))
        .map(id -> {
          var resource = SubmissionIntegrationTestUtils.createResource(entityManager, ResourceType.PROSPECT);
          var submission = SubmissionIntegrationTestUtils.createSubmission(resource, entityManager);
          var submissionDetail = SubmissionIntegrationTestUtils.createSubmissionDetail(submission, SubmissionType.CREATE_PROSPECT, entityManager);
          submissionDetail.setLegacyResDataId(id);
          return submissionDetail;
        })
        .toList();
  }
}