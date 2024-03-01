package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import java.util.Collection;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;

@Repository
public interface ProjectRepository extends CrudRepository<Project, UUID> {
  Collection<Project> findAllByField(Field field);
}
