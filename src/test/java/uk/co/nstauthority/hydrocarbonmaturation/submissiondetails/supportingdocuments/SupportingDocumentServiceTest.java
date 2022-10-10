package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.io.IOException;
import java.time.Instant;
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
import uk.co.nstauthority.hydrocarbonmaturation.file.DeleteOutcome;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadDetail;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadUtils;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadValidationService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadView;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadErrorType;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.VirtualFolder;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class SupportingDocumentServiceTest {

  @Mock
  private FileUploadService fileUploadService;
  @Mock
  private FileUploadValidationService fileUploadValidationService;
  @Mock
  private SupportingDocumentRepository supportingDocumentRepository;

  @InjectMocks
  private SupportingDocumentService supportingDocumentService;
  private static final String filename = FileTestUtil.VALID_FILENAME;
  private static final long fileSize = FileTestUtil.VALID_FILE_SIZE;
  private static final String contentType = FileTestUtil.VALID_CONTENT_TYPE;
  private static final VirtualFolder virtualFolder = FileTestUtil.VALID_VIRTUAL_FOLDER;
  private static final UploadedFile uploadedFile = FileTestUtil.createValidUploadedFile();
  private static MultipartFile multipartFile;
  private static SubmissionDetail submissionDetail;
  private final Instant now = Instant.now();


  @BeforeAll
  static void beforeAll() throws IOException {
    multipartFile = FileTestUtil.createMultipartFileMock();
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @ParameterizedTest
  @EnumSource(
      value = UploadErrorType.class
  )
  void processFileUpload_FailsValidation_ReturnsInvalidResult(UploadErrorType uploadErrorType) {
    var submissionDetail = mock(SubmissionDetail.class);
    when(fileUploadService.sanitiseFilename(filename)).thenReturn(filename);
    when(fileUploadValidationService.validateFileUpload(multipartFile, fileSize, filename))
        .thenReturn(uploadErrorType);

    var fileUploadResult = supportingDocumentService.processFileUpload(submissionDetail, SupportingDocumentType.MAP_OF_OPPORTUNITY, multipartFile);

    assertThat(fileUploadResult.isValid()).isFalse();
  }

  @Test
  void processFileUpload_PassesValidationAndUploadsFile_VerifyInteractions() {
    var submissionDetail = mock(SubmissionDetail.class);
    when(fileUploadService.sanitiseFilename(filename)).thenReturn(filename);
    doReturn(uploadedFile)
        .when(fileUploadService)
        .uploadFileAndSaveRecord(virtualFolder, multipartFile, fileSize, filename, contentType);

    var fileUploadResult = supportingDocumentService.processFileUpload(submissionDetail, SupportingDocumentType.MAP_OF_OPPORTUNITY, multipartFile);

    assertThat(fileUploadResult.isValid()).isTrue();
  }

  @Test
  void deleteFile_FileDeleted_ReturnsValidResult() {
    var uuid = UUID.randomUUID();
    when(fileUploadService.findUploadedFileOrThrow(uuid)).thenReturn(uploadedFile);
    when(fileUploadService.deleteFile(uploadedFile)).thenReturn(DeleteOutcome.SUCCESS);

    var fileDeleteResult = supportingDocumentService.deleteFile(uuid);

    assertThat(fileDeleteResult.isValid()).isTrue();
  }

  @Test
  void deleteFile_DeleteFileFailed_ReturnsNotValidResult() {
    var uuid = UUID.randomUUID();
    when(fileUploadService.findUploadedFileOrThrow(uuid)).thenReturn(uploadedFile);
    when(fileUploadService.deleteFile(uploadedFile)).thenReturn(DeleteOutcome.INTERNAL_SERVER_ERROR);

    var fileDeleteResult = supportingDocumentService.deleteFile(uuid);

    assertThat(fileDeleteResult.isValid()).isFalse();
  }

  @Test
  void getFileUploadDetailListForSubmissionAndType() {
    var uuid = UUID.randomUUID();
    var supportingDocumentType = SupportingDocumentType.MAP_OF_OPPORTUNITY;
    var supportingDocument = createSupportingDocument(uuid, supportingDocumentType);

    when(supportingDocumentRepository.findAllBySubmissionDetailAndSupportingDocumentType(submissionDetail,
        supportingDocumentType)).thenReturn(List.of(supportingDocument));
    var fileUploadDetails = supportingDocumentService.getFileUploadDetailListForSubmissionAndType(
        submissionDetail, supportingDocumentType);

    assertThat(fileUploadDetails)
        .extracting(
            FileUploadDetail::getUploadedFileId,
            FileUploadDetail::getUploadedFileDescription,
            FileUploadDetail::getUploadedFileInstant
        )
        .contains(
            tuple(
                uploadedFile.getId().toString(),
                uploadedFile.getDescription(),
                uploadedFile.getUploadedTimeStamp()
            )
        );
  }

  @Test
  void getFileUploadViewList() {
    var uuid = UUID.randomUUID();

    when(fileUploadService.getFileUploadsByIdList(List.of(uuid))).thenReturn(List.of(uploadedFile));
    var fileUploadViews = supportingDocumentService.getFileUploadViewList(List.of(uuid));

    assertThat(fileUploadViews)
        .extracting(
            FileUploadView::getFileId,
            FileUploadView::getFileName,
            FileUploadView::getFileSize,
            FileUploadView::getFileDescription,
            FileUploadView::getFileUploadedTime
        )
        .contains(
            tuple(
                uploadedFile.getId().toString(),
                uploadedFile.getFilename(),
                FileUploadUtils.fileSizeFormatter(uploadedFile.getFileSizeBytes()),
                uploadedFile.getDescription(),
                uploadedFile.getUploadedTimeStamp()
            )
        );
  }

  @Test
  void buildFileUploadTemplate() {
    var uuid = UUID.randomUUID();
    var supportingDocumentType = SupportingDocumentType.MAP_OF_OPPORTUNITY;

    var fileUploadTemplate = supportingDocumentService.buildFileUploadTemplate(uuid,
        supportingDocumentType);

    verify(fileUploadService).buildFileUploadTemplate(
        ReverseRouter.route(on(SubmissionSupportingDocumentsController.class).download(uuid, null)),
        ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
            .upload(uuid, supportingDocumentType, null)),
        ReverseRouter.route(on(SubmissionSupportingDocumentsController.class).delete(uuid, null))
    );
  }

  @Test
  void updateSupportingDocumentDescriptions() {
    UUID uuid = UUID.randomUUID();
    FileUploadDetail fileUploadDetail = createFileUploadDetail(uuid);
    when(fileUploadService.findUploadedFileOrThrow(uuid)).thenReturn(uploadedFile);

    supportingDocumentService.updateSupportingDocumentDescriptions(List.of(fileUploadDetail));

    verify(fileUploadService, times(1)).updateFileDescription(uploadedFile, fileUploadDetail.getUploadedFileDescription());
  }

  private FileUploadDetail createFileUploadDetail(UUID uuid) {
    var fileUploadDetail = new FileUploadDetail();
    fileUploadDetail.setUploadedFileId(uuid.toString());
    fileUploadDetail.setUploadedFileDescription("test description");
    fileUploadDetail.setUploadedFileInstant(now);
    return fileUploadDetail;
  }

  private SupportingDocument createSupportingDocument(UUID uuid, SupportingDocumentType supportingDocumentType) {
    var supportingDocument = new SupportingDocument();
    supportingDocument.setId(uuid);
    supportingDocument.setSupportingDocumentType(supportingDocumentType);
    supportingDocument.setUploadedFile(uploadedFile);
    return supportingDocument;
  }
}