package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.file.s3.S3Folder;

@Entity
@Table(name = "files")
class UploadedFile {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @Column(name = "s3_folder")
  private S3Folder s3Folder;

  @Column(name = "s3_key")
  private String s3Key;

  private String filename;

  private Instant uploadedTimeStamp;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public S3Folder getS3Folder() {
    return s3Folder;
  }

  public void setS3Folder(S3Folder s3Folder) {
    this.s3Folder = s3Folder;
  }

  public String getS3Key() {
    return s3Key;
  }

  public void setS3Key(String s3Key) {
    this.s3Key = s3Key;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public Instant getUploadedTimeStamp() {
    return uploadedTimeStamp;
  }

  public void setUploadedTimeStamp(Instant uploadedTimeStamp) {
    this.uploadedTimeStamp = uploadedTimeStamp;
  }
}
