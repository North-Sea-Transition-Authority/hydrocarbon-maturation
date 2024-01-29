package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsLatestExtantSubmissionDetailVersion;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/supporting-documents")
@IsFromResponsibleTeam
@IsLatestExtantSubmissionDetailVersion
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
  @ResponseBody
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
}