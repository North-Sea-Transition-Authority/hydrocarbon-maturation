package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDeleteResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadDetail;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadFdsView;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadTemplate;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadUtils;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadValidationService;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.VirtualFolder;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class SupportingDocumentService {
  private final FileUploadService fileUploadService;
  private final FileUploadValidationService fileUploadValidationService;
  private final SupportingDocumentRepository supportingDocumentRepository;

  @Autowired
  public SupportingDocumentService(FileUploadService fileUploadService, FileUploadValidationService fileUploadValidationService,
                                   SupportingDocumentRepository supportingDocumentRepository) {
    this.fileUploadService = fileUploadService;
    this.fileUploadValidationService = fileUploadValidationService;
    this.supportingDocumentRepository = supportingDocumentRepository;
  }

  @Transactional
  public FileUploadResult processFileUpload(SubmissionDetail submissionDetail, SupportingDocumentType supportingDocumentType,
                                            MultipartFile multipartFile) {
    var fileSize = multipartFile.getSize();
    var filename = fileUploadService.sanitiseFilename(Objects.requireNonNull(multipartFile.getOriginalFilename()));
    var contentType = multipartFile.getContentType();

    var uploadErrorType = fileUploadValidationService.validateFileUpload(multipartFile, fileSize, filename);
    if (uploadErrorType != null) {
      return FileUploadResult.error(filename, multipartFile, uploadErrorType);
    }

    var uploadedFile = fileUploadService.createdAndSaveUploadedFile(
        VirtualFolder.SUPPORTING_DOCUMENTS, fileSize, filename, contentType);

    var supportingDocument = new SupportingDocument();
    supportingDocument.setSubmissionDetail(submissionDetail);
    supportingDocument.setSupportingDocumentType(supportingDocumentType);
    supportingDocument.setUploadedFile(uploadedFile);

    supportingDocumentRepository.save(supportingDocument);

    // Persist uploaded file and supporting document records first,
    // so if it fails, we don't upload. If upload fails, we will roll back
    fileUploadService.uploadFile(multipartFile, uploadedFile);

    return FileUploadResult.valid(uploadedFile.getId().toString(), filename, multipartFile);
  }

  public List<FileUploadDetail> getFileUploadDetailListForSubmissionAndType(SubmissionDetail submissionDetail,
                                                                            SupportingDocumentType supportingDocumentType) {
    return supportingDocumentRepository.findAllBySubmissionDetailAndSupportingDocumentType(
            submissionDetail, supportingDocumentType).stream()
        .map(SupportingDocument::getUploadedFile)
        .map(this::createFileUploadDetail)
        .toList();
  }

  public List<FileUploadFdsView> getFileUploadFdsViewList(List<UUID> fileUploadIdList) {
    return fileUploadService.getFileUploadsByIdList(fileUploadIdList).stream()
        .map(this::createFileUploadFdsView)
        .toList();
  }

  public FileUploadTemplate buildFileUploadTemplate(UUID submissionDetailId, SupportingDocumentType supportingDocumentType) {
    return fileUploadService.buildFileUploadTemplate(
        ReverseRouter.route(on(SubmissionSupportingDocumentsController.class).download(submissionDetailId, null)),
        ReverseRouter.route(
            on(SubmissionSupportingDocumentsController.class).upload(submissionDetailId, supportingDocumentType, null)),
        ReverseRouter.route(on(SubmissionSupportingDocumentsController.class).delete(submissionDetailId, null))
    );
  }

  @Transactional
  public FileDeleteResult deleteFile(SubmissionDetail submissionDetail, UUID uploadedFileId) {
    var uploadedFile = fileUploadService.findUploadedFileOrThrow(uploadedFileId);
    var supportingDocuments = supportingDocumentRepository.findAllByUploadedFile(uploadedFile);

    var supportingDocument =
        supportingDocumentRepository.findBySubmissionDetailAndUploadedFile(submissionDetail, uploadedFile);
    supportingDocumentRepository.delete(supportingDocument);

    // Check if the uploaded file is associated with other submission supporting documents and only delete if it is not
    if (supportingDocuments.size() == 1) {
      fileUploadService.deleteFile(uploadedFile);
    }
    // TODO HM-218 add logging for else size is greater than one

    return FileDeleteResult.success(uploadedFile.getId().toString());
  }

  @Transactional
  public void updateSupportingDocumentFileDescriptions(List<FileUploadDetail> fileUploadDetailList) {
    fileUploadDetailList
        .forEach(fileUploadDetail -> {
          var uploadedFileId = UUID.fromString(fileUploadDetail.getUploadedFileId());
          var uploadedFile = fileUploadService.findUploadedFileOrThrow(uploadedFileId);
          fileUploadService.updateFileDescription(uploadedFile, fileUploadDetail.getUploadedFileDescription());
        });
  }

  private FileUploadFdsView createFileUploadFdsView(UploadedFile uploadedFile) {
    return new FileUploadFdsView(
        uploadedFile.getId().toString(),
        uploadedFile.getFilename(),
        FileUploadUtils.fileSizeFormatter(uploadedFile.getFileSizeBytes()),
        uploadedFile.getDescription(),
        uploadedFile.getUploadedTimeStamp()
    );
  }

  private FileUploadDetail createFileUploadDetail(UploadedFile uploadedFile) {
    FileUploadDetail fileUploadDetail = new FileUploadDetail();
    fileUploadDetail.setUploadedFileId(uploadedFile.getId().toString());
    fileUploadDetail.setUploadedFileDescription(uploadedFile.getDescription());
    fileUploadDetail.setUploadedFileInstant(uploadedFile.getUploadedTimeStamp());
    return fileUploadDetail;
  }

  UploadedFile findUploadedFileOrThrow(SubmissionDetail submissionDetail, UUID uploadedFileId) {
    var uploadedFile = fileUploadService.findUploadedFileOrThrow(uploadedFileId);
    var supportingDocument = supportingDocumentRepository.findBySubmissionDetailAndUploadedFile(
        submissionDetail, uploadedFile);
    return supportingDocument.getUploadedFile();
  }
}
