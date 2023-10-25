package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsLatestExtantSubmissionDetailVersion;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDeleteResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/supporting-documents")
@IsFromResponsibleTeam
@IsLatestExtantSubmissionDetailVersion
public class SubmissionSupportingDocumentsController {

  private final SupportingDocumentService supportingDocumentService;
  private final SubmissionDetailService submissionDetailService;
  private final FileUploadService fileUploadService;

  @Autowired
  public SubmissionSupportingDocumentsController(SupportingDocumentService supportingDocumentService,
                                                 SubmissionDetailService submissionDetailService,
                                                 FileUploadService fileUploadService) {
    this.supportingDocumentService = supportingDocumentService;
    this.submissionDetailService = submissionDetailService;
    this.fileUploadService = fileUploadService;
  }

  @PostMapping("upload/{supportingDocumentType}")
  @ResponseBody
  @HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
  public FileUploadResult upload(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                 @PathVariable("supportingDocumentType") SupportingDocumentType supportingDocumentType,
                                 @RequestParam("file") MultipartFile multipartFile) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return supportingDocumentService.processFileUpload(submissionDetail, supportingDocumentType, multipartFile);
  }

  @GetMapping("download/{uploadedFileId}")
  @ResponseBody
  @HasSubmissionStatus(statuses = {
      SubmissionDetailStatus.DRAFT,
      SubmissionDetailStatus.ACCEPTED,
      SubmissionDetailStatus.SUBMITTED}
  )
  public ResponseEntity<InputStreamResource> download(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                      @PathVariable("uploadedFileId") UUID uploadedFileId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var uploadedFile = supportingDocumentService.findUploadedFileOrThrow(submissionDetail, uploadedFileId);
    var inputStream = fileUploadService.downloadFile(uploadedFile);
    return FileUploadUtils.getFileResourceResponseEntity(uploadedFile, new InputStreamResource(inputStream));
  }

  @PostMapping("delete/{uploadedFileId}")
  @ResponseBody
  @HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
  public FileDeleteResult delete(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                 @PathVariable("uploadedFileId") UUID uploadedFileId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return supportingDocumentService.deleteFile(submissionDetail, uploadedFileId);
  }
}