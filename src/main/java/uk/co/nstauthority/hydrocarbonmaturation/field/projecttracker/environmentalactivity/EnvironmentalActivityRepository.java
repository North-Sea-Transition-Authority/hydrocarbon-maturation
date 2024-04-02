package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.environmentalactivity;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;

@Repository
interface EnvironmentalActivityRepository extends CrudRepository<EnvironmentalActivity, UUID> {
  Optional<EnvironmentalActivity> findByProject(Project project);
}
