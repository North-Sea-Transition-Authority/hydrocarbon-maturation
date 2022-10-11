package uk.co.nstauthority.hydrocarbonmaturation.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.web.multipart.MultipartFile;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.S3ClientService;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.S3DeleteFileFailedException;

class FileUploadServiceTest {
  private S3ClientService s3ClientService;
  private UploadedFileRepository uploadedFileRepository;
  private Clock clock;
  private FileUploadService fileUploadService;
  private FileUploadConfig fileUploadConfig;
  private static MultipartFile multipartFile;
  private static final UploadedFile uploadedFile = FileTestUtil.createValidUploadedFile();
  private static final long fileSize = FileTestUtil.VALID_FILE_SIZE;
  private static final String contentType = FileTestUtil.VALID_CONTENT_TYPE;
  private static final String filename = FileTestUtil.VALID_FILENAME;
  private static final String bucketName = FileTestUtil.VALID_BUCKET_NAME;
  private static final VirtualFolder virtualFolder = FileTestUtil.VALID_VIRTUAL_FOLDER;

  @BeforeAll
  static void beforeAll() throws IOException {
    multipartFile = FileTestUtil.createMultipartFileMock(filename, fileSize, contentType);
  }

  @BeforeEach
  void setUp() {
    s3ClientService = mock(S3ClientService.class);
    when(s3ClientService.getDefaultBucketName()).thenReturn(bucketName);
    uploadedFileRepository = mock(UploadedFileRepository.class);
    clock = mock(Clock.class);
    fileUploadConfig = FileTestUtil.validFileUploadConfig();

    fileUploadService =
        new FileUploadService(s3ClientService, uploadedFileRepository, fileUploadConfig, clock);
  }

  @Test
  void uploadFileAndSaveRecord() {
    fileUploadService.uploadFileAndSaveRecord(virtualFolder, multipartFile, fileSize, filename, contentType);
    verify(uploadedFileRepository).save(any(UploadedFile.class));
    verify(s3ClientService).storeFile(eq(bucketName), anyString(), any(InputStream.class), any(ObjectMetadata.class));
  }

  @Test
  void uploadFileAndSaveRecord_UploadFailed() {
    doThrow(MockitoException.class).when(s3ClientService)
        .storeFile(eq(bucketName), anyString(), any(InputStream.class), any(ObjectMetadata.class));

    assertThatExceptionOfType(FileUploadException.class).isThrownBy(() -> {
      fileUploadService.uploadFileAndSaveRecord(virtualFolder, multipartFile, fileSize, filename, contentType);
    });
  }

  @Test
  void uploadFileAndSaveRecord_UploadedFileRecordSaved() {
    when(s3ClientService.getDefaultBucketName()).thenReturn(bucketName);

    var argumentCaptor = ArgumentCaptor.forClass(UploadedFile.class);
    fileUploadService.uploadFileAndSaveRecord(virtualFolder, multipartFile, fileSize, filename, contentType);

    verify(uploadedFileRepository).save(argumentCaptor.capture());
    var file = argumentCaptor.getValue();

    assertThat(file)
        .extracting(
            UploadedFile::getBucketName,
            UploadedFile::getVirtualFolder,
            UploadedFile::getFilename,
            UploadedFile::getFileContentType,
            UploadedFile::getFileSizeBytes,
            UploadedFile::getUploadedTimeStamp,
            UploadedFile::getDescription
        )
        .contains(
            bucketName,
            virtualFolder,
            filename,
            contentType,
            fileSize,
            clock.instant(),
            null
        );
  }

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

    assertThatExceptionOfType(RemiEntityNotFoundException.class).isThrownBy(() -> {
      var uploadedFile = fileUploadService.findUploadedFileOrThrow(uuid);
    });
  }

  @Test
  void deleteFile_FileDeleted() {
    fileUploadService.deleteFile(uploadedFile);

    verify(s3ClientService).deleteFile(uploadedFile.getBucketName(), uploadedFile.getS3Key());
    verify(uploadedFileRepository).deleteById(uploadedFile.getId());
  }

  @Test
  void deleteFile_DeleteFileFailed() throws S3DeleteFileFailedException {
    doThrow(S3DeleteFileFailedException.class).when(s3ClientService)
        .deleteFile(uploadedFile.getBucketName(), uploadedFile.getS3Key());

    try {
      fileUploadService.deleteFile(uploadedFile);
    } catch (S3DeleteFileFailedException ignore) {}

    verify(uploadedFileRepository, never()).deleteById(any());
  }

  @Test
  void downloadFile_VerifyInteractions() {
    fileUploadService.downloadFile(uploadedFile);
    verify(s3ClientService).fetchFile(uploadedFile.getBucketName(), uploadedFile.getS3Key());
  }

  @Test
  void buildFileUploadTemplate() {
    var downloadUrl = "download";
    var uploadUrl = "upload";
    var deleteUrl = "delete";

    var fileUploadTemplate =
        fileUploadService.buildFileUploadTemplate(downloadUrl, uploadUrl, deleteUrl);

    assertThat(fileUploadTemplate)
        .extracting(
            FileUploadTemplate::downloadUrl,
            FileUploadTemplate::uploadUrl,
            FileUploadTemplate::deleteUrl,
            FileUploadTemplate::maxAllowedSize,
            FileUploadTemplate::allowedExtensions
        )
        .contains(
            downloadUrl,
            uploadUrl,
            deleteUrl,
            fileUploadConfig.maxAllowedSize().toString(),
            fileUploadConfig.allowedExtensions()
        );
  }

  @Test
  void sanitiseFilename() {
    String filename = "this%is%test.txt";

    String sanitiseFilename = fileUploadService.sanitiseFilename(filename);

    assertThat(sanitiseFilename).isEqualTo("this_is_test.txt");
  }

  @Test
  void getFileUploadsByIdList_VerifyCalls() {
    var uuid = UUID.randomUUID();
    List<UUID> uuidList = List.of(uuid);

    fileUploadService.getFileUploadsByIdList(uuidList);

    verify(uploadedFileRepository, times(1)).findAllByIdIn(uuidList);
  }

  @Test
  void updateFileDescription() {
    UploadedFile file = new UploadedFile();
    String uploadedFileDescription = "desc";

    assertThat(file.getDescription()).isNull();

    fileUploadService.updateFileDescription(file, uploadedFileDescription);

    verify(uploadedFileRepository, times(1)).save(file);
    assertThat(file.getDescription()).isEqualTo(uploadedFileDescription);
  }
}