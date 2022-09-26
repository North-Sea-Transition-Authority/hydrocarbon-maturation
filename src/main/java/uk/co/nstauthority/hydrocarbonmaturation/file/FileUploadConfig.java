package uk.co.nstauthority.hydrocarbonmaturation.file;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file-upload")
class FileUploadConfig {
  private Integer maxAllowedSize;
  private String allowedExtensions;
  private String fileNameFilter;

  public Integer getMaxAllowedSize() {
    return maxAllowedSize;
  }

  public void setMaxAllowedSize(Integer maxAllowedSize) {
    this.maxAllowedSize = maxAllowedSize;
  }

  public String getAllowedExtensions() {
    return allowedExtensions;
  }

  public void setAllowedExtensions(String allowedExtensions) {
    this.allowedExtensions = allowedExtensions;
  }

  public String getFileNameFilter() {
    return fileNameFilter;
  }

  public void setFileNameFilter(String fileNameFilter) {
    this.fileNameFilter = fileNameFilter;
  }
}