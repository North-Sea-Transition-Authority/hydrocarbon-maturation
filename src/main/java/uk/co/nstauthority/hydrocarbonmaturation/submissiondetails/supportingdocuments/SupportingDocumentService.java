package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDeleteResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadTemplate;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadValidationService;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFileView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class SupportingDocumentService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SupportingDocumentService.class);
  private final FileUploadService fileUploadService;
  private final FileUploadValidationService fileUploadValidationService;
  private final SupportingDocumentRepository supportingDocumentRepository;
  private final FileUploadCustomValidatorFactory fileUploadCustomValidatorFactory;

  @Autowired
  public SupportingDocumentService(FileUploadService fileUploadService,
                                   FileUploadValidationService fileUploadValidationService,
                                   SupportingDocumentRepository supportingDocumentRepository,
                                   FileUploadCustomValidatorFactory fileUploadCustomValidatorFactory) {
    this.fileUploadService = fileUploadService;
    this.fileUploadValidationService = fileUploadValidationService;
    this.supportingDocumentRepository = supportingDocumentRepository;
    this.fileUploadCustomValidatorFactory = fileUploadCustomValidatorFactory;
  }

  @Transactional
  public FileUploadResult processFileUpload(SubmissionDetail submissionDetail,
                                            SupportingDocumentType supportingDocumentType,
                                            MultipartFile multipartFile) {
    var fileSize = multipartFile.getSize();
    var filename = fileUploadService.sanitiseFilename(Objects.requireNonNull(multipartFile.getOriginalFilename()));
    var contentType = multipartFile.getContentType();

    var uploadErrorType = fileUploadValidationService.validateFileUpload(multipartFile, fileSize, filename);
    if (uploadErrorType != null) {
      return FileUploadResult.error(filename, multipartFile, uploadErrorType);
    }

    var errors = validateWithCustomValidator(supportingDocumentType, multipartFile);
    if (!errors.isEmpty()) {
      var lineSeparator = System.lineSeparator();
      var errorMessage = String.join(lineSeparator, errors);
      LOGGER.warn("Validation errors for file upload:{}{}", lineSeparator, errorMessage);
      // TODO: fully handle custom validation errors when File Upload Library is available
      return FileUploadResult.customError(
          multipartFile.getOriginalFilename(),
          multipartFile,
          errorMessage
      );
    }

    var uploadedFile = fileUploadService.createUploadedFile(
        supportingDocumentType.getVirtualFolder(), fileSize, filename, contentType);

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

  private List<String> validateWithCustomValidator(SupportingDocumentType supportingDocumentType, MultipartFile multipartFile) {
    var fileUploadCustomValidator = fileUploadCustomValidatorFactory.getFileUploadCustomValidator(supportingDocumentType);

    if (fileUploadCustomValidator.isEmpty()) {
      return List.of();
    }

    try {
      return fileUploadCustomValidator.get()
          .validateFileUpload(multipartFile.getInputStream(), multipartFile.getOriginalFilename());
    } catch (IOException e) {
      throw new IllegalArgumentException("Could not open file upload %s".formatted(multipartFile.getOriginalFilename()), e);
    }
  }

  public List<FileUploadForm> getFileUploadFormListForSubmissionAndType(
      SubmissionDetail submissionDetail,
      SupportingDocumentType supportingDocumentType) {
    return getSupportingDocumentsForTypes(submissionDetail, Set.of(supportingDocumentType)).stream()
        .map(SupportingDocument::getUploadedFile)
        .map(fileUploadService::createFileUploadForm)
        .toList();
  }

  public List<SupportingDocument> getSupportingDocumentsForTypes(SubmissionDetail submissionDetail,
                                                                 Set<SupportingDocumentType> supportingDocumentTypes) {
    return supportingDocumentRepository
        .findAllBySubmissionDetailAndSupportingDocumentTypeIn(submissionDetail, supportingDocumentTypes);
  }

  public Optional<SupportingDocument> getSupportingDocumentsForType(SubmissionDetail submissionDetail,
                                                                    SupportingDocumentType supportingDocumentType) {
    return supportingDocumentRepository
        .findBySubmissionDetailAndSupportingDocumentType(submissionDetail, supportingDocumentType);
  }

  public List<UploadedFileView> getUploadedFileViewList(List<UUID> fileUploadIdList) {
    return fileUploadService.getUploadedFileViewList(fileUploadIdList);
  }

  public FileUploadTemplate buildFileUploadTemplate(UUID submissionDetailId, SupportingDocumentType supportingDocumentType) {
    return fileUploadService.buildFileUploadTemplate(
      getDownloadUrl(submissionDetailId),
      getUploadUrl(submissionDetailId, supportingDocumentType),
      getDeleteUrl(submissionDetailId)
    );
  }

  private static String getDeleteUrl(UUID submissionDetailId) {
    return ReverseRouter.route(on(SubmissionSupportingDocumentsController.class).delete(submissionDetailId, null));
  }

  private static String getUploadUrl(UUID submissionDetailId, SupportingDocumentType supportingDocumentType) {
    return ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
            .upload(submissionDetailId, supportingDocumentType, null));
  }

  private static String getDownloadUrl(UUID submissionDetailId) {
    return ReverseRouter.route(on(SubmissionSupportingDocumentsController.class).download(submissionDetailId, null));
  }

  public FileUploadTemplate buildFileUploadTemplate(UUID submissionDetailId, SupportingDocumentType supportingDocumentType,
                                                     String allowedExtensions) {
    return fileUploadService.buildFileUploadTemplate(
      getDownloadUrl(submissionDetailId),
      getUploadUrl(submissionDetailId, supportingDocumentType),
      getDeleteUrl(submissionDetailId),
      allowedExtensions
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
    } else {
      LOGGER.info("Uploaded file {} is associated with {} supporting documents, uploaded file not deleted.",
          uploadedFileId, supportingDocuments.size());
    }

    return FileDeleteResult.success(uploadedFile.getId().toString());
  }

  @Transactional
  public void updateSupportingDocumentFileDescriptions(List<FileUploadForm> fileUploadFormList) {
    fileUploadService.updateFileUploadDescriptions(fileUploadFormList);
  }

  UploadedFile findUploadedFileOrThrow(SubmissionDetail submissionDetail, UUID uploadedFileId) {
    var uploadedFile = fileUploadService.findUploadedFileOrThrow(uploadedFileId);
    var supportingDocument = supportingDocumentRepository.findBySubmissionDetailAndUploadedFile(
        submissionDetail, uploadedFile);
    return supportingDocument.getUploadedFile();
  }
}
