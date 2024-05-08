package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.util.List;

public record CrossTabRow(String key, List<String> values) {
  int valueCount() {
    return values.size();
  }
}