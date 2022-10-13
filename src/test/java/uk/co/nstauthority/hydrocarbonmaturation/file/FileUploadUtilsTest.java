package uk.co.nstauthority.hydrocarbonmaturation.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FileUploadUtilsTest {

  @Test
  void fileSizeFormatter() {
    Map<Long, String> DATA_MAP = new HashMap<>() {{
      put(0L, "0 B");
      put(1023L, "1023 B");
      put(1024L, "1 KB");
      put(12_345L, "12.1 KB");
      put(10_123_456L, "9.7 MB");
      put(10_123_456_798L, "9.4 GB");;
    }};
    DATA_MAP.forEach((in, expected) -> {
      String humanReadableSize = FileUploadUtils.fileSizeFormatter(in);
      assertThat(humanReadableSize).isEqualTo(expected);
    });
  }
}