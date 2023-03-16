package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDeleteResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadUtils;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/resource/{resourceId}/resource-supporting-documents")
@HasPermission(permissions = RolePermission.EXPLORATION)
public class ResourceSupportingDocumentsController {

  private final ResourceSupportingDocumentService resourceSupportingDocumentService;
  private final ResourceService resourceService;
  private final FileUploadService fileUploadService;

  @Autowired
  ResourceSupportingDocumentsController(ResourceSupportingDocumentService resourceSupportingDocumentService,
                                        ResourceService resourceService, FileUploadService fileUploadService) {
    this.resourceSupportingDocumentService = resourceSupportingDocumentService;
    this.resourceService = resourceService;
    this.fileUploadService = fileUploadService;
  }

  @PostMapping("upload/{resourceSupportingDocumentType}")
  @ResponseBody
  public FileUploadResult upload(
      @PathVariable("resourceId") UUID resourceId,
      @PathVariable("resourceSupportingDocumentType") ResourceSupportingDocumentType resourceSupportingDocumentType,
      @RequestParam("file") MultipartFile multipartFile) {
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find a resource with id %s"
                .formatted(resourceId)));
    return resourceSupportingDocumentService.processFileUpload(
        resource, resourceSupportingDocumentType, multipartFile);
  }

  @GetMapping("download/{uploadedFileId}")
  @ResponseBody
  public ResponseEntity<InputStreamResource> download(@PathVariable("resourceId") UUID resourceId,
                                                      @PathVariable("uploadedFileId") UUID uploadedFileId) {
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find a resource with id %s"
                .formatted(resourceId)));
    var uploadedFile = resourceSupportingDocumentService.findUploadedFileOrThrow(resource, uploadedFileId);
    var inputStream = fileUploadService.downloadFile(uploadedFile);
    return FileUploadUtils.getFileResourceResponseEntity(uploadedFile, new InputStreamResource(inputStream));
  }

  @PostMapping("delete/{uploadedFileId}")
  @ResponseBody
  public FileDeleteResult delete(@PathVariable("resourceId") UUID resourceId,
                                 @PathVariable("uploadedFileId") UUID uploadedFileId) {
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find a resource with id %s"
                .formatted(resourceId)));
    return resourceSupportingDocumentService.deleteFile(resource, uploadedFileId);
  }
}
