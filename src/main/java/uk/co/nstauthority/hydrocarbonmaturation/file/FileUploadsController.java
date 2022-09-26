package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/file-uploads")
public class FileUploadsController {

  private final FileUploadService fileUploadService;

  @Autowired
  public FileUploadsController(FileUploadService fileUploadService) {
    this.fileUploadService = fileUploadService;
  }

  @PostMapping("upload/{virtualFolder}")
  @ResponseBody
  public FileUploadResult upload(@PathVariable("virtualFolder") VirtualFolder virtualFolder,
                                 @RequestParam("file") MultipartFile multipartFile) {
    return fileUploadService.processFileUpload(virtualFolder, multipartFile);
  }

  @GetMapping("download/{uploadedFileId}")
  @ResponseBody
  public ResponseEntity<InputStreamResource> download(@PathVariable("uploadedFileId") UUID uploadedFileId) {
    var uploadedFile = fileUploadService.findUploadedFileOrThrow(uploadedFileId);
    var inputStream = fileUploadService.downloadFile(uploadedFile);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .contentLength(uploadedFile.getFileSizeBytes())
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            String.format("attachment; filename=\"%s\"", uploadedFile.getFilename())
        )
        .body(new InputStreamResource(inputStream));
  }

  @PostMapping("delete/{uploadedFileId}")
  @ResponseBody
  public FileDeleteResult delete(@PathVariable("uploadedFileId") UUID uploadedFileId) {
    var uploadedFile = fileUploadService.findUploadedFileOrThrow(uploadedFileId);
    return fileUploadService.deleteFile(uploadedFile);
  }
}