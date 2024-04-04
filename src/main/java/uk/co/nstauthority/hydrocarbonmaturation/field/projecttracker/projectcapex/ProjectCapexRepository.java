package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectcapex;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;

@Repository
interface ProjectCapexRepository extends CrudRepository<ProjectCapex, UUID> {
  List<ProjectCapex> findAllByProject(Project project);
}
