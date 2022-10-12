package uk.co.nstauthority.hydrocarbonmaturation.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FileUploadUtilsTest {

  @Test
  void fileSizeFormatter() {
    Map<Long, String> DATA_MAP = new HashMap<>() {{
      put(0L, "0 Bytes");
      put(1023L, "1023 Bytes");
      put(1024L, "1 KiB");
      put(12_345L, "12.06 KiB");
      put(10_123_456L, "9.65 MiB");
      put(10_123_456_798L, "9.43 GiB");;
    }};
    DATA_MAP.forEach((in, expected) -> {
      String humanReadableSize = FileUploadUtils.fileSizeFormatter(in);
      assertThat(humanReadableSize).isEqualTo(expected);
    });
  }
}