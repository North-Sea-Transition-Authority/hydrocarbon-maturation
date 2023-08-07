package uk.co.nstauthority.hydrocarbonmaturation.field;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Repository
interface FieldRepository extends CrudRepository<Field, Integer> {
  List<Field> findByDeterminationStatusAndCorrectedLegalFieldNameContainingIgnoreCase(DeterminationStatus determinationStatus,
                                                                                      String fieldName);

  List<Field> findFieldsByCorrectedLegalFieldNameContainingIgnoreCase(String fieldName);

  Optional<Field> findFieldByResource(Resource resource);
}
