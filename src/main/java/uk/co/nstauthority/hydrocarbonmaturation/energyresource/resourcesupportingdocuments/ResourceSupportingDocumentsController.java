package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/resource/{resourceId}/resource-supporting-documents")
@HasPermission(permissions = RolePermission.EXPLORATION)
public class ResourceSupportingDocumentsController {

  private final ResourceSupportingDocumentService resourceSupportingDocumentService;
  private final ResourceService resourceService;
  private final FileService fileService;

  @Autowired
  ResourceSupportingDocumentsController(ResourceSupportingDocumentService resourceSupportingDocumentService,
                                        ResourceService resourceService, FileService fileService) {
    this.resourceSupportingDocumentService = resourceSupportingDocumentService;
    this.resourceService = resourceService;
    this.fileService = fileService;
  }

  @GetMapping("download/{fileId}")
  @ResponseBody
  public ResponseEntity<InputStreamResource> download(@PathVariable("resourceId") UUID resourceId,
                                                      @PathVariable("fileId") UUID fileId) {
    var resource = resourceService.getResourceOrThrow(resourceId);
    var uploadedFile = fileService.find(fileId)
        .orElseThrow(() -> resourceSupportingDocumentService.getFileNotFoundException(fileId, resource));

    resourceSupportingDocumentService.throwIfFileDoesNotBelongToResource(uploadedFile, resource);

    return fileService.download(uploadedFile);
  }
}
