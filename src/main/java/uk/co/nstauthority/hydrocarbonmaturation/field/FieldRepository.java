package uk.co.nstauthority.hydrocarbonmaturation.field;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldRepository extends CrudRepository<Field, Integer> {
  List<Field> findByDeterminationStatusAndCorrectedLegalFieldNameContainingIgnoreCase(DeterminationStatus determinationStatus,
                                                                                      String fieldName);
}
