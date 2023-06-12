package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileUploadService {
  private final FileUploadStorageService fileUploadStorageService;
  private final UploadedFilePersistenceService uploadedFilePersistenceService;
  private final FileUploadConfig fileUploadConfig;

  @Autowired
  public FileUploadService(FileUploadStorageService fileUploadStorageService,
                           UploadedFilePersistenceService uploadedFilePersistenceService,
                           FileUploadConfig fileUploadConfig) {
    this.fileUploadStorageService = fileUploadStorageService;
    this.uploadedFilePersistenceService = uploadedFilePersistenceService;
    this.fileUploadConfig = fileUploadConfig;
  }

  public void uploadFile(InputStream inputStream, UploadedFile uploadedFile) {
    fileUploadStorageService.uploadFile(inputStream, uploadedFile);
  }

  @Transactional
  public UploadedFile createUploadedFile(VirtualFolder virtualFolder, long fileSize, String filename, String contentType) {
    return uploadedFilePersistenceService.createUploadedFile(virtualFolder, fileSize, filename, contentType);
  }

  @Transactional
  public void deleteFile(UploadedFile uploadedFile) {
    uploadedFilePersistenceService.deleteFile(uploadedFile);
    fileUploadStorageService.deleteFile(uploadedFile);
  }

  public InputStream downloadFile(UploadedFile uploadedFile) {
    return fileUploadStorageService.downloadFile(uploadedFile);
  }

  public UploadedFile findUploadedFileOrThrow(UUID uploadedFileId) {
    return uploadedFilePersistenceService.findUploadedFileOrThrow(uploadedFileId);
  }

  public List<UploadedFileView> getUploadedFileViewList(List<UUID> fileUploadIdList) {
    return uploadedFilePersistenceService.getUploadedFilesByIdList(fileUploadIdList).stream()
        .map(this::createUploadedFileView)
        .toList();
  }

  @Transactional
  public void updateFileUploadDescriptions(List<FileUploadForm> fileUploadFormList) {
    fileUploadFormList
        .forEach(fileUploadForm -> {
          if (fileUploadForm.getUploadedFileId() != null) {
            var uploadedFile = findUploadedFileOrThrow(fileUploadForm.getUploadedFileId());
            uploadedFilePersistenceService.updateFileDescription(uploadedFile,
                fileUploadForm.getUploadedFileDescription());
          }
        });
  }

  public FileUploadTemplate buildFileUploadTemplate(String downloadUrl, String uploadUrl, String deleteUrl) {
    return buildFileUploadTemplate(downloadUrl, uploadUrl, deleteUrl, fileUploadConfig.allowedExtensions());
  }

  public FileUploadTemplate buildFileUploadTemplate(String downloadUrl, String uploadUrl, String deleteUrl,
                                                     String allowedExtensions) {
    return new FileUploadTemplate(
        downloadUrl,
        uploadUrl,
        deleteUrl,
        fileUploadConfig.maxAllowedSize().toString(),
        allowedExtensions
    );
  }

  public String sanitiseFilename(String filename) {
    return filename.replaceAll(fileUploadConfig.filenameDisallowedCharactersRegex(), "_");
  }

  public FileUploadForm createFileUploadForm(UploadedFile uploadedFile) {
    var fileUploadForm = new FileUploadForm();
    fileUploadForm.setUploadedFileId(uploadedFile.getId());
    fileUploadForm.setUploadedFileDescription(uploadedFile.getDescription());
    fileUploadForm.setUploadedFileInstant(uploadedFile.getUploadedTimeStamp());
    return fileUploadForm;
  }

  private UploadedFileView createUploadedFileView(UploadedFile uploadedFile) {
    return new UploadedFileView(
        uploadedFile.getId().toString(),
        uploadedFile.getFilename(),
        FileUploadUtils.fileSizeFormatter(uploadedFile.getFileSizeBytes()),
        uploadedFile.getDescription(),
        uploadedFile.getUploadedTimeStamp()
    );
  }
}
