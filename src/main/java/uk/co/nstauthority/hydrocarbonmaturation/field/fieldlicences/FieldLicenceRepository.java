package uk.co.nstauthority.hydrocarbonmaturation.field.fieldlicences;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;

@Repository
public interface FieldLicenceRepository extends CrudRepository<FieldLicence, UUID> {
  void deleteAllByField(Field field);

  List<FieldLicence> getLicencesByField(Field field);
}
