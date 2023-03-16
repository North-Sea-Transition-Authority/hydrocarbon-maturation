package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDeleteResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadTemplate;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadValidationService;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFileView;
import uk.co.nstauthority.hydrocarbonmaturation.file.VirtualFolder;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Service
public class ResourceSupportingDocumentService {

  private final FileUploadService fileUploadService;
  private final FileUploadValidationService fileUploadValidationService;
  private final ResourceSupportingDocumentRepository resourceSupportingDocumentRepository;

  @Autowired
  public ResourceSupportingDocumentService(FileUploadService fileUploadService,
                                           FileUploadValidationService fileUploadValidationService,
                                           ResourceSupportingDocumentRepository resourceSupportingDocumentRepository) {
    this.fileUploadService = fileUploadService;
    this.fileUploadValidationService = fileUploadValidationService;
    this.resourceSupportingDocumentRepository = resourceSupportingDocumentRepository;
  }

  @Transactional
  public FileUploadResult processFileUpload(Resource resource, ResourceSupportingDocumentType resourceSupportingDocumentType,
                                            MultipartFile multipartFile) {
    var fileSize = multipartFile.getSize();
    var filename = fileUploadService.sanitiseFilename(Objects.requireNonNull(multipartFile.getOriginalFilename()));
    var contentType = multipartFile.getContentType();

    var uploadErrorType = fileUploadValidationService.validateFileUpload(multipartFile, fileSize, filename);
    if (uploadErrorType != null) {
      return FileUploadResult.error(filename, multipartFile, uploadErrorType);
    }

    var uploadedFile = fileUploadService.createUploadedFile(
        VirtualFolder.SUPPORTING_DOCUMENTS, fileSize, filename, contentType);

    var resourceSupportingDocument = new ResourceSupportingDocument();
    resourceSupportingDocument.setResource(resource);
    resourceSupportingDocument.setResourceSupportingDocumentType(resourceSupportingDocumentType);
    resourceSupportingDocument.setUploadedFile(uploadedFile);

    resourceSupportingDocumentRepository.save(resourceSupportingDocument);

    // Persist uploaded file and ResourceSupporting document records first,
    // so if it fails, we don't upload. If upload fails, we will roll back
    fileUploadService.uploadFile(multipartFile, uploadedFile);

    return FileUploadResult.valid(uploadedFile.getId().toString(), filename, multipartFile);
  }

  public List<FileUploadForm> getFileUploadFormListForSubmissionAndType(
      Resource resource,
      ResourceSupportingDocumentType resourceSupportingDocumentType) {
    return resourceSupportingDocumentRepository
        .findAllByResourceAndResourceSupportingDocumentType(resource, resourceSupportingDocumentType).stream()
        .map(ResourceSupportingDocument::getUploadedFile)
        .map(fileUploadService::createFileUploadForm)
        .toList();
  }

  public List<ResourceSupportingDocument> getResourceSupportingDocumentsForType(
      Resource resource, ResourceSupportingDocumentType resourceSupportingDocumentType) {
    return resourceSupportingDocumentRepository
        .findAllByResourceAndResourceSupportingDocumentType(resource, resourceSupportingDocumentType);
  }

  public List<UploadedFileView> getUploadedFileViewList(List<UUID> fileUploadIdList) {
    return fileUploadService.getUploadedFileViewList(fileUploadIdList);
  }

  public FileUploadTemplate buildFileUploadTemplate(
      UUID resourceId, ResourceSupportingDocumentType resourceSupportingDocumentType) {
    return fileUploadService.buildFileUploadTemplate(
        ReverseRouter.route(on(ResourceSupportingDocumentsController.class).download(resourceId, null)),
        ReverseRouter.route(
            on(ResourceSupportingDocumentsController.class).upload(resourceId, resourceSupportingDocumentType, null)),
        ReverseRouter.route(on(ResourceSupportingDocumentsController.class).delete(resourceId, null))
    );
  }

  @Transactional
  public FileDeleteResult deleteFile(Resource resource, UUID uploadedFileId) {
    var uploadedFile = fileUploadService.findUploadedFileOrThrow(uploadedFileId);
    var resourceSupportingDocuments = resourceSupportingDocumentRepository
        .findAllByUploadedFile(uploadedFile);

    var resourceSupportingDocument =
        resourceSupportingDocumentRepository.findByResourceAndUploadedFile(resource, uploadedFile);
    resourceSupportingDocumentRepository.delete(resourceSupportingDocument);

    // Check if the uploaded file is associated with other resource ResourceSupportingDocuments and only delete if it is not
    if (resourceSupportingDocuments.size() == 1) {
      fileUploadService.deleteFile(uploadedFile);
    }
    // TODO HM-218 add logging for else size is greater than one

    return FileDeleteResult.success(uploadedFile.getId().toString());
  }

  @Transactional
  public void updateResourceSupportingDocumentFileDescriptions(List<FileUploadForm> fileUploadFormList) {
    fileUploadService.updateFileUploadDescriptions(fileUploadFormList);
  }

  UploadedFile findUploadedFileOrThrow(Resource resource, UUID uploadedFileId) {
    var uploadedFile = fileUploadService.findUploadedFileOrThrow(uploadedFileId);
    var resourceSupportingDocument = resourceSupportingDocumentRepository
        .findByResourceAndUploadedFile(resource, uploadedFile);
    return resourceSupportingDocument.getUploadedFile();
  }
}
