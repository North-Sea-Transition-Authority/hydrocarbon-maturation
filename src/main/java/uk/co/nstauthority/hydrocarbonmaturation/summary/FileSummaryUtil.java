package uk.co.nstauthority.hydrocarbonmaturation.summary;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentsController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentsController;

public class FileSummaryUtil {
  private FileSummaryUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static List<SummaryFileViewRow> convertSubmissionSupportingFilesToSummaryRows(String key,
                                                                                       List<UploadedFile> files,
                                                                                       SubmissionDetail submissionDetail) {
    return files.stream()
        .map(uploadedFile -> {
          var downloadUrl = ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
              .download(submissionDetail.getId(), uploadedFile.getId()));
          return SummaryFileViewRow.from(key, uploadedFile, downloadUrl);
        })
        .toList();
  }

  public static List<SummaryFileViewRow> convertResourceSupportingFilesToSummaryRows(String key,
                                                                                     List<UploadedFile> files,
                                                                                     Resource resource) {
    return files.stream()
        .map(uploadedFile -> {
          var downloadUrl = ReverseRouter.route(on(ResourceSupportingDocumentsController.class)
              .download(resource.getId(), uploadedFile.getId()));
          return SummaryFileViewRow.from(key, uploadedFile, downloadUrl);
        })
        .toList();
  }
}
