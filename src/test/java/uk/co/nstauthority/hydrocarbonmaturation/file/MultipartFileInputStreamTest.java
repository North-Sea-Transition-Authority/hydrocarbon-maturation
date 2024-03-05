package uk.co.nstauthority.hydrocarbonmaturation.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MultipartFileInputStreamTest {


  private static final String NAME = "NAME";
  private static final String CONTENT_TYPE = "CONTENT_TYPE";
  private static final long SIZE = 48;

  public MultipartFileInputStream createMultipartFileInputStream(InputStream inputStream) throws IOException {
     return new MultipartFileInputStream(CONTENT_TYPE, NAME, SIZE, inputStream);
  }

  @Nested
  class WhenGettingInputStream {
    @Test
    void markIsSupported_shouldSetInputStream() throws IOException {
      var inputStream = mockInputStreamWithMarkSupportedIs(true);

      var multipartFileInputStream = createMultipartFileInputStream(inputStream);

      assertThat(multipartFileInputStream.getInputStream()).isEqualTo(inputStream);
    }

    @Test
    void markIsSupported_shouldCreateNewByteArrayInputStream() throws IOException {
      var inputStream = mockInputStreamWithMarkSupportedIs(false);

      var multipartFileInputStream = createMultipartFileInputStream(inputStream);

      assertThat(multipartFileInputStream.getInputStream())
          .isNotEqualTo(inputStream)
          .isOfAnyClassIn(ByteArrayInputStream.class);
    }

    @Test
    void resetIsCalled() throws IOException {
      var inputStream = mockInputStreamWithMarkSupportedIs(true);
      var multipartFileInputStream = createMultipartFileInputStream(inputStream);

      multipartFileInputStream.getInputStream();

      verify(inputStream).reset();
      assertThat(multipartFileInputStream.getInputStream()).isEqualTo(inputStream);
    }
  }

  private InputStream mockInputStreamWithMarkSupportedIs(boolean markSupported) throws IOException {
    var inputStream = mock(InputStream.class);
    when(inputStream.markSupported()).thenReturn(markSupported);
    if (!markSupported) {
      when(inputStream.readAllBytes()).thenReturn(new byte[0]);
    }
    return inputStream;
  }
}