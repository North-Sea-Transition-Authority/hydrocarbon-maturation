package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;

@Repository
public interface FieldStatusHistoryRepository extends CrudRepository<FieldStatusHistory, UUID> {
  Set<FieldStatusHistory> findAllByField(Field field);
}
