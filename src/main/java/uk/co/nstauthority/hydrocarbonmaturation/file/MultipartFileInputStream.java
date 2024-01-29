package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

public class MultipartFileInputStream implements MultipartFile {

  private final String name;
  private final String contentType;
  private final long size;
  private final InputStream inputStream;

  public MultipartFileInputStream(String name, String contentType, long size, InputStream inputStream) {
    this.name = name;
    this.contentType = contentType;
    this.size = size;
    this.inputStream = inputStream;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getOriginalFilename() {
    return name;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public long getSize() {
    return size;
  }

  @Override
  public byte[] getBytes() throws IOException {
    return FileCopyUtils.copyToByteArray(inputStream);
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return inputStream;
  }

  @Override
  public void transferTo(File dest) throws IOException, IllegalStateException {
    transferTo(dest.toPath());
  }
}
