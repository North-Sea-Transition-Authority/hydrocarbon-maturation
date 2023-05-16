package uk.co.nstauthority.hydrocarbonmaturation.gis;

import com.esri.core.geometry.Geometry;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.gis.esri.ShapefileGeometryLoader;

@Service
class ShapefileImportService {

  List<Geometry> importShapefileAsGeometryList(InputStream shapefileStream) throws IOException {
    var shapefileGeometryLoader = new ShapefileGeometryLoader(shapefileStream);
    return shapefileGeometryLoader.getGeometryList();
  }
}
