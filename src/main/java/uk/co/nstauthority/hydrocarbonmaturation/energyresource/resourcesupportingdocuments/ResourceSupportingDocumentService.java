package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments;

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
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;

@Service
public class ResourceSupportingDocumentService {

  private final FileService fileService;
  private final FileDocumentService fileDocumentService;

  @Autowired
  ResourceSupportingDocumentService(FileService fileService, FileDocumentService fileDocumentService) {
    this.fileService = fileService;
    this.fileDocumentService = fileDocumentService;
  }

  @Transactional
  public void saveDocuments(
      Resource resource,
      Collection<UploadedFileForm> uploadedFileForms,
      FileDocumentType fileDocumentType
  ) {
    fileDocumentService.saveDocuments(uploadedFileForms, fileDocumentType, getUsageId(resource), getUsageType());
  }

  public List<UploadedFile> getUploadedFiles(Resource resource, FileDocumentType documentType) {
    return fileService.findAll(getUsageId(resource), getUsageType(), documentType.name());
  }

  public List<UploadedFileForm> getUploadedFileForms(Resource resource, FileDocumentType documentType) {
    var uploadedFiles = getUploadedFiles(resource, documentType);
    return fileDocumentService.getUploadedFileForms(uploadedFiles);
  }

  public ResponseStatusException getFileNotFoundException(UUID fileId, Resource resource) {
    return fileDocumentService.getFileNotFoundException(fileId, getUsageType(), getUsageId(resource));
  }

  public void throwIfFileDoesNotBelongToResource(
      UploadedFile uploadedFile,
      Resource resource
  ) {
    fileDocumentService
        .throwIfFileDoesNotBelongToUsageType(uploadedFile, null, getUsageId(resource), getUsageType());
  }

  public FileUploadComponentAttributes fileUploadComponentAttributes(
      List<UploadedFileForm> existingFiles,
      FileDocumentType fileDocumentType
  ) {
    return fileDocumentService.getFileUploadComponentAttributes(existingFiles, fileDocumentType);
  }

  private String getUsageId(Resource resource) {
    return resource.getId().toString();
  }

  private String getUsageType() {
    return Resource.class.getSimpleName();
  }
}
