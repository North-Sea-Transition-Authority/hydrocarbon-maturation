package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

import java.util.Objects;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.fivium.fileuploadlibrary.fds.FileDeleteResponse;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUsageType;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUsageUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.util.StringUtil;

@RestController
@RequestMapping("/submission-details/{submissionDetailId}/supporting-documents")
@IsFromResponsibleTeam
public class SubmissionSupportingDocumentsController {

  private final SubmissionSupportingDocumentService submissionSupportingDocumentService;
  private final SubmissionDetailService submissionDetailService;
  private final FileService fileService;

  @Autowired
  public SubmissionSupportingDocumentsController(SubmissionSupportingDocumentService submissionSupportingDocumentService,
                                                 SubmissionDetailService submissionDetailService,
                                                 FileService fileService) {
    this.submissionSupportingDocumentService = submissionSupportingDocumentService;
    this.submissionDetailService = submissionDetailService;
    this.fileService = fileService;
  }

  @GetMapping("download/{fileId}")
  @HasSubmissionStatus(statuses = {
      SubmissionDetailStatus.DRAFT,
      SubmissionDetailStatus.ACCEPTED,
      SubmissionDetailStatus.SUBMITTED}
  )
  public ResponseEntity<InputStreamResource> download(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                      @PathVariable("fileId") UUID fileId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var uploadedFile = fileService.find(fileId)
        .orElseThrow(() -> submissionSupportingDocumentService.getFileNotFoundException(fileId, submissionDetail));

    submissionSupportingDocumentService.throwIfFileDoesNotBelongToSubmissionDetail(uploadedFile, submissionDetail);

    return fileService.download(uploadedFile);
  }

  @PostMapping("/delete/{fileId}")
  public FileDeleteResponse delete(@PathVariable UUID submissionDetailId, @PathVariable String fileId, User user) {

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    var fileUuid = StringUtil.toUuid(fileId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Unable to convert file id [%s] to UUID".formatted(fileId)
        ));

    return fileService.find(fileUuid)
        .filter(uploadedFile -> canAccessFile(uploadedFile, user, submissionDetail))
        .map(fileService::delete)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "No file found with ID [%s] belonging to user [%d]".formatted(fileUuid, user.wuaId())
        ));
  }

  private boolean canAccessFile(UploadedFile uploadedFile, User user, SubmissionDetail submissionDetail) {
    return FileUsageUtil.hasNoUsage(uploadedFile) && fileBelongsToUser(uploadedFile, user)
        || fileBelongsToSubmissionDetail(uploadedFile, submissionDetail);
  }

  private boolean fileBelongsToSubmissionDetail(UploadedFile uploadedFile, SubmissionDetail submissionDetail) {
    return Objects.equals(uploadedFile.getUsageId(), submissionDetail.getId().toString())
        && Objects.equals(uploadedFile.getUsageType(), FileUsageType.SUBMISSION_DETAIL.getUsageType());
  }

  private boolean fileBelongsToUser(UploadedFile uploadedFile, User user) {
    return Objects.equals(uploadedFile.getUploadedBy(), user.wuaId().toString());
  }
}