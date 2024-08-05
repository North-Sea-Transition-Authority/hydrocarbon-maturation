package uk.co.nstauthority.hydrocarbonmaturation.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentsController;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFileTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentsController;

class FileSummaryUtilTest {

  private static final UploadedFile UPLOADED_FILE = UploadedFileTestUtil.builder().build();
  private static final SubmissionDetail SUBMISSION_DETAIL = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  private static final Resource RESOURCE = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
  private static final String KEY = "key";

  @Test
  void convertSubmissionSupportingFilesToSummaryRows() {
    assertThat(FileSummaryUtil.convertSubmissionSupportingFilesToSummaryRows(
        KEY, List.of(UPLOADED_FILE), SUBMISSION_DETAIL))
        .extracting(
            SummaryFileViewRow::key,
            row -> row.fileView().filename(),
            row -> row.fileView().description(),
            row -> row.fileView().downloadUrl()
        )
        .containsExactlyInAnyOrder(Tuple.tuple(
            KEY,
            UPLOADED_FILE.getName(),
            UPLOADED_FILE.getDescription(),
            ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
                .download(SUBMISSION_DETAIL.getId(), UPLOADED_FILE.getId()))
        ));
  }

  @Test
  void convertResourceSupportingFilesToSummaryRows() {
    assertThat(FileSummaryUtil.convertResourceSupportingFilesToSummaryRows(
        KEY, List.of(UPLOADED_FILE), RESOURCE))
        .extracting(
            SummaryFileViewRow::key,
            row -> row.fileView().filename(),
            row -> row.fileView().description(),
            row -> row.fileView().downloadUrl()
        )
        .containsExactlyInAnyOrder(Tuple.tuple(
            KEY,
            UPLOADED_FILE.getName(),
            UPLOADED_FILE.getDescription(),
            ReverseRouter.route(on(ResourceSupportingDocumentsController.class)
                .download(RESOURCE.getId(), UPLOADED_FILE.getId()))
        ));
  }
}