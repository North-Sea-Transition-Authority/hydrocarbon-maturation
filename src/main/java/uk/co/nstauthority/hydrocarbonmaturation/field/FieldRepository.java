package uk.co.nstauthority.hydrocarbonmaturation.field;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Repository
public interface FieldRepository extends CrudRepository<Field, Integer> {
  List<Field> findByDeterminationStatusAndCorrectedLegalFieldNameContainingIgnoreCase(DeterminationStatus determinationStatus,
                                                                                      String fieldName);

  List<Field> findFieldsByCorrectedLegalFieldNameContainingIgnoreCase(String fieldName);

  Optional<Field> findByResource(Resource resource);

  List<Field> findFieldsByParentDeterminedField(Field parentDeterminedField);

  Optional<Field> findTopByFieldNumberNotNullOrderByFieldNumberDesc();

  @EntityGraph(attributePaths = {"resource"})
  List<Field> findAllByFieldIdentifierIn(List<Integer> fieldIds);
}
