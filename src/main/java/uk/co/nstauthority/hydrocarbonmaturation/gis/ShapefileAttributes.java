package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.util.List;
import java.util.Optional;

record ShapefileAttributes(List<String> headers, List<List<String>> rows) {
  Optional<Integer> getHeaderIndex(String headerName) {
    var index = headers.indexOf(headerName);

    if (index == -1) {
      return Optional.empty();
    }

    return Optional.of(index);
  }

  Integer getHeaderIndexOrThrow(String headerName) {
    return getHeaderIndex(headerName)
        .orElseThrow(() -> new IllegalArgumentException("Shapefile attributes does not contain %s header".formatted(headerName)));
  }
}
