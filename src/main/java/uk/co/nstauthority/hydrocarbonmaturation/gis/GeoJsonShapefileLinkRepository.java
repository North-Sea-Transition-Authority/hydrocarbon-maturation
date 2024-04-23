package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;

@Repository
interface GeoJsonShapefileLinkRepository extends CrudRepository<GeoJsonShapefileLink, UUID> {

  Optional<GeoJsonShapefileLink> findByShapefile(UploadedFile shapefile);

  void deleteByShapefile(UploadedFile shapefile);
}
