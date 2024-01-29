package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.fivium.fileuploadlibrary.fds.FileUploadComponentAttributes;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class SubmissionSupportingDocumentService {

  private final FileService fileService;
  private final FileDocumentService fileDocumentService;

  @Autowired
  SubmissionSupportingDocumentService(FileService fileService, FileDocumentService fileDocumentService) {
    this.fileService = fileService;
    this.fileDocumentService = fileDocumentService;
  }

  @Transactional
  public void saveDocuments(
      SubmissionDetail submissionDetail,
      Collection<UploadedFileForm> uploadedFileForms,
      FileDocumentType fileDocumentType
  ) {
    fileDocumentService.saveDocuments(uploadedFileForms, fileDocumentType, getUsageId(submissionDetail), getUsageType());
  }

  public List<UploadedFile> getUploadedFiles(SubmissionDetail submissionDetail, FileDocumentType documentType) {
    return fileService.findAll(getUsageId(submissionDetail), getUsageType(), documentType.name());
  }

  public List<UploadedFileForm> getUploadedFileForms(SubmissionDetail submissionDetail, FileDocumentType documentType) {
    var uploadedFiles = getUploadedFiles(submissionDetail, documentType);
    return fileDocumentService.getUploadedFileForms(uploadedFiles);
  }

  ResponseStatusException getFileNotFoundException(UUID fileId, SubmissionDetail submissionDetail) {
    return fileDocumentService.getFileNotFoundException(fileId, getUsageType(), getUsageId(submissionDetail));
  }

  void throwIfFileDoesNotBelongToSubmissionDetail(UploadedFile uploadedFile, SubmissionDetail submissionDetail) {
    fileDocumentService
        .throwIfFileDoesNotBelongToUsageType(uploadedFile, null, getUsageId(submissionDetail), getUsageType());
  }

  public FileUploadComponentAttributes fileUploadComponentAttributes(
      List<UploadedFileForm> existingFiles,
      FileDocumentType fileDocumentType
  ) {
    return fileDocumentService.fileUploadComponentAttributes(existingFiles, fileDocumentType);
  }

  public void copyUploadedFiles(SubmissionDetail oldSubmissionDetail,
                                SubmissionDetail newSubmissionDetail,
                                FileDocumentType fileDocumentType) {
    getUploadedFiles(
        oldSubmissionDetail,
        fileDocumentType
    ).forEach(uploadedFile ->
        fileService.copy(
            uploadedFile,
            usageBuilder -> fileDocumentService.buildFileUsage(
                usageBuilder,
                fileDocumentType.name(),
                getUsageId(newSubmissionDetail),
                getUsageType()
            )
        )
    );
  }

  public void deleteFile(UUID fileId) {
    fileService.find(fileId).ifPresent(fileService::delete);
  }

  private static String getUsageId(SubmissionDetail submissionDetail) {
    return submissionDetail.getId().toString();
  }

  private static String getUsageType() {
    return SubmissionDetail.class.getSimpleName();
  }
}
