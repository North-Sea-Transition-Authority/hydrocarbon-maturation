package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ShapefileMigrationProgressRepository extends CrudRepository<ShapefileMigrationProgress, UUID> {

  Optional<ShapefileMigrationProgress> findByPolygonId(String polygonId);

  @Query(
      value =
          """
          SELECT COUNT(prdh.polygon_id) = 1
          FROM remi.padi_res_data_header prdh
          WHERE prdh.polygon_id = :polygonId
          """,
      nativeQuery = true
  )
  boolean padiResDataHeaderExistsByPolygonId(String polygonId);
}
