package uk.co.nstauthority.hydrocarbonmaturation.file;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.S3ClientService;

class FileUploadServiceTest {
  private S3ClientService s3ClientService;
  private FileUploadValidationService fileUploadValidationService;
  private UploadedFileRepository uploadedFileRepository;
  private FileUploadConfig fileUploadConfig;
  private Clock clock;
  private FileUploadService fileUploadService;
  private static UploadedFile uploadedFile;

  private static MultipartFile multipartFile;
  private static long fileSize;
  private static String filename;

  @BeforeAll
  static void beforeAll() throws IOException {
    uploadedFile = createValidUploadedFile();
    multipartFile = createMultipartFile();
  }

  @BeforeEach
  void setUp() {
    s3ClientService = Mockito.mock(S3ClientService.class);
    fileUploadValidationService = Mockito.mock(FileUploadValidationService.class);
    uploadedFileRepository = Mockito.mock(UploadedFileRepository.class);
    fileUploadConfig = Mockito.mock(FileUploadConfig.class);
    when(fileUploadConfig.getFileNameFilter()).thenReturn("[/\\\\?%*:|\\\"<>]");

    clock = Mockito.mock(Clock.class);

    fileUploadService =
        new FileUploadService(s3ClientService, uploadedFileRepository, fileUploadConfig, clock);
  }

  private static MultipartFile createMultipartFile() throws IOException {
    var multipartFile = Mockito.mock(MultipartFile.class);
    fileSize = 123L;
    filename = "file.txt";
    when(multipartFile.getSize()).thenReturn(fileSize);
    when(multipartFile.getContentType()).thenReturn("text");
    when(multipartFile.getOriginalFilename()).thenReturn(filename);
    when(multipartFile.getInputStream()).thenReturn(mock(InputStream.class));

    return multipartFile;
  }

  private static UploadedFile createValidUploadedFile() {
    var uuid = UUID.randomUUID();
    var uploadedFile = new UploadedFile();
    var s3Key = "testKey";
    uploadedFile.setId(uuid);
    uploadedFile.setS3Key(s3Key);

    return uploadedFile;
  }

//  @ParameterizedTest
//  @EnumSource(
//      value = UploadErrorType.class
//  )
//  void processFileUpload_FailsValidation_ReturnsInvalidResult(UploadErrorType uploadErrorType) {
//    when(fileUploadValidationService.validateFileUpload(multipartFile, fileSize, filename))
//        .thenReturn(uploadErrorType);
//
//    FileUploadResult fileUploadResult = fileUploadService.processFileUpload(VirtualFolder.SUPPORTING_DOCUMENTS, multipartFile);
//
//    verify(s3ClientService, never()).storeFile(anyString(), any(), any(ObjectMetadata.class));
//    verify(uploadedFileRepository, never()).save(any(UploadedFile.class));
//    assertThat(fileUploadResult.isValid()).isFalse();
//  }
//
//  @Test
//  void processFileUpload_PassesValidationAndUploadsFile_VerifyInteractions() {
//
//    FileUploadService fileUploadServiceSpy = spy(fileUploadService);
//    VirtualFolder virtualFolder = VirtualFolder.SUPPORTING_DOCUMENTS;
//    doReturn(uploadedFile)
//        .when(fileUploadServiceSpy)
//        .uploadFileAndSaveRecord(eq(virtualFolder), eq(multipartFile), anyLong(), anyString(), anyString());
//    FileUploadResult fileUploadResult = fileUploadServiceSpy.processFileUpload(virtualFolder, multipartFile);
//
//    assertThat(fileUploadResult.isValid()).isTrue();
//  }

  @Test
  void findUploadedFileOrThrow_UploadedFileFound_VerifyInteractions() {
    var uuid = UUID.randomUUID();
    when(uploadedFileRepository.findById(uuid)).thenReturn(Optional.of(mock(UploadedFile.class)));
    var uploadedFile = fileUploadService.findUploadedFileOrThrow(uuid);
    verify(uploadedFileRepository).findById(uuid);
  }

  @Test
  void findUploadedFileOrThrow_UploadedFileNotFound_VerifyThrowsException() {
    var uuid = UUID.randomUUID();
    when(uploadedFileRepository.findById(uuid)).thenReturn(Optional.empty());

    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
      var uploadedFile = fileUploadService.findUploadedFileOrThrow(uuid);
    });
  }

//  @Test
//  void deleteFile_FileDeleted_ReturnsValidResult() {
//    FileDeleteResult fileDeleteResult = fileUploadService.deleteFile(uploadedFile);
//
//    verify(s3ClientService).deleteFile(uploadedFile.getS3Key());
//    verify(uploadedFileRepository).deleteById(uploadedFile.getId());
//    assertThat(fileDeleteResult.isValid()).isTrue();
//  }
//
//  @Test
//  void deleteFile_DeleteFileFailed_ReturnsNotValidResult() {
//    doThrow(S3DeleteFileFailedException.class).when(s3ClientService).deleteFile(uploadedFile.getS3Key());
//
//    FileDeleteResult fileDeleteResult = fileUploadService.deleteFile(uploadedFile);
//
//    verify(uploadedFileRepository, never()).deleteById(any());
//    assertThat(fileDeleteResult.isValid()).isFalse();
//  }

  @Test
  void downloadFile_VerifyInteractions() {
    fileUploadService.downloadFile(uploadedFile);
    verify(s3ClientService).fetchFile(uploadedFile.getS3Key());
  }
}