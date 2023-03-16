package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadValidationService;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadErrorType;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.VirtualFolder;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@ExtendWith(MockitoExtension.class)
class ResourceSupportingDocumentServiceTest {

  @Mock
  private FileUploadService fileUploadService;
  @Mock
  private FileUploadValidationService fileUploadValidationService;
  @Mock
  private ResourceSupportingDocumentRepository resourceSupportingDocumentRepository;

  @InjectMocks
  private ResourceSupportingDocumentService resourceSupportingDocumentService;

  private static final String filename = FileTestUtil.VALID_FILENAME;
  private static final long fileSize = FileTestUtil.VALID_FILE_SIZE;
  private static final String contentType = FileTestUtil.VALID_CONTENT_TYPE;
  private static final VirtualFolder virtualFolder = FileTestUtil.VALID_VIRTUAL_FOLDER;
  private static final UploadedFile uploadedFile = FileTestUtil.createValidUploadedFile();
  private static MultipartFile multipartFile;
  private static Resource resource;

  @BeforeAll
  static void beforeAll() throws IOException {
    multipartFile = FileTestUtil.createMultipartFileMock();
    resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
  }

  @ParameterizedTest
  @EnumSource(
      value = UploadErrorType.class
  )
  void processFileUpload_FailsValidation_ReturnsInvalidResult(UploadErrorType uploadErrorType) {
    var resource1 = mock(Resource.class);
    when(fileUploadService.sanitiseFilename(filename)).thenReturn(filename);
    when(fileUploadValidationService.validateFileUpload(multipartFile, fileSize, filename))
        .thenReturn(uploadErrorType);

    var fileUploadResult = resourceSupportingDocumentService.processFileUpload(
        resource1, ResourceSupportingDocumentType.RELINQUISHMENT_REPORT, multipartFile);

    assertThat(fileUploadResult.isValid()).isFalse();
  }

  @Test
  void processFileUpload_PassesValidationAndUploadsFile_VerifyInteractions() {
    var resource1 = mock(Resource.class);
    when(fileUploadService.sanitiseFilename(filename)).thenReturn(filename);
    doReturn(uploadedFile)
        .when(fileUploadService)
        .createUploadedFile(virtualFolder, fileSize, filename, contentType);

    var fileUploadResult = resourceSupportingDocumentService.processFileUpload(
        resource1, ResourceSupportingDocumentType.RELINQUISHMENT_REPORT, multipartFile);

    assertThat(fileUploadResult.isValid()).isTrue();
  }

  @Test
  void deleteFile_FileDeleted_SupportingDocAndFileDeleted() {
    var uuid = UUID.randomUUID();
    var supportingDocument = mock(ResourceSupportingDocument.class);
    when(fileUploadService.findUploadedFileOrThrow(uuid)).thenReturn(uploadedFile);
    when(resourceSupportingDocumentRepository.findByResourceAndUploadedFile(resource, uploadedFile))
        .thenReturn(supportingDocument);
    when(resourceSupportingDocumentRepository.findAllByUploadedFile(uploadedFile))
        .thenReturn(Collections.singletonList(supportingDocument));

    var fileDeleteResult = resourceSupportingDocumentService.deleteFile(resource, uuid);

    verify(resourceSupportingDocumentRepository, times(1)).delete(supportingDocument);
    verify(fileUploadService, times(1)).deleteFile(uploadedFile);
    assertThat(fileDeleteResult.isValid()).isTrue();
  }

  @Test
  void deleteFile_FileDeletedButExistsInOtherVersion_OnlySupportingDocDeleted() {
    var supportingDocument1 = mock(ResourceSupportingDocument.class);
    var supportingDocument2 = mock(ResourceSupportingDocument.class);
    when(fileUploadService.findUploadedFileOrThrow(uploadedFile.getId())).thenReturn(uploadedFile);
    when(resourceSupportingDocumentRepository.findByResourceAndUploadedFile(resource, uploadedFile))
        .thenReturn(supportingDocument1);
    when(resourceSupportingDocumentRepository.findAllByUploadedFile(uploadedFile))
        .thenReturn(List.of(supportingDocument1, supportingDocument2));

    var fileDeleteResult = resourceSupportingDocumentService.deleteFile(resource, uploadedFile.getId());

    verify(resourceSupportingDocumentRepository, times(1)).delete(supportingDocument1);
    verify(fileUploadService, never()).deleteFile(uploadedFile);
    assertThat(fileDeleteResult.isValid()).isTrue();
  }

  @Test
  void getFileUploadFormListForSubmissionAndType() {
    var uuid = UUID.randomUUID();
    var supportingDocumentType = ResourceSupportingDocumentType.RELINQUISHMENT_REPORT;
    var supportingDocument = createResourceSupportingDocument(uuid, supportingDocumentType);

    when(resourceSupportingDocumentRepository.findAllByResourceAndResourceSupportingDocumentType(resource,
        supportingDocumentType)).thenReturn(List.of(supportingDocument));
    when(fileUploadService.createFileUploadForm(uploadedFile)).thenReturn(createFileUploadForm(uploadedFile));

    var fileUploadForms = resourceSupportingDocumentService.getFileUploadFormListForSubmissionAndType(
        resource, supportingDocumentType);

    assertThat(fileUploadForms)
        .extracting(
            FileUploadForm::getUploadedFileId,
            FileUploadForm::getUploadedFileDescription,
            FileUploadForm::getUploadedFileInstant
        )
        .contains(
            tuple(
                uploadedFile.getId(),
                uploadedFile.getDescription(),
                uploadedFile.getUploadedTimeStamp()
            )
        );
  }

  @Test
  void getUploadedFileViewList() {
    var uuid = UUID.randomUUID();
    var fileUploadIdList = List.of(uuid);

    var uploadedFileViews = resourceSupportingDocumentService.getUploadedFileViewList(fileUploadIdList);

    verify(fileUploadService).getUploadedFileViewList(fileUploadIdList);
  }

  @Test
  void buildFileUploadTemplate() {
    var uuid = UUID.randomUUID();
    var supportingDocumentType = ResourceSupportingDocumentType.RELINQUISHMENT_REPORT;

    var fileUploadTemplate = resourceSupportingDocumentService.buildFileUploadTemplate(uuid,
        supportingDocumentType);

    verify(fileUploadService).buildFileUploadTemplate(
        ReverseRouter.route(on(ResourceSupportingDocumentsController.class).download(uuid, null)),
        ReverseRouter.route(on(ResourceSupportingDocumentsController.class)
            .upload(uuid, supportingDocumentType, null)),
        ReverseRouter.route(on(ResourceSupportingDocumentsController.class).delete(uuid, null))
    );
  }

  @Test
  void updateResourceSupportingDocumentDescriptions() {
    var fileUploadForm = mock(FileUploadForm.class);
    var fileUploadFormList = List.of(fileUploadForm);

    resourceSupportingDocumentService.updateResourceSupportingDocumentFileDescriptions(fileUploadFormList);

    verify(fileUploadService, times(1)).updateFileUploadDescriptions(fileUploadFormList);
  }

  @Test
  void getResourceSupportingDocumentsForType_verifyReturn() {
    var resource = new Resource();
    var returnedSupportingDocuments = List.of(new ResourceSupportingDocument());
    when(resourceSupportingDocumentRepository.findAllByResourceAndResourceSupportingDocumentType(
        resource, ResourceSupportingDocumentType.RELINQUISHMENT_REPORT))
        .thenReturn(returnedSupportingDocuments);
    var result = resourceSupportingDocumentService.getResourceSupportingDocumentsForType(
        resource, ResourceSupportingDocumentType.RELINQUISHMENT_REPORT);

    assertThat(result).isEqualTo(returnedSupportingDocuments);
  }

  @Test
  void findUploadedFileOrThrow_assertFound() {
    var uploadedFile = FileTestUtil.createValidUploadedFile();
    var resourceSupportingDocument = new ResourceSupportingDocument();
    resourceSupportingDocument.setUploadedFile(uploadedFile);
    when(fileUploadService.findUploadedFileOrThrow(uploadedFile.getId()))
        .thenReturn(uploadedFile);
    when(resourceSupportingDocumentRepository.findByResourceAndUploadedFile(resource, uploadedFile))
        .thenReturn(resourceSupportingDocument);

    var result = resourceSupportingDocumentService.findUploadedFileOrThrow(resource, uploadedFile.getId());

    assertThat(result).isEqualTo(uploadedFile);
  }

  private ResourceSupportingDocument createResourceSupportingDocument(UUID uuid, ResourceSupportingDocumentType supportingDocumentType) {
    var supportingDocument = new ResourceSupportingDocument();
    supportingDocument.setId(uuid);
    supportingDocument.setResourceSupportingDocumentType(supportingDocumentType);
    supportingDocument.setUploadedFile(uploadedFile);
    return supportingDocument;
  }

  private FileUploadForm createFileUploadForm(UploadedFile uploadedFile) {
    var fileUploadForm = new FileUploadForm();
    fileUploadForm.setUploadedFileId(uploadedFile.getId());
    fileUploadForm.setUploadedFileDescription(uploadedFile.getDescription());
    fileUploadForm.setUploadedFileInstant(uploadedFile.getUploadedTimeStamp());
    return fileUploadForm;
  }
}