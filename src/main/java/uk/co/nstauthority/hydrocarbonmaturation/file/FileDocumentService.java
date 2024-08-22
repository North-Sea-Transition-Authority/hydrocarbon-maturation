package uk.co.nstauthority.hydrocarbonmaturation.file;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import uk.co.fivium.fileuploadlibrary.FileUploadLibraryUtils;
import uk.co.fivium.fileuploadlibrary.core.FileService;
import uk.co.fivium.fileuploadlibrary.core.FileUsage;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.fivium.fileuploadlibrary.fds.FileUploadComponentAttributes;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Service
public class FileDocumentService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileDocumentService.class);

  private final FileService fileService;

  @Autowired
  public FileDocumentService(FileService fileService) {
    this.fileService = fileService;
  }

  public FileUploadComponentAttributes getFileUploadComponentAttributes(
      List<UploadedFileForm> existingFiles,
      FileDocumentType fileDocumentType
  ) {
    var controller = UnlinkedFileController.class;

    var fileUploadAttributes = fileService.getFileUploadAttributes();

    fileDocumentType.getAllowedExtensions().ifPresent(fileUploadAttributes::withAllowedExtensions);

    return fileUploadAttributes
        .withUploadUrl(ReverseRouter.route(on(controller).upload(null, fileDocumentType.name(), null)))
        .withDownloadUrl(ReverseRouter.route(on(controller).download(null, null)))
        .withDeleteUrl(ReverseRouter.route(on(controller).delete(null, null)))
        .withExistingFiles(existingFiles)
        .build();
  }

  public FileUploadComponentAttributes.Builder getFileUploadComponentAttributesBuilder(
      List<UploadedFileForm> existingFiles,
      FileDocumentType fileDocumentType
  ) {
    var fileUploadAttributes = fileService.getFileUploadAttributes();

    fileDocumentType.getAllowedExtensions().ifPresent(fileUploadAttributes::withAllowedExtensions);

    return fileUploadAttributes
        .withExistingFiles(existingFiles);
  }

  public ResponseStatusException getFileNotFoundException(UUID fileId, String usageType, String usageId) {
    return new ResponseStatusException(NOT_FOUND, "File %s does not exist for %s %s"
        .formatted(fileId, usageType, usageId));
  }

  public void throwIfFileDoesNotBelongToUsageType(
      UploadedFile uploadedFile,
      @Nullable String documentType,
      String usageId,
      String usageType
  ) {
    if (Objects.isNull(uploadedFile.getUsageId())
        && Objects.isNull(uploadedFile.getUsageType())
        && Objects.isNull(uploadedFile.getDocumentType())) {
      return;
    }
    var logMessage = "An attempt was made to download a file not linked to the correct %s".formatted(usageType);

    if (!uploadedFile.getUsageId().equals(usageId)) {
      LOGGER.warn(logMessage);
      throw getFileNotFoundException(uploadedFile.getId(), usageType, usageId);
    }

    if (!uploadedFile.getUsageType().equals(usageType)) {
      LOGGER.warn(logMessage);
      throw getFileNotFoundException(uploadedFile.getId(), usageType, usageId);
    }

    if (documentType != null && (!uploadedFile.getDocumentType().equals(documentType))) {
      LOGGER.warn(logMessage);
      throw getFileNotFoundException(uploadedFile.getId(), usageType, usageId);
    }
  }

  public List<UploadedFileForm> getUploadedFileForms(List<UploadedFile> uploadedFiles) {
    return uploadedFiles.stream()
        .map(FileUploadLibraryUtils::asForm)
        .toList();
  }

  @Transactional
  public void saveDocuments(Collection<UploadedFileForm> uploadedFileForms,
                            FileDocumentType fileDocumentType,
                            String usageId,
                            String usageType) {
    var fileIds = uploadedFileForms.stream()
        .map(UploadedFileForm::getFileId)
        .toList();

    var uploadedFiles = fileService.findAll(fileIds);

    uploadedFiles.forEach(uploadedFile ->
        throwIfFileDoesNotBelongToUsageType(uploadedFile, fileDocumentType.name(), usageId, usageType));

    var descriptions = FileUploadLibraryUtils.getFileDescriptionsByFileId(uploadedFileForms);

    for (var uploadedFile : uploadedFiles) {
      fileService.updateUsageAndDescription(
          uploadedFile,
          usageBuilder -> buildFileUsage(usageBuilder, fileDocumentType.name(), usageId, usageType),
          descriptions.get(uploadedFile.getId())
      );
    }
  }

  public FileUsage buildFileUsage(
      FileUsage.Builder fileUsageBuilder,
      String documentType,
      String usageId,
      String usageType
  ) {
    return fileUsageBuilder
        .withUsageId(usageId)
        .withUsageType(usageType)
        .withDocumentType(documentType)
        .build();
  }
}
