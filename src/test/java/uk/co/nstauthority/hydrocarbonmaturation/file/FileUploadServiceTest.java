package uk.co.nstauthority.hydrocarbonmaturation.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.S3ClientService;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.S3DeleteFileFailedException;

class FileUploadServiceTest {
  private S3ClientService s3ClientService;
  private UploadedFileRepository uploadedFileRepository;
  private FileUploadService fileUploadService;
  private static UploadedFile uploadedFile;


  @BeforeAll
  static void beforeAll() {
    uploadedFile = UploadedFileTestUtil.createValidUploadedFile();
  }

  @BeforeEach
  void setUp() {
    s3ClientService = Mockito.mock(S3ClientService.class);
    uploadedFileRepository = Mockito.mock(UploadedFileRepository.class);
    FileUploadConfig fileUploadConfig = new FileUploadConfig(1024, ".txt, .jpg, .jpeg", "[/\\\\?%*:|\\\"<>]");

    Clock clock = Mockito.mock(Clock.class);

    fileUploadService =
        new FileUploadService(s3ClientService, uploadedFileRepository, fileUploadConfig, clock);
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

    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
      var uploadedFile = fileUploadService.findUploadedFileOrThrow(uuid);
    });
  }

  @Test
  void deleteFile_FileDeleted_ReturnsValidResult() {
    DeleteOutcome deleteOutcome = fileUploadService.deleteFile(uploadedFile);

    verify(s3ClientService).deleteFile(uploadedFile.getS3Key());
    verify(uploadedFileRepository).deleteById(uploadedFile.getId());
    assertThat(deleteOutcome).isEqualTo(DeleteOutcome.SUCCESS);
  }

  @Test
  void deleteFile_DeleteFileFailed_ReturnsNotValidResult() {
    doThrow(S3DeleteFileFailedException.class).when(s3ClientService).deleteFile(uploadedFile.getS3Key());

    DeleteOutcome deleteOutcome = fileUploadService.deleteFile(uploadedFile);

    verify(uploadedFileRepository, never()).deleteById(any());
    assertThat(deleteOutcome).isEqualTo(DeleteOutcome.INTERNAL_SERVER_ERROR);
  }

  @Test
  void downloadFile_VerifyInteractions() {
    fileUploadService.downloadFile(uploadedFile);
    verify(s3ClientService).fetchFile(uploadedFile.getS3Key());
  }
}