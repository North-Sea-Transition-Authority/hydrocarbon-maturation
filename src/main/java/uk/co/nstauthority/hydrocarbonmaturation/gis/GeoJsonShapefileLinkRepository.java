package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface GeoJsonShapefileLinkRepository extends CrudRepository<GeoJsonShapefileLink, UUID> {
}
