package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.io.File;
import java.io.InputStream;
import java.time.Clock;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.S3ClientService;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.S3Folder;
import uk.co.nstauthority.hydrocarbonmaturation.file.virus.VirusCheckService;

public class UploadedFileService {

  private final VirusCheckService virusCheckService;
  private final S3ClientService s3ClientService;
  private final UploadedFileRepository uploadedFileRepository;
  private final Clock clock;

  @Autowired
  public UploadedFileService(VirusCheckService virusCheckService,
                             S3ClientService s3ClientService,
                             UploadedFileRepository uploadedFileRepository,
                             Clock clock) {
    this.virusCheckService = virusCheckService;
    this.s3ClientService = s3ClientService;
    this.uploadedFileRepository = uploadedFileRepository;
    this.clock = clock;
  }

  public UploadFileResult uploadFile(S3Folder s3Folder, File file) {
    if (virusCheckService.hasVirus(file)) {
      return UploadFileResult.VIRUS_DETECTED;
    }

    String s3Key = s3ClientService.storeFile(s3Folder, file);
    UploadedFile uploadedFile = createUploadedFile(s3Folder, s3Key, file);

    uploadedFileRepository.save(uploadedFile);
    return UploadFileResult.SUCCESS;
  }

  void deleteFile(UploadedFile uploadedFile) {
    s3ClientService.deleteFile(uploadedFile.getS3Key());
    uploadedFileRepository.deleteById(uploadedFile.getId());
  }

  InputStream downloadFile(UploadedFile uploadedFile) {
    return s3ClientService.fetchFile(uploadedFile.getS3Key());
  }

  private UploadedFile createUploadedFile(S3Folder s3Folder, String s3Key, File file) {
    UploadedFile uploadedFile = new UploadedFile();
    uploadedFile.setS3Folder(s3Folder);
    uploadedFile.setS3Key(s3Key);
    uploadedFile.setFilename(file.getName());
    uploadedFile.setUploadedTimeStamp(clock.instant());

    return uploadedFile;
  }
}
