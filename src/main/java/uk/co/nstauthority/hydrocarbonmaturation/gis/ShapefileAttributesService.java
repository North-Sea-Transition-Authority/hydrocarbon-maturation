package uk.co.nstauthority.hydrocarbonmaturation.gis;

import com.linuxense.javadbf.DBFReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
class ShapefileAttributesService {

  public ShapefileAttributes getAttributesFromShapefileDbase(InputStream inputStream) {

    try (var reader = new DBFReader(inputStream)) {

      var headers = getHeaders(reader);
      var rows = getRows(reader);

      return new ShapefileAttributes(headers, rows);
    }
  }

  private static List<String> getHeaders(DBFReader reader) {
    var header = new ArrayList<String>();
    var numberOfFields = reader.getFieldCount();
    for (var i = 0; i < numberOfFields; i++) {
      var field = reader.getField(i);

      header.add(field.getName());
    }

    return header;
  }

  private static List<List<String>> getRows(DBFReader reader) {
    var rows = new ArrayList<List<String>>();
    Object[] rowObjects;
    while ((rowObjects = reader.nextRecord()) != null) {
      rows.add(getColumns(rowObjects));
    }

    return rows;
  }

  private static List<String> getColumns(Object[] rowObjects) {
    return Arrays.stream(rowObjects)
        .map(String::valueOf)
        .toList();
  }
}
